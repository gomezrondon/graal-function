package com.example.graalfunction;

import com.gomezrondon.cloudruntest.TDCProcess;
import com.gomezrondon.cloudruntest.entities.Param;
import com.gomezrondon.cloudruntest.entities.Payload;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@RestController
@SpringBootApplication
public class GraalFunctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraalFunctionApplication.class, args);
	}

	@Bean
	public Function<String, String> uppercase() {
		return String::toUpperCase;
	}

	@Bean
	public Function<Person, String> person() {
		return Person::getName;
	}


	@PostMapping("/processtdc")
	public String processTDC(@RequestBody Payload payload) {
		String monto = getParameter(payload, "monto");
		String exchange_rate = getParameter(payload, "exchange_rate");
		String payload1 = payload.getPayload();

		String calculatePayment = TDCProcess.Companion.calculatePayment(exchange_rate, monto, payload1);
		return calculatePayment + "\n Done! " + getTime();
	}

//	@Bean
	public Function<Payload, String> processtdc() {
		return payload -> {
			String monto = getParameter(payload, "monto");
			String exchange_rate = getParameter(payload, "exchange_rate");
			String payload1 = payload.getPayload();

			String calculatePayment = TDCProcess.Companion.calculatePayment(exchange_rate, monto, payload1);
			return calculatePayment + "\n Done! " + getTime();
		};
	}


	private String getParameter(Payload payload, String param_name) {
		return payload.getParams()
				.stream()
				.filter(x -> x.getName()
						.equals(param_name))
				.map(Param::getValue)
				.findFirst().orElse("");
	}


	@Bean
	public Supplier<String> getTime() {

		return () -> {
			UUID uuid = UUID.randomUUID();
			String string = LocalDateTime.now() + " " + uuid;
			System.out.println(string);
			return string;
		};

	}

}
