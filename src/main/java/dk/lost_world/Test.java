package dk.lost_world;

import dk.lost_world.Client.Console;
import dk.lost_world.Game.Kalaha;

public class Test {

    public static void main(String... args) throws Exception {
        Kalaha kalaha = new Kalaha(
            new Console("A"),
            new Console("B")
        );
        kalaha.start();
        System.out.println(kalaha.getWinner());
    }
}
