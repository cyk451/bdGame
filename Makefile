JAR:=./desktop/build/libs/desktop-1.0.jar
SOURCES:=$(shell find ./core -name '*.java')


all: $(JAR)

$(JAR): $(SOURCES)
	./gradlew desktop:dist

run: $(JAR)
	java -jar $(JAR)
