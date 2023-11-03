package com.socia1ca3t.timepillbackup.pojo.vo;

import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;

import java.util.List;

public class NotebooksOfOneYear {

    private int year;

    private List<NoteBook> noteBooks;

    public NotebooksOfOneYear(int year, List<NoteBook> noteBooks) {
        this.year = year;
        this.noteBooks = noteBooks;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<NoteBook> getNoteBooks() {
        return noteBooks;
    }

    public void setNoteBooks(List<NoteBook> noteBooks) {
        this.noteBooks = noteBooks;
    }
}
