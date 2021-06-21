package com.robivan.simplenote;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class NoteListFragment extends Fragment {
    private MaterialButton createNoteButton;
    private RecyclerView recyclerView;
    private  NotesAdapter adapter;

    private final ArrayList<NoteEntity> noteList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        createNoteButton = view.findViewById(R.id.create_new_note);
        recyclerView = view.findViewById(R.id.recycle_view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new NotesAdapter();
        adapter.setOnItemClickListener(item -> getContract().editNote(item));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        renderList(noteList);
        createNoteButton.setOnClickListener(v -> getContract().createNewNote());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Contract)) {
            throw new IllegalStateException("Activity must implement Contract");
        }
    }

    public void addNote(NoteEntity note) {
        NoteEntity sameNote = findNoteById(note.id);
        if (sameNote != null) {
            noteList.remove(sameNote);
        }
        noteList.add(note);
        renderList(noteList);
    }

    @Nullable
    private NoteEntity findNoteById(String id) {
        for(NoteEntity note : noteList) {
            if (note.id.equals(id)) return note;
        }
        return null;
    }

    private void renderList(List<NoteEntity> notes) {
//        listLayout.removeAllViews();
//        for (NoteEntity note : notes) {
//            Button button = new Button(getContext());
//            button.setText(note.title);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = requireActivity();
//                    PopupMenu popupMenu = new PopupMenu(activity, v);
//                    activity.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            int id = item.getItemId();
//                            switch (id) {
//                                case R.id.edit_note_popup:
//                                    getContract().editNote(note);
//                                    return true;
//                                case R.id.add_note_to_favorite_popup:  //TODO реализовать добавление заметки в избранное
//                                case R.id.delete_popup:                //TODO реализовать удаление заметки
//                                    Toast.makeText(getContext(), getResources().getString(R.string.do_not_realised_toast),
//                                            Toast.LENGTH_SHORT).show();
//                                    return true;
//                            }
//                            return true;
//                        }
//                    });
//                    popupMenu.show();
//                }
//            });
//            listLayout.addView(button);
//        }
        adapter.setData(notes);
    }

    private Contract getContract() {
        return (Contract)getActivity();
    }

    interface Contract{
        void createNewNote();
        void editNote(NoteEntity noteEntity);
    }
}
