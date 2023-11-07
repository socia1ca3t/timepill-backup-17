package com.socia1ca3t.timepillbackup.pojo.dto;

import java.util.List;


public record ImagesDownloadData(boolean userIcon, List<NoteBook> hasCoverNotebooks, List<Diary> imgDiaries) {
}
