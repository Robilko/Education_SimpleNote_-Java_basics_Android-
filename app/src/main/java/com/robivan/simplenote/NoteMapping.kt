package com.robivan.simplenote

import java.util.*

object NoteMapping {
    fun toNoteData(id: String?, doc: Map<String?, Any?>): NoteEntity {
        val answer = NoteEntity(
            doc[Fields.TITLE] as String?,
            doc[Fields.NOTE_TEXT] as String?,
            doc[Fields.DATE] as Long
        )
        answer.id = id
        return answer
    }

    fun toDocument(note: NoteEntity): Map<String, Any?> {
        val result: MutableMap<String, Any?> = HashMap()
        result[Fields.TITLE] = note.title
        result[Fields.NOTE_TEXT] = note.noteText
        result[Fields.DATE] = note.date
        return result
    }

    object Fields {
        const val TITLE = "title"
        const val NOTE_TEXT = "noteText"
        const val DATE = "date"
    }
}