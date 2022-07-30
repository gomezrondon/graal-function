package com.example.graalfunction;

import com.gomezrondon.cloudruntest.TDCProcess;
import com.gomezrondon.cloudruntest.entities.Param;
import com.gomezrondon.cloudruntest.entities.Payload;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebSessionIdResolverAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration;
import org.springframework.boot.autoconfigure.webservices.client.WebServiceTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.reactive.WebSocketReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;


@SpringBootApplication
@EnableAutoConfiguration(exclude = {
		XADataSourceAutoConfiguration.class
		, WebSocketReactiveAutoConfiguration.class
, WebSessionIdResolverAutoConfiguration.class
, WebServicesAutoConfiguration.class
, WebServiceTemplateAutoConfiguration.class
, WebFluxAutoConfiguration.class,
		WebClientAutoConfiguration.class
, TransactionAutoConfiguration.class
, SolrAutoConfiguration.class
, SpringDataWebAutoConfiguration.class
, SendGridAutoConfiguration.class
, ActiveMQAutoConfiguration.class
		, ArtemisAutoConfiguration.class
		, BatchAutoConfiguration.class
, CacheAutoConfiguration.class
, CassandraAutoConfiguration.class
, CassandraDataAutoConfiguration.class
		, CassandraReactiveDataAutoConfiguration.class
, CodecsAutoConfiguration.class
		, CouchbaseAutoConfiguration.class
, CouchbaseDataAutoConfiguration.class
		, CouchbaseReactiveDataAutoConfiguration.class
, DataSourceAutoConfiguration.class
		, ElasticsearchDataAutoConfiguration.class
, EmbeddedMongoAutoConfiguration.class
, ErrorWebFluxAutoConfiguration.class
		, FlywayAutoConfiguration.class
		, GraphQlAutoConfiguration.class




})
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

	@Bean
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
