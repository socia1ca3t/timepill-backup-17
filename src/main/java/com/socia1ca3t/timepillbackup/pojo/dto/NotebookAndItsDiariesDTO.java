package com.socia1ca3t.timepillbackup.pojo.dto;

import java.util.List;

public class NotebookAndItsDiariesDTO {


    private NoteBook noteBook;

    private List<Diary> diarieList;


    public NotebookAndItsDiariesDTO() {
    }

    public NotebookAndItsDiariesDTO(NoteBook noteBook, List<Diary> diarieList) {
        this.noteBook = noteBook;
        this.diarieList = diarieList;
    }

    public NoteBook getNoteBook() {
        return noteBook;
    }

    public void setNoteBook(NoteBook noteBook) {
        this.noteBook = noteBook;
    }

    public List<Diary> getDiarieList() {
        return diarieList;
    }

    public void setDiarieList(List<Diary> diarieList) {
        this.diarieList = diarieList;
    }
}
