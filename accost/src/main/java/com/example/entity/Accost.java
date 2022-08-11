package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@TableName(value = "tb_accost")
@Getter
@Setter
public class Accost {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer locId;
    private Integer status;
    @TableField(exist = false)
    private MultipartFile file;
    private byte[] photo;
    private Timestamp createTime;
}
