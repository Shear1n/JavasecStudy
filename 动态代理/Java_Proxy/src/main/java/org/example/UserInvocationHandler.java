package org.example;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UserInvocationHandler implements InvocationHandler {

    IUser user;

    public UserInvocationHandler(){

    }

    public UserInvocationHandler(IUser user){
        this.user = user;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        method.invoke(user,args);
        return null;
    }
}
