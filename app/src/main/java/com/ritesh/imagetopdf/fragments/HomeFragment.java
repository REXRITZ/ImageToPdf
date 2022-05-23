package com.ritesh.imagetopdf.fragments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.ritesh.imagetopdf.R;
import com.ritesh.imagetopdf.adapters.PdfAdapter;
import com.ritesh.imagetopdf.adapters.PdfItemClickListener;
import com.ritesh.imagetopdf.db.PdfDetail;
import com.ritesh.imagetopdf.utils.Utils;
import com.ritesh.imagetopdf.viewmodel.PdfViewModel;
import com.ritesh.imagetopdf.viewmodel.PhotosDataViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class HomeFragment extends Fragment implements PdfItemClickListener, View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private FloatingActionButton mCreateBtn;
    private RecyclerView recyclerView;
    private PdfAdapter adapter;
    private BottomSheetDialog pdfOptionsDialog;
    private ShapeableImageView lock;
    private TextView fileName, fileSize, fileDate;
    private PdfDetail pdfDetail;
    private int selectedPos;
    private LinearProgressIndicator progress;
    private View emptyView;
    private final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mCreateBtn = view.findViewById(R.id.create_btn);
        recyclerView = view.findViewById(R.id.pdf_recyclerview);
        view.findViewById(R.id.settings).setOnClickListener(this);
        progress = view.findViewById(R.id.progress);
        emptyView = view.findViewById(R.id.empty_list_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PdfViewModel viewModel = new ViewModelProvider(requireActivity()).get(PdfViewModel.class);
        PhotosDataViewModel viewModel1 = new ViewModelProvider(requireActivity()).get(PhotosDataViewModel.class);
        viewModel1.removePhotos();
        adapter = new PdfAdapter(getContext(),this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerDecoration = new DividerItemDecoration(recyclerView.getContext(),layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerDecoration);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        progress.setVisibility(View.VISIBLE);
        initPdfOptionsDialog();
        viewModel.getAllPdfs().observe(getViewLifecycleOwner(), pdfDetails -> {
            if (pdfDetails.size() != 0) {
                emptyView.setVisibility(View.GONE);
                adapter.updateList(checkList(pdfDetails));
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
            new Handler().postDelayed(() -> progress.setVisibility(View.GONE),300);
        });

        mCreateBtn.setOnClickListener(view1 -> storagePermission());
    }

    private void storagePermission() {
        if(EasyPermissions.hasPermissions(requireContext(), permissions)) {
            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_selectImagesFragment);
        } else {
            int PERM_CODE = 221;
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.permission_rationale),
                    PERM_CODE,
                    permissions);
        }
    }

    private ArrayList<PdfDetail> checkList(List<PdfDetail> pdfDetails) {
        ArrayList<PdfDetail> pdfs = new ArrayList<>();
        for(PdfDetail pdf : pdfDetails) {
            if(new File(pdf.getFilePath()).exists()) {
                pdfs.add(pdf);
            } else {
                PdfViewModel.delete(pdf);
            }
        }
        return pdfs;
    }

    //View on click listener
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.share_file) {
            pdfOptionsDialog.dismiss();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            File file = new File(pdfDetail.getFilePath());
            Uri path = FileProvider.getUriForFile(requireContext(),
                    Utils.APPID + ".fileprovider",
                    file);
            shareIntent.putExtra(Intent.EXTRA_STREAM,path);
            shareIntent.setType("application/pdf");
            startActivity(Intent.createChooser(shareIntent,"Share pdf"));
        } else if (id == R.id.delete_file) {
            pdfOptionsDialog.dismiss();
            adapter.deleteFile(selectedPos);
        } else if (id == R.id.rename_file) {
            pdfOptionsDialog.dismiss();
            renamePdf();
        } else if (id == R.id.open_file) {
            pdfOptionsDialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(adapter.getItem(selectedPos).getFilePath());
            Uri path = FileProvider.getUriForFile(requireContext(),
                    Utils.APPID + ".fileprovider",
                    file);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent.createChooser(intent, "Open using");
            startActivity(Intent.createChooser(intent, "Open using"));
        } else if (id == R.id.settings) {
            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_settingsFragment);
        }
    }

    //Pdf item on click listeners
    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.more_options) {
            this.selectedPos = position;
            pdfOptionsDialog.show();
            populateOptionsView(position);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(adapter.getItem(position).getFilePath());
            Uri path = FileProvider.getUriForFile(requireContext(),
                    Utils.APPID + ".fileprovider",
                    file);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
    }

    private void renamePdf() {
        View view = View.inflate(requireContext(), R.layout.rename_file_dialog,null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(),R.style.RenameDialogTheme);
        builder.setView(view);
        TextInputLayout fileInput = view.findViewById(R.id.file_input);
        MaterialButton ok = view.findViewById(R.id.ok);
        MaterialButton cancel = view.findViewById(R.id.cancel);
        builder.setTitle(R.string.dialog_rename_title);
        AlertDialog dialog = builder.create();
        dialog.show();

        ok.setOnClickListener(view12 -> {
            String name = Objects.requireNonNull(fileInput.getEditText()).getText().toString().trim();
            name += ".pdf";
            if(name.length() == 4) {
                fileInput.setHelperText(getString(R.string.empty_file_msg));
            } else if(!isValid(name)) {
                fileInput.setHelperText(getString(R.string.special_char_msg));
            } else if (new File(Utils.PATH + File.separator + name).exists()) {
                fileInput.setError(getString(R.string.file_name_exists_msg));
            }else {
                adapter.renameFile(selectedPos, name + ".pdf");
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(view1 -> dialog.dismiss());
    }

    private boolean isValid(String name) {
        final String pattern = "/\\:*?\"<>|";
        for(int i = 0; i < name.length(); ++i) {
            for(int j = 0; j < pattern.length(); ++j) {
                if(name.charAt(i) == pattern.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initPdfOptionsDialog() {
        View view = View.inflate(requireContext(),R.layout.pdf_item_options, null);
        pdfOptionsDialog = new BottomSheetDialog(requireContext(),R.style.RoundedBottomSheet);
        pdfOptionsDialog.setContentView(view);
        view.findViewById(R.id.rename_file).setOnClickListener(this);
        view.findViewById(R.id.share_file).setOnClickListener(this);
        view.findViewById(R.id.delete_file).setOnClickListener(this);
        fileName = view.findViewById(R.id.file_name);
        fileSize = view.findViewById(R.id.file_size);
        fileDate = view.findViewById(R.id.date);
        lock = view.findViewById(R.id.lockImageView);
        view.findViewById(R.id.open_file).setOnClickListener(this);
    }

    private void populateOptionsView(int position) {
        this.pdfDetail = adapter.getItem(position);
        fileName.setText(pdfDetail.getFileName());
        fileSize.setText(Utils.convertFileSize(this.pdfDetail.getFileSize()));
        fileDate.setText(Utils.getFormattedDate(this.pdfDetail.getDateCreated()));
        if(this.pdfDetail.isEncrypted()) {
            lock.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_selectImagesFragment);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        new AppSettingsDialog.Builder(this).build().show();
    }
}