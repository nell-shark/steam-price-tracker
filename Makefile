postgres:
	docker run -d --rm \
    	--name postgres \
    	-p 5432:5432 \
    	-e POSTGRES_PASSWORD=root \
    	-e POSTGRES_INITDB_ARGS="--encoding=UTF-8 --lc-collate=en_US.UTF-8 --lc-ctype=en_US.UTF-8" \
    	-e POSTGRES_DB=steam_price_tracker \
    	postgres:16.1-alpine3.19

pgadmin:
	docker run -d --rm \
        --name pgadmin \
		-p 5050:80 \
		-e PGADMIN_DEFAULT_EMAIL=admin@gmail.com \
		-e PGADMIN_DEFAULT_PASSWORD=root \
		dpage/pgadmin4

redis:
	docker run -d --rm \
		--name redis \
		-p 6379:6379 \
		redis:7.2.4-alpine3.19

up:
	docker compose up --build

down:
	docker compose down
