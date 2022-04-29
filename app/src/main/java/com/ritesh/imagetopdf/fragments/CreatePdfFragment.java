package com.ritesh.imagetopdf.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.ritesh.imagetopdf.R;
import com.ritesh.imagetopdf.model.Result;
import com.ritesh.imagetopdf.utils.MakePdfDoc;
import com.ritesh.imagetopdf.utils.Utils;
import com.ritesh.imagetopdf.viewmodel.PhotosDataViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class CreatePdfFragment extends Fragment implements MakePdfDoc.SaveProgressListener {

    private TextInputLayout fileInput, passwordInput;
    private LinearLayout orientation, quality;
    private TextView orientationTv, qualityTv;
    private SwitchMaterial passwordEnabled;
    private MaterialButton goNextBtn;
    private LinearProgressIndicator progress;
    private TextView progressMessage;
    private AlertDialog dialog;
    private List<Uri> imagesList;
    private String path;
    private int orientationPos = 0, qualityPos = 2;
    private String[] orientVal;
    private String[] qualityVal;
    private MaterialToolbar toolbar;
    private String status;

    public CreatePdfFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        orientVal = getResources().getStringArray(R.array.orientation);
        qualityVal = getResources().getStringArray(R.array.quality);
        View view = inflater.inflate(R.layout.fragment_create_pdf, container, false);
        fileInput = view.findViewById(R.id.file_name_input);
        passwordInput = view.findViewById(R.id.password_input);
        orientation = view.findViewById(R.id.orientation);
        quality = view.findViewById(R.id.quality);
        orientationTv = view.findViewById(R.id.orientation_text);
        qualityTv = view.findViewById(R.id.quality_text);
        passwordEnabled = view.findViewById(R.id.password_enabled);
        goNextBtn = view.findViewById(R.id.createPdfBtn);
        toolbar = view.findViewById(R.id.toolBar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String fileName = "imgtopdf_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        Objects.requireNonNull(fileInput.getEditText()).setText(fileName);
        PhotosDataViewModel viewModel = new ViewModelProvider(requireActivity()).get(PhotosDataViewModel.class);
        imagesList = viewModel.getFinalizedPhotos();

        passwordEnabled.setOnCheckedChangeListener((compoundButton, checked) -> {
            if(checked) {
                passwordInput.setVisibility(View.VISIBLE);
            } else {
                passwordInput.setVisibility(View.GONE);
                Objects.requireNonNull(passwordInput.getEditText()).setText("");
            }
        });

        orientation.setOnClickListener(view14 -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_orientation_title)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    orientationTv.setText(orientVal[orientationPos]);
                    dialogInterface.dismiss();
                })
                .setNeutralButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .setSingleChoiceItems(orientVal, orientationPos, (dialogInterface, pos) -> orientationPos = pos)
                .show());
        quality.setOnClickListener(view13 -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_pdfquality)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    qualityTv.setText(qualityVal[qualityPos]);
                    dialogInterface.dismiss();
                })
                .setNeutralButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .setSingleChoiceItems(qualityVal, qualityPos, (dialogInterface, pos) -> qualityPos = pos)
                .show());

        goNextBtn.setOnClickListener(view12 -> {

            String name = fileInput.getEditText().getText().toString().trim();
            if(name.length() == 0) {
                fileInput.setError(getString(R.string.empty_file_msg));
                return;
            } else if (!isValid(name)) {
                fileInput.setError(getString(R.string.special_char_msg));
                return;
            }else if(new File(Utils.PATH + File.separator + name + ".pdf").exists()) {
                fileInput.setError(getString(R.string.file_name_exists_msg));
                return;
            }
            name += ".pdf";
            path = Utils.PATH + File.separator + name;
            final String pass = Objects.requireNonNull(passwordInput.getEditText()).getText().toString().trim();
            if(passwordEnabled.isChecked() && pass.length() == 0) {
                passwordInput.setError(getString(R.string.valid_password_msg));
                return;
            }
            MakePdfDoc document = new MakePdfDoc(CreatePdfFragment.this);
            document.setNameAndPass(pass, name);
            document.setOrientation(orientationTv.getText().toString(),requireContext());
            document.setQuality(qualityTv.getText().toString(),requireContext());
            showSaveProgressDialog(document);

        });

        toolbar.setNavigationOnClickListener(view1 -> requireActivity().onBackPressed());
    }

    private boolean isValid(@NonNull String name) {
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

    private void showSaveProgressDialog(MakePdfDoc document) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.save_progress, null, false);
        progress = view.findViewById(R.id.save_progress);
        progressMessage = view.findViewById(R.id.progress_message);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(view);
        builder.setTitle(R.string.dialog_savepdf_title);
        builder.setPositiveButton(R.string.cancel, (dialogInterface, i) -> document.cancel(true));
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        progress.setMax(imagesList.size());
        new Handler().postDelayed(() -> document.execute(imagesList,requireContext().getContentResolver()),100);
    }

    @Override
    public void onProgressUpdate(int prog) {
        progress.setProgress(prog);
        int percent = (prog) * 100 / imagesList.size();
        progressMessage.setText(String.format(Locale.getDefault(),getString(R.string.savepdf_converting_msg),percent));
    }

    @Override
    public void onDone(String status) {
        //create new intent
        this.status = status;
        dialog.dismiss();
        if(status.equals("OK")) {
            Result result = new Result(imagesList.get(0), path);
            CreatePdfFragmentDirections.ActionCreatePdfFragmentToFinishPdfFragment action =
                    CreatePdfFragmentDirections.actionCreatePdfFragmentToFinishPdfFragment(result);
            NavHostFragment.findNavController(this).navigate(action);
        } else {
            Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content),"Failed to create pdf", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        
        if (status != null) {
            this.onDone(status);
        }
        
    }
}