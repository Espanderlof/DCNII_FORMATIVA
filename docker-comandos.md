# Listar todos los contenedores en ejecución
docker ps -a

# Detener todos los contenedores en ejecución
docker stop $(docker ps -q)

# Eliminar todos los contenedores detenidos
docker rm $(docker ps -a -q)