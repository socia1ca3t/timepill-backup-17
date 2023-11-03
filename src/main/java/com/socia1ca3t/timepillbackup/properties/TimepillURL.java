package com.socia1ca3t.timepillbackup.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("url")
@ConfigurationProperties(prefix = "timepill.url")
public class TimepillURL {

    private String index;

    private String userHome;

    private String notebook;

    private String apiUser;

    private String apiTopicNotebook;

    private String apiNotebookList;

    private String apiDiary;


    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getUserHome() {
        return userHome;
    }

    public void setUserHome(String userHome) {
        this.userHome = userHome;
    }

    public String getNotebook() {
        return notebook;
    }

    public void setNotebook(String notebook) {
        this.notebook = notebook;
    }

    public String getApiUser() {
        return apiUser;
    }

    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }

    public String getApiTopicNotebook() {
        return apiTopicNotebook;
    }

    public void setApiTopicNotebook(String apiTopicNotebook) {
        this.apiTopicNotebook = apiTopicNotebook;
    }

    public String getApiNotebookList() {
        return apiNotebookList;
    }

    public void setApiNotebookList(String apiNotebookList) {
        this.apiNotebookList = apiNotebookList;
    }

    public String getApiDiary() {
        return apiDiary;
    }

    public void setApiDiary(String apiDiary) {
        this.apiDiary = apiDiary;
    }
}
