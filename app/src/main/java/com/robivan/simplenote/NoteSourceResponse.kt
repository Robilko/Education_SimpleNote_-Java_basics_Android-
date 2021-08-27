package com.robivan.simplenote;

public interface NoteSourceResponse {
    // Метод initialized() будет вызываться, когда данные проинициализируются и будут готовы.
    void initialized(NoteSource NotesData);
}
