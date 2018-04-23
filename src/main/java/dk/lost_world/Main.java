package dk.lost_world;

import dk.lost_world.Client.*;
import dk.lost_world.Game.Kalaha;
import org.apache.commons.cli.*;

public class Main {

    public static void main(String... args) throws Exception {
        Options options = new Options();
        options.addOption("o","opponent", true, "Choose which opponent to fight [MinMax, Random, Console, AlphaBeta]");
        options.addOption("s", "seeds", true, "Choose amount of seeds the game starts with.");
        options.addOption("r","rules", false, "Get all the rules printed out.");
        options.addOption("h", "help",  false, "Prints the help information.");
        options.addOption("d", "depth", true, "Choose the depth of the search algorithm. DEFAULT=8");
        options.addOption("n", "name", true, "Choose your player name.");

        System.out.println("Remember you can use -h to get help on how to start the program.");


        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            // parse the command line arguments
            cmd = parser.parse( options, args );
        }
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            System.exit(1);
        }

        if(cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java -jar kalaha.jar", options);
            System.exit(0);
        }

        if (cmd.hasOption("rules")) {
            printRules();
            System.exit(0);
        }

        Client opponent = getOpponent(cmd);
        int seeds = getSeeds(cmd);

        Kalaha kalaha = new Kalaha(
            opponent,
            new Console(cmd.getOptionValue("name", "A")),
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
        System.out.println("2. Each player controls the six houses and their seeds on the player's side of the board. The player's score is the number of seeds in the store to their right.");
        System.out.println("3. Players take turns sowing their seeds. On a turn, the player removes all seeds from one of the houses under their control. Moving counter-clockwise, the player drops one seed in each house in turn, including the player's own store but not their opponent's.");
        System.out.println("4. When one player no longer has any seeds in any of their houses, the game ends. The other player moves all remaining seeds to their store, and the player with the most seeds in their store wins.");
    }

    private static int getSeeds(CommandLine cmd) {
        String value = cmd.getOptionValue("seeds", "4");

        int seeds = 4;
        try {
            seeds = Integer.parseInt(value);
            System.out.println("["+seeds+"] seeds has ben chosen.");
        }
        catch (NumberFormatException e) {
            System.out.println("Couldn't parse the input for seed, has to be an int, setting to default 4.");
        }
        return seeds;
    }

    private static int getDepth(CommandLine cmd) {
        String value = cmd.getOptionValue("depth", "8");

        int depth = 8;
        try {
            depth = Integer.parseInt(value);
            System.out.println("["+depth+"] depths has ben chosen.");
        }
        catch (NumberFormatException e) {
            System.out.println("Couldn't parse the input for depth, has to be an int, setting to default 8.");
        }
        return depth;
    }

    private static Client getOpponent(CommandLine cmd) {
        Client opponent = null;
        switch (cmd.getOptionValue("opponent", "default")) {
            case "MinMax":
                opponent = new MinMax(getDepth(cmd));
                break;
            case "Random":
                opponent = new RandomAI();
                break;
            case "Console":
                opponent = new Console("B");
                break;
            case "AlphaBeta":
                opponent = new AlphaBeta(getDepth(cmd));
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
