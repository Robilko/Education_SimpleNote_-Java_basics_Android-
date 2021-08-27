package com.robivan.simplenote

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity(), NoteListFragment.Contract, EditNoteFragment.Contract,
    AuthFragment.Controller {

    companion object {
        private const val NOTES_LIST_FRAGMENT = "NOTES_LIST_FRAGMENT"
        private const val EDIT_NOTES_FRAGMENT = "EDIT_NOTES_FRAGMENT"
        private var backPressed: Long = 0
    }

    private lateinit var navigation: Navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation = Navigation(supportFragmentManager)
        if (User.nameUser == null) {
            navigation.addFragment(
                R.id.main_fragment_container,
                AuthFragment.newInstance(),
                ""
            )
        } else {
            initDrawer(initToolbar())
            if (savedInstanceState == null) {
                showNoteList()
            }
            setToolBarTitle()
        }
    }

    override fun openMainScreen() {
        initDrawer(initToolbar())
        navigation.addFragment(
            R.id.main_fragment_container,
            NoteListFragment(),
            NOTES_LIST_FRAGMENT
        )
    }

    // регистрация drawer
    private fun initDrawer(toolbar: Toolbar) {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // Обработка навигационного меню
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            val id = item.itemId
            if (navigateFragment(id)) {
                drawer.closeDrawer(GravityCompat.START)
                return@setNavigationItemSelectedListener true
            }
            false
        }
    }

    private fun initToolbar(): Toolbar {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar!!).setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        return toolbar
    }

    private fun navigateFragment(id: Int): Boolean {
        when (id) {
            R.id.action_favorite, R.id.action_deleted, R.id.action_settings -> {
                Toast.makeText(
                    this@MainActivity, resources.getString(R.string.do_not_realised_toast),
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val search = menu.findItem(R.id.search_menu)
        val searchText = search.actionView as SearchView
        searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(
                    this@MainActivity, resources.getString(R.string.do_not_realised_toast),
                    Toast.LENGTH_SHORT
                ).show()
                return true //TODO
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true //TODO
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_menu -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.do_not_realised_toast),
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }
            R.id.about_app_menu -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.about_app_toast),
                    Toast.LENGTH_LONG
                ).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showNoteList() {
        setToolBarTitle()
        navigation.addFragment(
            R.id.main_fragment_container,
            NoteListFragment(),
            NOTES_LIST_FRAGMENT
        )
    }

    private fun showEditNote(note: NoteEntity?, position: Int) {
        setTitle(EditNoteFragment.getTitle(note == null))
        navigation.addFragment(
            R.id.main_fragment_container,
            EditNoteFragment.newInstance(note, position),
            EDIT_NOTES_FRAGMENT
        )
    }

    override fun createNewNote(position: Int) {
        val newNote = NoteEntity(null, null, NoteEntity.currentDate)
        newNote.id = position.toString()
        showEditNote(newNote, position)
    }

    override fun editNote(noteEntity: NoteEntity?, position: Int) {
        showEditNote(noteEntity, position)
    }

    override fun saveNote(note: NoteEntity, position: Int) {
        supportFragmentManager.popBackStack()
        val noteListFragment =
            supportFragmentManager.findFragmentByTag(NOTES_LIST_FRAGMENT) as NoteListFragment?
        if (noteListFragment != null) {
            noteListFragment.addOrUpdateNote(note, position)
            setTitle(NoteListFragment.title)
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (fragment is NoteListFragment) {
            if (backPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed()
                finish()
            } else {
                Toast.makeText(baseContext, R.string.on_back_pressed_exit, Toast.LENGTH_SHORT)
                    .show()
            }
            backPressed = System.currentTimeMillis()
        } else {
            super.onBackPressed()
            setToolBarTitle()
        }
    }

    private fun setToolBarTitle() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (fragment is EditNoteFragment) {
            setTitle(EditNoteFragment.getTitle(false))
        } else if (fragment is NoteListFragment) {
            setTitle(NoteListFragment.title)
        }
    }
}