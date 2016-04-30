#ifndef SRC_JNI_STRUCTURES_H_
#define SRC_JNI_STRUCTURES_H_
#include <stdio.h>
#include <stdlib.h>

#include "FunctionsAndVariables.h"

/**
 * Definitions of all of the structures that are used in the game.
 */

//used to make true and false easier.
typedef int boolean;
enum { false, true };

//Used to represent a 2D point of a player in the game.
struct Point {
	int x;
	int y;
};

//Struct that holds all of the information about the player in one central place.
struct playerInformation {
	int playerIndex;
	struct Point position;
	boolean restoreTile;
	boolean onGold;
	boolean inGame;
	char tileToRestore;
	int collectedGold;
	struct playerInformation *next;
};

//All of the functions that can be called which either create, modify or edit the structures in the program.
void addtoList(struct playerInformation *ptr, int playerIndex, struct Point position);
struct playerInformation *make(int playerIndex, struct Point position);
struct playerInformation *findPlayerInformation(struct playerInformation *head, int playerIndex);

//The head of the list that is constant and is used to find the needed playerInformation struct.
struct playerInformation *listHead;



#endif /* SRC_JNI_STRUCTURES_H_ */
