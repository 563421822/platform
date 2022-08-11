package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.Accost;
import com.example.entity.ResultEntity;
import com.example.mapper.AccostMapper;
import com.example.service.IAccostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AccostServiceImpl implements IAccostService {
    @Autowired
    AccostMapper accostMapper;

    @Override
    public Long selectCount() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        QueryWrapper<Accost> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("create_time", dateFormat.format(new Date()) + " 18:00:00");
        return accostMapper.selectCount(queryWrapper);
    }

    @Override
    public ResultEntity insertOne(Accost accost) throws IOException {
        byte[] bytes = accost.getFile().getInputStream().readAllBytes();
        accost.setPhoto(bytes);
        int result = accostMapper.insert(accost);
        if (result == 1) return ResultEntity.success();
        else return ResultEntity.error("插入失败");
    }
}
