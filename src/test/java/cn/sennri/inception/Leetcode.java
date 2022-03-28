package cn.sennri.inception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

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
        B b = new B();
        A bb = new B();
        A bbb = b.newInstance();
        return;
    }
}
