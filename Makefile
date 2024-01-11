postgres:
	docker run -d --rm \
    	--name db \
    	-p 5432:5432 \
    	-e POSTGRES_PASSWORD=root \
    	-e POSTGRES_DB=steam_critic \
    	postgres:16.1-alpine3.19
