package com.gdpc215.exrateapi;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.web.reactive.function.client.WebClient;

import com.gdpc215.exrateapi.objects.TipoCambio;
import com.gdpc215.exrateapi.repositories.TipoCambioRepository;

import io.r2dbc.spi.ConnectionFactory;

@SpringBootApplication
public class ExRateApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExRateApiApplication.class, args);
	}
	
	@Bean
	ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));

		return initializer;
	}
	
	@Bean
	public ApplicationRunner tipoCambioInicial(TipoCambioRepository repository) {
		return (args) -> {
			/* Metodo para poblar la tabla de Tipo de Cambio con data reciente de las APIs publicas del Banco Central de Reserva */
			
			// Se toma el ultimo valor entre los 10 dias existentes dado que no existe fiabilidad al 100% en la actualizacion
			String initialDate = LocalDateTime.now().minusDays(10).format(DateTimeFormatter.ISO_LOCAL_DATE);
			String lastDate = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
			String valorSerieDolares = "PD04639PD";
			String valorSerieEuros = "PD04647PD";
			
			WebClient wClient = WebClient.create();
			
			// TC Dolar BCRP
			String valTC_Dolar = wClient
		            .get()
		            .uri(String.format("https://estadisticas.bcrp.gob.pe/estadisticas/series/api/%1$s/json/%2$s/%3$s/", valorSerieDolares, initialDate, lastDate))
		            .retrieve()
	                .bodyToMono(String.class)
	                .map(tcValue -> {
	                	JSONArray result = new JSONObject(tcValue).getJSONArray("periods");
	                	return result.getJSONObject(result.length() - 1).getJSONArray("values").getString(0).toString();
	                })
	                //.map(tcValue -> { return tcValue.substring(tcValue.length() - 5); }) // usar al cambiar de json a txt
	                .block();
			
			// TC Euro BCRP
			String valTC_Euro = wClient
		            .get()
		            .uri(String.format("https://estadisticas.bcrp.gob.pe/estadisticas/series/api/%1$s/json/%2$s/%3$s/", valorSerieEuros, initialDate, lastDate))
		            .retrieve()
	                .bodyToMono(String.class)
	                .map(tcValue -> {
	                	JSONArray result = new JSONObject(tcValue).getJSONArray("periods");
	                	return result.getJSONObject(result.length() - 1).getJSONArray("values").getString(0).toString();
	                })
	                //.map(tcValue -> { return tcValue.substring(tcValue.length() - 5); }) // usar al cambiar de json a txt
	                .block();

			repository
				.saveAll(
					Arrays.asList(
						new TipoCambio("PEN", new BigDecimal(1), "Tipo Cambio base"),
						new TipoCambio("USD", new BigDecimal(valTC_Dolar), "Tipo Cambio BCRP - inicio del dia"),
						new TipoCambio("EUR", new BigDecimal(valTC_Euro), "Tipo Cambio BCRP - inicio del dia")
					)
				)
				.blockLast(Duration.ofSeconds(5));
		};
	}

}
