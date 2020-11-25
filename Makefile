.PHONY: build start stop restart frestart brestart logs fsh bsh dsb

frontend=frontend
backend=backend
db=mysql

# build container
build:
	docker-compose build

# start container
start:
	docker-compose up -d

# stop container
stop:
	docker-compose down

# restart container
restart: stop start

frestart:
	docker-compose restart $(frontend)

brestart:
	docker-compose restart $(backend)

# check console output
logs:
	docker-compose logs -f

# get a shell within the frontend container
fsh:
	docker-compose exec $(frontend) /bin/sh

# get a shell within the backend container
bsh:
	docker-compose exec $(backend) /bin/sh

# get a shell within mysql
mysql:
	docker-compose exec $(db) mysql -u root --password=Test1234 -D caffstore
