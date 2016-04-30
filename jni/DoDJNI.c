#include <jni.h>
#include <stdio.h>
#include "DoDJNI.h"
#include <string.h>
#include <stdlib.h>
#include <time.h>

#include "FunctionsAndVariables.h"
#include "Structures.h"
/**
 * This is the main file for the C code, it is where the C function calls are made. The j version of data types are converted
 * and the relevant functions called, and then their returns are turn to the j verion and returned to the java code that
 * called the fucntion.
 */


//Functions that is called when a command is being passed, it deals with all of the protocol commands.
JNIEXPORT jstring JNICALL
Java_DoDJNI_parseCommand(JNIEnv *env, jobject thisObj, jstring command, jint playerIndex) {
	//Convert j version of string and int to c version.
	const char *str= (*env)->GetStringUTFChars(env,command,0);
	int playerIndexC = (int) playerIndex;

	char *reply;

	//If statement that works out what the command is.
	if(strcmp(str,"HELLO") == 0) {
		//temp is used so that the pointer value can be copied and then freed to avoid a memory leak.
		char * temp = hello(playerIndexC);
		reply = strdup(temp);
		free(temp);

	} else if(strcmp(str,"PICKUP") == 0) {
		reply = pickup(playerIndexC);

	} else if(strcmp(str,"LOOK") == 0) {
		char * temp = look(playerIndexC);
		reply = strdup(temp);
		free(temp);

	} else if(strcmp(str,"QUIT") == 0) {
		quitGame(playerIndexC);
		reply = quitGame(playerIndexC);

	} else if(strcmp(str, "MOVE N") == 0) {
		reply = movePlayer(playerIndexC, 'N');

	} else if(strcmp(str, "MOVE E") == 0) {
		reply = movePlayer(playerIndexC, 'E');

	} else if(strcmp(str, "MOVE S") == 0) {
		reply = movePlayer(playerIndexC, 'S');

	} else if(strcmp(str, "MOVE W") == 0) {
		reply = movePlayer(playerIndexC, 'W');

	} else {
		reply = "FAIL";
	}

	//Release the chars used for jstring conversion to avoid a memory leak, and convert the char * to a jstring.
	(*env)->ReleaseStringUTFChars(env, command, str);
	jstring result = (*env)->NewStringUTF(env, reply);
	return result;
}

//Method that is called once when the server starts to decide the map name.
JNIEXPORT void JNICALL
Java_DoDJNI_setMap(JNIEnv *env, jobject thisObj, jstring mapName) {
	const char *map = (*env)->GetStringUTFChars(env, mapName, 0);

	//Creates the initial globals of the program
	srand(time(NULL));
	playerIndexNumbers = 0;
	struct Point position1; position1.x = 0; position1.y = 0;
	listHead = make(0, position1);
	playerWon = false;
	readMap(map);
	(*env)->ReleaseStringUTFChars(env, mapName, map);
}

//This code is called whenever a player joins the game.
JNIEXPORT jint JNICALL
Java_DoDJNI_addPlayer(JNIEnv *env, jobject thisObj) {
	playerIndexNumbers++;
	//The below commented out code is part of the code that was going to be used in the restoring of a game, however
	//this functionality was not able to be operational in time.
		/*if((restoreGame == 1)) {
		printf("The if statement in DODJNI.c is running\n");
		struct playerInformation *info = findPlayerInformation(listHead, playerIndexNumbers);
		//printf("x: %d, y: %d", info->position.x, info->position.y);
		return playerIndexNumbers;
		} else {*/
	struct Point playerPosition;
	addtoList(listHead, playerIndexNumbers, playerPosition);
	createNewPlayer(playerIndexNumbers);
	return playerIndexNumbers;
}

JNIEXPORT jint JNICALL
Java_DoDJNI_playerWon(JNIEnv *env, jobject thisObj) {
	return playerWon;
}



