# AtomLayerCounter

Utility for molecular dynamic.

It defines atom layers and defines its size. Capable of consuming prety large .xyz files (only).

How to use: 

For Unix
   1) Put jar file in xyz files directory and start it with command "jar -jar directory_path/AtomLayerCounter-0.1.jar". 
       Default settings will be used(z axis, no additional detail, min distance between layers = 1)
   2) by using "config_unix.json" file: modify parameters (inputPath, outputPath are required, other may be omitted),
       and start it with same command
       
   For Windows
  1) Put jar file in xyz files directory and start it by double click on jar. 
       Default settings will be used(z axis, no additional detail, min distance between layers = 1)
  2) by using "config_win.json" file: modify parameters (inputPath, outputPath are required, other may be omitted),
       and start it by double click on jar