package com.ritesh.imagetopdf.ui.home.options

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ritesh.imagetopdf.data.PdfEntity
import com.ritesh.imagetopdf.databinding.PdfItemOptionsBinding
import com.ritesh.imagetopdf.databinding.RenameFileDialogBinding
import com.ritesh.imagetopdf.domain.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PdfOptionsSheet : BottomSheetDialogFragment(){

    private var _binding: PdfItemOptionsBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: PdfOptionViewModel by viewModels()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PdfItemOptionsBinding.inflate(inflater)
        viewModel.pdfData.observe(viewLifecycleOwner) {
            initData(it)
        }
        viewModel.showMessage.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        return binding.root
    }

    private fun initData(pdf : PdfEntity) {
        binding.run {
            fileName.text = pdf.fileName
            fileSize.text = pdf.fileSize
            lockImageView.visibility = if(pdf.isEncrypted) View.VISIBLE else View.GONE
            date.text = pdf.dateCreated
        }
    }

    override fun onResume() {
        super.onResume()
        binding.run {
            renameFile.setOnClickListener {
                showRenameDialog()
            }
            shareFile.setOnClickListener {
                val filePath = viewModel.pdfData.value!!.filePath
                startShareIntent(filePath)
                dismiss()
            }
            openFile.setOnClickListener {
                val filePath = viewModel.pdfData.value!!.filePath
                startOpenIntent(filePath)
                dismiss()
            }
            deleteFile.setOnClickListener {
                showConfirmationDialog()
            }
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Delete")
            setMessage("Are you sure you want to delete?")
            setPositiveButton("Delete") { dialog, _ ->
                viewModel.deleteFile()
                dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun showRenameDialog() {
        val view = RenameFileDialogBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext()).run {
            setView(view.root)
            view.fileInput.requestFocus()
            create()
        }
        view.ok.setOnClickListener {
            val name = view.fileInput.editText?.text.toString()
            if(name.isEmpty() || !Utils.isFileNameValid(name)) {
                view.fileInput.helperText = "Please provide a valid file name"
            } else {
                viewModel.renameFile(name)
                dialog.dismiss()
            }
        }
        view.cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun startOpenIntent(filePath: String) {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(requireContext(), Utils.AUTHORITY, file)
        val openIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(Intent.createChooser(openIntent, null))
    }

    private fun startShareIntent(filePath: String) {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(requireContext(), Utils.AUTHORITY, file)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
        }
        startActivity(Intent.createChooser(shareIntent, null))
        this.dismiss()
    }
}