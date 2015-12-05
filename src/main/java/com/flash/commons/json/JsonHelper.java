package com.flash.commons.json;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonHelper.class);
	
	private static final ObjectMapper mapper = new ObjectMapper();
	/**
	 * 获取json的某个key的value
	 * @param key
	 * @param jsonTree
	 * @return
	 */
	public static String getNode(String key, String jsonTree){
		try {
			JsonNode node = mapper.readTree(jsonTree);
			return node.get(key)+"";
		} catch (IOException e) {
			LOGGER.error("读取json树失败，源数据为{}", jsonTree);
		}
		return null;
	}
	/**
	 * 将一个对象，转换为json
	 * @param obj
	 * @return
	 */
	public static String transObjToJsonString(Object obj){
		String str;
		try {
			str = mapper.writeValueAsString(obj);
			return str;
		} catch (IOException e) {
			LOGGER.error("json转换失败,原因:{},cause:{}", e.getMessage(),e.getCause());
		}
		return null;
	}
	
	/**
	 * Json转换为对象
	 * @param json
	 * @param cla
	 * @return
	 */
	public static <T> T transJsonStringToObj(String json, Class<T> cla){
		try {
			T t = mapper.readValue(json, cla);
			return t;
		} catch (IOException e) {
			LOGGER.error("json转换失败,原因:{},cause:{}",e.getMessage(),e.getCause());
		}
		return null;
	}
	
	/**
	 * json转换对象 可以转换泛型
	 * @param json
	 * @param cla
	 * @param parameterClasses
	 * @return
	 */
	public static <T> T transJsonStringToObj(String json, Class<T> cla,Class<?>... parameterClasses) {
		try {
			mapper.getTypeFactory().constructParametrizedType(cla, cla, parameterClasses);
			T t = mapper.readValue(json, cla);
			return t;
		} catch (IOException e) {
			LOGGER.error("json转换失败,原因:{},cause:{}",e.getMessage(),e.getCause());
		}
		return null;
	}
	
}
