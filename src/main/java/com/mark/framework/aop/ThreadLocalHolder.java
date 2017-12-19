package com.mark.framework.aop;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * login中 保持User
 * @author Mark
 *
 */
public final class ThreadLocalHolder {

	private static final ThreadLocal<AuthenticatedUser> LOGGED_IN_USERS = new ThreadLocal<>();

	private static final ThreadLocal<DateFormat> LOCAL_DATE_FORMAT = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

	private ThreadLocalHolder(){}

	/**
     * 获取登录的用户
     * @return 获取线程变量
     */
    public static AuthenticatedUser get(){
        return LOGGED_IN_USERS.get();
    }

    /**
     * 线程变量
     * @return 获取线程变量
     */
    public static DateFormat getDateFormat(){
        return LOCAL_DATE_FORMAT.get();
    }

	/**
	 *设置Login中的用户
	 * @param authenticatedUser 被认证过的用户
	 */
	public static void set(AuthenticatedUser authenticatedUser){
		LOGGED_IN_USERS.set(authenticatedUser);
	}

	/**
	 * 删除Login用户
	 */
	public static void remove(){
		LOGGED_IN_USERS.set(null);
	}
	
}
