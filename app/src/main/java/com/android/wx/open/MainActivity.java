package com.android.wx.open;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startAuthorize(View view) {
        try {
            Class clazz = Class.forName("com.android.wx.open.Student");
            //获取构造方法
            Constructor[] constructors = clazz.getConstructors();
            for (Constructor constructor : constructors) {
                Log.d(TAG,"构造:"+constructor.toString());
            }
            //获取所有公共字段
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                Log.d(TAG,"公开属性:"+field.toString());
            }
            //获取所有字段(公开、私有)
            Field[] privateFields = clazz.getDeclaredFields();
            for (Field field : privateFields) {
                Log.d(TAG,"公开、私有属性:"+field.toString());
            }
            //获取指定的字段(公开、私有)
            Object student = clazz.getConstructor().newInstance();
            Field name = clazz.getDeclaredField("name");
            Field className = clazz.getDeclaredField("className");
            Field age = clazz.getDeclaredField("age");
            //获取公开的字段
            Field money = clazz.getField("money");
            Log.d(TAG,"属性NAME:"+name);
            //私有属性必须反射
            name.setAccessible(true);
            className.setAccessible(true);
            age.setAccessible(true);

            name.set(student,"张三丰");
            className.set(student,"302班");
            age.setInt(student,888);
            money.set(student,"888");

            Log.d(TAG,"反射后赋值赋值Student:"+((Student)student).toString());

            //反射一个对象并且实例化,无参构造
            Object instance = clazz.getConstructor().newInstance();
            Log.d(TAG,"无参构造初始化："+instance.toString());

            //获取所有公开方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                Log.d(TAG,"所有公开方法:"+method.toString());
            }
            //获取所有方法(公开、私有)
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                Log.d(TAG,"所有方法:"+method.toString());
            }
            //获取某个公开方法
            Method setAge = clazz.getDeclaredMethod("setClassName",String.class);
            setAge.invoke(student,"2312631431");
            Log.d(TAG,"公开方法改变值后：:"+student.toString());
            //获取某个公开、私有方法
            Method updateMoney = clazz.getDeclaredMethod("updateMoney", String.class);
            Log.d(TAG,"获取指定方法updateMoney:"+updateMoney.toString());
            //私有方法后必须反射
            updateMoney.setAccessible(true);
            //反射调用
            updateMoney.invoke(student,"1212121212");
            Log.d(TAG,"反射调用方法后student:"+student.toString());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}