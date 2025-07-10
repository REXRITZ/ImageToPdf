package com.ritesh.imagetopdf.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ritesh.imagetopdf.R
import com.ritesh.imagetopdf.databinding.FragmentCreatePdfBinding
import com.ritesh.imagetopdf.domain.utils.Extensions.toggleVisibility
import com.ritesh.imagetopdf.domain.utils.Utils
import com.ritesh.imagetopdf.model.Orientation
import com.ritesh.imagetopdf.model.Quality

class CreatePdfFragment : BottomSheetDialogFragment(){

    private var _binding: FragmentCreatePdfBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: CreatePdfViewModel by hiltNavGraphViewModels(R.id.pdf_creation_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        _binding = FragmentCreatePdfBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()

    }

    private fun initData() {
        val pdfOptions = viewModel.pdfOptions
        binding.run {
            fileNameInput.editText?.setText(pdfOptions.fileName)
            passwordEnabled.isChecked = pdfOptions.passwordEnabled
            passwordInput.toggleVisibility(pdfOptions.passwordEnabled)
            passwordInput.editText?.setText(pdfOptions.password)
            setOrientation(pdfOptions.orientation)
            setQuality(pdfOptions.pdfQuality)
        }
    }

    private fun setOrientation(orientation: Orientation) {
        val id = when(orientation) {
            Orientation.AUTO -> R.id.oa
            Orientation.PORTRAIT -> R.id.op
            Orientation.LANDSCAPE -> R.id.ol
        }
        binding.orientationGroup.check(id)
    }

    private fun setQuality(quality: Quality) {
        val id = when(quality) {
            Quality.LOW -> R.id.ql
            Quality.MEDIUM -> R.id.qm
            Quality.HIGH -> R.id.qh
            Quality.ORIGINAL -> R.id.qo
        }
        binding.qualityGroup.check(id)
    }


    override fun onResume() {
        super.onResume()

        binding.run {
            fileNameInput.editText?.doAfterTextChanged {
                viewModel.pdfOptions.fileName = it.toString()
            }
            passwordInput.editText?.doAfterTextChanged {
                viewModel.pdfOptions.password = it.toString()
            }
            passwordEnabled.setOnCheckedChangeListener { _, isChecked ->
                passwordInput.toggleVisibility(isChecked)
                viewModel.pdfOptions.passwordEnabled = isChecked
            }
            createPdfBtn.setOnClickListener {
                var error = false
                if (!Utils.isFileNameValid(viewModel.pdfOptions.fileName) || viewModel.pdfOptions.fileName.isEmpty()) {
                    fileNameInput.helperText = "please enter a valid file name"
                    error = true
                }
                if(viewModel.pdfOptions.password.isEmpty() && viewModel.pdfOptions.passwordEnabled) {
                    passwordInput.helperText = "please enter a valid password"
                    error = true
                }
                viewModel.pdfOptions.orientation = getSelectedOrientation()
                viewModel.pdfOptions.pdfQuality = getSelectedQuality()
                if(!error) {
                    findNavController().navigate(R.id.action_createPdfFragment_to_saveProgressDialog)
                }
            }
            cancelBtn.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun getSelectedQuality(): Quality {
        val id = binding.qualityGroup.getCheckedCheckableImageButtonId()
        return when(id) {
            R.id.ql -> Quality.LOW
            R.id.qm -> Quality.MEDIUM
            R.id.qh -> Quality.HIGH
            else -> Quality.ORIGINAL
        }
    }

    private fun getSelectedOrientation(): Orientation {
        val id = binding.orientationGroup.getCheckedCheckableImageButtonId()
        return when(id) {
            R.id.op -> Orientation.PORTRAIT
            R.id.ol -> Orientation.LANDSCAPE
            else -> Orientation.AUTO
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}