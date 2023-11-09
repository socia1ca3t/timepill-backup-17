package com.socia1ca3t.timepillbackup.core;

import com.socia1ca3t.timepillbackup.pojo.dto.Diary;
import com.socia1ca3t.timepillbackup.pojo.dto.ImgDownloadInfo;
import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;

import java.util.List;

public interface ImgPathSetter {


    ImgDownloadInfo userIcon(UserInfo userInfo);

    List<ImgDownloadInfo> notebooksCover(List<NoteBook> hasCoverNotebooks);

    List<ImgDownloadInfo> diaryImage(List<Diary> imgDiaryList);

}
