package com.robivan.simplenote;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NoteListFragment.Contract, EditNoteFragment.Contract, AuthFragment.Controller {
    private static final String NOTES_LIST_FRAGMENT = "NOTES_LIST_FRAGMENT";
    private static final String EDIT_NOTES_FRAGMENT = "EDIT_NOTES_FRAGMENT";
    public static final String SHARED_PREFERENCE_NAME = "FragmentNavigation";
    private static long backPressed;

    private Navigation navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readSettings();
        navigation = new Navigation(getSupportFragmentManager());
        if (User.getNameUser() == null) {
            navigation.addFragment(R.id.main_fragment_container, AuthFragment.newInstance(), "");
        } else {
            initDrawer(initToolbar());
            if (savedInstanceState == null) {
                showNoteList();
            }
            setToolBarTitle();
        }
    }

    @Override
    public void openMainScreen() {
        initDrawer(initToolbar());
        navigation.addFragment(R.id.main_fragment_container, new NoteListFragment(), NOTES_LIST_FRAGMENT);
    }

    private void readSettings() {
        getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE);
    }

    // регистрация drawer
    private void initDrawer(Toolbar toolbar) {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Обработка навигационного меню
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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        return toolbar;
    }

    private boolean navigateFragment(int id) {
        switch (id) {
            case R.id.action_favorite: //TODO реализовать фрагмент с избранными заметками
            case R.id.action_deleted:  //TODO реализовать фрагмент с удаленными заметками
            case R.id.action_settings: //TODO реализвать фрагмент с настройками приложения
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
        setToolBarTitle();
        navigation.addFragment(R.id.main_fragment_container, new NoteListFragment(), NOTES_LIST_FRAGMENT);
    }

    private void showEditNote(@Nullable NoteEntity note, int position) {
        setTitle(EditNoteFragment.getTitle(note == null));
        navigation.addFragment(R.id.main_fragment_container, EditNoteFragment.newInstance(note, position), EDIT_NOTES_FRAGMENT);
    }

    @Override
    public void createNewNote(int position) {
        NoteEntity newNote = new NoteEntity(null, null, NoteEntity.getCurrentDate());
        newNote.setId(Integer.toString(position));
        showEditNote(newNote, position);
    }

    @Override
    public void editNote(NoteEntity noteEntity, int position) {
        showEditNote(noteEntity, position);
    }

    @Override
    public void saveNote(NoteEntity note, int position) {
        getSupportFragmentManager().popBackStack();

        NoteListFragment noteListFragment = (NoteListFragment) getSupportFragmentManager().findFragmentByTag(NOTES_LIST_FRAGMENT);
        if (noteListFragment != null) {
            noteListFragment.addOrUpdateNote(note, position);
            setTitle(NoteListFragment.getTitle());
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (fragment instanceof NoteListFragment) {
            if (backPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                finish();
            } else {
                Toast.makeText(getBaseContext(), R.string.on_back_pressed_exit, Toast.LENGTH_SHORT).show();
            }
            backPressed = System.currentTimeMillis();
        } else if (fragment instanceof EditNoteFragment) {
            showNoteList();
        }
    }

    private void setToolBarTitle() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (fragment instanceof EditNoteFragment) {
            setTitle(EditNoteFragment.getTitle(false));
        } else if (fragment instanceof NoteListFragment) {
            setTitle(NoteListFragment.getTitle());
        }
    }
}