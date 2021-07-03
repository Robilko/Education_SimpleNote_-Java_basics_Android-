package com.robivan.simplenote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NoteListFragment.Contract, EditNoteFragment.Contract {
    private static final String NOTES_LIST_FRAGMENT = "NOTES_LIST_FRAGMENT";
    private static final String EDIT_NOTES_FRAGMENT = "EDIT_NOTES_FRAGMENT";
    private boolean isTwoPanel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawer(initToolbar());
        isTwoPanel = findViewById(R.id.second_fragment_container) != null;
        showNoteList();
    }

    private void initDrawer(Toolbar toolbar) {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (navigateFragment(id)) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private boolean navigateFragment(int id) {
        switch (id) {
            case R.id.action_favorite: //TODO реализовать фрагмент с настройками приложения
            case R.id.action_deleted:  //TODO реализовать фрагмент с удаленными заметками
            case R.id.action_settings: //TODO реализвать фрагмент с избранными заметками
                Toast.makeText(MainActivity.this, getResources().getString(R.string.do_not_realised_toast),
                        Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem search = menu.findItem(R.id.search_menu);
        SearchView searchText = (SearchView) search.getActionView();
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.do_not_realised_toast),
                        Toast.LENGTH_SHORT).show();
                return true; //TODO
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true; //TODO
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search_menu: //TODO реализовать поиск в заметках
                Toast.makeText(this, getResources().getString(R.string.do_not_realised_toast), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about_app_menu:
                Toast.makeText(this, getResources().getString(R.string.about_app_toast), Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNoteList() {
        setTitle(NoteListFragment.getTitle());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment_container, new NoteListFragment(), NOTES_LIST_FRAGMENT)
                .commit();
    }

    private void showEditNote() {
        showEditNote(null);
    }

    private void showEditNote(@Nullable NoteEntity note) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!isTwoPanel) {
            setTitle(EditNoteFragment.getTitle(note == null));
            transaction.addToBackStack(null);
        }
        transaction.add(isTwoPanel ? R.id.second_fragment_container : R.id.main_fragment_container, EditNoteFragment.newInstance(note), EDIT_NOTES_FRAGMENT)
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();

        NoteListFragment noteListFragment = (NoteListFragment) fragmentManager.findFragmentByTag(NOTES_LIST_FRAGMENT);
        if (noteListFragment != null) {
            noteListFragment.addNote(note);
            setTitle(NoteListFragment.getTitle());
        }
        EditNoteFragment editNoteFragment = (EditNoteFragment) fragmentManager.findFragmentByTag(EDIT_NOTES_FRAGMENT);
        if (editNoteFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(editNoteFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (fragment instanceof NoteListFragment) {
            setTitle(NoteListFragment.getTitle());
        }
    }
}