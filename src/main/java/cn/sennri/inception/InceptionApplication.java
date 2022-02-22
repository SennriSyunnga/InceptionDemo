package cn.sennri.inception;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class InceptionApplication {

	public static void main(String[] args) {
		SpringApplication.run(InceptionApplication.class, args);
	}

}
