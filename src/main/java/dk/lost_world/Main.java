package dk.lost_world;

import dk.lost_world.Client.Client;
import dk.lost_world.Client.Console;
import dk.lost_world.Client.MinMax;
import dk.lost_world.Client.RandomAI;
import dk.lost_world.Game.Kalaha;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String... args) throws Exception {
        Options options = new Options();
        options.addOption("opponent", true, "Choose which opponent to fight [MinMax, Random, Console]");
        options.addOption("seeds", true, "Choose amount of seeds the game starts with.");
        options.addOption("rules", "Get all the rules printed out.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        if (cmd.hasOption("rules")) {
            printRules();
            System.exit(0);
        }

        Client opponent = getOpponent(cmd);
        int seeds = getSeeds(cmd);

        Kalaha kalaha = new Kalaha(
            new Console("A"),
            opponent,
            seeds
        );
        kalaha.start();
        System.out.println("The winner is: " + kalaha.getWinner());
    }

    /** *
     *
     * 5. If the last sown seed lands in the player's store, the player gets an additional move.
     * There is no limit on the number of moves a player can make in their turn.
     */
    private static void printRules() {
        System.out.println("1. At the beginning of the game, x seeds are placed in each house.");
        System.out.println("2. Each player controls the six houses and their seeds on the player's side of the board.\n" +
            "The player's score is the number of seeds in the store to their right.");
        System.out.println("3. Players take turns sowing their seeds. On a turn, the player removes all seeds from one of the houses\n" +
            "under their control. Moving counter-clockwise, the player drops one seed in each house in turn,\n" +
            "including the player's own store but not their opponent's.");
        System.out.println("4. When one player no longer has any seeds in any of their houses, the game ends.\n" +
            "The other player moves all remaining seeds to their store,\n" +
            "and the player with the most seeds in their store wins.");

    }

    private static int getSeeds(CommandLine cmd) {
        String value = cmd.getOptionValue("seeds", "4");

        int seeds = 4;
        try {
            seeds = Integer.parseInt(value);
            System.out.println("["+seeds+"] seeds has ben chosen.");
        }
        catch (NumberFormatException e) {
            System.out.println("Couldn't parse the input for seed, has to be an int.");
        }
        return seeds;
    }

    private static Client getOpponent(CommandLine cmd) {
        Client opponent = null;
        switch (cmd.getOptionValue("opponent", "default")) {
            case "MinMax":
                opponent = new MinMax();
                break;
            case "Random":
                opponent = new RandomAI();
                break;
            case "Console":
                opponent = new Console("B");
                break;
        }
        if(opponent == null) {
            System.out.println("No opponent chosen, assuming 2 player mode.");
            opponent = new Console("B");
        }
        else {
            System.out.println("["+cmd.getOptionValue("opponent")+"] has been chosen as opponent.");
        }
        return opponent;
    }
}
