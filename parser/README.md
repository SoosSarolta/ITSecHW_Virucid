# CAFF Parser

This folder contains the CAFF parser of the team Virucid.

## Files

The folder contains the following files:
- C++ header files (.h)

- C++ source files (.cpp)

- Docker related files for testing
    - `docker-compose.yml`: Describes a simple environment for testing
    - `Dockerfile`: Describes what tools must be installed in the container
    - `run.sh`: Runs a permanent loop to be able to connect to the container

- Makefile: Contains compiler, container and testing related commands
    - Compiler related commands
        - `make compile` will simply compile the parser from the source (this works only from a unix environment with g++ installed, e.g. from inside the container)
    - Container related commands:
        - `make build` will build a container which has Valgrind and afl installed for testing.
        - `make start` will start the container
        - `make stop` will stop the container
        - `make restart` will restart the container
        - `make sh` will open a shell inside the container
    - Testing related commands
        - `make memcheck` will build and run the parser with Valgrind to check for memory leaks
        - `make fuzz` will build and fuzz the parser with afl
- `compile.sh`: compiles the parser (to be able to compile from a Windows host)

## Test Results

Running the parser with Valgrind showed that the code does not contain any memory related errors:

<p align="center">
  <img src="https://github.com/SoosSarolta/ITSecHW_Virucid/blob/main/images/valgrind_output.png">
  <br>
  Output of Valgrind with input file 3.caff
</p>
