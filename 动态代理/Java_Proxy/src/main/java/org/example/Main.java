package org.example;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) {

        IUser user = new IUserImpl();
//        user.show();

        //静态代理
//        IUser userProxy = new UserProxy(user);
//        userProxy.show2();

        //动态代理
        InvocationHandler userinvocationHandler = new UserInvocationHandler(user);
        IUser userProxy = (IUser) Proxy.newProxyInstance(user.getClass().getClassLoader(), user.getClass().getInterfaces(), userinvocationHandler);
        userProxy.create2();

    }
}