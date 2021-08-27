package com.robivan.simplenote

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.lang.Exception
import java.util.*

class NoteSourceFirebaseImpl : NoteSource {
    // База данных Firestore
    private var db = FirebaseFirestore.getInstance()

    // Коллекция документов
    private val collection = db.collection(NOTES_COLLECTION!!)

    // Загружаемый список карточек
    private var notesData: MutableList<NoteEntity?>? = ArrayList()
    override fun init(noteSourceResponse: (NoteSource) -> Unit): NoteSource {
        // Получить всю коллекцию, отсортированную по полю «Дата»
        // При удачном считывании данных загрузим список карточек
        collection.orderBy(NoteMapping.Fields.DATE, Query.Direction.DESCENDING).get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                if (task.isSuccessful) {
                    notesData = ArrayList()
                    for (document in Objects.requireNonNull(task.result)!!) {
                        val doc = document.data
                        val id = document.id
                        val noteData = NoteMapping.toNoteData(id, doc)
                        (notesData as ArrayList<NoteEntity?>).add(noteData)
                    }
                    Log.d(TAG, "success " + (notesData as ArrayList<NoteEntity?>).size + " qnt")
                    noteSourceResponse.initialized(this@NoteSourceFirebaseImpl)
                } else {
                    Log.d(TAG, "get failed with ", task.exception)
                }
            }.addOnFailureListener { e: Exception? -> Log.d(TAG, "get failed with ", e) }
        return this
    }

    override fun getNoteData(position: Int): NoteEntity? {
        return notesData?.get(position)
    }

    override fun size(): Int {
        return if (notesData == null) 0 else notesData!!.size
    }

    override fun deleteNoteData(position: Int) {
        // Удалить документ с определённым идентификатором
        val id = notesData!![position]?.id
        if (id != null) {
            collection.document(id).delete()
            notesData!!.removeAt(position)
        }
    }

    override fun updateNoteData(noteData: NoteEntity, position: Int) {
        val id = noteData.id
        if (id != null) {
            // Изменить документ по идентификатору
            notesData!![position]?.id?.let { collection.document(it).set(NoteMapping.toDocument(noteData)) }
            notesData!![position] = noteData
        }
    }

    override fun addNoteData(noteData: NoteEntity) {
        // Добавить документ
        collection.add(NoteMapping.toDocument(noteData))
            .addOnSuccessListener { documentReference: DocumentReference ->
                noteData.id = documentReference.id
            }
        notesData!!.add(noteData)
    }

    override fun clearNoteData() {
        for (note in notesData!!) {
            if (note != null) {
                note.id?.let { collection.document(it).delete() }
            }
        }
        notesData!!.clear()
    }

    companion object {
        private val NOTES_COLLECTION = User.emailUser
        private const val TAG = "NoteSourceFirebaseImpl"
    }
}