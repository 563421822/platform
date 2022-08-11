package com.example.controller;

import com.example.entity.Location;
import com.example.entity.ResultEntity;
import com.example.service.ILocationService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Controller
@CommonsLog
public class LocationController {
    final ILocationService locationService;

    public LocationController(ILocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    String dispatch(HttpServletRequest request, Model model) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        return locationService.dispatch(request, model);
    }

    /**
     * elasticsearch中检查可用位置信息
     *
     * @param location
     * @param logId
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    @PostMapping(value = "location/checkLoc")
    @ResponseBody
    ResultEntity checkLoc(@RequestBody Location location, Integer logId) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, IOException {
        return locationService.checkLoc(location, logId);
    }

    /**
     * 开始打卡控制器
     *
     * @param locId
     * @param status
     * @param file
     * @return
     * @throws IllegalAccessException
     */
    @PostMapping(value = "location/clockIn", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    ResultEntity clockIn(Integer locId, Integer status, MultipartFile file) throws IllegalAccessException {
        return locationService.clockIn(locId, status, file);
    }

    /**
     * 前端获取定位错误提交后端发送邮件
     *
     * @param map
     * @throws IOException
     */
    @PostMapping(value = "location/errEmail")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    void errEmail(@RequestBody Map<String, Object> map) throws IOException {
        locationService.sendError(map);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    ResultEntity handle(Exception e) {
        log.error("Exception:", e);
        return ResultEntity.error(e.getMessage());
    }
}
