/*
 * StructuresFunctions.c
 *
 *  Created on: 25 Apr 2016
 *      Author: liamberrisford
 */
#include <stdio.h>
#include <stdlib.h>
#include "Structures.h"

//The function will return a point of the type struct playerInformation , which is populated with the relevant data
struct playerInformation *make(int playerIndex, struct Point playerPosition) {
	//Creates a pointer to a struct playerInformation
	struct playerInformation *newItem;

	//The needed amount of memory is then allocated for the struct.
	newItem = (struct playerInformation*)malloc(sizeof(struct playerInformation));
	newItem->playerIndex = playerIndex;
	newItem->position = playerPosition;
	newItem->next = NULL;

	//The pointer to the new struct is then returned so that it can be added to a linked list.
	return newItem;
}

//Method that will create a playerInformation struct for a new player with their playerIndex and desired starting position. ptr is the head of the list that it is to be added to (listHead).
void addtoList(struct playerInformation *ptr, int playerIndex, struct Point playerPosition) {
	//It will iterate through the list until the end of the list is found, and then will create and add a new node to the end.
	if(ptr->next) {
		addtoList(ptr->next, playerIndex, playerPosition);
	} else {
		ptr->next = make(playerIndex, playerPosition);
	}
}

//Will return the struct whose playerIndex was passed so that the player info can be read or changed.
struct playerInformation *findPlayerInformation(struct playerInformation *head, int playerIndex) {
	struct playerInformation *ptr;
	//Iterates through the linked list whose head was passed until the desired index is found.
		for(ptr = head; ptr != NULL; ptr = ptr->next) {
			if(ptr->playerIndex == playerIndex) {
				return ptr;
			}
		}
	return NULL;
}


