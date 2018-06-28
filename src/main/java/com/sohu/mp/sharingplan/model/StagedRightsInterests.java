package com.sohu.mp.sharingplan.model;

import lombok.Data;

import java.util.Date;

@Data
public class StagedRightsInterests {
    private int id;
    private String name;
    private String code;
    private String title;
    private String url;
    private String content;
    private Date startTime;
    private Integer category;
    private Integer status;
    private Date createdTime;
    private Date modifiedTime;
}
