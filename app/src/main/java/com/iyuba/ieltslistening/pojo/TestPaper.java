package com.iyuba.ieltslistening.pojo;

// 试卷类
public class TestPaper {

    /**
     * 除了name和testTime字段，其他均
     * 不需要用作展示，存下可能以后有用。
     */
    private int downloadState;
    private int id;
    private boolean isDownload;
    private boolean isFree;
    private boolean isVip;
    private String name;  // 列表展示的名称
    private String productID;  // 这里存为String是因为接口返回的这个字段是空字符串("")，我也不知道干啥的，先存着
    private int progress;
    private String testTime;  // 哪一年的试卷
    private int version;

    public TestPaper(int downloadState, int id, boolean isDownload, boolean isFree, boolean isVip, String name, String productID, int progress, String testTime, int version) {
        this.downloadState = downloadState;
        this.id = id;
        this.isDownload = isDownload;
        this.isFree = isFree;
        this.isVip = isVip;
        this.name = name;
        this.productID = productID;
        this.progress = progress;
        this.testTime = testTime;
        this.version = version;
    }

    public TestPaper(String name, String testTime) {
        this.name = name;
        this.testTime = testTime;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "TestPaper{" +
                "downloadState=" + downloadState +
                ", id=" + id +
                ", isDownload=" + isDownload +
                ", isFree=" + isFree +
                ", isVip=" + isVip +
                ", name='" + name + '\'' +
                ", productID=" + productID +
                ", progress=" + progress +
                ", testTime='" + testTime + '\'' +
                ", version=" + version +
                '}';
    }
}
