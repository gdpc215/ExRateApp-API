package com.gdpc215.exrateapi.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.gdpc215.exrateapi.objects.TipoCambio;

import reactor.core.publisher.Mono;

public interface TipoCambioRepository extends ReactiveCrudRepository<TipoCambio, String> {

    @Query("SELECT * FROM Tipo_Cambio WHERE LOWER(moneda) = LOWER(:moneda) LIMIT 1;")
    Mono<TipoCambio> getTipoCambioMoneda(String moneda);

}
