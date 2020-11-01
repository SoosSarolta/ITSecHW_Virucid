.PHONY: compile build start stop restart sh memcheck fuzz

# compile code
compile:
	@echo Compiling ciff.cpp...
	g++ -c -o ciff.o ciff.cpp

	@echo Compiling caff.cpp...
	g++ -c -o caff.o caff.cpp

	@echo Compiling bitmap.cpp...
	g++ -c -o bitmap.o bitmap.cpp

	@echo Compiling gif.cpp...
	g++ -c -o gif.o gif.cpp

	@echo Compiling ciff_caff_parser.cpp...
	g++ -c -o ciff_caff_parser.o ciff_caff_parser.cpp

	@echo Linking modules to a single executable...
	g++ -o ciff_caff_parser ciff.o caff.o bitmap.o gif.o ciff_caff_parser.o

##################################### DOCKER RELATED ######################################

container=ciffcaff

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

# get a shell within the container
sh:
	docker-compose exec $(container) /bin/sh

##################################### TESTING RELATED #####################################

# check compiled parser for memory leaks with valgrind
memcheck:
	docker-compose exec -T $(container) /bin/sh -c "./compile.sh && valgrind -v --error-exitcode=1 --leak-check=full --show-leak-kinds=all ./ciff_caff_parser caff_files/3.caff"

# fuzz compiled parser with afl
fuzz:
	docker-compose exec -T $(container) /bin/sh -c "./compile.sh && afl-fuzz"
