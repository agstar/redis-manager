package com.redis.manager.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result<T> {

    private boolean flag;
    private Integer code;
    private String message;
    private T data;

    private Result() {
    }

    private Result(boolean flag, Integer code) {
        this.flag = flag;
        this.code = code;
    }


    private Result(boolean flag, Integer code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

    private Result(boolean flag, Integer code, String message, T data) {
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private Result(boolean flag, Integer code, T data) {
        this.flag = flag;
        this.code = code;
        this.data = data;
    }


    public static <T> Result<T> success() {
        return new Result<>(true, StatusCode.OK);
    }

    public static <T> Result<T> successMsg(String message) {
        return new Result<>(true, StatusCode.OK, message);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, StatusCode.OK, data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, StatusCode.OK, message, data);
    }
    public static <T> Result<T> error() {
        return new Result<>(false, StatusCode.ERROR);
    }
    public static <T> Result<T> errorCode(Integer code) {
        return new Result<>(false, code);
    }

    public static <T> Result<T> errorMsg(String message) {
        return new Result<>(false, StatusCode.ERROR, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(false, code, message);
    }
}
