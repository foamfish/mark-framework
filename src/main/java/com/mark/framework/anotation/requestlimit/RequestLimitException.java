package com.mark.framework.anotation.requestlimit;


import com.mark.framework.exception.base.MyBaseException;

public class RequestLimitException extends MyBaseException {
    private static final long serialVersionUID = 1L;

    public RequestLimitException() {
        super("HTTP请求超出设定的限制");
    }

    public RequestLimitException(String message) {
        super(message);
    }

}