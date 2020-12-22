package com.gdpc215.exrateapi.objects;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data(staticConstructor = "of")
public class TipoCambioOperacion {

	@Id
	private Long id;
	private BigDecimal monto;
	private BigDecimal montoObtenido;
	private String monedaOrigen;
	private String monedaDestino;
	private BigDecimal tipoCambioUsado;
	private ZonedDateTime ejecucion;

	public TipoCambioOperacion() {
		
	}

	public TipoCambioOperacion(BigDecimal monto, BigDecimal montoObtenido, String monedaOrigen, String monedaDestino, BigDecimal tipoCambioUsado) {
		this.monto = monto;
		this.montoObtenido = montoObtenido;
		this.monedaOrigen = monedaOrigen;
		this.monedaDestino = monedaDestino;
		this.tipoCambioUsado = tipoCambioUsado;
		this.ejecucion = ZonedDateTime.now();
	}
}
