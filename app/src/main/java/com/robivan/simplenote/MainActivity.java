package com.robivan.simplenote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements NoteListFragment.Contract, EditNoteFragment.Contract {
    private static final String NOTES_LIST_FRAGMENT = "NOTES_LIST_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        showNoteList();
    }

    private void showNoteList() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new NoteListFragment(), NOTES_LIST_FRAGMENT)
                .commit();
    }
    private void showEditNote() {
        showEditNote(null);
    }

    private void showEditNote(@Nullable NoteEntity noteEntity) {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .add(R.id.fragment_container, EditNoteFragment.newInstance(noteEntity))
                .commit();
    }

    @Override
    public void createNewNote() {
        showEditNote();
    }

    @Override
    public void editNote(NoteEntity noteEntity) {
        showEditNote(noteEntity);
    }

    @Override
    public void saveNote(NoteEntity note) {
        getSupportFragmentManager().popBackStack();
        NoteListFragment noteListFragment = (NoteListFragment) getSupportFragmentManager().findFragmentByTag(NOTES_LIST_FRAGMENT);
        noteListFragment.addNote(note);
    }
}