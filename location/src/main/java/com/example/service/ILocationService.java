package com.example.service;

import com.example.entity.Location;
import com.example.entity.ResultEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface ILocationService {
    String dispatch(HttpServletRequest request, Model model) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException;

    ResultEntity checkLoc(Location location, Integer logId) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException;

    void sendError(Map<String, Object> map) throws IOException;

    ResultEntity clockIn(Integer locId, Integer status, MultipartFile file) throws IllegalAccessException;
}
