package com.ykfirst.simplecrawler.core;

import com.ykfirst.simplecrawler.core.anno.Action;
import com.ykfirst.simplecrawler.core.anno.Init;
import com.ykfirst.simplecrawler.core.anno.TaskSolving;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by baoz on 2017/3/23.
 */
public class TaskExecutor<T>{
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private List<RunInfo> initMethodList = new ArrayList<>();
    private Map<String, RunInfo> taskSolvingMaps = new HashMap<>();

    private ExecutorService es;
    private MethodInterceptor mi;

    public TaskExecutor(int poolSize, String... packages){
        try {
            mi = new TaskSumiterStrong<T>(this);

            initTaskSolvingMap(packages);

            initConsumerThread(poolSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TaskExecutor(int poolSize,  MethodInterceptor mi,String... packages){
        try {
            this.mi = mi;

            initTaskSolvingMap(packages);

            initConsumerThread(poolSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TaskExecutor(ThreadPoolExecutor es, String... packages){
        try {
            mi = new TaskSumiterStrong<T>(this);
            initTaskSolvingMap(packages);
            this.es = es;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TaskExecutor(ThreadPoolExecutor es, MethodInterceptor mi, String... packages){
        try {
            this.mi = mi;
            initTaskSolvingMap(packages);
            this.es = es;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            for(RunInfo ri: initMethodList) {
                ri.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void submit(final Task<T> task) {
        es.submit(geneRunnableFromTask(task));
    }

    protected Runnable geneRunnableFromTask(final Task<T> task){
        return new Runnable() {
            @Override
            public void run() {
                RunInfo ri = taskSolvingMaps.get(task.getType());
                if (ri == null) return;
                try {
                    ri.execute(task.getDate());
                } catch (Exception e) {
                    log.info("handle {}, {}", task.toString(), " error");
                }
            }
        };
    }

    protected RunInfo getRunInfo(String type) {
        return  taskSolvingMaps.get(type);
    }

    private void initTaskSolvingMap(String[] packages) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        for(String pack : packages) {
            initSinglePackage(pack);
        }
    }

    private void initSinglePackage(String pack) throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        ClassLoader loader = this.getClass().getClassLoader();
        String path = pack.replaceAll("\\.", "/");
        URL resource = loader.getResource(path);


        File file = new File(resource.getFile());
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getAbsolutePath().endsWith(".class");
            }
        });
        for (File sub : files) {
            String name = sub.getName();
            // remove inner class
            if (name.contains("$"))
                continue;
            name = name.substring(0, name.length() - 6);
            Class<?> clazz = Class.forName(pack + "." + name);
            if(clazz.getAnnotation(Action.class) == null) continue;
            Object target = getStrong(clazz);

            Method initMethod = null;
            Class<?> strongClazz = target.getClass();
            for(Method method : clazz.getDeclaredMethods()){
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                if(method.getAnnotation(Init.class) != null) {
                    initMethod = strongClazz.getMethod(methodName, parameterTypes);
                }

                TaskSolving ts = method.getAnnotation(TaskSolving.class);
                if(ts== null)continue;
                String key = ts.value();
                taskSolvingMaps.put(key, new RunInfo(target, strongClazz.getMethod(methodName, parameterTypes)));
            }

            initMethodList.add(new RunInfo(target, initMethod));
        }

    }

    private Object getStrong(Class<?> clazz) {
        Enhancer enhancer = new  Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(mi);
        return enhancer.create();
    }

    private void initConsumerThread(int poolSize) {
        es = Executors.newFixedThreadPool(poolSize);
    }

}
