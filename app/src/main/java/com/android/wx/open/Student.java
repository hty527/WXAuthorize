package com.android.wx.open;

import android.util.Log;

/**
 * Created by TinyHung@outlook.com
 * 2019/5/28
 */

public class Student {

    private static final String TAG = "Student";
    private String name;
    private String className;
    private int age;

    public String money;

    public Student(){
        Log.d(TAG,"Student-->");
    }

    public Student(String money) {
        this.money = money;
        Log.d(TAG,"Student-->money:"+money);
    }


    public Student(String name, String className, int age,String money) {
        this.name = name;
        this.className = className;
        this.age = age;
        this.money = money;
        Log.d(TAG,"Student-->name:"+name+",className:"+className+",age:"+age+",money:"+money);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    private void updateMoney(String money){
        this.money = money;
    }


    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", age=" + age +
                ", money='" + money + '\'' +
                '}';
    }
}