run:
	javac *.java
	java -XX:MaxDirectMemorySize=1g -Xmx2g MenuPrincipal
clean:
	rm -f *.class