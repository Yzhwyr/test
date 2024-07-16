package org.example;

public class Role {
    private int level;
    private String code;
    private String title;
    private String parentCode;

    public Role(int level, String code, String title) {
        this.level = level;
        this.code = code;
        this.title = title;
        this.parentCode = "";
    }

    public int getLevel() {
        return level;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
}