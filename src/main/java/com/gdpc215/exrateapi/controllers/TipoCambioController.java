package com.gdpc215.exrateapi.controllers;

import java.time.ZonedDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gdpc215.exrateapi.objects.TipoCambio;
import com.gdpc215.exrateapi.repositories.TipoCambioRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
class TipoCambioController {
    private TipoCambioRepository repository;

    @Autowired
    public TipoCambioController(TipoCambioRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/tipocambio")
    Flux<TipoCambio> getTiposCambioDisponibles() {
        return repository.findAll();
    }
    
    @GetMapping("/tipocambio/moneda/{moneda}")
    Mono<TipoCambio> getTipoCambioMoneda(@PathVariable("moneda") String moneda) {
        return repository.getTipoCambioMoneda(moneda);
    }
    
    @PostMapping("/tipocambio")
    Flux<TipoCambio> addTipoCambio(@RequestBody TipoCambio[] tiposCambio) {
        return repository.saveAll(Arrays.asList(tiposCambio));
    }
    
    @PutMapping("/tipocambio")
    Mono<ResponseEntity<TipoCambio>> updateTipoCambio(@PathVariable(value = "moneda") String tcMoneda, @RequestBody TipoCambio tipoCambio) {
    	return repository
    		.getTipoCambioMoneda(tcMoneda)
    		.flatMap(objTC -> {
    			objTC.setCambio(tipoCambio.getCambio());
    			objTC.setOrigen(tipoCambio.getOrigen());
    			objTC.setModificacion(ZonedDateTime.now());
				return repository.save(objTC);
			})
    		.map(objTC -> new ResponseEntity<>(objTC, HttpStatus.OK))
			.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}