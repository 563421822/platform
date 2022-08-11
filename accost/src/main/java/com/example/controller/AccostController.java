package com.example.controller;

import com.example.entity.Accost;
import com.example.entity.ResultEntity;
import com.example.service.IAccostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class AccostController {
    @Autowired
    IAccostService accostService;

    @PutMapping(value = "clockIn", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    ResultEntity clockIn(Accost accost) throws IOException {
        return accostService.insertOne(accost);
    }
}
