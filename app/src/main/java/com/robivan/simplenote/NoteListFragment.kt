package com.robivan.simplenote

import android.content.Context
import com.google.android.material.button.MaterialButton
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.content.SharedPreferences
import android.content.DialogInterface
import android.content.res.Configuration
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class NoteListFragment : Fragment() {
    private lateinit var createNoteButton: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotesAdapter
    private lateinit var data: NoteSource
    private var noteListLayout = 0
    private lateinit var preferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_list, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preferences = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        initView(view)
        setLayoutSettings()
        adapter = NotesAdapter()
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener({ item: NoteEntity?, position: Int, popupId: Int -> {
                when (popupId) {
                    adapter.CMD_UPDATE -> contract!!.editNote(item, position)
                    adapter.CMD_DELETE -> deleteNoteAndShowDialog(position)
                }
            }
        })
        data = NoteSourceFirebaseImpl().init { adapter.notifyDataSetChanged() }
        adapter.setDataSource(data)
        createNoteButton.setOnClickListener {
            contract!!.createNewNote(
                data.size()
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is Contract) { "Activity must implement Contract" }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_list_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.change_layout_menu) {
            noteListLayout = preferences.getInt(LAYOUT_SETTINGS, 0)
            if (noteListLayout == CMD_STAGGERED_GRID) {
                preferences.edit().putInt(LAYOUT_SETTINGS, CMD_LINEAR).apply()
            } else {
                preferences.edit().putInt(LAYOUT_SETTINGS, CMD_STAGGERED_GRID).apply()
            }
            setLayoutSettings()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteNoteAndShowDialog(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.alert_title_delete_note)
            .setMessage(R.string.alert_message_delete_note)
            .setCancelable(false)
            .setPositiveButton(R.string.positive_button) { _: DialogInterface?, _: Int ->
                data.deleteNoteData(position)
                adapter.notifyItemRemoved(position)
            }
            .setNegativeButton(R.string.negative_button) { _: DialogInterface?, _: Int -> }
            .setIcon(android.R.drawable.ic_menu_delete)
            .show()
    }

    private fun initView(view: View) {
        createNoteButton = view.findViewById(R.id.create_new_note)
        recyclerView = view.findViewById(R.id.recycler_view)
    }

    private fun setLayoutSettings() {
        if (!preferences.contains(LAYOUT_SETTINGS)) {
            preferences.edit().putInt(LAYOUT_SETTINGS, CMD_STAGGERED_GRID).apply()
        } else {
            noteListLayout = preferences.getInt(LAYOUT_SETTINGS, 0)
            if (noteListLayout == CMD_STAGGERED_GRID) {
                setStaggeredGridLayoutFromOrientation()
            } else if (noteListLayout == CMD_LINEAR) {
                recyclerView.layoutManager = LinearLayoutManager(context)
            }
        }
    }

    private fun setStaggeredGridLayoutFromOrientation() {
        if (resources.configuration.orientation ==
            Configuration.ORIENTATION_LANDSCAPE
        ) {
            recyclerView.layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        } else {
            recyclerView.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    fun addOrUpdateNote(note: NoteEntity, position: Int) {
        if (data.size() != position) {
            data.updateNoteData(note, position)
        } else {
            data.addNoteData(note)
        }
        //метод init ооповещает обозревателей
        data.init { adapter.notifyDataSetChanged() }
        //позицианируется на новой позиции
        recyclerView.smoothScrollToPosition(position)
    }

    private val contract: Contract?
        get() = activity as Contract?

    internal interface Contract {
        fun createNewNote(position: Int)
        fun editNote(noteEntity: NoteEntity?, position: Int)
    }

    companion object {
        private const val APP_PREFERENCES = "my_settings"
        private const val LAYOUT_SETTINGS = "layout_settings"
        private const val CMD_STAGGERED_GRID = 0
        private const val CMD_LINEAR = 1
        val title: Int
            get() = R.string.notes_list_title
    }
}