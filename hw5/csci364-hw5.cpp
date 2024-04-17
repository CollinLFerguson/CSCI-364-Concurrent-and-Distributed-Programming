#include <iostream>
#include <string>
#include <random>
#include <omp.h>

#include <stdio.h> //used to get int command line argument.

#define __MATRIXSIZE 2

const int seed=1;
std::mt19937 engine(seed);
std::uniform_int_distribution<int> dist(0,100);

void allocateMatrix(int**& matrix, int matrixSize, int initZero){
///testValue should be either a 0 or 1.
	matrix = new int*[matrixSize];
	for(int x = 0; x<matrixSize; x++){
		matrix[x] = new int[matrixSize];
		for(int i = 0; i<matrixSize; i++){
#ifdef TEST
			matrix[x][i] = testValue;
#else
			matrix[x][i] = dist(engine);
#endif
		}
	}
}

void allocateMatrix(int**& matrix, int matrixSize){
	allocateMatrix(matrix, matrixSize, 1);
}

void deallocateMatrix(int**& matrix, int matrixSize){
	for(int x = matrixSize; x>=0; x--){
		delete[] matrix[x];
	}
	delete[] matrix;
	matrix = NULL;
}

void printMatrix(int** matrix, int matrixSize){
	for(int x = 0; x<matrixSize; x++){
		for(int i = 0; i<matrixSize; i++){
			printf ("%3d ", matrix[x][i]);
		}
		std::cout << std::endl;
	}
	std::cout << std::endl;
}

void multiplyMatrix(int** matrix1, int** matrix2, int** productMatrix, int matrixSize)
{
	double start = omp_get_wtime();
	//#pragma omp parallel{
		//#pragma omp for{

			for(int i = 0; i < matrixSize; i++){
				for(int j = 0; j < matrixSize; j++){
					productMatrix[i][j] = 0;

					for(int x = 0; x < matrixSize; x++){
						productMatrix[i][j] += matrix1[i][x] * matrix2[x][j]; //hope this works.
					}
				}
			}
		//}
	//}
	double end = omp_get_wtime();
	std::cout << end - start << std::endl;
}

int main(int argc, char *argv[]) {
	if(argc == 1){
		std::cout << "Usage: ./csci364-hw5 <matrix size> [<print flag>]";
		return 0;
	}

	bool printResults = false;
	int matrixSize = atoi(argv[1]);

	if(argc > 2 && (*argv[2] == 'Y'|| *argv[2] == 'y' || *argv[2] == '1')){
		printResults = true;
	}

	int **matrix1;
	int **matrix2;
	int **productMatrix;

	allocateMatrix(matrix1, matrixSize, 1);

#ifdef TEST
	allocateMatrix(matrix2, matrixSize, 0);
#else
	allocateMatrix(matrix2, matrixSize, 1);
#endif


	allocateMatrix(matrix2, matrixSize, 0);
	allocateMatrix(productMatrix, matrixSize, 0);

	if(printResults){
		printMatrix(matrix1, matrixSize);
		printMatrix(matrix2, matrixSize);
	}

	multiplyMatrix(matrix1, matrix2, productMatrix, matrixSize);

	printMatrix(productMatrix, matrixSize);

	deallocateMatrix(matrix1, matrixSize);
	deallocateMatrix(matrix2, matrixSize);
	deallocateMatrix(productMatrix, matrixSize);

	return 0;
}
