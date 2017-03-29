package com.ykfirst.simplecrawler.core;
/**
 *
 *@author yankang
 *@date 2014��12��9��
 */
public class Task<T> {

	private  String type;
	private  T data;


	public Task(){
	}

	public Task(String type, T date){
		this.type = type;
		this.data = date;
	}
	
	public String getType() {
		return type;
	}
	public T getDate() {
		return data;
	}

	@Override
	public String toString() {
		return "Task [type=" + type + ", date=" + data + "]";
	}
	
}
