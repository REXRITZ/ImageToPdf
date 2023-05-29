package com.ritesh.imagetopdf.ui.create

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ritesh.imagetopdf.R
import com.ritesh.imagetopdf.databinding.SaveProgressDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveProgressDialog : DialogFragment() {

    private var _binding: SaveProgressDialogBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: CreatePdfViewModel by hiltNavGraphViewModels(R.id.pdf_creation_graph)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = SaveProgressDialogBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(binding.root)
            setCancelable(false)
        }
        return dialog.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.onEvent(CreateEvent.CreatePdf)
        viewModel.uiState.observe(viewLifecycleOwner) {
            when(it) {
                is CreatePdfViewModel.UiState.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                is CreatePdfViewModel.UiState.Progress -> {
                    binding.progressTxt.text = "${it.value}%"
                    if (it.value == 100) {
                        findNavController().navigate(R.id.action_saveProgressDialog_to_finishPdfFragment)
                    }
                }
                else -> {}
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}