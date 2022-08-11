package com.example.service;

import com.example.entity.Accost;
import com.example.entity.ResultEntity;

import java.io.IOException;

public interface IAccostService {
    Long selectCount();

    ResultEntity insertOne(Accost accost) throws IOException;
}
