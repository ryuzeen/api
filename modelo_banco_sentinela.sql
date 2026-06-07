-- =====================================================================
--  GLOBAL SOLUTION 2026/1 - PARTE 1: MODELO DE BANCO DE DADOS
--  Projeto SENTINELA - Monitoramento e resposta a desastres naturais
--                      com dados de satelite + sensores IoT
--  SGBD-alvo: Oracle Database (12c+). Usa colunas IDENTITY.
--
--  Observacao sobre IDs: as PKs sao GENERATED ALWAYS AS IDENTITY, geradas
--  na ordem de insercao (1, 2, 3...). As FKs nos INSERTs abaixo referenciam
--  esses IDs sequenciais. Rode o script de cima para baixo, na ordem dada.
-- =====================================================================


-- ---------------------------------------------------------------------
-- 0. LIMPEZA (permite reexecutar o script sem erro de "tabela ja existe")
--    Ignora o erro ORA-00942 (tabela inexistente) na primeira execucao.
-- ---------------------------------------------------------------------
BEGIN
  FOR t IN (SELECT table_name FROM user_tables
            WHERE table_name IN ('ATENDIMENTO','ALERTA','LEITURA',
                                 'OCORRENCIA','SENSOR','EQUIPE','REGIAO')) LOOP
    EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS';
  END LOOP;
END;
/


-- ---------------------------------------------------------------------
-- 1. CRIACAO DAS TABELAS (na ordem de dependencia das FKs)
-- ---------------------------------------------------------------------

-- Regiao monitorada (area geografica acompanhada via satelite/sensores)
CREATE TABLE regiao (
  regiao_id   NUMBER GENERATED ALWAYS AS IDENTITY,
  nome        VARCHAR2(100) NOT NULL,
  uf          CHAR(2)       NOT NULL,
  latitude    NUMBER(9,6)   NOT NULL,
  longitude   NUMBER(9,6)   NOT NULL,
  bioma       VARCHAR2(50),
  CONSTRAINT pk_regiao PRIMARY KEY (regiao_id)
);

-- Sensor IoT instalado em uma regiao
CREATE TABLE sensor (
  sensor_id        NUMBER GENERATED ALWAYS AS IDENTITY,
  regiao_id        NUMBER        NOT NULL,
  tipo             VARCHAR2(20)  NOT NULL,
  unidade_medida   VARCHAR2(20)  NOT NULL,
  status           VARCHAR2(10)  DEFAULT 'ATIVO' NOT NULL,
  data_instalacao  DATE          NOT NULL,
  CONSTRAINT pk_sensor PRIMARY KEY (sensor_id),
  CONSTRAINT fk_sensor_regiao FOREIGN KEY (regiao_id) REFERENCES regiao(regiao_id),
  CONSTRAINT ck_sensor_tipo   CHECK (tipo IN ('TEMPERATURA','FUMACA','NIVEL_AGUA')),
  CONSTRAINT ck_sensor_status CHECK (status IN ('ATIVO','INATIVO'))
);

-- Leitura (medicao) enviada por um sensor
CREATE TABLE leitura (
  leitura_id  NUMBER GENERATED ALWAYS AS IDENTITY,
  sensor_id   NUMBER      NOT NULL,
  valor       NUMBER(10,2) NOT NULL,
  data_hora   TIMESTAMP   NOT NULL,
  CONSTRAINT pk_leitura PRIMARY KEY (leitura_id),
  CONSTRAINT fk_leitura_sensor FOREIGN KEY (sensor_id) REFERENCES sensor(sensor_id)
);

-- Ocorrencia (evento de desastre detectado em uma regiao)
CREATE TABLE ocorrencia (
  ocorrencia_id   NUMBER GENERATED ALWAYS AS IDENTITY,
  regiao_id       NUMBER        NOT NULL,
  tipo            VARCHAR2(20)  NOT NULL,
  severidade      VARCHAR2(10)  NOT NULL,
  fonte_deteccao  VARCHAR2(10)  NOT NULL,
  status          VARCHAR2(15)  DEFAULT 'ABERTA' NOT NULL,
  data_deteccao   DATE          NOT NULL,
  CONSTRAINT pk_ocorrencia PRIMARY KEY (ocorrencia_id),
  CONSTRAINT fk_ocorrencia_regiao FOREIGN KEY (regiao_id) REFERENCES regiao(regiao_id),
  CONSTRAINT ck_ocorr_tipo   CHECK (tipo IN ('QUEIMADA','ENCHENTE','DESLIZAMENTO')),
  CONSTRAINT ck_ocorr_sev    CHECK (severidade IN ('BAIXA','MEDIA','ALTA','CRITICA')),
  CONSTRAINT ck_ocorr_fonte  CHECK (fonte_deteccao IN ('SATELITE','SENSOR','MANUAL')),
  CONSTRAINT ck_ocorr_status CHECK (status IN ('ABERTA','EM_ATENDIMENTO','ENCERRADA'))
);

