package com.fansqz.mirpc.example;

public class ServiceImpl implements Service{

    public String HelloWorld(String name){
        String s = "Hello " + name;
        System.out.println(s);
        return s;
    }
}
