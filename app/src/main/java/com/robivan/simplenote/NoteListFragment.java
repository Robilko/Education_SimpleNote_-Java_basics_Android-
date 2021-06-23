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
    private  NotesAdapter adapter;

    private final ArrayList<NoteEntity> noteList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        createNoteButton = view.findViewById(R.id.create_new_note);
        recyclerView = view.findViewById(R.id.recycle_view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new NotesAdapter();
        adapter.setOnItemClickListener(item -> getContract().editNote(item));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setData(noteList);
        createNoteButton.setOnClickListener(v -> getContract().createNewNote());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Contract)) {
            throw new IllegalStateException("Activity must implement Contract");
        }
    }

    public void addNote(NoteEntity note) {
        NoteEntity sameNote = findNoteById(note.id);
        if (sameNote != null) {
            noteList.remove(sameNote);
        }
        noteList.add(note);
        adapter.setData(noteList);
    }

    @Nullable
    private NoteEntity findNoteById(String id) {
        for(NoteEntity note : noteList) {
            if (note.id.equals(id)) return note;
        }
        return null;
    }

    private Contract getContract() {
        return (Contract)getActivity();
    }

    interface Contract{
        void createNewNote();
        void editNote(NoteEntity noteEntity);
    }
}