-- Alerta gerado a partir de uma ocorrencia
CREATE TABLE alerta (
  alerta_id      NUMBER GENERATED ALWAYS AS IDENTITY,
  ocorrencia_id  NUMBER        NOT NULL,
  nivel          VARCHAR2(15)  NOT NULL,
  mensagem       VARCHAR2(200) NOT NULL,
  canal          VARCHAR2(10)  NOT NULL,
  data_emissao   DATE          NOT NULL,
  CONSTRAINT pk_alerta PRIMARY KEY (alerta_id),
  CONSTRAINT fk_alerta_ocorrencia FOREIGN KEY (ocorrencia_id) REFERENCES ocorrencia(ocorrencia_id),
  CONSTRAINT ck_alerta_canal CHECK (canal IN ('SMS','APP','EMAIL'))
);

-- Equipe de resposta (bombeiros, defesa civil, voluntarios)
CREATE TABLE equipe (
  equipe_id  NUMBER GENERATED ALWAYS AS IDENTITY,
  nome       VARCHAR2(100) NOT NULL,
  tipo       VARCHAR2(15)  NOT NULL,
  contato    VARCHAR2(50),
  CONSTRAINT pk_equipe PRIMARY KEY (equipe_id),
  CONSTRAINT ck_equipe_tipo CHECK (tipo IN ('BOMBEIROS','DEFESA_CIVIL','VOLUNTARIOS'))
);

-- Atendimento: tabela associativa (N:N) entre OCORRENCIA e EQUIPE
CREATE TABLE atendimento (
  atendimento_id  NUMBER GENERATED ALWAYS AS IDENTITY,
  ocorrencia_id   NUMBER       NOT NULL,
  equipe_id       NUMBER       NOT NULL,
  data_inicio     DATE         NOT NULL,
  data_fim        DATE,
  situacao        VARCHAR2(15) DEFAULT 'EM_ANDAMENTO' NOT NULL,
  CONSTRAINT pk_atendimento PRIMARY KEY (atendimento_id),
  CONSTRAINT fk_atend_ocorrencia FOREIGN KEY (ocorrencia_id) REFERENCES ocorrencia(ocorrencia_id),
  CONSTRAINT fk_atend_equipe     FOREIGN KEY (equipe_id)     REFERENCES equipe(equipe_id),
  CONSTRAINT ck_atend_situacao   CHECK (situacao IN ('EM_ANDAMENTO','CONCLUIDO'))
);


-- ---------------------------------------------------------------------
-- 2. DADOS DE EXEMPLO (para simular o uso do sistema)
-- ---------------------------------------------------------------------

-- regiao_id => 1, 2, 3
INSERT INTO regiao (nome, uf, latitude, longitude, bioma)
  VALUES ('Vale do Ribeira', 'SP', -24.500000, -47.800000, 'Mata Atlantica');
INSERT INTO regiao (nome, uf, latitude, longitude, bioma)
  VALUES ('Chapada dos Veadeiros', 'GO', -14.100000, -47.600000, 'Cerrado');
INSERT INTO regiao (nome, uf, latitude, longitude, bioma)
  VALUES ('Baixada do Itajai', 'SC', -26.900000, -48.600000, 'Mata Atlantica');

-- sensor_id => 1, 2, 3, 4
INSERT INTO sensor (regiao_id, tipo, unidade_medida, status, data_instalacao)
  VALUES (2, 'TEMPERATURA', 'Celsius', 'ATIVO',   DATE '2026-01-10');
INSERT INTO sensor (regiao_id, tipo, unidade_medida, status, data_instalacao)
  VALUES (2, 'FUMACA',      'ppm',     'ATIVO',   DATE '2026-01-10');
INSERT INTO sensor (regiao_id, tipo, unidade_medida, status, data_instalacao)
  VALUES (3, 'NIVEL_AGUA',  'metros',  'ATIVO',   DATE '2026-02-01');
INSERT INTO sensor (regiao_id, tipo, unidade_medida, status, data_instalacao)
  VALUES (1, 'FUMACA',      'ppm',     'INATIVO', DATE '2025-11-20');

INSERT INTO leitura (sensor_id, valor, data_hora)
  VALUES (1, 41.5,  TIMESTAMP '2026-05-20 13:00:00');
INSERT INTO leitura (sensor_id, valor, data_hora)
  VALUES (2, 180.0, TIMESTAMP '2026-05-20 13:00:00');
INSERT INTO leitura (sensor_id, valor, data_hora)
  VALUES (2, 640.0, TIMESTAMP '2026-05-20 14:30:00');
INSERT INTO leitura (sensor_id, valor, data_hora)
  VALUES (3, 3.8,   TIMESTAMP '2026-05-21 06:00:00');
INSERT INTO leitura (sensor_id, valor, data_hora)
  VALUES (3, 5.2,   TIMESTAMP '2026-05-21 09:00:00');

