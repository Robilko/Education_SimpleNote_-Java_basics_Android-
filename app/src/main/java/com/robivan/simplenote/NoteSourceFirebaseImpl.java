package com.robivan.simplenote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoteSourceFirebaseImpl implements NoteSource{

    private static final String NOTES_COLLECTION = "notes";
    private static final String TAG = "NoteSourceFirebaseImpl";

    // База данных Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Коллекция документов
    private final CollectionReference collection = db.collection(NOTES_COLLECTION);

    // Загружаемый список карточек
    private List<NoteEntity> notesData = new ArrayList<>();

    public NoteSourceFirebaseImpl() {}

    @Override
    public NoteSource init(NoteSourceResponse noteSourceResponse) {
        // Получить всю коллекцию, отсортированную по полю «Дата»
        // При удачном считывании данных загрузим список карточек
        collection.orderBy(NoteMapping.Fields.DATE, Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notesData = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> doc = document.getData();
                            String id = document.getId();
                            NoteEntity noteData = NoteMapping.toNoteData(id, doc);
                            notesData.add(noteData);
                         }
                        Log.d(TAG, "success " + notesData.size() + " qnt");
                        noteSourceResponse.initialized(NoteSourceFirebaseImpl.this);
                    }
                    else {
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
    public void updateNoteData(NoteEntity noteData, int position) {
        String id = noteData.getId();
        if (id != null) {
            // Изменить документ по идентификатору
            collection.document(id).set(noteData);
        }
    }

    @Override
    public void addNoteData(NoteEntity noteData) {
        // Добавить документ
        collection.add(noteData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                noteData.setId(documentReference.getId());
            }
        });
    }

    @Override
    public void clearNoteData() {
        for (NoteEntity noteData : notesData) {
            collection.document(noteData.getId()).delete()
                    .addOnSuccessListener(command -> notesData.clear());
        }
    }
}
