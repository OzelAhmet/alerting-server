package yte.intern.alertingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlertingServerApplication {


	public static void main(String[] args) {
		SpringApplication.run(AlertingServerApplication.class, args);
	}

}
