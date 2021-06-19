package com.robivan.simplenote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NoteListFragment.Contract, EditNoteFragment.Contract {
    private static final String NOTES_LIST_FRAGMENT = "NOTES_LIST_FRAGMENT";
    private static final String EDIT_NOTES_FRAGMENT = "EDIT_NOTES_FRAGMENT";
    private boolean isTwoPanel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isTwoPanel = findViewById(R.id.second_fragment_container) != null;
        showNoteList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_menu:
                Toast.makeText(this, getResources().getString(R.string.settings_toast), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about_app_menu:
                Toast.makeText(this, getResources().getString(R.string.about_app_toast), Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        transaction.add(isTwoPanel ? R.id.second_fragment_container : R.id.main_fragment_container, EditNoteFragment.newInstance(noteEntity), EDIT_NOTES_FRAGMENT)
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
        if (noteListFragment != null) {
            noteListFragment.addNote(note);
        }
        EditNoteFragment editNoteFragment = (EditNoteFragment) getSupportFragmentManager().findFragmentByTag(EDIT_NOTES_FRAGMENT);
        if (editNoteFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(editNoteFragment).commit();
        }
    }
}