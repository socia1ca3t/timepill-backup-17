package com.socia1ca3t.timepillbackup.pojo.vo;

/**
 * 用户数据统计
 */
public class NotebookStatisticDataVO {

    // 胶囊年龄
    private int age;

    // 日记本数量
    private int notebookNum;

    private int expiredNum;

    private int privateNum;

//    // 日记数量
//    private int diaryNum;
//
//    // 发布图片数量
//    private int imageNum;

    public NotebookStatisticDataVO(int age, int notebookNum, int expiredNum, int privateNum) {
        this.age = age;
        this.notebookNum = notebookNum;
        this.expiredNum = expiredNum;
        this.privateNum = privateNum;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getNotebookNum() {
        return notebookNum;
    }

    public void setNotebookNum(int notebookNum) {
        this.notebookNum = notebookNum;
    }

    public int getExpiredNum() {
        return expiredNum;
    }

    public void setExpiredNum(int expiredNum) {
        this.expiredNum = expiredNum;
    }

    public int getPrivateNum() {
        return privateNum;
    }

    public void setPrivateNum(int privateNum) {
        this.privateNum = privateNum;
    }
}
