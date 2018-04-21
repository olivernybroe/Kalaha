package dk.lost_world.Game;

import dk.lost_world.Client.Client;
import dk.lost_world.Game.Artifacts.Pit;
import dk.lost_world.Game.Artifacts.House;
import dk.lost_world.Game.Artifacts.Store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Following the rules from https://en.wikipedia.org/wiki/Kalah
 *
 * 1. At the beginning of the game, four seeds are placed in each house. This is the traditional method.
 *
 * 2. Each player controls the six houses and their seeds on the player's side of the board.
 * The player's score is the number of seeds in the store to their right.
 *
 * 3. Players take turns sowing their seeds. On a turn, the player removes all seeds from one of the houses
 * under their control. Moving counter-clockwise, the player drops one seed in each house in turn,
 * including the player's own store but not their opponent's.
 *
 * 4. If the last sown seed lands in an empty house owned by the player, and the opposite house contains
 * seeds, both the last seed and the opposite seeds are captured and placed into the player's store.
 *
 * 5. If the last sown seed lands in the player's store, the player gets an additional move.
 * There is no limit on the number of moves a player can make in their turn.
 *
 * 6. When one player no longer has any seeds in any of their houses, the game ends.
 * The other player moves all remaining seeds to their store,
 * and the player with the most seeds in their store wins.
 */
public class State {
    private List<Pit> pits;

    private Client currentPlayer;
    private Client playerA;
    private Client playerB;
    private Client firstPlayer;

    public State(int seeds, Client playerA, Client playerB) {
        this.pits = Arrays.asList(
            new House(seeds, playerA),
            new House(seeds, playerA),
            new House(seeds, playerA),
            new House(seeds, playerA),
            new House(seeds, playerA),
            new House(seeds, playerA),
                new Store(playerA),
            new House(seeds, playerB),
            new House(seeds, playerB),
            new House(seeds, playerB),
            new House(seeds, playerB),
            new House(seeds, playerB),
            new House(seeds, playerB),
                new Store(playerB)
        );

        this.playerA = playerA;
        this.playerB = playerB;

        this.currentPlayer = playerA;
        this.firstPlayer = currentPlayer;
    }

    public State(State original) {
        this.pits = cloneList(original.pits);
        this.playerA = original.playerA;
        this.playerB = original.playerB;
        this.currentPlayer = original.currentPlayer;
        this.firstPlayer = original.firstPlayer;
    }

    public static List<Pit> cloneList(List<Pit> list) {
        List<Pit> clone = new ArrayList<>(list.size());
        for (Pit item : list) clone.add((Pit) item.clone());
        return clone;
    }

    public boolean isDone() {
        return this.pits.stream()
            .filter(pit ->
                pit instanceof House &&
                    pit.getOwner().equals(this.currentPlayer)
            ).allMatch(Pit::isEmpty);
    }


    public boolean playerBHasWon() {
        return playerHasWon(playerB);
    }

    public boolean playerAHasWon() {
        return playerHasWon(playerA);
    }

    public boolean currentPlayerHasWon() {
        return this.playerHasWon(this.currentPlayer);
    }

    public boolean isDraw() {
        return this.getCurrentPlayersStore().getSeed() == this.getOpponentsStore().getSeed();
    }

    public Client getWinner() throws Exception {
        if(this.isOngoing()) {
            throw new Exception("Game still running");
        }
        if(isDraw()) {
            return null;
        }
        return this.playerAHasWon() ? this.playerA : this.playerB;
    }

    private boolean playerHasWon(Client client) {
        return client.equals(this.currentPlayer) ?
            this.getCurrentPlayersStore().getSeed() > this.getOpponentsStore().getSeed() :
            this.getCurrentPlayersStore().getSeed() < this.getOpponentsStore().getSeed();
    }


    public boolean isOngoing() {
        return !this.isDone();
    }

    public State takeTurn() {
        return this.takeTurn(
            this.currentPlayer.takeTurn(this)
        );
    }

    public boolean isValidNum(int pitNum) {
        // Needs to be a number between 1-6.
        if(pitNum > 6 || pitNum < 1) {
            return false;
        }

        Pit chosenPit = this.pits.get(
            this.firstPlayer.equals(this.currentPlayer) ?
                pitNum-1 :
                pitNum+6
        );

        // The chosen pit cannot be empty.
        return !chosenPit.isEmpty();
    }

