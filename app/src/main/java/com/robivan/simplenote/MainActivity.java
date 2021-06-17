package com.robivan.simplenote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements NoteListFragment.Contract, EditNoteFragment.Contract {
    private static final String NOTES_LIST_FRAGMENT = "NOTES_LIST_FRAGMENT";
    private boolean isTwoPanel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isTwoPanel = findViewById(R.id.second_fragment_container) != null;
        showNoteList();
    }

    private void showNoteList() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment_container, new NoteListFragment(), NOTES_LIST_FRAGMENT)
                .commit();
    }
    private void showEditNote() {
        showEditNote(null);
    }

    private void showEditNote(@Nullable NoteEntity noteEntity) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!isTwoPanel) {
            transaction.addToBackStack(null);
        }
        transaction.add(isTwoPanel ? R.id.second_fragment_container : R.id.main_fragment_container, EditNoteFragment.newInstance(noteEntity))
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