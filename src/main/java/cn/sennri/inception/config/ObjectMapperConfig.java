package cn.sennri.inception.config;

import cn.sennri.inception.message.ClientActiveMessage;
import cn.sennri.inception.message.Message;
import cn.sennri.inception.message.ServerAnswerActiveMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


/**
 * Jackson ObjectMapper手动配置
 * @author Sennri
 */
@Configuration
public class ObjectMapperConfig {
    /**
     * 定义时间格式
     */
    private final static DateFormat MY_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 定义时区
     */
    private final static TimeZone SHANGHAI_TIME_ZONE = TimeZone.getTimeZone("Asia/Shanghai");

    private ObjectMapper objectMapper;

    /**
     * 生成用户自定义配置的jackson ObjectMapper
     * 主要是为了支持手动配置多态映射
     * @param builder
     * @return
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder)
    {
        objectMapper = builder.createXmlMapper(false).build();

        // 通过该方法对mapper对象进行设置，所有序列化的对象都将按该规则进行序列化

        // 写入时仅写入非空字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 设置时间格式
        objectMapper.setDateFormat(MY_FORMAT);
        // 设置时区
        objectMapper.setTimeZone(SHANGHAI_TIME_ZONE);
        /*
          由于放在接口ModelParam.class @JsonTypeInfo注解会递归生效，对于没有子类的类型，在反序列化时也会查找其子类的反序列化方法
          为了防止这种错误的逻辑，需要配置若无可以使用的子类，就使用该类本身的反序列化方法。
         */
        objectMapper.configure(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL,true);
        // 拒绝序列化内容为空的beans并抛出错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,true);
        // 反序列化是拒绝名字不匹配的域，并抛出错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        // 多态支持

        // 登记父类
        objectMapper.registerSubtypes(Message.class);
        // 登记子类，新的类型都添加在这里。
        registerSubtypeToMapper(ClientActiveMessage.class);
        registerSubtypeToMapper(ServerAnswerActiveMessage.class);

        return objectMapper;
    }

    /**
     * 登记子类
     * @param clazz
     */
    public void registerSubtypeToMapper(Class<?> clazz){
        this.objectMapper.registerSubtypes(new NamedType(clazz, clazz.getSimpleName()));
    }
}
