package com.robivan.simplenote;

import java.util.HashMap;
import java.util.Map;

public class NoteMapping {

    public static class Fields {

        public final static String TITLE = "title";
        public final static String NOTE_TEXT = "noteText";
        public final static String DATE = "date";
    }

    public static NoteEntity toNoteData(String id, Map<String, Object> doc) {
        NoteEntity answer = new NoteEntity(
                (String) doc.get(Fields.TITLE),
                (String) doc.get(Fields.NOTE_TEXT),
                (long) doc.get(Fields.DATE));
        answer.setId(id);
        return answer;
    }

    public static Map<String, Object> toDocument(NoteEntity note) {
        Map<String, Object> result = new HashMap<>();
        result.put(Fields.TITLE, note.getTitle());
        result.put(Fields.NOTE_TEXT, note.getNoteText());
        result.put(Fields.DATE, note.getDate());
        return result;
    }
}
