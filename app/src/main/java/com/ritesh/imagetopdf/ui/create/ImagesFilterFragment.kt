package com.ritesh.imagetopdf.ui.create

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.ritesh.imagetopdf.R
import com.ritesh.imagetopdf.ui.create.CreatePdfViewModel.UiState
import com.ritesh.imagetopdf.adapters.ImageSelectAdapter
import com.ritesh.imagetopdf.adapters.ItemDecorator
import com.ritesh.imagetopdf.databinding.FragmentImagesFilterBinding
import com.ritesh.imagetopdf.domain.utils.Extensions.toggleVisibility
import com.ritesh.imagetopdf.domain.utils.Utils
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ImagesFilterFragment : Fragment() {

    private var _binding: FragmentImagesFilterBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: CreatePdfViewModel by hiltNavGraphViewModels(R.id.pdf_creation_graph)

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(100)) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.onEvent(CreateEvent.OnInsert(uris))
            }
        }


    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object : SimpleCallback(UP or DOWN or START or END, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                val oldPos = viewHolder.adapterPosition
                val newPos = target.adapterPosition
                viewModel.onEvent(CreateEvent.OnSwapped(oldPos, newPos))
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //No need
            }


        }
        ItemTouchHelper(simpleItemTouchCallback)
    }
    private val adapter = ImageSelectAdapter(
        onClick = { sourceUri, pos ->
            viewModel.cropUriPos = pos
            val destinationUri = Uri.fromFile(File.createTempFile(Utils.generateImageName(),".jpg", requireContext().cacheDir))
            UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(1280, 720)
                .start(requireContext(), this)
        },
        onDelete = {
            viewModel.onEvent(CreateEvent.OnDelete(it))
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagesFilterBinding.inflate(inflater)

        binding.bottomNav.background = null
        binding.imagesRecyclerView.run {
            addItemDecoration(ItemDecorator(4))
            layoutManager = GridLayoutManager(context, 3)
        }
        binding.imagesRecyclerView.adapter = adapter
        itemTouchHelper.attachToRecyclerView(binding.imagesRecyclerView)
        viewModel.uiState.observe(viewLifecycleOwner) { state->
            when(state) {
                is UiState.OnListUpdate -> {
                    adapter.submitList(state.data)
                    binding.emptyStateView.toggleVisibility(state.data.isEmpty())
                }
                else -> {}
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.run {
            imagePickerBtn.setOnClickListener {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            nextBtn.setOnClickListener {
                if(adapter.itemCount == 0) {
                    Toast.makeText(requireContext(), "Please add images to continue", Toast.LENGTH_SHORT).show()
                } else {
                    findNavController().navigate(R.id.action_selectImagesFragment_to_createPdfFragment)
                }
            }
            toolBar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            emptyAnimation.setOnClickListener {
                println("YOOO")
                if(!emptyAnimation.isAnimating) {
                    emptyAnimation.playAnimation()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val uri = UCrop.getOutput(data!!)
            viewModel.onEvent(CreateEvent.UpdateUri(uri!!))
        } else if(resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(requireContext(), UCrop.getError(data!!).toString(), Toast.LENGTH_SHORT).show()
        }
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