package com.ykfirst.simplecrawler.core;

import com.ykfirst.simplecrawler.core.Task;
import com.ykfirst.simplecrawler.core.anno.TaskSubmiting;
import com.ykfirst.simplecrawler.web.SimpleCrawler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 *
 *@author yankang
 *@date 2015年1月5日
 */
public class TaskSumiterStrong<T> implements MethodInterceptor {
	private final Logger log  = LoggerFactory.getLogger(this.getClass());

	private TaskExecutor<T> sc;

	public TaskSumiterStrong(TaskExecutor<T> sc) {
		this.sc = sc;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		Object result = proxy.invokeSuper(obj, args);
		if(method.getAnnotation(TaskSubmiting.class) == null) return result;

		if(result == null) return null;

		if(result instanceof Task){
			Task<T> task = (Task<T>) result;
			sc.submit(task);
			log.debug("add a task " + task);
			return result;
		}

		List<Task<T>> list = (List<Task<T>>) result;
		for(Task<T> task : list){
			sc.submit(task);
			log.debug("add a task " + task);
		}

		return result;
	}

}
