package com.robivan.simplenote;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
            clickListener.onItemClick(noteEntity);
        });
    }

    public void bind(NoteEntity noteEntity) {
        this.noteEntity = noteEntity;
        titleTextView.setText(noteEntity.title);
        bodyTextView.setText(noteEntity.noteText);
    }
}
