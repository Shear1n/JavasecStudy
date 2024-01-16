package org.example;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import sun.misc.Unsafe;

import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException, TransformerConfigurationException {

//        System.out.println("Hello world!");
//        Class.forName("org.example.Person");
        ClassLoader cl = ClassLoader.getSystemClassLoader();
//        cl.loadClass("org.example.Person");
//        c.newInstance();

        // 利用ClassLoader远程加载任意类
//        ClassLoader classLoader = new URLClassLoader(new URL[]{new URL("http://localhost:9999/")});
//        Class cl = classLoader.loadClass("org.example.Test");
//        cl.newInstance();

        // 利用URLClassLoader.defineClass 加载字节码
        // ClassLoader#loadClass -> ClassLoader#findClass -> ClassLoader#defineClass
//        Method method = ClassLoader.class.getDeclaredMethod("defineClass",String.class, byte[].class, int.class, int.class);
//        method.setAccessible(true);
//        byte[] code = Files.readAllBytes(Paths.get("/Users/Shear1n/Downloads/Test.class"));
//        Class c = (Class) method.invoke(cl,"org.example.Test",code,0,code.length);
//        c.newInstance();

        // 利用Unsafe.defineClass 加载字节码
//        Class c = Unsafe.class;
//        Field field = c.getDeclaredField("theUnsafe");
//        field.setAccessible(true);
//        Unsafe unsafe = (Unsafe) field.get(null);

        byte[] code = Files.readAllBytes(Paths.get("/Users/Shear1n/Downloads/TemplatesCalc.class"));

//        Class cc = unsafe.defineClass("org.example.Test",code,0,code.length,cl,null);
//        cc.newInstance();

//        Class t = TemplatesImpl.class;

        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates,"_name","Test");
        setFieldValue(templates,"_bytecodes",new byte[][]{code});
        setFieldValue(templates,"_tfactory",new TransformerFactoryImpl());
        templates.newTransformer();

    }

    private static void setFieldValue(Object obj, String code, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(code);
        field.setAccessible(true);
        field.set(obj,value);
    }

}