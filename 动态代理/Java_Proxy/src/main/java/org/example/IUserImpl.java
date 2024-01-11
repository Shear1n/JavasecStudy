package org.example;

public class IUserImpl implements IUser{
    @Override
    public void show2(){
        System.out.println("展示");
    }

    @Override
    public void create2(){
        System.out.println("创建");
    }

    @Override
    public void delete(){
        System.out.println("删除");
    }
//
}
