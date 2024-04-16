#include <iostream>
#include <string>
#include <random>

#define __MATRIXSIZE 2

const int seed=1;
std::mt19937 engine(seed);
std::uniform_int_distribution<int> dist(0,100);

void allocateMatrix(int**& matrix, int testValue){
///testValue should be either a 0 or 1.
	matrix = new int*[__MATRIXSIZE];
	for(int x = 0; x<__MATRIXSIZE; x++){
		matrix[x] = new int[__MATRIXSIZE];
		for(int i = 0; i<__MATRIXSIZE; i++){
#ifdef DTEST
			matrix[x][i] = testValue;
#else
			matrix[x][i] = dist(engine);
#endif
		}
	}
}

void allocateMatrix(int**& matrix){
	allocateMatrix(matrix,1);
}

void deallocateMatrix(int**& matrix){
	for(int x = __MATRIXSIZE; x>=0; x--){
		delete[] matrix[x];
	}
	delete[] matrix;
	matrix = NULL;
}

void printMatrix(int** matrix){
	for(int x = 0; x<__MATRIXSIZE; x++){
		for(int i = 0; i<__MATRIXSIZE; i++){
			std::cout<<matrix[x][i] << " ";
		}
		std::cout << std::endl;
	}
	std::cout << std::endl;
}

int main(int argc, char *argv[]) {
	bool printResults = false;

	if(argc > 1 && (*argv[0] == 'Y'|| *argv[0] == 'y' || *argv[0] == '1')){
		printResults = true;
	}

	int **matrix1;
	int **matrix2;
	allocateMatrix(matrix1,1);
	allocateMatrix(matrix2,1);

	if(printResults){
		printMatrix(matrix1);
		printMatrix(matrix2);
	}

	std::cout << "Bello Borld" << std::endl;
	std::cout << __MATRIXSIZE << std::endl;
	return 0;
}
