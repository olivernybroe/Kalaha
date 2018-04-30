
## Kalaha (With AI, school project)

This is the traditional Kalaha game, where you can play against an AlphaBeta, MinMax, Random or another person.

### Usage
- Download the latest release
- Run the program with `java -jar kalaha.jar` and apply the arguments you wish to run with, `-h` for help. (eg. `java -jar kalaha.jar -o AlphaBeta -s 4 -d 6`)
- If this is your first time playing run `java -jar kalaha.jar --rules`, to get the rules of the game.
- Play and have fun!

### Arguments

The supported arguments for the game are
```
usage: java -jar kalaha.jar
 -d,--depth <arg>      Choose the depth of the search algorithm. DEFAULT=6
 -h,--help             Prints the help information.
 -n,--name <arg>       Choose your player name.
 -o,--opponent <arg>   Choose which opponent to fight [MinMax, Random,
                       Console, AlphaBeta]
 -r,--rules            Get all the rules printed out.
 -s,--seeds <arg>      Choose amount of seeds the game starts with.
```