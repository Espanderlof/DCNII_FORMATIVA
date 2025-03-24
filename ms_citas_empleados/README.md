# APP_SPRING

## Limpiar proyecto e instalar
mvn clean install

## Iniciar aplicacion
mvn spring-boot:run

## Construir imagen
docker build -t ms_citas_empleados .

## Levantar contendor docker
docker run -d -p 8084:8084 --name ms_citas_empleados ms_citas_empleados

## Levantar contenedor docker
sudo docker start ms_citas_empleados

## Ver logs del contenedor
docker logs ms_citas_empleados

## Ver todos los contenedores
docker ps -a

## Detener contenedor
docker stop ms_mhc

## eliminar contenedor
docker rm ms_citas_empleados

## DockerHub
1. Crear repo en https://hub.docker.com/
2. Primero, asegúrate de estar logueado en Docker Hub desde tu terminal docker login
3. Identifica tu imagen local. Puedes ver tus imágenes locales con: docker images
4. Etiqueta tu imagen local con el formato requerido por Docker Hub: Por ejemplo, si tu imagen local se llama "backend-app:1.0", los comandos serían: docker tag ms_citas_empleados espanderlof/dcn2_for_citas_empleados:latest 
5. Para subir la imagen al repositorio seria: docker push espanderlof/dcn2_for_citas_empleados:latest

## Cargar contenedor en Azure MV
1. Traer el contenedor: docker pull espanderlof/dcn2_for_citas_empleados:latest
2. Levantar el contenedor: docker run -d -p 8084:8084 --name ms_citas_empleados espanderlof/dcn2_for_citas_empleados:latest