package com.socia1ca3t.timepillbackup.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "timepill")
public class TimepillConfig {


    private List<String> roleAdmins;

    private String fileBasePath;

    private String notebookBackgroudImg;

    private String indexURL;

    private String userHomeURL;

    private String notebookURL;

    private String apiUserURL;

    private String apiTopicNotebookURL;

    private String apiNotebookListURL;

    private String apiDiaryURL;


    public List<String> getRoleAdmins() {
        return roleAdmins;
    }

    public void setRoleAdmins(List<String> roleAdmins) {
        this.roleAdmins = roleAdmins;
    }

    public String getFileBasePath() {
        return fileBasePath;
    }

    public void setFileBasePath(String fileBasePath) {
        this.fileBasePath = fileBasePath;
    }

    public String getIndexURL() {
        return indexURL;
    }

    public void setIndexURL(String indexURL) {
        this.indexURL = indexURL;
    }

    public String getNotebookBackgroudImg() {
        return notebookBackgroudImg;
    }

    public void setNotebookBackgroudImg(String notebookBackgroudImg) {
        this.notebookBackgroudImg = notebookBackgroudImg;
    }

    public String getUserHomeURL() {
        return userHomeURL;
    }

    public void setUserHomeURL(String userHomeURL) {
        this.userHomeURL = userHomeURL;
    }

    public String getNotebookURL() {
        return notebookURL;
    }

    public void setNotebookURL(String notebookURL) {
        this.notebookURL = notebookURL;
    }

    public String getApiUserURL() {
        return apiUserURL;
    }

    public void setApiUserURL(String apiUserURL) {
        this.apiUserURL = apiUserURL;
    }

    public String getApiTopicNotebookURL() {
        return apiTopicNotebookURL;
    }

    public void setApiTopicNotebookURL(String apiTopicNotebookURL) {
        this.apiTopicNotebookURL = apiTopicNotebookURL;
    }

    public String getApiNotebookListURL() {
        return apiNotebookListURL;
    }

    public void setApiNotebookListURL(String apiNotebookListURL) {
        this.apiNotebookListURL = apiNotebookListURL;
    }

    public String getApiDiaryURL() {
        return apiDiaryURL;
    }

    public void setApiDiaryURL(String apiDiaryURL) {
        this.apiDiaryURL = apiDiaryURL;
    }

}
