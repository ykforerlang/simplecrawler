package com.ykfirst.simplecrawler.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 *@author yankang
 */
public class RunInfo {
	private final Object target;
	private final Method method;
	
	public RunInfo(Object target, Method method){
		this.target = target;
		this.method = method;
	}
	
	/**
	 * ִ����
	 * @param args
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object execute(Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		return method.invoke(target, args);
	}
}
