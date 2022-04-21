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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ritesh.imagetopdf.utils.Utils;
import com.ritesh.imagetopdf.viewmodel.PhotosDataViewModel;
import com.ritesh.imagetopdf.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SelectPhotoFragment extends BottomSheetDialogFragment implements View.OnClickListener{

    private NavController navController;
    private Uri cameraPhotoUri;
    private boolean fromOptions;
    private PhotosDataViewModel viewModel;

    private final ActivityResultLauncher<String> getGalleryContent = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(),
            this::onPhotosSelected);

    private final ActivityResultLauncher<Intent> getCameraContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        addTogallery();
                        List<Uri> list = new ArrayList<>();
                        list.add(cameraPhotoUri);
                        onPhotosSelected(list);
                    } else {
                        File file = new File(cameraPhotoUri.getPath());
                        file.delete();
                    }
                }
            });

    public SelectPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.RoundedBottomSheet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.select_photo_sheet, container, false);
        view.findViewById(R.id.select_camera).setOnClickListener(this);
        view.findViewById(R.id.select_gallery).setOnClickListener(this);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PhotosDataViewModel.class);
        fromOptions = SelectPhotoFragmentArgs.fromBundle(getArguments()).getFromOptions();
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.select_gallery) {
            getGalleryContent.launch("image/*");
        } else if( id == R.id.select_camera) {
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
        }
    }

    private void onPhotosSelected(List<Uri> list) {
        if(list == null) {
            return;
        }
        if(list.size() > 0) {
            viewModel.addNewPhotos((ArrayList<Uri>) list);
        }
        if (!fromOptions) {
            navController.navigate(R.id.action_selectPhotoFragment_to_chooseFragment);
        }
        this.dismiss();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "camera_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        cameraPhotoUri = Uri.fromFile(image);
        return image;
    }

    private void addTogallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(cameraPhotoUri);
        requireActivity().sendBroadcast(mediaScanIntent);
    }
}