package com.iyuba.ieltslistening.pojo;

// 选择试卷后的那一页
public class Sections {
    private long titleNum;
    private String titleNum1;
    private String partType;
    private String titleName;
    private String sound;

    public Sections(long titleNum, String titleNum1, String partType, String titleName, String sound) {
        this.titleNum = titleNum;
        this.titleNum1 = titleNum1;
        this.partType = partType;
        this.titleName = titleName;
        this.sound = sound;
    }

    public long getTitleNum() {
        return titleNum;
    }

    public void setTitleNum(long titleNum) {
        this.titleNum = titleNum;
    }

    public String getTitleNum1() {
        return titleNum1;
    }

    public void setTitleNum1(String titleNum1) {
        this.titleNum1 = titleNum1;
    }

    public String getPartType() {
        return partType;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    @Override
    public String toString() {
        return "Sections{" +
                "titleNum='" + titleNum + '\'' +
                ", titleNum1='" + titleNum1 + '\'' +
                ", partType='" + partType + '\'' +
                ", titleName='" + titleName + '\'' +
                ", sound='" + sound + '\'' +
                '}';
    }
}
