package com.robivan.simplenote;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class NoteListFragment extends Fragment {

    private MaterialButton createNoteButton;
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private NoteSource data;

    private final ArrayList<NoteEntity> noteList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotesAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((item, position) -> getContract().editNote(item, position));
        data = new NoteSourceFirebaseImpl().init(noteSource ->adapter.notifyDataSetChanged());
        adapter.setDataSource(data);
        createNoteButton.setOnClickListener(v -> {
            getContract().createNewNote(data.size());
        });
    }

    private void initView(View view) {
        createNoteButton = view.findViewById(R.id.create_new_note);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Contract)) {
            throw new IllegalStateException("Activity must implement Contract");
        }
    }

    public void addOrUpdateNote(NoteEntity note, int position) {
        if (data.size() != position) {
            data.updateNoteData(note, position);
        } else data.addNoteData(note);
        //метод init ооповещает обозревателей
        data.init(noteSource ->adapter.notifyDataSetChanged());
        //позицианируется на новой позиции
        recyclerView.scrollToPosition(position);
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
