#include <stdio.h>
#include <stdlib.h>

#include "FunctionsAndVariables.h"
#include "Structures.h"
/**
 * Source file that contains all of the functions that are used to change the state of the game based on a user command.
 */

//Works out whether or not whether any of the current players still in the game can still win the game when a player leaves.
int canWin(int goldOnMap) {
	struct playerInformation *info = findPlayerInformation(listHead, 0);
	for(info = info->next; info != NULL; info = info->next) {
		if(((info->collectedGold) + goldOnMap) >= goldForWin && (info->inGame == true)) {
			return 1;
		}
	}
	return -1;
}

//Calclates the look window for the player based on their current position.
char * look(int playerIndex) {
	struct playerInformation *info = findPlayerInformation(listHead, playerIndex);
	struct Point playerPosition = info->position;

	playerPosition.x -= 2;
	playerPosition.y -= 2;
	//This memory is freed in DoDJNI.c when it is copied into another pointer and sent to the java code.
	char * reply = (char *)malloc(sizeof(char)*36);


	int i,j,k, posX, posY;
	//Iterates through the surronsing area of the map, if it is not a real position (outside of the map) then a # is used instead.
	for(i = 0, k = 0; i < 5; i++, k++) {
		for(j = 0; j < 5; j++, k++) {
			posX = playerPosition.x + i;
			posY = playerPosition.y + j;
			if(((posX >= 0) && (posX < mapHeight)) && ((posY >= 0) && (posY < mapLength))) {
				reply[k] = map[posX][posY];
			} else {
				reply[k] = '#';

			}
		}
		reply[k] = '\n';
	}

	//All the corners have an X.
	reply[0] = 'X';
	reply[4] = 'X';
	reply[24] = 'X';
	reply[28] = 'X';

	return reply;
}

//Used to work out whether or not the player is on a gold or not and will make the following relevant changes to the game state if they are.
char * pickup(int playerIndex) {
	struct playerInformation *info = findPlayerInformation(listHead, playerIndex);
	if(info->onGold == true) {
		//Used to not restore the gold when the player next moves.
		info->onGold = false;
		info->restoreTile = false;
		info->collectedGold++;
		//This memory is freed within DoDJNI.c
		char *reply = malloc(sizeof(char) * 100);
		snprintf(reply, sizeof(reply), "SUCCESS, GOLD COINS: %d", info->collectedGold);
		return reply;
	} else {
		return "FAIL";
	}

}

//Take the current position of a player and will update their playerInformation struct and will show them on the servers map.
void showPlayerOnMap(struct Point playerPosition, int playerIndex) {
	struct playerInformation *info = findPlayerInformation(listHead, playerIndex);
	if((map[playerPosition.x][playerPosition.y] == 'E') || (map[playerPosition.x][playerPosition.y] == 'G')) {
		info->restoreTile = true;
		if(map[playerPosition.x][playerPosition.y] == 'E') {
			info->tileToRestore = 'E';
		} else {
			info->tileToRestore = 'G';
		}
	}
	info->position = playerPosition;
	map[playerPosition.x][playerPosition.y] = 'P';
}

//Removes a player from the map and restores the needed tile.
void removePlayerFromMap(struct Point oldPlayerPosition, int playerIndex) {
	struct playerInformation *info = findPlayerInformation(listHead, playerIndex);
	if(info->restoreTile == true) {
		map[oldPlayerPosition.x][oldPlayerPosition.y] = info->tileToRestore;
		info->restoreTile = false;
	} else {
		map[oldPlayerPosition.x][oldPlayerPosition.y] = '.';
	}
}

//Will move the player from their current position based on their input.
char *  movePlayer(int playerIndex, char direction) {
	//Checks to see if someone can still win, and whether anyone has won the game.
	if((canWin(goldOnMap())) == -1) {
		return "Game Over! Thank you for playing! No one can win the game, the server will now shut down!";
	} else if(playerWon) {
		return "Game Over! Thank you for playing! The game was won by player " + winningPlayer;
	}

	//This function was used when the tempMap.txt was trying to implemented. (it does populate the file correctly).
	populateFile();

	//Works out how their position changes based on their command
	struct playerInformation *info = findPlayerInformation(listHead, playerIndex);
	struct Point oldPlayerPosition = info->position;
	if(direction == 'N') {
		info->position.x -= 1;
	} else if(direction == 'E') {
		info->position.y += 1;
	} else if(direction == 'S') {
		info->position.x += 1;
	} else if(direction == 'W') {
		info->position.y -= 1;
	}

	//Whether the move is allowed in the game, and if any tiles need to be restored when they move.
	if((map[info->position.x][info->position.y] != '#') && (map[info->position.x][info->position.y] != 'P')){
		if(map[info->position.x][info->position.y] == 'G') {
			info->onGold = true;
		}
		//Checks to see if the player has won, by moving on the exit tile with the needed amount of gold.
		if((map[info->position.x][info->position.y] == 'E') && (info->collectedGold >= goldForWin)) {
			removePlayerFromMap(oldPlayerPosition, playerIndex);
			playerWon = true;
			winningPlayer = playerIndex;
			return "Congratulations!!! \n You have escaped the Dungeon of Dooom!!!!!! \n Thank you for playing!";
		} else {
		//Otherwise it will update the position and inform the user.
			removePlayerFromMap(oldPlayerPosition, playerIndex);
			showPlayerOnMap(info->position, playerIndex);
			return "SUCCESS";
		}
	} else {
		//Used if the move was not valid.
		info->position = oldPlayerPosition;
		return "FAIL";
	}
}

//Sets up all of the data that is needed for a new player to take part in the game.
void createNewPlayer(int playerIndex) {
	struct playerInformation *info = findPlayerInformation(listHead, playerIndex);
	struct Point playerPosition;
	int counter = 0;
	info->inGame = true;
	do {
		playerPosition.x = rand() % mapHeight;
		playerPosition.y = rand() % mapLength;
		counter++;
	} while((counter < (mapLength*mapHeight)) && ((map[playerPosition.x][playerPosition.y] == '#') || (map[playerPosition.x][playerPosition.y] == 'P')));
	map[playerPosition.x][playerPosition.y] = 'P';
	info->position = playerPosition;
}


//Sets the data in the server to say that the player is no longer playing the game.
char * quitGame(int playerIndex) {
	struct playerInformation *info = findPlayerInformation(listHead, playerIndex);
	info->inGame = false;
	removePlayerFromMap(info->position, playerIndex);
	if(canWin(goldOnMap()) == 1) {
		return "Game Over!";
	}
	playerWon = true;
	return "Game Over! Thank you for playing! No one can win the game, the server will now shut down!";
}


//Function that is used to tell the user how much gold they have.
char * hello(int playerIndex) {
	struct playerInformation *info = findPlayerInformation(listHead, playerIndex);
	char * reply = malloc(sizeof(char) * 100);
	snprintf(reply, sizeof(reply), "GOLD: %d", (goldForWin - (info->collectedGold)));
	return reply;
}


