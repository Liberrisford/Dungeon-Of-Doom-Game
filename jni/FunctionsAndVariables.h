#ifndef SRC_JNI_FUNCTIONSANDVARIABLES_H_
#define SRC_JNI_FUNCTIONSANDVARIABLES_H_
/**
 * Header file for all of the values and functions that are need for another source file. that are used across the source files.
 */

//Gloabls that are used by multiple source files.
int playerWon;
int winningPlayer;
int notEnoughGold;
int goldForWin;
int playerIndexNumbers;

//All of the functions and details about the map.
void readMap();
char **map;
int mapLength;
int mapHeight;
void printMap();
int goldOnMap();
char * winCondition;


//The commands that are from multiplayerLogic.
void createNewPlayer(int playerIndex);

//The protocol commands with the functionality for the relevant command.
char * movePlayer(int playerIndex, char direction);
char * pickup(int playerIndex);
char * look(int playerIndex);
char * hello(int playerIndex);
char * quitGame();
void createGlobals();


//Used in writing for the tempMap.txt file.
void populateFile();

//Used in reading for the tempMap.txt file.
void restorePlayerStates(FILE *f);
int restoreGame;
int oldPlayers;


#endif /* SRC_JNI_FUNCTIONSANDVARIABLES_H_ */
