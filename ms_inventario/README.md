# APP_SPRING

## Limpiar proyecto e instalar
mvn clean install

## Iniciar aplicacion
mvn spring-boot:run

## Construir imagen
docker build -t ms_inventario .

## Levantar contendor docker primera vez
docker run -d -p 8081:8081 --name ms_inventario ms_inventario

## Levantar contenedor docker
sudo docker start ms_inventario

## Ver logs del contenedor
docker logs ms_inventario

## Ver todos los contenedores
docker ps -a

## Detener contenedor
docker stop ms_inventario

## eliminar contenedor
docker rm ms_inventario

## DockerHub
1. Crear repo en https://hub.docker.com/
2. Primero, asegúrate de estar logueado en Docker Hub desde tu terminal docker login
3. Identifica tu imagen local. Puedes ver tus imágenes locales con: docker images
4. Etiqueta tu imagen local con el formato requerido por Docker Hub: Por ejemplo, si tu imagen local se llama "backend-app:1.0", los comandos serían: docker tag ms_inventario espanderlof/dcn2_for_inventario:latest 
5. Para subir la imagen al repositorio seria: docker push espanderlof/dcn2_for_inventario:latest

## Cargar contenedor en Azure MV
1. Traer el contenedor: docker pull espanderlof/dcn2_for_inventario:latest
2. Levantar el contenedor: docker run -d -p 8081:8081 --name ms_inventario espanderlof/dcn2_for_inventario:latest