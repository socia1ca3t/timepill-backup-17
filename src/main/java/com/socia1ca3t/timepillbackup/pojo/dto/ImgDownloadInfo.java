package com.socia1ca3t.timepillbackup.pojo.dto;

import com.socia1ca3t.timepillbackup.core.ImgDownloader;

public record ImgDownloadInfo(String url, String absolutePath, String fileName, ImgDownloader.ImgType imgType) {
}
