package com.ritesh.imagetopdf.ui.create

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.ritesh.imagetopdf.R
import com.ritesh.imagetopdf.databinding.FragmentFinishPdfBinding
import com.ritesh.imagetopdf.domain.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class FinishPdfFragment : Fragment() {

    private var _binding: FragmentFinishPdfBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: CreatePdfViewModel by hiltNavGraphViewModels(R.id.pdf_creation_graph)

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishPdfBinding.inflate(inflater)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        viewModel.logEvent(firebaseAnalytics)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext())
            .load(viewModel.getThumbnail())
            .into(binding.thumbnail)
        binding.fileName.text = viewModel.pdfOptions.fileName
    }

    override fun onResume() {
        super.onResume()
        val path = "${Utils.PATH}/${viewModel.pdfOptions.fileName}.pdf"
        binding.run {
            goBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            share.setOnClickListener {
                val f = File(path)
                val uri = FileProvider.getUriForFile(requireContext(),Utils.AUTHORITY, f)
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "application/pdf"
                }
                startActivity(Intent.createChooser(shareIntent,null))
            }
            open.setOnClickListener {
                val f = File(path)
                val uri = FileProvider.getUriForFile(requireContext(),Utils.AUTHORITY, f)
                val openIntent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(openIntent, null))
            }
            openWhatsapp.setOnClickListener {
                val f = File(path)
                val uri = FileProvider.getUriForFile(requireContext(),Utils.AUTHORITY, f)
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "application/pdf"
                    setPackage("com.whatsapp")
                }
                startActivity(sendIntent)
            }
        }
    }
}