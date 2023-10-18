run: compile
	java -Xmx2g -Xms2g -cp bin MenuPrincipal

compile: clean
	mkdir -p bin
	javac -d bin *.java

clean:
	rm -rf bin