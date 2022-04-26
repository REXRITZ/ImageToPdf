package com.ritesh.imagetopdf.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.ritesh.imagetopdf.R;
import com.ritesh.imagetopdf.db.AppPref;
import com.ritesh.imagetopdf.model.Result;
import com.ritesh.imagetopdf.utils.Utils;

import java.io.File;


public class FinishPdfFragment extends Fragment{

    private TextView fileName;
    private ShapeableImageView pdfThumbnail;
    private MaterialButton share, openPdf;
    private MaterialToolbar toolbar;
    private MaterialCardView rateLayout;
    private MaterialButton rateBtn;
    public FinishPdfFragment() {
        // Required empty public constructor
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finish_pdf, container, false);
        fileName = view.findViewById(R.id.file_name);
        pdfThumbnail = view.findViewById(R.id.thumbnail);
        share = view.findViewById(R.id.share);
        openPdf = view.findViewById(R.id.open);
        toolbar = view.findViewById(R.id.toolBar);
        rateLayout = view.findViewById(R.id.rate_layout);
        rateBtn = view.findViewById(R.id.rate_app);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppPref appPref = AppPref.getInstance(requireContext());
        rateLayout.setVisibility(appPref.getRateDialogVisibility());
        final Result result = FinishPdfFragmentArgs.fromBundle(getArguments()).getResult();
        File file = new File(result.filePath);
        Uri path = FileProvider.getUriForFile(requireContext(),
                Utils.APPID + ".fileprovider",
                file);
        fileName.setText(result.filePath.substring(result.filePath.lastIndexOf(File.separator) + 1));
        try {
            Glide.with(requireContext()).load(result.thumbnailPath).into(pdfThumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        openPdf.setOnClickListener(view13 -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });

        share.setOnClickListener(view12 -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM,path);
            shareIntent.setType("application/pdf");
            startActivity(Intent.createChooser(shareIntent,"Share pdf"));
        });

        rateBtn.setOnClickListener(view14 -> {
            appPref.setRateDialogVisibility();
            Intent rateIntent = new Intent(Intent.ACTION_VIEW);
            rateIntent.setData(Uri.parse("market://details?id=" + Utils.APPID));
            rateIntent.setPackage("com.android.vending");
            startActivity(rateIntent);
        });

        toolbar.setNavigationOnClickListener(view1 -> requireActivity().onBackPressed());

    }
}