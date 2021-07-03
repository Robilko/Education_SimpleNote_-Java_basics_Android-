package com.robivan.simplenote;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class EditNoteFragment extends Fragment {
    private MaterialButton saveButton;
    private EditText noteHeading, noteTextBody;
    private TextView noteDateCreate;
    private static final String NOTE_EXTRA_KEY = "NOTE_EXTRA_KEY";

    @Nullable
    private NoteEntity note = null;

    public static EditNoteFragment newInstance(@Nullable NoteEntity noteEntity) {
        EditNoteFragment fragment = new EditNoteFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NOTE_EXTRA_KEY, noteEntity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);
        saveButton = view.findViewById(R.id.save_btn);
        noteHeading = view.findViewById(R.id.note_heading);
        noteTextBody = view.findViewById(R.id.note_text_body);
        noteDateCreate = view.findViewById(R.id.note_date);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            note = (NoteEntity) getArguments().getSerializable(NOTE_EXTRA_KEY);
        }
        fillNote(note);
        saveButton.setOnClickListener(v -> getContract().saveNote(collectNote()));
    }

    private NoteEntity collectNote() {
        return new NoteEntity(
                note == null ? NoteEntity.generateNewId() : note.id,
                noteHeading.getText().toString(),
                noteTextBody.getText().toString(),
                note == null ? NoteEntity.getCurrentDate() : note.date);
    }

    private void fillNote(NoteEntity note) {
        if (note == null) return;
        noteHeading.setText(note.title);
        noteTextBody.setText(note.noteText);
        String dateCreate = getResources().getString(R.string.note_item_date) + note.createDate;
        noteDateCreate.setText(dateCreate);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Contract)) {
            throw new IllegalStateException("Activity must implement Contract");
        }
    }

    public static int getTitle(boolean newNote) {
        return newNote ? R.string.create_note_title : R.string.edit_note_title;
    }

    private Contract getContract() {
        return (Contract) getActivity();
    }

    interface Contract {
        void saveNote(NoteEntity note);
    }
}