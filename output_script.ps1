for ($i=1; $i -le 10; $i++) {
    Write-Host "Printing to file output_$($i).txt"
    javac SkipListSet.java SkipListTestHarness.java
    java SkipListTestHarness.java > outputs/output_$i.txt
    del *.class
    Write-Host "Finished printing to file output_$($i).txt"
  }
