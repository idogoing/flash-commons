package com.flash.test.jackson;

public class TestResult<T> {
	private int id;
	private String msg;
	private T data;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "TestResult [id=" + id + ", msg=" + msg + ", data=" + data + "]";
	}

}
