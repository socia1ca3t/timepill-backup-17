package com.socia1ca3t.timepillbackup.service;


import com.socia1ca3t.timepillbackup.pojo.dto.DiariesDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.Diary;
import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.util.JacksonUtil;
import com.socia1ca3t.timepillbackup.util.SpringContextUtil;
import com.socia1ca3t.timepillbackup.util.TimepillUtil;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 请求胶囊开放接口
 */

public class CurrentUserTimepillApiService {

    private static final Log logger = LogFactory.getLog(CurrentUserTimepillApiService.class);

    private final CacheManager cacheManager = SpringContextUtil.getBean("httpApiCacheManager");
    private final String NOTEBOOK_URL;
    private final String DIARIES_URL;
    private final RestTemplate userBasicAuthRestTemplate;

    public CurrentUserTimepillApiService(@NotNull RestTemplate userBasicAuthRestTemplate) {

        this.userBasicAuthRestTemplate = userBasicAuthRestTemplate;
        NOTEBOOK_URL = TimepillUtil.getConfig().apiNotebookListURL();
        DIARIES_URL = TimepillUtil.getConfig().apiDiaryURL();
    }

    /**
     * 查询所有日记本
     */
    public List<NoteBook> getCachableNotebookList() {

        int key = userBasicAuthRestTemplate.hashCode();

        return cacheManager.getCache("getNotebookList")
                .get(key, this::getNotebookList);

    }

    public List<NoteBook> getNotebookList () {

        ResponseEntity<String> entity = userBasicAuthRestTemplate.getForEntity(NOTEBOOK_URL, String.class);

        if (logger.isDebugEnabled()) {
            logger.debug("getNotebookList 响应日志：" + entity.getBody());
        }

        return StringUtils.isEmpty(entity.getBody())
                ? new ArrayList<>()
                : JacksonUtil.jsonToArrayList(entity.getBody(), NoteBook.class);
    }


    public List<Diary> getCachableDiaryList(int notebookId) {

        int key = userBasicAuthRestTemplate.hashCode() + notebookId;

        return cacheManager.getCache("getDiaryList")
                .get(key, () -> getDiaryList(notebookId));
    }

    /**
     * 查询用户指定单本日记本的所有日记
     */
    public List<Diary> getDiaryList(int notebookId) {

        ResponseEntity<String> entity = userBasicAuthRestTemplate.getForEntity(DIARIES_URL, String.class, notebookId, 1);

        if (logger.isDebugEnabled()) {
            logger.debug("getDiaryList 响应日志：" + entity.getBody());
        }

        final List<Diary> allList = new ArrayList<>(); // 所有日记
        if (StringUtils.isEmpty(entity.getBody())) {
            return allList;
        }

        DiariesDTO diariesDTO = JacksonUtil.jsonToBean(entity.getBody(), DiariesDTO.class);

        int pageSize = diariesDTO.getPageSize(); // 每一页的日记数量
        int count = diariesDTO.getCount(); // 日记总数
        allList.addAll(diariesDTO.getItems());

        int pageTotalNum = (count - count % pageSize) / pageSize + 1; // 总页数

        if (pageTotalNum > 1) {
            for (int i = 2; i <= pageTotalNum; i++) {

                try {Thread.sleep(1000);} catch (InterruptedException ignored) {}

                entity = userBasicAuthRestTemplate.getForEntity(DIARIES_URL, String.class, notebookId, i);

                if (!StringUtils.isEmpty(entity.getBody())) {
                    diariesDTO = JacksonUtil.jsonToBean(entity.getBody(), DiariesDTO.class);
                    allList.addAll(diariesDTO.getItems());
                }
            }
        }

        return allList;
    }

}
