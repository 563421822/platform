package com.example.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultEntity {
    private static final int SUCCESS = 200;
    private static final int ERROR = 500;
    private static final String NO_MSG = null;
    private int code;
    private String msg;
    private Object data;

    public static ResultEntity success() {
        return new ResultEntity(SUCCESS, NO_MSG, null);
    }

    public static ResultEntity success(Object data) {
        return new ResultEntity(SUCCESS, NO_MSG, data);
    }

    public static ResultEntity error(String msg) {
        return new ResultEntity(ERROR, msg, null);
    }


}
