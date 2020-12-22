CREATE TABLE TIPO_CAMBIO (
	id SERIAL PRIMARY KEY,
	moneda VARCHAR(100),
	cambio DECIMAL(18, 4),
	modificacion TIMESTAMP WITH TIME ZONE,
	origen VARCHAR(200)
);

CREATE TABLE TIPO_CAMBIO_OPERACION (
	id SERIAL PRIMARY KEY,
	monto DECIMAL(18, 4),
	monto_obtenido DECIMAL(18, 4),
	moneda_origen VARCHAR(100),
	moneda_destino VARCHAR(100),
	tipo_cambio_usado DECIMAL(18, 4),
	ejecucion TIMESTAMP WITH TIME ZONE
);