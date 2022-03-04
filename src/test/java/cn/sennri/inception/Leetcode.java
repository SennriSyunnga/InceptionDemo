package cn.sennri.inception;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Leetcode {
    public static class A{
        final String type;
        public A(){
            this.type = this.getClass().getSimpleName();
            log.debug(type);
        }
    }

    public static class B extends A{

    }

    public static void main(String[] args) throws InterruptedException {
        B b = new B();
        A bb = new B();
    }


}
