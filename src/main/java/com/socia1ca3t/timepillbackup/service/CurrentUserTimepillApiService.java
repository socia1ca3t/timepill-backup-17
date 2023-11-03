package com.socia1ca3t.timepillbackup.service;


import com.socia1ca3t.timepillbackup.pojo.dto.DiariesDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.Diary;
import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.properties.TimepillConfig;
import com.socia1ca3t.timepillbackup.util.JacksonUtil;
import com.socia1ca3t.timepillbackup.util.SecurityContextUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 请求胶囊开放接口
 */
@Service
@Retryable(noRetryFor = HttpClientErrorException.class,
        retryFor = {ResourceAccessException.class, HttpServerErrorException.class},
        maxAttempts = 5,
        backoff = @Backoff(delay = 1000, multiplier = 2))
public class CurrentUserTimepillApiService {

    private static final Log logger = LogFactory.getLog(CurrentUserTimepillApiService.class);

    @Autowired
    private TimepillConfig url;


    /**
     * 查询所有日记本
     *
     * @param email
     * @return
     */
    @Cacheable(value = "notebook", key = "#email")
    public List<NoteBook> getNotebookList(String email) {

        RestTemplate rest = SecurityContextUtil.getCurrentUserBasicAuthRestTemplate();
        ResponseEntity<String> entity = rest.getForEntity(url.getApiNotebookListURL(), String.class);

        if (logger.isDebugEnabled()) {
            logger.debug("getNotebookList 响应日志：" + entity.getBody());
        }

        List<NoteBook> list;
        if (StringUtils.isEmpty(entity.getBody())) {
            list = new ArrayList<>();
            return list;
        }

        list = JacksonUtil.jsonToArrayList(entity.getBody(), NoteBook.class);

        return list;
    }

    /**
     * 查询用户指定单本日记本的所有日记
     */
    @Cacheable(value = "diary", key = "#notebookId")
    public List<Diary> getDiaryList(int notebookId) {

        final RestTemplate rest = SecurityContextUtil.getCurrentUserBasicAuthRestTemplate();
        ResponseEntity<String> entity = rest.getForEntity(url.getApiDiaryURL(), String.class, notebookId, 1);

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

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                entity = rest.getForEntity(url.getApiDiaryURL(), String.class, notebookId, i);

                if (!StringUtils.isEmpty(entity.getBody())) {
                    diariesDTO = JacksonUtil.jsonToBean(entity.getBody(), DiariesDTO.class);
                    allList.addAll(diariesDTO.getItems());
                }
            }
        }

        return allList;
    }

}
