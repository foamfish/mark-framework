package com.mark.framework.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Mark
 *
 */
public final class MyJsonUtil {
	private static Logger logger = LoggerFactory.getLogger(MyJsonUtil.class);

	/**
	 * 返回json封装结果
	 * @param model 模型bean
	 * @param msg 消息
	 * @return JSONObject JSON结果
	 */
	public static JSONObject success(Object model, String msg) {
		JSONObject result = new JSONObject();
		result.put("code", 0);
		result.put("data", JSON.toJSON(model));
		result.put("msg", msg);
		return result;
	}

	/**
	 * 返回json封装结果
	 * @param msg 消息
	 * @return JSONObject JSON结果
	 */
	public static JSONObject success(String msg) {
		return success(null, msg);
	}

	/**
	 * 返回json封装结果
	 * @param model 模型bean
	 * @return JSONObject JSON结果
	 */
	public static JSONObject success(Object model) {
		return success(model, "success");
	}

	/**
	 * 返回json封装结果
	 * @param msg 消息
	 * @return JSONObject JSON结果
	 */
	public static JSONObject error(int code, String msg) {
		JSONObject result = new JSONObject();
		result.put("code", code);
		result.put("msg", msg);
		logger.error(result.toJSONString());
		return result;
	}
	/**
	 * 返回json封装结果
	 * @param msg 消息
	 * @return JSONObject JSON结果
	 */
	public static JSONObject error(String msg) {
		return error(-1, msg);
	}
}
