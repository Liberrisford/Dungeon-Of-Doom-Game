#include "Structures.h"
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>

#include "FunctionsAndVariables.h"
#include "Structures.h"

/**
 * Source file that is used for the saving of the state of the game to a file tempMap.txt so that the game can be restarted when
 * needed.
 */

//Used to save the details of the map to the file pointer that has been passed to it.
void saveMap(FILE *f) {
		int i,j;
		for(i = 0; i < mapHeight; i++) {
			for(j = 0; j < mapLength; j++) {
				fprintf(f, "%c", map[i][j]);
			}
			fprintf(f, "\n");
		}
}

//Used to save the state of the player to the file pointer that was passed.
void savePlayerStates(FILE *f) {
	fprintf(f, "%d\n", goldForWin);
	fprintf(f, "%d\n", playerIndexNumbers);
	int i;
	for(i = 1; i <= playerIndexNumbers; i++) {
		struct playerInformation *info = findPlayerInformation(listHead, i);
		fprintf(f, "%d\n", info->position.x);
		fprintf(f, "%d\n", info->position.y);
		fprintf(f, "%d\n", info->onGold);
		fprintf(f, "%d\n", info->tileToRestore);
		fprintf(f, "%d\n", info->collectedGold);
		fprintf(f, "%d\n", info->restoreTile);
	}
}


//Function that is called to save the current state of the game to tempMap.txt
void populateFile() {
	//Tries to open the tempMap.txt if it can't then it will tell the user and return.
	FILE *f;
	f = fopen("maps/tempMap.txt", "w");
	if(f == NULL) {
		printf("The tempMap file could not be found, please ensure the file is present.");
		return;
	}

	//Calls the two methods responsible for saving the relevant component of the game.
	savePlayerStates(f);
	saveMap(f);

	fclose(f);
}

//This code is not use but it is what was wrote when trying to implement the restoring of the game functionality.
void restorePlayerStates(FILE *f) {
	fscanf(f, "%d", &goldForWin);
	fscanf(f, "%d", &oldPlayers);

	int i;
	for(i = 0; i < oldPlayers; i++) {
		struct Point position;
		fscanf(f, "%d", &position.x);
		fscanf(f, "%d", &position.y);
		addtoList(listHead, i, position);
		struct playerInformation *info = findPlayerInformation(listHead, i);

		//State of where the player was on the map and the statistics.
		fscanf(f, "%d", &info->onGold);
		fscanf(f, "%c", &info->tileToRestore);
		fscanf(f, "%d", &info->collectedGold);
		fscanf(f, "%d", &info->restoreTile);

	}
}





