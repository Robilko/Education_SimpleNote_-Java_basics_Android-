package com.robivan.simplenote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private NoteSource dataSource;
    public final int CMD_UPDATE = 0;
    public final int CMD_DELETE = 1;
    private OnItemClickListener onItemClickListener; // Слушатель будет устанавливаться извне

    public NotesAdapter() {
    }

    // Передаём в конструктор источник данных
    // В нашем случае это массив, но может быть и запрос к БД
    public void setDataSource(NoteSource dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    interface OnItemClickListener {
        void onItemClick(NoteEntity noteEntity, int position, int popupId);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder viewHolder, int position) {
        viewHolder.bind(dataSource, position);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView, bodyTextView;
        private NoteEntity note;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            CardView cardView = (CardView) itemView;
            titleTextView = itemView.findViewById(R.id.subject_title_view);
            bodyTextView = itemView.findViewById(R.id.subject_text_view);

            cardView.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.edit_note_popup:
                            onItemClickListener.onItemClick(note, getAdapterPosition(), CMD_UPDATE);
                            return true;
                        case R.id.add_note_to_favorite_popup:  //TODO реализовать добавление заметки в избранное
                            Toast.makeText(v.getContext(), v.getResources().getString(R.string.do_not_realised_toast),
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.delete_popup:
                            onItemClickListener.onItemClick(note, getAdapterPosition(), CMD_DELETE);
                            return true;
                    }
                    return true;
                });
                popupMenu.show();
            });
        }

        public void bind(NoteSource noteSourceImpl, int position) {
            note = noteSourceImpl.getNoteData(position);
            titleTextView.setText(note.getTitle());
            bodyTextView.setText(note.getNoteText());

        }
    }

}
