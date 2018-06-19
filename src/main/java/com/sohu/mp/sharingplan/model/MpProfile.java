package com.sohu.mp.sharingplan.model;

import lombok.Data;

import java.util.Date;

@Data
public class MpProfile {
    private Long id;
    private String passport;
    private String userName;
    private String desc;
    private String avator;
    private String homePage;
    private Integer mediaType;
    private Integer status;
    private Integer fromWhere;
    private Integer auditType;
    private Integer mpChannelId;
    private Long cmsId;
    private String cmsName;
    private Integer level;
    private Integer localLevel;
    private Long topNewsId;
    private Date createTime;
    private Date updateTime;
    private Date modifyTime;
}
