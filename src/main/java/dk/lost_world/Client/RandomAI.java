package dk.lost_world.Client;

import dk.lost_world.Game.State;

import java.util.Random;

public class RandomAI implements Client {
    @Override
    public int takeTurn(State state) {
        int choice = new Random().nextInt(6)+1;
        System.out.println("Random AI chooses: "+choice);
        return choice;
    }

}
