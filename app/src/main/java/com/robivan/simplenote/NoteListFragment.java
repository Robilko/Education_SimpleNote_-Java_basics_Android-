package com.robivan.simplenote;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.button.MaterialButton;

public class NoteListFragment extends Fragment {

    private MaterialButton createNoteButton;
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private NoteSource data;
    private int noteListLayout;
    private SharedPreferences preferences;
    private static final String APP_PREFERENCES = "my_settings";
    private static final String LAYOUT_SETTINGS = "layout_settings";
    private static final int CMD_STAGGERED_GRID = 0;
    private static final int CMD_LINEAR = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        preferences = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        initView(view);
        setLayoutSettings();
        adapter = new NotesAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((item, position, popupId) -> {
            if (popupId == adapter.CMD_UPDATE) {
                getContract().editNote(item, position);
            } else if (popupId == adapter.CMD_DELETE) {
                deleteNoteAndShowDialog(position);
            }

        });
        data = new NoteSourceFirebaseImpl().init(noteSource -> adapter.notifyDataSetChanged());
        adapter.setDataSource(data);
        createNoteButton.setOnClickListener(v -> getContract().createNewNote(data.size()));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Contract)) {
            throw new IllegalStateException("Activity must implement Contract");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.note_list_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.change_layout_menu) {
            noteListLayout = preferences.getInt(LAYOUT_SETTINGS, 0);
            if (noteListLayout == CMD_STAGGERED_GRID) {
                preferences.edit().putInt(LAYOUT_SETTINGS, CMD_LINEAR).apply();
            } else {
                preferences.edit().putInt(LAYOUT_SETTINGS, CMD_STAGGERED_GRID).apply();
            }
            setLayoutSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteNoteAndShowDialog(int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.alert_title_delete_note)
                .setMessage(R.string.alert_message_delete_note)
                .setCancelable(false)
                .setPositiveButton(R.string.positive_button, (d, i) -> {
                    data.deleteNoteData(position);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton(R.string.negative_button, (d, i) -> {
                })
                .setIcon(android.R.drawable.ic_menu_delete)
                .show();
    }

    private void initView(View view) {
        createNoteButton = view.findViewById(R.id.create_new_note);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void setLayoutSettings() {
        if (!preferences.contains(LAYOUT_SETTINGS)) {
            preferences.edit().putInt(LAYOUT_SETTINGS, CMD_STAGGERED_GRID).apply();
        } else {
            noteListLayout = preferences.getInt(LAYOUT_SETTINGS, 0);
            if (noteListLayout == CMD_STAGGERED_GRID) {
                setStaggeredGridLayoutFromOrientation();
            } else if (noteListLayout == CMD_LINEAR) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }
    }

    private void setStaggeredGridLayoutFromOrientation() {
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }
    }

    public void addOrUpdateNote(NoteEntity note, int position) {
        if (data.size() != position) {
            data.updateNoteData(note, position);
        } else {
            data.addNoteData(note);
        }
        //метод init ооповещает обозревателей
        data.init(noteSource -> adapter.notifyDataSetChanged());
        //позицианируется на новой позиции
        recyclerView.smoothScrollToPosition(position);
    }

    public static int getTitle() {
        return R.string.notes_list_title;
    }

    private Contract getContract() {
        return (Contract) getActivity();
    }

    interface Contract {
        void createNewNote(int position);

        void editNote(NoteEntity noteEntity, int position);
    }
}
