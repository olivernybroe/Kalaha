package dk.lost_world;

import dk.lost_world.Client.Console;
import dk.lost_world.Client.MinMax;
import dk.lost_world.Client.RandomAI;
import dk.lost_world.Game.Kalaha;

public class Test {

    public static void main(String... args) throws Exception {
        Kalaha kalaha = new Kalaha(
            new MinMax(),
            new Console("A")
        );
        kalaha.start();
        System.out.println("The winner is: " + kalaha.getWinner());
    }
}
