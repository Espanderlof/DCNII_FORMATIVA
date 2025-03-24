# APP_SPRING

## Limpiar proyecto e instalar
mvn clean install

## Iniciar aplicacion
mvn spring-boot:run

## Construir imagen
docker build -t ms_mhc .

## Levantar contendor docker
docker run -d -p 8083:8083 --name ms_mhc ms_mhc

## Levantar contenedor docker
sudo docker start ms_mhc

## Ver logs del contenedor
docker logs ms_mhc

## Ver todos los contenedores
docker ps -a

## Detener contenedor
docker stop ms_mhc

## eliminar contenedor
docker rm ms_mhc

## DockerHub
1. Crear repo en https://hub.docker.com/
2. Primero, asegúrate de estar logueado en Docker Hub desde tu terminal docker login
3. Identifica tu imagen local. Puedes ver tus imágenes locales con: docker images
4. Etiqueta tu imagen local con el formato requerido por Docker Hub: Por ejemplo, si tu imagen local se llama "backend-app:1.0", los comandos serían: docker tag ms_mhc espanderlof/dcn2_for_mhc:latest 
5. Para subir la imagen al repositorio seria: docker push espanderlof/dcn2_for_mhc:latest

## Cargar contenedor en Azure MV
1. Traer el contenedor: docker pull espanderlof/dcn2_for_mhc:latest
2. Levantar el contenedor: docker run -d -p 8083:8083 --name ms_mhc espanderlof/dcn2_for_mhc:latest