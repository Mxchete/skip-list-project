FILE_NUM = 1
OUTPUT_FILE = .\outputs\output_$(FILE_NUM).txt

term:
	@javac *.java
	@java SkipListTestHarness.java
	@del *.class

file:
	@javac *.java
	# @while [ Test-Path -Path $(OUTPUT_FILE) -PathType Leaf] ; 
	# do
	# 	$(FILE_NUM) = $(FILE_NUM) + 1 
	# done
	# @echo "printing to file at $(OUTPUT_FILE)"
	@java SkipListTestHarness.java > outputs/output.txt
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
