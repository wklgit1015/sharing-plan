package com.sohu.mp.sharingplan.enums;

/**
 * @author zhuchen
 */
public enum DataSourceTypeEnum {
    /**
     * 写库
     * 读库
     */
    WRITE("write"),
    READ("read");

    private String name;

    DataSourceTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
