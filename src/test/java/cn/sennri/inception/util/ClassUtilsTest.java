package cn.sennri.inception.util;

import cn.sennri.inception.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
public class ClassUtilsTest {
    @Test
    void getAllClassesInTheSamePackage() throws IOException, ClassNotFoundException {
        Collection<Class<?>> classes = ClassUtils.getAllClassesInTheSamePackage(Message.class);
    }

    /**
     * 该测试证明 primitive 数组在log时如果通过占位符替换的形式输出，那么，会直接解析为数组字面量形式。
     * 如果是对象数组，则需要进行转型以便于确定该输出什么。如果转型成Object则能输出字面量，如果不转型则会输出首个对象的值。
     */
    @Test
    public void testLog(){
        String[] ss = new String[]{"2","2","3"};
        log.info("{}", (Object) ss);
        List<String> sss = Arrays.asList(ss);
        log.info("{}", sss);
    }
}