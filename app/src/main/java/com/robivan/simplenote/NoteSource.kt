package com.robivan.simplenote

interface NoteSource {
    fun init(noteSourceResponse: NoteSourceResponse): NoteSource
    fun getNoteData(position: Int): NoteEntity?
    fun size(): Int
    fun deleteNoteData(position: Int)
    fun updateNoteData(noteData: NoteEntity?, position: Int)
    fun addNoteData(noteData: NoteEntity?)
    fun clearNoteData() //TODO реализовать очистку
}