package dk.lost_world.Game;

import dk.lost_world.Client.Client;
import dk.lost_world.Game.Artifacts.Pit;
import dk.lost_world.Game.Artifacts.House;
import dk.lost_world.Game.Artifacts.Store;

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
    private Client opponent;
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

        this.currentPlayer = playerA;
        this.opponent = playerB;
        this.firstPlayer = currentPlayer;
    }

    public boolean isDone() {
        return this.pits.stream()
            .filter(pit ->
                pit instanceof House &&
                    pit.getOwner().equals(this.currentPlayer)
            ).allMatch(Pit::isEmpty);
    }

    public boolean opponentHasWon() {
        return this.playerHasWon(this.opponent);
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
        return this.currentPlayerHasWon() ? this.currentPlayer : this.opponent;
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
        int pitNum = this.currentPlayer.takeTurn(this);

        // Make player retake the turn if choosing an invalid number.
        if(pitNum > 6 || pitNum < 1) {
            return this.takeTurn();
        }

        Pit currentPit = this.pits.get(
            this.firstPlayer.equals(this.currentPlayer) ?
                pitNum-1 :
                pitNum+6
        );

        // Make player retake the turn if choosing an invalid pit.
        if(currentPit.isEmpty()) {
            return this.takeTurn();
        }


        int seeds = currentPit.removeAllSeeds();

        // Players take turns sowing their seeds.
        // On a turn, the player removes all seeds from one of the houses under their control.
        // Moving counter-clockwise, the player drops one seed in each house in turn,
        // including the player's own store but not their opponent's.
        while (seeds != 0) {
            currentPit = getNextPit(currentPit);
            currentPit.incrementSeed();
            --seeds;
        }

        // When one player no longer has any seeds in any of their houses, the game ends.
        if(this.isDone()) {
            // The other player moves all remaining seeds to their store.
            this.takeOpponentSeed();
        }

        // If the last sown seed lands in the player's store, the player gets an additional move.
        // There is no limit on the number of moves a player can make in their turn.
        if(currentPit.equals(getCurrentPlayersStore())) {
            return this.takeTurn();
        }

        this.changeTurn();
        return this;
    }

    private void changeTurn() {
        Client newPlayersTurn = this.opponent;
        this.opponent = this.currentPlayer;
        this.currentPlayer = newPlayersTurn;
    }

    private Pit getNextPit(Pit currentPit) {
        return this.pits.get((this.pits.indexOf(currentPit)+1)%this.pits.size());
    }

    private Store getCurrentPlayersStore() {
        return getPlayerStore(this.currentPlayer);
    }

    private Store getOpponentsStore() {
        return this.getPlayerStore(this.opponent);
    }

    private Store getPlayerStore(Client client) {
        return (Store) this.pits.stream().filter(pit -> pit instanceof Store && pit.getOwner().equals(client)).findFirst().get();
    }

    private void takeOpponentSeed() {
        this.pits.stream()
            .filter(pit ->
                pit.getOwner().equals(this.opponent) &&
                pit instanceof House
            ).forEach(pit -> getCurrentPlayersStore().incrementSeed(pit.removeAllSeeds()));
    }

    @Override
    public String toString() {
        String format = "{%2d}{%2d}{%2d}{%2d}{%2d}{%2d}\n{%2d}                {%2d}\n{%2d}{%2d}{%2d}{%2d}{%2d}{%2d}";

        if(firstPlayer.equals(currentPlayer)) {
            return String.format(
                format,
                this.pits.get(7).getSeed(),
                this.pits.get(8).getSeed(),
                this.pits.get(9).getSeed(),
                this.pits.get(10).getSeed(),
                this.pits.get(11).getSeed(),
                this.pits.get(12).getSeed(),
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
                this.pits.get(0).getSeed(),
                this.pits.get(1).getSeed(),
                this.pits.get(2).getSeed(),
                this.pits.get(3).getSeed(),
                this.pits.get(4).getSeed(),
                this.pits.get(5).getSeed(),
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
