CXX = g++
CXXFLAGS = -std=c++17 -g -O2 

.PHONY: all
all: csci364-hw5
	@echo The hello program has been compiled 

# implicit rule: create x from x.cpp
.cpp:
	module load gcc; \
	g++ -std=c++17 -g -O2 csci364-hw5.cpp -o csci364-hw5
test:
	g++ -std=c++17 -DTEST -g -O2 csci364-hw5.cpp -o csci364-hw5TEST
.PHONY: clean
clean:
	$(RM) -r csci364-hw5
	