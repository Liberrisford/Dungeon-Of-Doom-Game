/*
 * Map
 *
 *  Created on: 25 Apr 2016
 *      Author: liamberrisford
 */
#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
#include <string.h>

#include "FunctionsAndVariables.h"

/**
 * Source file that deals with all of the claculation and reading in of the map from an external file.
 */

//Function used to extract the int for the win condition from the game file.
void setWin(char *win) {
	char *p = win;
	while(*p){
		if(isdigit(*p)) {
			long val = strtol(p, &p, 10);
			goldForWin = val;
		} else {
			p++;
		}
	}
}

//Calculates the amount of gold that is currently left on the map (it does not include gold a player is on).
int goldOnMap() {
	int i, j, gold = 0;
	for(i = 0; i < mapHeight; i++) {
		for(j = 0; j < mapLength; j++) {
			if(map[i][j] == 'G') {
				gold++;
			}
		}
	}
	return gold;
}

//Prints out to the terminal (server side) the current state of the map.
void printMap() {
	int i,j;
	for(j = 0; j < mapHeight; j++) {
			for(i = 0; i < mapLength; i++) {
			printf("%c", map[j][i]);
		}
		printf("\n");
	}
}

//Takes a 2d char array and puts it into a global version to be used by all source files
void populateMap(char map[100][100], char **finalMap, int length, int lines) {
	int j;
	int i;
	for(j = 0; j < lines; j++) {
		for(i = 0; i < length; i++) {
			finalMap[j][i] = map[j][i];
		}
	}
}

//The main function used to read in a map from the a file.
void readMap(const char *fileNamePart) {

	FILE *in;
	char fileName[200];
	strcat(fileName, "maps/");
	strcat(fileName, fileNamePart);

	//Trys to open the filename that was given, if it can then it will open the default map.
	in = fopen(fileName, "r");
	if(in == NULL || fileNamePart[0] == '\0') {
		if(fileNamePart[0] == '\0') {
			printf("The default map will be loaded.\n");
		} else {
			printf("The file that was given could not be found, the default map will be used instead.\n");
		}
		in = fopen("maps/example_map.txt", "r");

	}

	char tempMap[100][100];
	int ch;

	//This code was wrote for the tempMap.txt, as when restoring it would not have the same layout, the code will never be
	//executed but this part of the code did work as intended.
	if(strcmp(fileNamePart, "tempMap.txt") != 0) {
		char line[1000];
		printf("The name of the map is: %s", fgets(line, sizeof(line), in));

		char *win;
		win = (char *)malloc(1000*sizeof(char));
		fgets(win, sizeof(win), in);
		setWin(win);
		winCondition = win;
		restoreGame = -1;
	} else {
		//restorePlayerStates(in);
		restoreGame = 1;
	}




	//Varibales used in the code to read in the map.
	int lines = 1;
	int lineLength = 0;
	int longestLineLength = 0;
	int i = 0;
	int j = 0;

	//Loops used to read the map until the end of the file is met (its assumed the map is always the final part of the file
	//as was given in the example_map.txt
	while((ch = fgetc(in)) != EOF) {
		if(ch == '\n') {
			//Working out the dimension of the map to be used in the global version of the map array.
			if(lineLength > longestLineLength) {
				longestLineLength = lineLength;
			}
			lines++;
			lineLength = 0;
			i++;
			j = 0;
		} else {
			tempMap[i][j] = ch;
			j++;
			lineLength++;
		}
	}

	//It is a double pointer as its a 2d array (an array of arrays)
	char **finalMap;
	finalMap = (char **)malloc(lines * sizeof(char *));

	//Allocates memory for each array within the memory.
	for(i = 0; i < lines; i++) {
	   finalMap[i] = (char *)malloc(longestLineLength * sizeof(char));
	}

	//Will the final map from the data in the temporary char array used above.
	populateMap(tempMap,finalMap, longestLineLength, lines);

	//Sets the global variables.
	map = finalMap;
	mapLength = longestLineLength;
	mapHeight = lines;

	fclose(in);
}






