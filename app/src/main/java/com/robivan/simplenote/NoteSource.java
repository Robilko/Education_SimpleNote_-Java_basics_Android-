package com.robivan.simplenote;

public interface NoteSource {
    NoteSource init(NoteSourceResponse noteSourceResponse);
    NoteEntity getNoteData(int position);
    int size();
    void deleteNoteData(int position);
    void updateNoteData(NoteEntity noteData, int position);
    void addNoteData(NoteEntity noteData);
    void clearNoteData();
}

