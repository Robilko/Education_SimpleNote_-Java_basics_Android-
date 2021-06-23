package com.robivan.simplenote;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder {
    private final TextView titleTextView, bodyTextView;
    private final CardView cardView;
    private NoteEntity noteEntity;

    public NoteViewHolder(@NonNull View itemView, @NonNull NotesAdapter.OnItemClickListener clickListener) {
        super(itemView);
        cardView = (CardView)itemView;
        titleTextView = itemView.findViewById(R.id.subject_title_view);
        bodyTextView = itemView.findViewById(R.id.subject_text_view);
        cardView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (id) {
                    case R.id.edit_note_popup:
                        clickListener.onItemClick(noteEntity);
                        return true;
                    case R.id.add_note_to_favorite_popup:  //TODO реализовать добавление заметки в избранное
                    case R.id.delete_popup:                //TODO реализовать удаление заметки
                        Toast.makeText(v.getContext(), v.getResources().getString(R.string.do_not_realised_toast),
                                Toast.LENGTH_SHORT).show();
                        return true;
                }
                return true;
            });
            popupMenu.show();
        });
    }

    public void bind(NoteEntity noteEntity) {
        this.noteEntity = noteEntity;
        titleTextView.setText(noteEntity.title);
        bodyTextView.setText(noteEntity.noteText);
    }
}