    public State takeTurn(int pitNum) {
        // Make player retake the turn if choosing an invalid number.
        if(!isValidNum(pitNum)) {
            return this.takeTurn();
        }

        Pit currentPit = this.pits.get(
            this.firstPlayer.equals(this.currentPlayer) ?
                pitNum-1 :
                pitNum+6
        );

        // Players take turns sowing their seeds.
        // On a turn, the player removes all seeds from one of the houses under their control.
        // Moving counter-clockwise, the player drops one seed in each house in turn,
        // including the player's own store but not their opponent's.
        int seeds = currentPit.removeAllSeeds();
        while (seeds != 0) {
            currentPit = getNextPit(currentPit);
            currentPit.incrementSeed();
            --seeds;
        }

        // When one player no longer has any seeds in any of their houses, the game ends.
        if(this.isDone()) {
            // The other player moves all remaining seeds to their store.
            this.takeOpponentSeed();
            return this;
        }

        // If the last sown seed lands in the player's store, the player gets an additional move.
        // There is no limit on the number of moves a player can make in their turn.
        if(currentPit.equals(getCurrentPlayersStore())) {
            //return this.takeTurn(); // TODO: enable extra turn again.
        }

        this.changeTurn();
        return this;
    }

    private void changeTurn() {
        this.currentPlayer = this.playerA.equals(this.currentPlayer) ? this.playerB : this.playerA;
    }

    private Pit getNextPit(Pit currentPit) {
        Pit nextPit = this.pits.get((this.pits.indexOf(currentPit)+1)%this.pits.size());
        if(nextPit.equals(this.getOpponentsStore())) {
            nextPit = this.getNextPit(nextPit);
        }
        return nextPit;
    }

    public Store getCurrentPlayersStore() {
        return getPlayerStore(this.currentPlayer);
    }

    public Store getOpponentsStore() {
        return this.getCurrentPlayersStore().equals(getPlayerAStore()) ? getPlayerBStore() : getPlayerAStore();
    }

    public Store getPlayerAStore() {
        return this.getPlayerStore(this.playerA);
    }

    public Store getPlayerBStore() {
        return this.getPlayerStore(this.playerB);
    }

    public Client getCurrentPlayer() {
        return currentPlayer;
    }

    public Client getPlayerA() {
        return playerA;
    }

    public Client getPlayerB() {
        return playerB;
    }

    private Store getPlayerStore(Client client) {
        return (Store) this.pits.stream().filter(pit -> pit instanceof Store && pit.getOwner().equals(client)).findFirst().get();
    }

    private void takeOpponentSeed() {
        this.pits.stream()
            .filter(pit ->
                !pit.getOwner().equals(this.currentPlayer) &&
                pit instanceof House
            ).forEach(pit -> getCurrentPlayersStore().incrementSeed(pit.removeAllSeeds()));
    }

    @Override
    public String toString() {
        String format = "{%2d}{%2d}{%2d}{%2d}{%2d}{%2d}\n{%2d}                {%2d}\n{%2d}{%2d}{%2d}{%2d}{%2d}{%2d}";

        if(firstPlayer.equals(currentPlayer)) {
            return String.format(
                format,
                this.pits.get(12).getSeed(),
                this.pits.get(11).getSeed(),
                this.pits.get(10).getSeed(),
                this.pits.get(9).getSeed(),
                this.pits.get(8).getSeed(),
                this.pits.get(7).getSeed(),
                this.pits.get(13).getSeed(),
                this.pits.get(6).getSeed(),
                this.pits.get(0).getSeed(),
                this.pits.get(1).getSeed(),
                this.pits.get(2).getSeed(),
                this.pits.get(3).getSeed(),
                this.pits.get(4).getSeed(),
                this.pits.get(5).getSeed()
            );
        }
        else {
            return String.format(
                format,
                this.pits.get(5).getSeed(),
                this.pits.get(4).getSeed(),
                this.pits.get(3).getSeed(),
                this.pits.get(2).getSeed(),
                this.pits.get(1).getSeed(),
                this.pits.get(0).getSeed(),
                this.pits.get(6).getSeed(),
                this.pits.get(13).getSeed(),
                this.pits.get(7).getSeed(),
                this.pits.get(8).getSeed(),
                this.pits.get(9).getSeed(),
                this.pits.get(10).getSeed(),
                this.pits.get(11).getSeed(),
                this.pits.get(12).getSeed()
            );
        }
    }
}
