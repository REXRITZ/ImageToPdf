package com.ritesh.imagetopdf.ui.home

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.ritesh.imagetopdf.R
import com.ritesh.imagetopdf.adapters.PdfAdapter
import com.ritesh.imagetopdf.databinding.FragmentHomeBinding
import com.ritesh.imagetopdf.domain.utils.Extensions.toggleVisibility
import com.ritesh.imagetopdf.domain.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

@AndroidEntryPoint
class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val STORAGE_PERM_CODE = 121
    private val permissions =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

    private var _binding: FragmentHomeBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        val layoutManager = LinearLayoutManager(context)
        val dividerDecoration =
            DividerItemDecoration(context, layoutManager.orientation)
        val adapter = PdfAdapter(
            onItemClick = { path ->
                startOpenIntent(path)
            },
            onOptionsClick = {
                val dir = HomeFragmentDirections.actionHomeFragmentToPdfOptionsSheet(it)
                findNavController().navigate(dir)
            }
        )

        binding.pdfRecyclerview.run {
            setLayoutManager(layoutManager)
            addItemDecoration(dividerDecoration)
        }
        binding.pdfRecyclerview.adapter = adapter

        viewModel.uiState.observe(viewLifecycleOwner) { state->
            when(state) {
                is UiState.ProgressShow -> {
                    binding.progress.toggleVisibility(true)
                }
                is UiState.Success -> {
                    val pdfList = state.data
                    binding.emptyStateView.toggleVisibility(pdfList.isEmpty())
                    binding.progress.toggleVisibility(false)
                    adapter.submitList(pdfList)
                    if (pdfList.isEmpty()) {
                        binding.emptyAnimation.playAnimation()
                    }
                }
            }
        }
        binding.emptyAnimation.repeatCount = LottieDrawable.INFINITE

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.createBtn.setOnClickListener {
            checkPermission()
        }
        binding.toolbar.setOnMenuItemClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
            return@setOnMenuItemClickListener true
        }
    }


    private fun startOpenIntent(path: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(requireContext(),Utils.AUTHORITY, file)
        val openIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(openIntent)
    }

    private fun checkPermission() {

        if (EasyPermissions.hasPermissions(requireContext(),*permissions )) {
            findNavController()
                .navigate(R.id.action_homeFragment_to_pdf_creation_graph)
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_rationale),
                STORAGE_PERM_CODE,
                *permissions)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        findNavController()
            .navigate(R.id.action_homeFragment_to_pdf_creation_graph)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        println(perms)
        AppSettingsDialog.Builder(this).build().show()
    }

    override fun onStop() {
        super.onStop()
        if(binding.emptyAnimation.isAnimating) {
            binding.emptyAnimation.pauseAnimation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}