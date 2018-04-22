package dk.lost_world.Game;

import dk.lost_world.Client.Client;

public class Kalaha {

    private final int seeds;
    private Client playerA;
    private Client playerB;
    private State winnerState;

    public Kalaha(Client playerA, Client playerB, int seeds) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.seeds = seeds;
    }

    protected State initializeState() {
        return new State(this.seeds, playerA, playerB);
    }

    public void start() {

        State currentState = initializeState();

        while (currentState.isOngoing()) {
            currentState = currentState.takeTurn();
        }

        this.winnerState = currentState;
    }

    public Client getWinner() throws Exception {
        return this.winnerState.getWinner();
    }
}
