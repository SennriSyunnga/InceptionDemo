package cn.sennri.inception;

import cn.sennri.inception.model.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Leetcode {
    public static class A{
        final String type;
        public A(){
            this.type = this.getClass().getSimpleName();
            log.debug(type);
            C c = this.getC();
        }

        public C getC(){
            return new C(this);
        }

        public A newInstance() throws InstantiationException {
            try {
                return this.getClass().newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class B extends A{
    }

    public static class C {
        String type;
        C(A a){
            this.type = a.type;
            log.debug(type);
        }
    }

    public static void main(String[] args) throws InterruptedException, InstantiationException {
//        B b = new B();
//        A bb = new B();
//        A bbb = b.newInstance();

        Map<Long, List<?>> map = new ConcurrentHashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        List<Long> list2 = new ArrayList<>();
        list2.add(1L);
        map.put(1L, list);
        map.put(2L, list2);
        List<Integer> list1 = (List<Integer>) map.get(1);
        list.add(1);
        return;
    }


}
