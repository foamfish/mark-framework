package com.mark.framework.cache;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Field缓存
 * @author Mark(VB Inc.)
 *
 */
public final class FieldCache {
	private static final FieldCache INSTANCE = new FieldCache();

	private Map<Class<?>, Map<String, Field>> cache;

	private FieldCache() {
		cache = new ConcurrentHashMap<Class<?>, Map<String, Field>>();
	}

	/**
	 * 返回唯一的instance
	 * @return instance
	 */
	public static FieldCache getInstance(){
		return INSTANCE;
	}

	/**
	 * 参数从缓存中获取
	 * @param clazz 对象类
	 * @return Field
	 */
	public Collection<Field> getFields(Class<?> clazz) {
		Map<String, Field> fields = cache.get(clazz);
		if (fields == null) {
			fields = loadFields(clazz);
		}
		return fields.values();
	}

	/**
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	public Field getField(Class<?> clazz,String name) {
		Map<String, Field> fields = cache.get(clazz);
		if (fields == null) {
			fields = loadFields(clazz);
		}
		return fields.get(name);
	}

	private Map<String, Field> loadFields(Class<?> clazz){
		Map<String, Field> fields = new HashMap<String, Field>();
		Field[] fs = getAllFields(clazz);
		for(Field f : fs){
			fields.put(f.getName(), f);
		}
		cache.put(clazz, fields);
		return fields;
	}

	/**
	 *
	 * @param clazz
	 * @return
	 */
	private static Field[] getAllFields(Class<?> clazz){
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		if(clazz.getSuperclass() != Object.class){
			fields.addAll(Arrays.asList(getAllFields(clazz.getSuperclass())));
		}
		return fields.toArray(new Field[0]);
	}
}
