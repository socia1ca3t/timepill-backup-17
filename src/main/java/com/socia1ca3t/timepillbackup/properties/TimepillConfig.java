package com.socia1ca3t.timepillbackup.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;



@ConfigurationProperties(prefix = "timepill")
public record TimepillConfig (

        List<String> roleAdmins,
        String notebookBackgroudImg,
        String indexURL,
        String userHomeURL,
        String notebookURL,
        String apiUserURL,
        String apiTopicNotebookURL,
        String apiNotebookListURL,
        String apiDiaryURL,
        int httpCacheMinutes
) {

    @ConstructorBinding
    public TimepillConfig(List<String> roleAdmins, String notebookBackgroudImg, String indexURL, String userHomeURL, String notebookURL, String apiUserURL, String apiTopicNotebookURL, String apiNotebookListURL, String apiDiaryURL, int httpCacheMinutes) {
        this.roleAdmins = roleAdmins;
        this.notebookBackgroudImg = notebookBackgroudImg;
        this.indexURL = indexURL;
        this.userHomeURL = userHomeURL;
        this.notebookURL = notebookURL;
        this.apiUserURL = apiUserURL;
        this.apiTopicNotebookURL = apiTopicNotebookURL;
        this.apiNotebookListURL = apiNotebookListURL;
        this.apiDiaryURL = apiDiaryURL;
        this.httpCacheMinutes = httpCacheMinutes;
    }
}
