package com.ritesh.imagetopdf.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.ritesh.imagetopdf.viewmodel.PhotosDataViewModel;
import com.ritesh.imagetopdf.R;
import com.ritesh.imagetopdf.adapters.ItemDecorator;
import com.ritesh.imagetopdf.adapters.MyAdapter;
import com.ritesh.imagetopdf.adapters.OnItemClickListener;
import com.ritesh.imagetopdf.adapters.PhotosItemClickListener;
import com.ritesh.imagetopdf.model.ClickMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;


public class ChooseFragment extends Fragment implements View.OnClickListener, ActionMode.Callback, OnItemClickListener {

    private RecyclerView mainRecyclerView;
    private MyAdapter adapter;
    private BottomSheetDialog optionsDialog;
    private PhotosDataViewModel viewModel;
    private ClickMode clickMode;
    private ActionMode actionMode;
    private LinearLayout bottomLayout;
    private TextView helpMessage;
    private MaterialToolbar toolbar;
    private AlertDialog alertDialog;
    private NavController navController;
    public ChooseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose, container, false);
        mainRecyclerView = view.findViewById(R.id.imagesRecyclerView);
        view.findViewById(R.id.go_next_btn).setOnClickListener(this);
        LinearLayout options = view.findViewById(R.id.more_options);
        options.setOnClickListener(this);
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
        initOptionsDialog();
        initLoadDialog();
        clickMode = ClickMode.IDLE;
        mainRecyclerView.addItemDecoration(new ItemDecorator((int) getResources().getDimension(R.dimen.dimen_4)));
        mainRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mainRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapter = new MyAdapter(getContext(),this);
        mainRecyclerView.setAdapter(adapter);
        viewModel.getRecentlyAddedPhotos().observe(getViewLifecycleOwner(), uris -> {
            if (uris != null) {
                adapter.addItemsToList(uris);
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mainRecyclerView);
        mainRecyclerView.addOnItemTouchListener(new PhotosItemClickListener(getContext(), this));

        toolbar.setNavigationOnClickListener(view1 -> requireActivity().onBackPressed());
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
        //options dialog
        if(id == R.id.more_options) {
            optionsDialog.show();
        } else if(id == R.id.add_more) {
            optionsDialog.dismiss();
            ChooseFragmentDirections.ActionChooseFragmentToSelectPhotoFragment action =
                    ChooseFragmentDirections.actionChooseFragmentToSelectPhotoFragment(true);
            navController.navigate(action);
        } else if(id == R.id.rearrange_photos) {
            optionsDialog.dismiss();
            clickMode = ClickMode.DRAG;
            showAndHideBottomLayout(View.GONE);
            if (actionMode == null) {
                changeColor(ContextCompat.getColor(requireContext(),R.color.primaryVariant),
                        ContextCompat.getColor(requireContext(), R.color.primary), 300);
                actionMode = ((AppCompatActivity)requireActivity()).startSupportActionMode(this);
            }
            helpMessage.setText(R.string.drag_items_msg);
        }else if(id == R.id.go_next_btn) {
            if (adapter.getItemCount() == 0) {
                Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        getString(R.string.emptylistmsg),
                        Snackbar.LENGTH_SHORT);
                snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                snackbar.show();
            } else {
                alertDialog.show();
                Executors.newSingleThreadExecutor().execute(() -> {
                    viewModel.setFinalizedPhotos(adapter.getImagesList());
                    requireActivity().runOnUiThread(() -> new Handler().postDelayed(() -> {
                        alertDialog.dismiss();
                        NavHostFragment
                                .findNavController(ChooseFragment.this)
                                .navigate(R.id.action_chooseFragment_to_createPdfFragment);
                    },500));
                });
            }
        } else if(id == R.id.drag_finish) {
            clickMode = ClickMode.IDLE;
            showAndHideBottomLayout(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (clickMode == ClickMode.IDLE) {
            //open using intent
        } else {
            adapter.toggleSelection(position);
            if (adapter.getSelectedItemCount() == 0) {
                stopActionMode();
            }
        }
    }

    @Override
    public void onItemLongClick(int position) {
        if(clickMode == ClickMode.IDLE) {
            clickMode = ClickMode.MULTI;
            showAndHideBottomLayout(View.GONE);
            optionsDialog.dismiss();
        }
        if(clickMode == ClickMode.MULTI) {
            if (actionMode == null) {
                changeColor(ContextCompat.getColor(requireContext(),R.color.primaryVariant),
                        ContextCompat.getColor(requireContext(), R.color.primary), 300);
                actionMode = ((AppCompatActivity)requireActivity()).startSupportActionMode(this);
            }
            adapter.toggleSelection(position);
            actionMode.setTitle(String.valueOf(adapter.getSelectedItemCount()));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (clickMode == ClickMode.MULTI) {
            mode.getMenuInflater().inflate(R.menu.filter_photo_contextual, menu);
        } else if (clickMode == ClickMode.DRAG){
            mode.getMenuInflater().inflate(R.menu.filter_drag, menu);
        }
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
        } else if(id == R.id.drag_finish) {
            clickMode = ClickMode.IDLE;
            stopActionMode();
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

    private void initOptionsDialog() {
        optionsDialog = new BottomSheetDialog(requireContext(),R.style.RoundedBottomSheet);
        optionsDialog.setContentView(R.layout.options_sheet);
        optionsDialog.findViewById(R.id.add_more).setOnClickListener(this);
        optionsDialog.findViewById(R.id.rearrange_photos).setOnClickListener(this);
    }

    private void initLoadDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(R.layout.processing_dialog);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
    }
}