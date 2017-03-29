package com.ykfirst.simplecrawler.core.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *annotate a method which to submit a task/tasks, the method must return Task/List<Task>
 *@author yankang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TaskSubmiting {

}
