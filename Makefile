postgres:
	docker run -d --rm \
    	--name postgres_db \
    	-p 5432:5432 \
    	-e POSTGRES_PASSWORD=root \
    	-e POSTGRES_INITDB_ARGS="--encoding=UTF-8 --lc-collate=en_US.UTF-8 --lc-ctype=en_US.UTF-8" \
    	-e POSTGRES_DB=steam_prices \
    	postgres:16.1-alpine3.19

up:
	docker compose up --build

down:
	docker compose down
