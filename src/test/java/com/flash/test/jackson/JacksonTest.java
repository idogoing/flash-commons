package com.flash.test.jackson;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonTest {
	public static void main(String[] args) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TestResult<User> result = new TestResult<>();
		result.setId(1);
		result.setMsg("msg");
		User user = new User();
		user.setId(1);
		user.setName("name");
		result.setData(user);
		
		String writeValueAsString = mapper.writeValueAsString(result);
		System.out.println(writeValueAsString);
		TestResult readValue = transJsonStringToObj(writeValueAsString, TestResult.class, User.class);
		System.out.println(readValue);
	}
	
	public static <T> T transJsonStringToObj(String json, Class<T> cla,Class<?>... parameterClasses) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.getTypeFactory().constructParametrizedType(cla, cla, parameterClasses);
			T t = mapper.readValue(json, cla);
			return t;
		} catch (IOException e) {
			
		}
		return null;
	}
}
