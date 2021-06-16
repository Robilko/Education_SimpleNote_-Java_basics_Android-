package com.robivan.simplenote;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class NoteListFragment extends Fragment {
    private MaterialButton createNoteButton;
    private LinearLayout listLayout;

    private ArrayList<NoteEntity> noteList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        createNoteButton = view.findViewById(R.id.create_new_note);
        listLayout = view.findViewById(R.id.list_layout);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        renderList(noteList);
        createNoteButton.setOnClickListener(v -> {
            getContract().createNewNote();
        });
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
        renderList(noteList);
    }

    @Nullable
    private NoteEntity findNoteById(String id) {
        for(NoteEntity note : noteList) {
            if (note.id.equals(id)) return note;
        }
        return null;
    }

    private void renderList(List<NoteEntity> notes) {
        listLayout.removeAllViews();
        for (NoteEntity note : notes) {
            Button button = new Button(getContext());
            button.setText(note.title);
            button.setOnClickListener(v -> {
                getContract().editNote(note);
            });
            listLayout.addView(button);
        }
    }

    private Contract getContract() {
        return (Contract)getActivity();
    }

    interface Contract{
        void createNewNote();
        void editNote(NoteEntity noteEntity);
    }
}
