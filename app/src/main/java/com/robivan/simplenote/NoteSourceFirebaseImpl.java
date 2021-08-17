package com.robivan.simplenote;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NoteSourceFirebaseImpl implements NoteSource {

    private static final String NOTES_COLLECTION = "notes";
    private static final String TAG = "NoteSourceFirebaseImpl";

    // База данных Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Коллекция документов
    private final CollectionReference collection = db.collection(NOTES_COLLECTION);

    // Загружаемый список карточек
    private List<NoteEntity> notesData = new ArrayList<>();

    public NoteSourceFirebaseImpl() {
    }

    @Override
    public NoteSource init(NoteSourceResponse noteSourceResponse) {
        // Получить всю коллекцию, отсортированную по полю «Дата»
        // При удачном считывании данных загрузим список карточек
        collection.orderBy(NoteMapping.Fields.DATE, Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notesData = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Map<String, Object> doc = document.getData();
                            String id = document.getId();
                            NoteEntity noteData = NoteMapping.toNoteData(id, doc);
                            notesData.add(noteData);
                        }
                        Log.d(TAG, "success " + notesData.size() + " qnt");
                        noteSourceResponse.initialized(NoteSourceFirebaseImpl.this);
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));
        return this;
    }

    @Override
    public NoteEntity getNoteData(int position) {
        return notesData.get(position);
    }

    @Override
    public int size() {
        return notesData == null ? 0 : notesData.size();
    }

    @Override
    public void deleteNoteData(int position) {
        // Удалить документ с определённым идентификатором
        String id = notesData.get(position).getId();
        if (id != null) {
            collection.document(id).delete();
            notesData.remove(position);
        }
    }

    @Override
    public void updateNoteData(NoteEntity note, int position) {
        String id = note.getId();
        if (id != null) {
            // Изменить документ по идентификатору
            collection.document(id).set(note);
            notesData.set(position, note);
        }
    }

    @Override
    public void addNoteData(NoteEntity note) {
        // Добавить документ
        collection.add(note).addOnSuccessListener(documentReference -> note.setId(documentReference.getId()));
        notesData.add(note);
    }

    @Override
    public void clearNoteData() {
        for (NoteEntity note : notesData) {
            collection.document(note.getId()).delete().addOnSuccessListener(command -> notesData.clear());
        }
    }
}
