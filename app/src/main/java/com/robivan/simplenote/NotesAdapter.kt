package com.robivan.simplenote

import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.robivan.simplenote.NotesAdapter.NoteViewHolder

class NotesAdapter : RecyclerView.Adapter<NoteViewHolder>() {

    private var dataSource: NoteSource? = null
    val CMD_UPDATE = 0
    val CMD_DELETE = 1

    // Слушатель будет устанавливаться извне
    private lateinit var onItemClickListener: OnItemClickListener

    // Передаём в конструктор источник данных
    // В нашем случае это массив, но может быть и запрос к БД
    fun setDataSource(dataSource: NoteSource?) {
        this.dataSource = dataSource
        notifyDataSetChanged()
    }

    fun interface OnItemClickListener {
        fun onItemClick(noteEntity: NoteEntity?, position: Int, popupId: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: NoteViewHolder, position: Int) {
        viewHolder.bind(dataSource, position)
    }

    override fun getItemCount(): Int {
        return dataSource!!.size()
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView
        private val bodyTextView: TextView
        private val dateTextView: TextView
        private var note: NoteEntity? = null
        fun bind(noteSourceImpl: NoteSource?, position: Int) {
            note = noteSourceImpl!!.getNoteData(position)
            titleTextView.text = note!!.title
            bodyTextView.text = note!!.noteText
            dateTextView.text = note!!.createDate
        }

        init {
            val cardView = itemView as CardView
            titleTextView = itemView.findViewById(R.id.subject_title_view)
            bodyTextView = itemView.findViewById(R.id.subject_text_view)
            dateTextView = itemView.findViewById(R.id.subject_date_view)
            cardView.setOnClickListener { v: View ->
                val popupMenu = PopupMenu(v.context, v)
                popupMenu.inflate(R.menu.popup_menu)
                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.edit_note_popup -> {
                            onItemClickListener.onItemClick(note, adapterPosition, CMD_UPDATE)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.add_note_to_favorite_popup -> {
                            Toast.makeText(
                                v.context, v.resources.getString(R.string.do_not_realised_toast),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnMenuItemClickListener true
                        }
                        R.id.delete_popup -> {
                            onItemClickListener.onItemClick(note, adapterPosition, CMD_DELETE)
                            return@setOnMenuItemClickListener true
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }
}