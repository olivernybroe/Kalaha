package dk.lost_world.Client;

import dk.lost_world.Game.State;

import java.util.Scanner;

public class Console implements Client {

    private String name;

    public Console(String name) {
        this.name = name;
    }

    @Override
    public int takeTurn(State state) {
        Scanner sc = new Scanner(System.in);
        System.out.println(state);
        System.out.println("Player ["+this.name+"] turn (1-6): ");
        while (!sc.hasNextInt()) sc.next();
        return sc.nextInt();
    }

    @Override
    public String toString() {
        return "Console{" +
            "name='" + name + '\'' +
            '}';
    }
}
