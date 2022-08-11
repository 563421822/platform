package com.example.service.impl;

import com.example.entity.Location;
import com.example.entity.RequestLog;
import com.example.entity.ResultEntity;
import com.example.feign.AccostFeign;
import com.example.mapper.LocationMapper;
import com.example.mapper.RequestLogMapper;
import com.example.service.ILocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
@CommonsLog
public class LocationServiceImpl implements ILocationService {
    @Autowired
    RequestLogMapper logMapper;
    @Autowired
    LocationMapper locationMapper;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    RestHighLevelClient highLevelClient;
    @Autowired
    AccostFeign accostFeign;

    @Override
    public String dispatch(HttpServletRequest request, Model model) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        RequestLog log = (RequestLog) getIns(request);
        logMapper.insert(log);
        model.addAttribute("logId", log.getId());
        return "page";
    }

    @Override
    public ResultEntity checkLoc(Location location, Integer logId) throws IllegalAccessException, IOException {
        extracted(location);
        location.setLog(logId);
        locationMapper.insert(location);
        GlobalCoordinates start = new GlobalCoordinates(location.getLat(), location.getLng());
        GlobalCoordinates end = new GlobalCoordinates(22.523741, 114.057229);
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84, start, end);
        if (geoCurve.getEllipsoidalDistance() < (location.getStatus() != null ? 325 : 650))
            throw new IllegalStateException("请不要在家附近打卡");
//        Elasticsearch中查询地址范围
        SearchRequest searchRequest = getRequest(location);
        SearchHit hit = highLevelClient.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits()[0];
        Map<String, Object> map = new HashMap<>();
        map.put("name", hit.getSourceAsMap().get("name"));
        map.put("distance", hit.getSortValues()[0]);
        map.put("locId", location.getId());
        return ResultEntity.success(map);
    }

    private void extracted(Location location) throws IllegalAccessException {
        for (Field field : location.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (location.getPosition() != null)
                for (Map.Entry<String, Double> entry : location.getPosition().entrySet()) {
                    if (entry.getKey().toLowerCase().equals(field.getName())) {
                        field.setDouble(location, entry.getValue());
                        break;
                    }
                }
            if (location.getAddressComponent() != null)
                for (Map.Entry<String, Object> entry : location.getAddressComponent().entrySet()) {
                    if (entry.getKey().equals(field.getName())) {
                        field.set(location, entry.getValue());
                        break;
                    }
                }
        }
    }

    private SearchRequest getRequest(Location location) {
        SearchRequest searchRequest = new SearchRequest("maps");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());
        GeoDistanceSortBuilder geoDistanceSortBuilder = new GeoDistanceSortBuilder("location", location.getLat(), location.getLng());
        geoDistanceSortBuilder.order(SortOrder.ASC).unit(DistanceUnit.KILOMETERS);
        builder.sort(geoDistanceSortBuilder).size(1);
        searchRequest.source(builder);
        return searchRequest;
    }

    private Object getIns(HttpServletRequest request) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object instance = RequestLog.class.getConstructor().newInstance();
        for (Field field : RequestLog.class.getDeclaredFields()) {
            field.setAccessible(true);
            switch (field.getName()) {
                case "remoteAddr":
                    extracted(request.getRemoteAddr(), instance, field);
                    continue;
                case "remotePort":
                    extracted(request.getRemotePort(), instance, field);
                    continue;
                case "remoteUser":
                    extracted(request.getRemoteUser(), instance, field);
                    continue;
                case "remoteHost":
                    extracted(request.getRemoteHost(), instance, field);
                    continue;
            }
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String ele = headerNames.nextElement();
                if (ele.contains("-")) {
                    String[] split = ele.split("-");
                    StringBuilder curr = new StringBuilder(split[0]);
                    for (int i = 1; i < split.length; i++) {
                        String s = split[i];
                        curr.append(s.substring(0, 1).toUpperCase().concat(s.substring(1)));
                    }
                    if (curr.toString().equals(field.getName())) {
                        extracted(request.getHeader(ele), instance, field);
                        break;
                    }
                } else if (ele.equals(field.getName())) {
                    extracted(request.getHeader(ele), instance, field);
                    break;
                }
            }
        }
        return instance;
    }

    private void extracted(Object value, Object instance, Field field) throws IllegalAccessException {
        field.set(instance, value);
    }

    public void sendError(Map<String, Object> map) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
        objectOutputStream.writeObject(map);
        rabbitTemplate.convertAndSend("emailExchange", "loc.send.ema", stream.toByteArray());
    }

    @Override
    public ResultEntity clockIn(Integer locId, Integer status, MultipartFile file) throws IllegalAccessException {
        return accostFeign.clockIn(locId, status, file);
    }
}
