package com.robivan.simplenote;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class EditNoteFragment extends Fragment {

    private static final String NOTE_EXTRA_KEY = "NOTE_EXTRA_KEY";
    private static final String POSITION_EXTRA_KEY = "POSITION_EXTRA_KEY";

    private MaterialButton saveButton;
    private EditText noteHeading, noteTextBody;
    private TextView noteDateCreate;
    private int position;

    @Nullable
    private NoteEntity note = null;

    public static EditNoteFragment newInstance(@Nullable NoteEntity noteEntity, int position) {
        EditNoteFragment fragment = new EditNoteFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NOTE_EXTRA_KEY, noteEntity);
        bundle.putInt(POSITION_EXTRA_KEY, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        saveButton = view.findViewById(R.id.save_btn);
        noteHeading = view.findViewById(R.id.note_heading);
        noteTextBody = view.findViewById(R.id.note_text_body);
        noteDateCreate = view.findViewById(R.id.note_date);
        noteHeading.requestFocus();
        hideKeyboardAfterRefocusing(noteHeading);
        hideKeyboardAfterRefocusing(noteTextBody);
    }

    private void hideKeyboardAfterRefocusing(EditText editText) {
        editText.setOnFocusChangeListener((view1, focused) -> {
            InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (focused)
                keyboard.showSoftInput(editText, 0);
            else
                keyboard.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments().getParcelable(NOTE_EXTRA_KEY) != null) {
            note = getArguments().getParcelable(NOTE_EXTRA_KEY);
            position = getArguments().getInt(POSITION_EXTRA_KEY);
        }
        fillNote(note);
        saveButton.setOnClickListener(v ->
                getContract().saveNote(changeOrCreateNote(), position));
    }

    private NoteEntity changeOrCreateNote() {
        String name =  noteHeading.getText().toString();
        String description = noteTextBody.getText().toString();
        long date = NoteEntity.getCurrentDate();
        if (note != null){
            note.setTitle(name);
            note.setNoteText(description);
            note.setDate(date);
            return note;
        } else return new NoteEntity(name,description,date);
    }

    private void fillNote(NoteEntity note) {
        if (note == null) return;
        noteHeading.setText(note.getTitle());
        noteTextBody.setText(note.getNoteText());
        String dateCreate = getResources().getString(R.string.note_item_date) + note.getCreateDate();
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
        void saveNote(NoteEntity note, int position);
    }
}