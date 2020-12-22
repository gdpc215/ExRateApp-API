package com.gdpc215.exrateapi.objects;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data(staticConstructor = "of")
public class TipoCambio {

	@Id
	private Long id;
	private String moneda;
	private BigDecimal cambio;
	private ZonedDateTime modificacion;
	private String origen;

	public TipoCambio() {
	}

	public TipoCambio(String moneda, BigDecimal valorTC, String origen) {
		this.moneda = moneda;
		this.cambio = valorTC;
		this.origen = origen;
		this.modificacion = ZonedDateTime.now();
	}
}
