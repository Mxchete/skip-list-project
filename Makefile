term:
	@javac *.java
	@java SkipListTestHarness.java

file:
	@javac *.java
	@java SkipListTestHarness.java > output.txt

clean:
	@rm -f *.class

help:
	@echo "term:		run project and print output to terminal"
	@echo "file:		run project and print output to output.txt"
	@echo "clean:		remove javac generated class files"
