package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;

import java.util.Date;

@TableName(value = "tb_request_log")
@Getter
public class RequestLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String host;
    private String userAgent;
    private String accept;
    private String acceptLanguage;
    private String dnt;
    private String connection;
    private String upgradeInsecureRequests;
    private String secFetchDest;
    private String secFetchMode;
    private String secFetchSite;
    private String secFetchUser;
    private String remoteAddr;
    private Integer remotePort;
    private String remoteUser;
    private String remoteHost;
    private Date createTime;
}