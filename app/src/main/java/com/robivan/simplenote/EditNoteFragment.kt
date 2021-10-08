package com.robivan.simplenote

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class EditNoteFragment : Fragment() {
    private lateinit var saveButton: MaterialButton
    private lateinit var noteHeading: EditText
    private lateinit var noteTextBody: EditText
    private lateinit var noteDateCreate: TextView
    private var position = 0
    private var note: NoteEntity? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_note, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        saveButton = view.findViewById(R.id.save_btn)
        noteHeading = view.findViewById(R.id.note_heading)
        noteTextBody = view.findViewById(R.id.note_text_body)
        noteDateCreate = view.findViewById(R.id.note_date)
        noteHeading.requestFocus()
        hideKeyboardAfterRefocusing(noteHeading)
        hideKeyboardAfterRefocusing(noteTextBody)
    }

    private fun hideKeyboardAfterRefocusing(editText: EditText?) {
        editText!!.onFocusChangeListener = OnFocusChangeListener { _: View?, focused: Boolean ->
            val keyboard = requireActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            if (focused) {
                keyboard.showSoftInput(editText, 0)
            } else {
                keyboard.hideSoftInputFromWindow(editText.windowToken, 0)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        assert(arguments != null)
        val noteParcel: NoteEntity? = requireArguments().getParcelable(NOTE_EXTRA_KEY)
        if (noteParcel != null) {
            note = noteParcel
            position = requireArguments().getInt(POSITION_EXTRA_KEY)
        }
        fillNote(note)
        saveButton.setOnClickListener {
            contract!!.saveNote(
                changeOrCreateNote(),
                position
            )
        }
    }

    private fun changeOrCreateNote(): NoteEntity? {
        val name = noteHeading.text.toString()
        val description = noteTextBody.text.toString()
        val date: Long = NoteEntity.currentDate
        return if (note != null) {
            note!!.title = name
            note!!.noteText = description
            note!!.date = date
            note
        } else NoteEntity(name, description, date)
    }

    private fun fillNote(note: NoteEntity?) {
        if (note == null) return
        noteHeading.setText(note.title)
        noteTextBody.setText(note.noteText)
        val dateCreate = resources.getString(R.string.note_item_date) + note.createDate
        noteDateCreate.text = dateCreate
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is Contract) { "Activity must implement Contract" }
    }

    private val contract: Contract?
        get() = activity as Contract?

    internal interface Contract {
        fun saveNote(note: NoteEntity?, position: Int)
    }

    companion object {
        private const val NOTE_EXTRA_KEY = "NOTE_EXTRA_KEY"
        private const val POSITION_EXTRA_KEY = "POSITION_EXTRA_KEY"
        fun newInstance(noteEntity: NoteEntity?, position: Int): EditNoteFragment {
            val fragment = EditNoteFragment()
            val bundle = Bundle()
            bundle.putParcelable(NOTE_EXTRA_KEY, noteEntity)
            bundle.putInt(POSITION_EXTRA_KEY, position)
            fragment.arguments = bundle
            return fragment
        }

        fun getTitle(newNote: Boolean): Int {
            return if (newNote) R.string.create_note_title else R.string.edit_note_title
        }
    }
}