package com.socia1ca3t.timepillbackup.core.download;


import com.socia1ca3t.timepillbackup.util.BackupUtil;
import com.socia1ca3t.timepillbackup.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * 图片下载器
 */
public class ImgRealDownloader {

    private static final Logger logger = LoggerFactory.getLogger(ImgRealDownloader.class);

    private static final RestTemplate restTemplate = SpringContextUtil.getBean(RestTemplate.class);


    /**
     * 将日记的图片下载到本地
     *
     * @param abPath 图片下载的路径
     * @param url    图片URL
     * @return 文件名
     */
    public static String downloadImg(String abPath, String url) {


        try {

            url = queryParamFilter(url);
            final String fileName = getFileName(url);
            final String targetFileURL = abPath + fileName;

            if (new File(targetFileURL).exists()) {

                logger.info("图片已经存在：" + targetFileURL);
                return fileName;
            }

            Thread.sleep(1000); // 请求频率过快会触发CDN服务器的CC防护，导致几分钟内都无法下载图片

            ResponseEntity<byte[]> entity = restTemplate.getForEntity(url, byte[].class);

            if (entity.getStatusCode() != HttpStatus.OK || entity.getBody() == null) {

                logger.info("图片下载异常，HTTP响应码{}，响应体{}", entity.getStatusCode(), Arrays.toString(entity.getBody()));
                throw new HttpServerErrorException(entity.getStatusCode());
            }

            BackupUtil.copyFile(entity.getBody(), new File(targetFileURL));
            logger.info("图片下载成功：" + targetFileURL);

            return fileName;

        } catch (Exception e) {
            logger.error("下载图片失败", e);
            throw new RuntimeException(e);
        }

    }

    /**
     * 将用户的头像下载到本地
     */
    public static String downloadIcon(String abPath, String url) {

        return downloadImg(abPath, url);
    }

    /**
     * 将日记封面下载到本地
     */
    public static String downloadCover(String abPath, String url) {

        return downloadImg(abPath, url);
    }

    /**
     * 如果为日记图片，则需要过滤!large后缀
     */
    private static String getFileName(String url) {

        try {
            return new File(new URL(url).getPath()).getName().replace("!large", "");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 得到没有查询字符串的URL路径，
     */
    public static String queryParamFilter(String url) {

        URL correcetURL;
        try {
            correcetURL = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL地址不合法");
        }

        int index = correcetURL.toString().indexOf('?');
        if (index > -1) {
            return correcetURL.toString().substring(0, index);
        }

        return correcetURL.toString();
    }

}
