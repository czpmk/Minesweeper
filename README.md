# Minesweeper
Project created with the support of JetBrains Academy as an introduction to Kotlin.
- created by: Michał Czapiewski
- email: czapiewskimk@gmail.com
- github: https://github.com/czpmk
# Purpose
The program is a recreation of a popular game called Minesweeper. 
The game starts with the 9x9 minefield and specified by a user number of mines. 
The player can either expose a cell or mark it if she/he thinks it hides a mine. 
The game ends when all the mines have been marked, all save places exposed, or 
when the user steps on a mine.
# Available actions:
x and y - coordinates, range 1 to 9.
- x y mine - place a marker 
- x y free - expose a cell
# Meaning of the symbols:
- "*" - marked cell
- "." - unexposed cell
- "/" - exposed, save cell
- "X" - exposed mine
- "1" or another number in a range from 1 to 8 - exposed cell, the number specifies 
how many mines there are around it.