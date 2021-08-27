package com.robivan.simplenote

interface NoteSourceResponse {
    // Метод initialized() будет вызываться, когда данные проинициализируются и будут готовы.
    fun initialized(NotesData: NoteSource)
}