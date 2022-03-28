package cn.sennri.inception.util;

import cn.sennri.inception.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;

@Slf4j
public class ClassUtilsTest {
    @Test
    void getAllClassesInTheSamePackage() throws IOException, ClassNotFoundException {
        Collection<Class<?>> classes = ClassUtils.getAllClassesInTheSamePackage(Message.class);
    }
}