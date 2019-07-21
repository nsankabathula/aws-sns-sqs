package com.example;

public interface ITestInterface {

     void method1();
     public default void method2(){
         System.out.println("ITestInterface method2");
     }

}
