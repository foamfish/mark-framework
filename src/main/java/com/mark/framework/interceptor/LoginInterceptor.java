package com.mark.framework.interceptor;

import com.mark.framework.aop.AuthenticatedUser;
import com.mark.framework.aop.ThreadLocalHolder;
import com.mark.framework.util.MyJsonUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 资源访问拦截器
 *
 * @author mark
 * @date 2017-10-22
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static String islogon = "/user/islogon";
    /**
     * 无需登录就可以访问的URI
     */
    private static Set<String> skipComponents = new HashSet<>();

    static {
        skipComponents.addAll(Arrays.asList(
                "/user"
        ));
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // uri
        String requestUri = request.getRequestURI();
        // 请求的上下文
        String contextPath = request.getContextPath();
        // 实际匹配的path
        String path = requestUri.substring(contextPath.length());

        System.out.println("filter= " + requestUri);

        HttpSession session = request.getSession(false);

        // 检查是否登录的api
        if (path.startsWith(islogon)) {
            OutputStream ps = response.getOutputStream();
            if (session != null) {
                ps.write(MyJsonUtil.success("已登录").toJSONString().getBytes("UTF-8"));
            } else {
                //这句话的意思，使得放入流的数据是utf8格式
                ps.write(MyJsonUtil.error(-1, "未登录").toJSONString().getBytes("UTF-8"));
            }
            return false;
        }

        if (session == null || session.getAttribute("user") == null) {
            if (checkSkipUris(path)) {
                return true;
            }
            // 跳转到登录页

            OutputStream ps = response.getOutputStream();
            //这句话的意思，使得放入流的数据是utf8格式
            ps.write(MyJsonUtil.error(-1, "未登录，禁止访问").toJSONString().getBytes("UTF-8"));
            return false;

        } else {
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");
            ThreadLocalHolder.set(user);
        }
        return true;
    }

    /**
     * 验证跳过的uri
     * @param uri
     * @return
     */
    private static boolean checkSkipUris(String uri) {
        for (String url : skipComponents) {
            if (uri.startsWith(url)) {
                return true;
            }
        }
        return false;
    }
}
