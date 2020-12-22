package com.gdpc215.exrateapi.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.gdpc215.exrateapi.objects.TipoCambioOperacion;

public interface TipoCambioOperacionRepository extends ReactiveCrudRepository<TipoCambioOperacion, String> {

}
