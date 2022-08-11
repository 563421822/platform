package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

@Getter
@Setter
@TableName(value = "tb_location")
public class Location implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String type;
    private String info;
    private Integer status;
    @TableField(value = "fEa")
    @JsonProperty(value = "fEa")
    private String fEa;
    @TableField(exist = false)
    private LinkedHashMap<String, Double> position;
    private double q;
    private double r;
    private double lng;
    private double lat;
    private String message;
    @JsonAlias(value = "location_type")
    private String locationType;
    private String accuracy;
    private Boolean isConverted;
    @TableField(exist = false)
    private LinkedHashMap<String, Object> addressComponent;
    private String citycode;
    private String adcode;
    @TableField(exist = false)
    private ArrayList<LinkedHashMap<String, Object>> businessAreas;
    private String neighborhoodType;
    private String neighborhood;
    private String building;
    private String buildingType;
    private String street;
    private String streetNumber;
    private String country;
    private String province;
    private String city;
    private String district;
    private String towncode;
    private String township;
    private String formattedAddress;
    /*    @TableField(jdbcType = JdbcType.ARRAY)
        private ArrayList<String> roads;
        @TableField(jdbcType = JdbcType.ARRAY)
        private ArrayList<String> crosses;
        @TableField(jdbcType = JdbcType.ARRAY)
        private ArrayList<String> pois;*/
    private Date createTime;
    private Integer log;
}