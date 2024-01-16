package org.example;

public class Person {

    static{
        System.out.println("静态代码块");
    }
    {
        System.out.println("构造代码块");
    }
    public Person(){
        System.out.println("无参构造器");
    }

    public Person(String args){
        System.out.println("有参构造器");
    }

}
