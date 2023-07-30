term:
	@javac *.java
	@java SkipListTestHarness.java
	@del *.class

file:
	@javac *.java
	@java SkipListTestHarness.java > output.txt
	@del *.class

files:
	@echo printing output to output_1.txt
	@javac *.java
	@java SkipListTestHarness.java > outputs/output_1.txt
	@del *.class
	@echo Finished printing output to output_1.txt
	@echo printing output to output_2.txt
	@javac *.java
	@java SkipListTestHarness.java > outputs/output_2.txt
	@del *.class
	@echo Finished printing output to output_2.txt
	@echo printing output to output_3.txt
	@javac *.java
	@java SkipListTestHarness.java > outputs/output_3.txt
	@del *.class
	@echo Finished printing output to output_3.txt
	@echo printing output to output_4.txt
	@javac *.java
	@java SkipListTestHarness.java > outputs/output_4.txt
	@del *.class
	@echo Finished printing output to output_4.txt
	@echo printing output to output_5.txt
	@javac *.java
	@java SkipListTestHarness.java > outputs/output_5.txt
	@del *.class
	@echo Finished printing output to output_5.txt
	@echo printing output to output_6.txt
	@javac *.java
	@java SkipListTestHarness.java > outputs/output_6.txt
	@del *.class
	@echo Finished printing output to output_6.txt
	@echo printing output to output_7.txt
	@javac *.java
	@java SkipListTestHarness.java > outputs/output_7.txt
	@del *.class
	@echo Finished printing output to output_7.txt
	@echo printing output to output_8.txt
	@javac *.java
	@java SkipListTestHarness.java > outputs/output_8.txt
	@del *.class
	@echo Finished printing output to output_8.txt
	@echo printing output to output_9.txt
	@javac *.java
	@java SkipListTestHarness.java > outputs/output_9.txt
	@del *.class
	@echo Finished printing output to output_9.txt
	@echo printing output to output_10.txt
	@javac *.java
	@java SkipListTestHarness.java > outputs/output_10.txt
	@del *.class
	@echo Finished printing output to output_10.txt

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
