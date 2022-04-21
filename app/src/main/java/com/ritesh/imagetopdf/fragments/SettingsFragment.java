package com.ritesh.imagetopdf.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.ritesh.imagetopdf.R;
import com.ritesh.imagetopdf.db.AppPref;
import com.ritesh.imagetopdf.utils.Utils;

import java.util.Locale;

public class SettingsFragment extends Fragment implements View.OnClickListener{

    private SwitchMaterial darkMode;
    private AppPref appPref;
    private MaterialToolbar toolbar;
    private TextView versionNum;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPref = AppPref.getInstance(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        darkMode = view.findViewById(R.id.dark_mode);
        view.findViewById(R.id.share_app).setOnClickListener(this);
        view.findViewById(R.id.rate_app).setOnClickListener(this);
        view.findViewById(R.id.version_app).setOnClickListener(this);
        versionNum = view.findViewById(R.id.version_number);
        toolbar = view.findViewById(R.id.toolBar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        versionNum.setText(Utils.VERSION);
        darkMode.setChecked(appPref.isDarkMode());
        darkMode.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            appPref.setAppTheme(checked);
        });

        toolbar.setNavigationOnClickListener(view1 -> requireActivity().onBackPressed());
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.share_app) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, String.format(Locale.getDefault(),getString(R.string.share_msg),getString(R.string.app_name),Utils.STORE_LINK));
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        } else if (id == R.id.rate_app) {
            Intent rateIntent = new Intent(Intent.ACTION_VIEW);
            rateIntent.setData(Uri.parse("market://details?id=" + Utils.APPID));
            rateIntent.setPackage("com.android.vending");
            startActivity(rateIntent);
        } else if (id == R.id.version_app) {
            ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(getString(R.string.version), versionNum.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(requireContext(), getString(R.string.clipboard_msg), Toast.LENGTH_SHORT).show();
        }
    }
}