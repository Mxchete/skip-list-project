term:
	@javac *.java
	@java SkipListTestHarness.java
	@del *.class

file:
	@javac *.java
	@java SkipListTestHarness.java > output.txt
	@del *.class

other:
	@javac *.java
	@java SkipTest.java
	@del *.class

compile:
	@javac *.java

clean:
	@del *.class

help:
	@echo term:		run project and print output to terminal
	@echo file:		run project and print output to output.txt
	@echo other:		run other test harness
	@echo compile:	run compile only
	@echo clean:		remove javac generated class files
