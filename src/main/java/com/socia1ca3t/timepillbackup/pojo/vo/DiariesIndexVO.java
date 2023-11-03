package com.socia1ca3t.timepillbackup.pojo.vo;

import com.socia1ca3t.timepillbackup.pojo.dto.Diary;
import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;

import java.util.List;

public class DiariesIndexVO {


    private List<Diary> diaries;

    private DiariesStatisticData statisticData;

    private NoteBook noteBook;


    public DiariesIndexVO(NoteBook noteBook, List<Diary> diaries, DiariesStatisticData statisticData) {

        this.noteBook = noteBook;
        this.diaries = diaries;
        this.statisticData = statisticData;
    }

    public List<Diary> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<Diary> diaries) {
        this.diaries = diaries;
    }

    public DiariesStatisticData getStatisticData() {
        return statisticData;
    }

    public void setStatisticData(DiariesStatisticData statisticData) {
        this.statisticData = statisticData;
    }

    public NoteBook getNoteBook() {
        return noteBook;
    }

    public void setNoteBook(NoteBook noteBook) {
        this.noteBook = noteBook;
    }
}
