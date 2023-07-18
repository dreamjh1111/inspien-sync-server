package inspien.serversync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ServersyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServersyncApplication.class, args);
	}

}
