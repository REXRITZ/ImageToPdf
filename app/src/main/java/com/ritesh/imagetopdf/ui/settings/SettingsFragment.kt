package com.ritesh.imagetopdf.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.ritesh.imagetopdf.R
import com.ritesh.imagetopdf.data.AppPref
import com.ritesh.imagetopdf.databinding.FragmentSettingsBinding
import com.ritesh.imagetopdf.domain.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding ?= null
    private val binding get() = _binding!!

    @Inject
    lateinit var appPref: AppPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            versionNumber.text = Utils.VERSION
            darkMode.isChecked = appPref.isDarkMode
        }
    }

    override fun onResume() {
        super.onResume()
        binding.run {
            shareApp.setOnClickListener {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, String.format(Locale.getDefault(),
                        getString(R.string.share_msg),
                        getString(R.string.app_name),
                        Utils.STORE_LINK))
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, null))
            }
            rateApp.setOnClickListener {
                val rateIntent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse("market://details?id=" + Utils.APPID)
                    setPackage("com.android.vending")
                }
                startActivity(rateIntent)
            }
            versionApp.setOnClickListener {
                val clipboardManager =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData =
                    ClipData.newPlainText(getString(R.string.version), Utils.VERSION)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(requireContext(), getString(R.string.clipboard_msg), Toast.LENGTH_SHORT)
                    .show()
            }
            toolBar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            darkMode.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                appPref.setAppTheme(isChecked)
            }
        }
    }
}