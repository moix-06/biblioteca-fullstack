# Sistema de Biblioteca - Docker Compose

Orquestacion completa del sistema de biblioteca basado en microservicios Spring Boot, usando Docker Compose.

## Arquitectura

```
                    +-----------------+
   Cliente  ----->  |  api-gateway    | :8080
                    +-----------------+
                            |
                            v
                    +-----------------+
                    |  eureka-server  | :8761
                    +-----------------+
                            |
        +-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+
        v       v       v       v       v       v       v       v       v       v       v
    ms-       ms-     ms-      ms-     ms-     ms-     ms-     ms-     ms-     ms-     ms-
    usuarios  autores editor.  categ.  libros  ejemp.  sucurs. prest.  reserv. multas  resenas
    :8081     :8082   :8083    :8084   :8085   :8086   :8087   :8088   :8089   :8090   :8091
        \       \       \      /       /       /       /       /       /       /       /
         \       \       \    /       /       /       /       /       /       /       /
          +----------------+     +----------------------------------+
          |     MySQL      |     |        (ms_usuarios, ...         |
          |     :3306      |<--->|         ms_resenas)              |
          +----------------+     +----------------------------------+
```

- **MySQL** administra 11 bases de datos, una por microservicio
- **Eureka Server** actua como service discovery
- **API Gateway** enruta las peticiones externas a los microservicios
- Los microservicios se registran en Eureka y se comunican entre si via Feign clients

## Requisitos

- Docker 24+
- Docker Compose v2 (incluido con Docker Desktop)
- Al menos 4 GB de RAM libre
- Conexion a internet (descarga de imagenes base y dependencias Maven)

## Estructura de archivos

```
biblioteca-fullstack/
├── docker-compose.yml          # Orquestacion principal
├── .env                        # Variables de entorno (credenciales, URLs)
├── README-DOCKER.md            # Este archivo
├── mysql/
│   └── init.sql                # Crea las 11 bases de datos
├── eureka-server/              # Service discovery
├── api-gateway/                # API Gateway
└── ms-*/                       # 11 microservicios (cada uno con Dockerfile)
```

## Comandos principales

### Levantar el stack completo

```bash
# Construir las imagenes (primera vez o tras cambios en el codigo)
docker compose build

# Levantar en segundo plano
docker compose up -d

# Ver el estado de los servicios
docker compose ps

# Ver logs en tiempo real
docker compose logs -f
```

### Reconstruir y reiniciar un servicio especifico

```bash
# Solo un microservicio
docker compose up -d --build ms-usuarios

# Ver logs de un servicio
docker compose logs -f ms-libros
```

### Apagar el stack

```bash
# Apagar sin borrar volumenes (conserva datos de MySQL)
docker compose down

# Apagar y borrar TODO (incluyendo datos de MySQL)
docker compose down -v
```

### Inspeccion y debug

```bash
# Entrar a un contenedor
docker compose exec mysql bash
docker compose exec ms-usuarios sh

# Conectarse a MySQL desde el host
docker compose exec mysql mysql -u biblioteca -p
# Password: biblioteca_pw (definido en .env)

# Ver bases de datos
docker compose exec mysql mysql -u biblioteca -pbiblioteca_pw -e "SHOW DATABASES;"
```

## URLs utiles

| Servicio | URL |
|----------|-----|
| API Gateway | http://localhost:8080/api/... |
| Eureka Dashboard | http://localhost:8761 |
| Swagger ms-usuarios | http://localhost:8081/swagger-ui.html |
| Swagger ms-autores | http://localhost:8082/swagger-ui.html |
| Swagger ms-editoriales | http://localhost:8083/swagger-ui.html |
| Swagger ms-categorias | http://localhost:8084/swagger-ui.html |
| Swagger ms-libros | http://localhost:8085/swagger-ui.html |
| Swagger ms-ejemplares | http://localhost:8086/swagger-ui.html |
| Swagger ms-sucursales | http://localhost:8087/swagger-ui.html |
| Swagger ms-prestamos | http://localhost:8088/swagger-ui.html |
| Swagger ms-reservas | http://localhost:8089/swagger-ui.html |
| Swagger ms-multas | http://localhost:8090/swagger-ui.html |
| Swagger ms-resenas | http://localhost:8091/swagger-ui.html |
| MySQL (desde host) | `localhost:3306` usuario `biblioteca` password `biblioteca_pw` |

> Las URLs de Swagger tambien son accesibles a traves del gateway en `http://localhost:8080/api/<servicio>/swagger-ui.html` solo si el MS expone la ruta bajo `/api`.

## Ejemplos de uso

### Listar autores a traves del gateway

```bash
curl http://localhost:8080/api/autores
```

### Crear un autor

```bash
curl -X POST http://localhost:8080/api/autores \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Gabriel","apellido":"Garcia Marquez","nacionalidad":"Colombiana"}'
```

### Verificar el estado de los servicios en Eureka

Abrir http://localhost:8761 en el navegador. Todos los microservicios deberian aparecer como `UP`.

## Orden de arranque

El `docker-compose.yml` define dependencias explicitas con healthchecks para garantizar este orden:

1. **MySQL** espera a estar saludable
2. **Eureka Server** arranca
3. **11 microservicios** arrancan en paralelo (cada uno espera MySQL + Eureka)
4. **API Gateway** arranca (espera Eureka)

La primera vez puede tardar varios minutos (descarga de dependencias Maven). Builds subsiguientes son mucho mas rapidos gracias al cache de capas Docker.

## Personalizacion

Edita el archivo `.env` para cambiar:

- Credenciales de MySQL (`MYSQL_ROOT_PASSWORD`, `MYSQL_PASSWORD`)
- Parametros de negocio (`PRESTAMOS_DURACION_DIAS`, `MULTAS_MONTO_POR_DIA`, etc.)

> El archivo `.env` no debe subirse al repositorio. Agregalo a `.gitignore`.

## Reinicializacion completa (borrar todo y empezar de cero)

```bash
docker compose down -v
docker compose build --no-cache
docker compose up -d
```

Esto borra el volumen de MySQL (con todas las bases) y reconstruye las imagenes desde cero. Las tablas se crearan automaticamente al arrancar cada microservicio (Hibernate `ddl-auto: update`).

## Troubleshooting

### Un servicio no arranca

```bash
# Ver logs especificos
docker compose logs ms-usuarios

# Si dice "Connection refused" a MySQL, esperar unos segundos
# y volver a intentar (puede ser que MySQL no este listo)
```

### Eureka no muestra los microservicios

1. Verificar que el microservicio este corriendo: `docker compose ps`
2. Ver sus logs: `docker compose logs ms-usuarios`
3. Confirmar que la variable `EUREKA_URL` apunte a `http://eureka-server:8761/eureka`

### MySQL no arranca / init.sql no se ejecuta

Si el volumen `biblioteca-mysql-data` ya existia con datos, el `init.sql` NO se vuelve a ejecutar. Para forzar la reinicializacion:

```bash
docker compose down -v
docker compose up -d mysql
```

### "Bind for 0.0.0.0:8080 failed: port is already allocated"

Otro proceso en el host esta usando el puerto. Detenlo o cambia el mapeo en `docker-compose.yml` (por ejemplo `"8081:8080"` para el gateway).

### Healthcheck falla constantemente

Si un servicio reporta `unhealthy` repetidamente, prueba a aumentar el `start_period` en `docker-compose.yml` (especialmente en maquinas lentas).
