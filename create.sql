-- Schema: VMserver

-- DROP SCHEMA "VMserver";

CREATE SCHEMA "VMserver"
  AUTHORIZATION postgres;


-- Table: "VMserver".ip_pool

-- DROP TABLE "VMserver".ip_pool;

CREATE TABLE "VMserver".ip_pool
(
  id_ip serial NOT NULL,
  ip character varying(20),
  port integer DEFAULT 80,
  reserve boolean DEFAULT false,
  last_reserve timestamp without time zone,
  CONSTRAINT id_ip PRIMARY KEY (id_ip)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "VMserver".ip_pool
  OWNER TO postgres;


-- Table: "VMserver".system_params

-- DROP TABLE "VMserver".system_params;

CREATE TABLE "VMserver".system_params
(
  key character varying(100),
  value character varying(500)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "VMserver".system_params
  OWNER TO postgres;


-- Table: "VMserver".vmachine

-- DROP TABLE "VMserver".vmachine;

CREATE TABLE "VMserver".vmachine
(
  id_vmachine serial NOT NULL,
  cpu integer,
  ram bigint,
  free_count integer DEFAULT 0,
  id_ip integer,
  last_connect timestamp without time zone,
  CONSTRAINT vmachine_pkey PRIMARY KEY (id_vmachine),
  CONSTRAINT id_ip FOREIGN KEY (id_ip)
      REFERENCES "VMserver".ip_pool (id_ip) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "VMserver".vmachine
  OWNER TO postgres;

-- Index: "VMserver".fki_id_ip

-- DROP INDEX "VMserver".fki_id_ip;

CREATE INDEX fki_id_ip
  ON "VMserver".vmachine
  USING btree
  (id_ip);


