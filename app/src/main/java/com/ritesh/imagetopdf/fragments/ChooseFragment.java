package com.ritesh.imagetopdf.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.ritesh.imagetopdf.viewmodel.PhotosDataViewModel;
import com.ritesh.imagetopdf.R;
import com.ritesh.imagetopdf.adapters.ItemDecorator;
import com.ritesh.imagetopdf.adapters.ImageAdapter;
import com.ritesh.imagetopdf.adapters.ImageClickListener;
import com.ritesh.imagetopdf.model.ClickMode;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;


public class ChooseFragment extends Fragment implements View.OnClickListener, ActionMode.Callback, ImageClickListener {

    private RecyclerView mainRecyclerView;
    private ImageAdapter adapter;
    private PhotosDataViewModel viewModel;
    private ClickMode clickMode;
    private ActionMode actionMode;
    private LinearLayout bottomLayout;
    private TextView helpMessage;
    private MaterialToolbar toolbar;
    private AlertDialog alertDialog;
    private NavController navController;
    private CoordinatorLayout coordinatorLayout;
    int position = 0;
    public ChooseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose, container, false);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        mainRecyclerView = view.findViewById(R.id.imagesRecyclerView);
        view.findViewById(R.id.go_next_btn).setOnClickListener(this);
        view.findViewById(R.id.more_options).setOnClickListener(this);
        bottomLayout = view.findViewById(R.id.linearLayout2);
        helpMessage = view.findViewById(R.id.help_message);
        toolbar = view.findViewById(R.id.toolBar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(requireActivity()).get(PhotosDataViewModel.class);
        initLoadDialog();
        clickMode = ClickMode.IDLE;
        mainRecyclerView.addItemDecoration(new ItemDecorator(4));
        mainRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mainRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapter = new ImageAdapter(getContext(),this);
        mainRecyclerView.setAdapter(adapter);
        viewModel.getRecentlyAddedPhotos().observe(getViewLifecycleOwner(), uris -> {
            if (uris != null) {
                adapter.addItemsToList(uris);
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mainRecyclerView);
//        mainRecyclerView.addOnItemTouchListener(new PhotosItemClickListener(getContext(), this));
        toolbar.setNavigationOnClickListener(view1 -> requireActivity().onBackPressed());

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.select) {
                clickMode = ClickMode.IDLE;
                onImageLongClick(0);
                return true;
            } else if(item.getItemId() == R.id.drag) {
                clickMode = ClickMode.DRAG;
                helpMessage.setText(R.string.drag_items_msg);
                return true;
            }
            return false;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setEnabled(true);
                new MaterialAlertDialogBuilder(requireContext(),R.style.RenameDialogTheme)
                        .setTitle(R.string.discard_changes)
                        .setMessage(getString(R.string.discard_msg))
                        .setPositiveButton(R.string.discard, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            setEnabled(false);
                            requireActivity().onBackPressed();
                        })
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    final ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (clickMode != ClickMode.DRAG) {
                return 0;
            }
            return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                    ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
        }
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            if(clickMode == ClickMode.DRAG) {
                Collections.swap(adapter.getImagesList(), viewHolder.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());
                adapter.notifyItemMoved(viewHolder.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());
                return true;
            }
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    // View click
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.go_next_btn) {
            if (adapter.getItemCount() == 0) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                        getString(R.string.emptylistmsg),
                        Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(),R.color.primary));
                snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                snackbar.show();
            } else {
                alertDialog.show();
                Executors.newSingleThreadExecutor().execute(() -> {
                    viewModel.setFinalizedPhotos(adapter.getImagesList());
                    requireActivity().runOnUiThread(() -> new Handler().postDelayed(() -> {
                        alertDialog.dismiss();
                        navController.navigate(R.id.action_chooseFragment_to_createPdfFragment);
                    },300));
                });
            }
        } else if (id == R.id.more_options) {
            navController.navigate(R.id.action_chooseFragment_to_selectImagesFragment);
        }
    }

    @Override
    public void onImageClick(int position) {
        if (clickMode == ClickMode.IDLE) {
            this.position = position;
            List<Uri> images = adapter.getImagesList();
            final String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
            UCrop.Options options = new UCrop.Options();
            options.setFreeStyleCropEnabled(true);
            options.setCompressionQuality(100);
            options.setActiveControlsWidgetColor(ContextCompat.getColor(requireContext(),R.color.primary));
            UCrop.of(images.get(position),Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),fileName)))
                    .withMaxResultSize(720,1280)
                    .withOptions(options)
                    .start(requireContext(),this);
        } else if (clickMode == ClickMode.MULTI){
            adapter.toggleSelection(position);
            actionMode.setTitle(String.valueOf(adapter.getSelectedItemCount()));
            if (adapter.getSelectedItemCount() == 0) {
                stopActionMode();
            }
        }
    }

    @Override
    public void onImageLongClick(int position) {
        if(clickMode == ClickMode.IDLE) {
            clickMode = ClickMode.MULTI;
            showAndHideBottomLayout(View.GONE);
        }
        if(clickMode == ClickMode.MULTI) {
            startActionMode();
            adapter.toggleSelection(position);
            actionMode.setTitle(String.valueOf(adapter.getSelectedItemCount()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP && data != null) {
            final Uri resultUri = UCrop.getOutput(data);
            adapter.updateItem(position,resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getContext(), "Error while cropping", Toast.LENGTH_SHORT).show();
        }
    }

    private void startActionMode() {
        if (actionMode == null) {
            changeColor(ContextCompat.getColor(requireContext(),R.color.primaryVariant),
                    ContextCompat.getColor(requireContext(), R.color.primary), 300);
            actionMode = ((AppCompatActivity)requireActivity()).startSupportActionMode(this);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.filter_photo_contextual, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.delete) {
            adapter.deleteItems();
            stopActionMode();
        } else if(id == R.id.select_all){
            adapter.selectAllItems();
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        stopActionMode();
    }

    public void changeColor(int from, int to, long duration) {
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
        animator.setDuration(duration);

        animator.addUpdateListener(valueAnimator -> requireActivity().getWindow().setStatusBarColor((Integer) valueAnimator.getAnimatedValue()));
        animator.start();
    }

    private void stopActionMode() {
        changeColor(ContextCompat.getColor(requireContext(),R.color.primary),
                ContextCompat.getColor(requireContext(), R.color.primaryVariant), 200);
        adapter.removeSelectedItems();
        actionMode.finish();
        actionMode = null;
        clickMode = ClickMode.IDLE;
        helpMessage.setText(R.string.select_item_msg);
        showAndHideBottomLayout(View.VISIBLE);
    }

    private void showAndHideBottomLayout(final int visibility) {
        float alpha;
        int translation;
        if(visibility == View.GONE) {
            alpha = 0.0f;
            translation = bottomLayout.getHeight();
        } else {
            alpha = 1.0f;
            translation = 0;
        }
        bottomLayout.animate()
                .translationY(translation)
                .alpha(alpha)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bottomLayout.setVisibility(visibility);
                    }
                });

    }

    private void initLoadDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(R.layout.processing_dialog);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
    }
}