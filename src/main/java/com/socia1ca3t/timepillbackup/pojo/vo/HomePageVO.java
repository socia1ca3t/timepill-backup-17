package com.socia1ca3t.timepillbackup.pojo.vo;

import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 用户个人主页
 */
public class HomePageVO implements Serializable {

    private UserInfo userInfo;

    private NotebookStatisticDataVO userData;

    private List<NoteBook> myNotebooks;

    private List<NoteBook> joinNotebooks;

    public HomePageVO() {
    }

    public HomePageVO(UserInfo userInfo, NotebookStatisticDataVO userData, List<NoteBook> noteBooks) {
        this.userInfo = userInfo;
        this.userData = userData;
        this.myNotebooks = noteBooks;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public NotebookStatisticDataVO getUserData() {
        return userData;
    }

    public void setUserData(NotebookStatisticDataVO userData) {
        this.userData = userData;
    }

    public List<NoteBook> getMyNotebooks() {
        return myNotebooks;
    }

    public void setMyNotebooks(List<NoteBook> myNotebooks) {
        this.myNotebooks = myNotebooks;
    }

    public List<NoteBook> getJoinNotebooks() {
        return joinNotebooks;
    }

    public void setJoinNotebooks(List<NoteBook> joinNotebooks) {
        this.joinNotebooks = joinNotebooks;
    }
}
