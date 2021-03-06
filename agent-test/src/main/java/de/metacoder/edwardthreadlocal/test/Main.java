package de.metacoder.edwardthreadlocal.test;

import java.util.Arrays;

public class Main {

  private static final ThreadLocal<String> firstThreadLocal = new ThreadLocal<String>();
  private static final ThreadLocal<String> secondThreadLocal = new ThreadLocal<String>();

  public static void beforeBL() {
    System.out.println("beforebl");
  }

  public static void main(String[] args) {

    try {
      beforeBL();
      for(ThreadLocal<String> tl : Arrays.asList(firstThreadLocal, secondThreadLocal)) {
        System.out.println("Hello World, writing to greeting to the first thread local");
        tl.set("Hello Felix");
        System.out.println("removing the thread local value");
        tl.remove();
        System.out.println("Removed =)");
        tl.set("now this is bad");
        System.out.println("Intentionally set another value, which should be registered as faulty");
      }
    } finally {
      afterBL();
    }

  }


  public static void afterBL() {
    System.out.println("afterbl");
  }

}
