package com.example.feign;

import com.example.entity.ResultEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "ACCOST")
public interface AccostFeign {
    @PutMapping(value = "/accost/clockIn", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResultEntity clockIn(@RequestParam Integer locId, @RequestParam Integer status, @RequestPart MultipartFile file);
}