-- ocorrencia_id => 1, 2, 3, 4
INSERT INTO ocorrencia (regiao_id, tipo, severidade, fonte_deteccao, status, data_deteccao)
  VALUES (2, 'QUEIMADA',     'ALTA',    'SENSOR',   'EM_ATENDIMENTO', DATE '2026-05-20');
INSERT INTO ocorrencia (regiao_id, tipo, severidade, fonte_deteccao, status, data_deteccao)
  VALUES (3, 'ENCHENTE',     'CRITICA', 'SATELITE', 'ABERTA',         DATE '2026-05-21');
INSERT INTO ocorrencia (regiao_id, tipo, severidade, fonte_deteccao, status, data_deteccao)
  VALUES (1, 'DESLIZAMENTO', 'MEDIA',   'MANUAL',   'ABERTA',         DATE '2026-05-19');
INSERT INTO ocorrencia (regiao_id, tipo, severidade, fonte_deteccao, status, data_deteccao)
  VALUES (2, 'QUEIMADA',     'BAIXA',   'SATELITE', 'ENCERRADA',      DATE '2026-04-30');

INSERT INTO alerta (ocorrencia_id, nivel, mensagem, canal, data_emissao)
  VALUES (1, 'ALTO',    'Foco de queimada detectado por sensor de fumaca', 'APP',   DATE '2026-05-20');
INSERT INTO alerta (ocorrencia_id, nivel, mensagem, canal, data_emissao)
  VALUES (2, 'CRITICO', 'Nivel de agua acima do limite de seguranca',      'SMS',   DATE '2026-05-21');
INSERT INTO alerta (ocorrencia_id, nivel, mensagem, canal, data_emissao)
  VALUES (2, 'CRITICO', 'Evacuacao recomendada na Baixada do Itajai',      'EMAIL', DATE '2026-05-21');

-- equipe_id => 1, 2, 3
INSERT INTO equipe (nome, tipo, contato)
  VALUES ('CB-GO 3o Batalhao',  'BOMBEIROS',    '193');
INSERT INTO equipe (nome, tipo, contato)
  VALUES ('Defesa Civil SC',    'DEFESA_CIVIL', '199');
INSERT INTO equipe (nome, tipo, contato)
  VALUES ('Voluntarios Itajai', 'VOLUNTARIOS',  'contato@vol.org');

INSERT INTO atendimento (ocorrencia_id, equipe_id, data_inicio, data_fim, situacao)
  VALUES (1, 1, DATE '2026-05-20', NULL, 'EM_ANDAMENTO');
INSERT INTO atendimento (ocorrencia_id, equipe_id, data_inicio, data_fim, situacao)
  VALUES (2, 2, DATE '2026-05-21', NULL, 'EM_ANDAMENTO');
INSERT INTO atendimento (ocorrencia_id, equipe_id, data_inicio, data_fim, situacao)
  VALUES (2, 3, DATE '2026-05-21', NULL, 'EM_ANDAMENTO');

COMMIT;


-- ---------------------------------------------------------------------
-- 3. CONSULTAS DE SIMULACAO DE USO
-- ---------------------------------------------------------------------

-- Q1) Ocorrencias em aberto, com a regiao e a severidade
SELECT r.nome AS regiao, o.tipo, o.severidade, o.status
FROM   ocorrencia o
JOIN   regiao r ON r.regiao_id = o.regiao_id
WHERE  o.status = 'ABERTA'
ORDER  BY o.severidade;

-- Q2) Leituras de sensores de FUMACA acima do limite de 200 ppm
--     (gatilho tipico para abrir uma ocorrencia de queimada)
SELECT r.nome AS regiao, l.valor, l.data_hora
FROM   leitura l
JOIN   sensor s ON s.sensor_id = l.sensor_id
JOIN   regiao r ON r.regiao_id = s.regiao_id
WHERE  s.tipo = 'FUMACA'
AND    l.valor > 200
ORDER  BY l.valor DESC;

-- Q3) Total de alertas emitidos por nivel
SELECT nivel, COUNT(*) AS total_alertas
FROM   alerta
GROUP  BY nivel
ORDER  BY total_alertas DESC;

-- Q4) Equipes atualmente em atendimento (sem data de fim)
SELECT e.nome AS equipe, e.tipo, o.tipo AS ocorrencia, r.nome AS regiao
FROM   atendimento a
JOIN   equipe e     ON e.equipe_id     = a.equipe_id
JOIN   ocorrencia o ON o.ocorrencia_id = a.ocorrencia_id
JOIN   regiao r     ON r.regiao_id     = o.regiao_id
WHERE  a.data_fim IS NULL;

-- Q5) Ranking de regioes por numero de ocorrencias registradas
SELECT r.nome AS regiao, COUNT(o.ocorrencia_id) AS qtd_ocorrencias
FROM   regiao r
LEFT   JOIN ocorrencia o ON o.regiao_id = r.regiao_id
GROUP  BY r.nome
ORDER  BY qtd_ocorrencias DESC;
