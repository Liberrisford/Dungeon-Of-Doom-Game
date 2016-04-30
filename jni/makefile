CLASS_PATH = ../

vpath %.class $(CLASS_PATH)

all: libDoDJNI.so

libDoDJNI.so : DoDJNI.o StructuresFunctions.o Map.o MultiplayerLogic.o SavedMap.o
	gcc -Wall -shared -o $@ $^
	
DoDJNI.o : DoDJNI.c DoDJNI.h FunctionsAndVariables.h
	gcc -fPIC -I /usr/lib/jvm/java-1.6.0-openjdk-1.6.0.38.x86_64/include -I /usr/lib/jvm/java-1.6.0-openjdk-1.6.0.38.x86_64/include/linux -c $< -o $@	
	
Map.o : Map.c FunctionsAndVariables.h
	gcc -Wall -fPIC -c -o $@ $<

MultiplayerLogic.o : MultiplayerLogic.c FunctionsAndVariables.h Structures.h 
	gcc -Wall -fPIC -c -o $@ $<
	
StructuresFunctions.o : StructuresFunctions.c Structures.h  
	gcc -Wall -fPIC -c -o $@ $<

SavedMap.o : SavedMap.c Structures.h FunctionsAndVariables.h
	gcc -Wall -fPIC -c -o $@ $<
	
DoDJNI.h : DoDJNI.class
	javah -classpath $(CLASS_PATH) $*
	
clean :
	rm DoDJNI.h DoDJNI.o libDoDJNI.so StructuresFunctions.o MultiplayerLogic.o Map.o SavedMap.o
	
	
	
