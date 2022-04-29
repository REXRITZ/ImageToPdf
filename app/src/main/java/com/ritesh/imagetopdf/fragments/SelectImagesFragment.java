package com.ritesh.imagetopdf.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.ritesh.imagetopdf.R;
import com.ritesh.imagetopdf.adapters.AlbumAdapter;
import com.ritesh.imagetopdf.adapters.AlbumClickListener;
import com.ritesh.imagetopdf.adapters.ImageClickListener;
import com.ritesh.imagetopdf.adapters.ImageSelectAdapter;
import com.ritesh.imagetopdf.adapters.ItemDecorator;
import com.ritesh.imagetopdf.model.Album;
import com.ritesh.imagetopdf.utils.Utils;
import com.ritesh.imagetopdf.viewmodel.PhotosDataViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class SelectImagesFragment extends Fragment implements View.OnClickListener, AlbumClickListener, ImageClickListener {

    private TextView albumPicker;
    private MaterialButton importBtn;
    private RecyclerView recyclerView;
    private ImageSelectAdapter adapter;
    private List<Album> albumList;
    private BottomSheetDialog albumDialog;
    private Uri cameraPhotoUri;
    PhotosDataViewModel viewModel;
    private MaterialToolbar toolbar;

    private final ActivityResultLauncher<String> getGalleryContent = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(),
            this::onPhotosSelected);

    private final ActivityResultLauncher<Intent> getCameraContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        addToGallery();
                        List<Uri> list = new ArrayList<>();
                        list.add(cameraPhotoUri);
                        onPhotosSelected(list);
                    } else {
                        File file = new File(cameraPhotoUri.getPath());
                        file.delete();
                    }
                }
            });

    public SelectImagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_images, container, false);
        albumPicker = view.findViewById(R.id.albumPicker);
        importBtn = view.findViewById(R.id.importBtn);
        recyclerView = view.findViewById(R.id.imagesRecyclerView);
        albumPicker.setOnClickListener(this);
        importBtn.setOnClickListener(this);
        toolbar = view.findViewById(R.id.toolBar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PhotosDataViewModel.class);
        recyclerView.addItemDecoration(new ItemDecorator(4));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapter = new ImageSelectAdapter(getContext(),this);
        recyclerView.setAdapter(adapter);
        importBtn.setText(String.format(Locale.getDefault(),getString(R.string.import_text),adapter.getSelectedImagesCount()));
        importBtn.setEnabled(false);
        Executors.newSingleThreadExecutor().execute(() -> viewModel.loadData(requireActivity()));

        viewModel.getAlbumPhotosList().observe(getViewLifecycleOwner(), album -> {
            if (album != null) {
                albumList = album;
                albumPicker.setText(album.get(2).albumName);
                adapter.updateList(album.get(2).getAlbumImages());
            }
        });
        toolbar.setNavigationOnClickListener(view1 -> requireActivity().onBackPressed());
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.albumPicker) {
            showAlbumPickerDialog();
        } else if (id == R.id.importBtn) {
            viewModel.addNewPhotos(adapter.getSelectedImages());
            NavHostFragment.findNavController(this).navigate(R.id.action_selectImagesFragment_to_chooseFragment);
        }
    }

    @Override
    public void onImageClick(int position) {
        adapter.toggleSelection(position);
        importBtn.setText(String.format(Locale.getDefault(),getString(R.string.import_text),adapter.getSelectedImagesCount()));
        importBtn.setEnabled(adapter.getSelectedImagesCount() != 0);
    }

    @Override
    public void onImageLongClick(int position) {
        adapter.toggleSelection(position);
        importBtn.setText(String.format(Locale.getDefault(),getString(R.string.import_text),adapter.getSelectedImagesCount()));
        importBtn.setEnabled(adapter.getSelectedImagesCount() != 0);
    }

    @Override
    public void onAlbumClicked(int position) {
        albumDialog.hide();
        if (position == 0) {
            //camera intent
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(requireContext(),
                        Utils.APPID + ".fileprovider",
                        photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                getCameraContent.launch(takePhotoIntent);
            }
        } else if (position == 1) {
            //gallery intent
            getGalleryContent.launch("image/*");
        } else {
            albumPicker.setText(albumList.get(position).albumName);
            adapter.updateList(albumList.get(position).getAlbumImages());
        }
    }

    private void onPhotosSelected(List<Uri> list) {
        if(list == null) {
            return;
        }
        adapter.toggleSelection(list);
        importBtn.setText(String.format(Locale.getDefault(),getString(R.string.import_text),adapter.getSelectedImagesCount()));
        importBtn.setEnabled(adapter.getSelectedImagesCount() != 0);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "camera_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        cameraPhotoUri = Uri.fromFile(image);
        return image;
    }

    private void addToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(cameraPhotoUri);
        requireActivity().sendBroadcast(mediaScanIntent);
    }

    private void showAlbumPickerDialog() {
        albumDialog = new BottomSheetDialog(requireContext());
        albumDialog.setContentView(R.layout.image_picker_sheet);
        RecyclerView recyclerView = albumDialog.findViewById(R.id.albumList);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        AlbumAdapter adapter = new AlbumAdapter(requireContext(),albumList,this);
        recyclerView.setAdapter(adapter);
        albumDialog.show();
    }
}