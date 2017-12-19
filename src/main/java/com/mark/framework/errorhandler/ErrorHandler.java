package com.mark.framework.errorhandler;

import com.mark.framework.exception.base.MyBaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常类统一处理
 *
 * @author mark
 * @date 2017-10-22
 */
@RestController
public class ErrorHandler {

    @RequestMapping("/framework/error")
    public Object errorHandler(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> data = new HashMap<>();
        int status = (int)request.getAttribute("javax.servlet.error.status_code");

        data.put("code", status);

        // 404
        if (HttpStatus.NOT_FOUND.value() == status) {
            notFoundHandler(request, data);

            // 400
        } else if (HttpStatus.BAD_REQUEST.value() == status) {
            badRequestHandler(request, data);

            // 其他
        } else {
            Throwable throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
            if (throwable != null) {
                if (NestedServletException.class.equals(throwable.getClass())) {
                    throwable = throwable.getCause();
                }
                if (throwable != null) {
                    if (throwable.getClass().isAssignableFrom(MyBaseException.class)) {
                        businessErrorHandler(data, throwable);
                    } else {
                        frameworkErrorHandler(data, throwable);
                    }
                }
            }
        }
        return data;
    }

    /**
     *
     * 400异常
     * @param request servletRequest
     * @param data 响应数据
     */
    private void badRequestHandler(HttpServletRequest request, Map<String, Object> data) {
        Object uri = request.getAttribute("javax.servlet.error.request_uri");
        Object servletErrorMsg = request.getAttribute("javax.servlet.error.message");
        data.put("msg", String.format("请求的资源[%s]，参数非法[%s]！", uri, servletErrorMsg));
    }

    /**
     *
     * 404异常
     * @param request servletRequest
     * @param data 响应数据
     */
    private void notFoundHandler(HttpServletRequest request, Map<String, Object> data) {
        Object uri = request.getAttribute("javax.servlet.error.request_uri");
        data.put("msg", String.format("请求的资源[%s]不存在！", uri));
    }

    /**
     * 系统异常
     * @param data 响应数据
     * @param cause 异常栈
     */
    private void frameworkErrorHandler(Map<String, Object> data, Throwable cause) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        cause.printStackTrace(pw);
        data.put("msg", sw.toString());
    }

    /**
     * 业务异常
     * @param data 响应数据
     * @param cause 异常栈
     */
    private void businessErrorHandler(Map<String, Object> data, Throwable cause) {
        Object msg = cause.getMessage();
        data.put("msg", msg);
    }
}
