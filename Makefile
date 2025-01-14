all: package up

package:
	./mvnw clean package -DskipTests

up:
	docker-compose up --build

postgres:
	docker-compose up postgres

down:
	docker-compose down

clean:
	docker-compose down -v
