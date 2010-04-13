SENSORBOARD=mts300
COMPONENT=UBSenseAppC

BUILD_EXTRA_DEPS=UBSenseMsg.class

UBSenseMsg.class: UBSenseMsg.java
	javac UBSenseMsg.java
	
UBSenseMsg.java:
	mig java -target=null -java-classname=UBSenseMsg UBSense.h UBSenseMsg -o $@
	
java-clean:
	rm *.class *.java
	
clean: java-clean

include $(MAKERULES)
