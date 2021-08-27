package com.robivan.simplenote

fun interface NoteSourceResponse {
    // Метод initialized() будет вызываться, когда данные проинициализируются и будут готовы.
    fun initialized(NotesData: NoteSource)
}