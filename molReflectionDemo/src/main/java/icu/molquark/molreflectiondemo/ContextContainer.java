package icu.molquark.molreflectiondemo;

import icu.molquark.molreflectiondemo.domain.Classroom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.*;
import java.util.*;

/**
 * 容器, 目前只实现从指定类中扫描静态方法, 获取Bean
 */
public class ContextContainer {
    private static volatile ContextContainer singleton = null;
    private Map<Class<?>, Method> beanMethod;
    private Map<Class<?>, Object> beanStore;
    private Map<Method, Object> methodRunObj;
    private Object nowObj;


    public ContextContainer(){
        beanMethod = new HashMap<>();
        beanStore = new HashMap<>();
        methodRunObj = new HashMap<>();
        try {
            Class<?> clazz = Class.forName(
                    "icu.molquark.molreflectiondemo.MolBeanGenerator");
            nowObj = clazz.getConstructor().newInstance();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {

                // 有注解的静态方法
                if (method.getAnnotation(Bean.class) != null) {
                    if(Modifier.isStatic(method.getModifiers())){
                        methodRunObj.put(method,null);
                    }else {
                        methodRunObj.put(method, nowObj);
                    }
                    beanMethod.put(method.getReturnType(), method);
                    System.out.println(getMethodSign(method));
                }
            }
        }catch (ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 传入指定类型, 返回该类型的Bean, 尚未考虑多线程的情况
     * @param clazz
     * @return
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz){
        if(beanStore.containsKey(clazz)){
            return (T) beanStore.get(clazz);
        }
        // 先调用方法获取Bean
        // 目前默认全为static, 无参
        if(!beanMethod.containsKey(clazz)) return null;
        try {
            Method method = beanMethod.get(clazz);
            beanStore.put(clazz, method.invoke(methodRunObj.get(method)));
        }catch (ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
        return (T) beanStore.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public Classroom getClassRoom(){
        try {
            Class<?> clz = Class.forName("icu.molquark.molreflectiondemo.domain.Classroom");
            Constructor<?> constructor = getAutowiredConstructor(clz);
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];
            for(int i=0;i< paramTypes.length;++i){
                params[i] = getBean(paramTypes[i]);
            }
            return (Classroom) constructor.newInstance(params);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> getAutowiredConstructor(Class<?> clz){
        Constructor<?>[] constructors = clz.getConstructors();
        Constructor<?> constructor = null;
        if(constructors.length>1){
            List<Constructor<?>> constructorList = new ArrayList<>();
            for(Constructor<?> c: constructors){
                if(c.getAnnotation(Autowired.class)!=null){
                    // 后续考虑排除有数组参数的构造器
                    constructorList.add(c);
                }
            }
            if(constructorList.size()==0){
                throw new RuntimeException("need @Autowired mark constructor");
            } else if (constructorList.size() > 1) {
                throw new RuntimeException("@Autowired mark more than one constructor");
            }
            constructor = constructorList.get(0);
        }else {
            constructor = constructors[0];
        }
        return constructor;
    }



    public static ContextContainer getContainer(){
        if(singleton==null){
            synchronized (ContextContainer.class){
                if (singleton==null){
                    singleton = new ContextContainer();
                }
            }
        }
        return singleton;
    }


    public String getMethodSign(Method method){
        StringBuilder sb = new StringBuilder(32);
        sb.append(method.getReturnType().getName());
        sb.append(' ');
        sb.append(method.getName());
        sb.append('(');
        boolean flag = false;
        for(Parameter p: method.getParameters()){
            flag=true;
            sb.append(p.getType().getName());
            sb.append(' ');
            sb.append(p.getName());
            sb.append(',');
        }
        if(flag){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(')');
        return sb.toString();
    }
}
