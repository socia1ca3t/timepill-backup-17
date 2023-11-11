package com.socia1ca3t.timepillbackup.core;

import com.socia1ca3t.timepillbackup.pojo.dto.DiaryDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.ImgDownloadInfo;
import com.socia1ca3t.timepillbackup.pojo.dto.NotebookDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.UserDTO;

import java.util.List;

public interface ImgPathSetter {


    ImgDownloadInfo userIcon(UserDTO userInfo);

    List<ImgDownloadInfo> notebooksCover(List<NotebookDTO> hasCoverNotebooks);

    List<ImgDownloadInfo> diaryImage(List<DiaryDTO> imgDiaryList);

}
