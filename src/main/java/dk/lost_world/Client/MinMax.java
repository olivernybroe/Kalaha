package dk.lost_world.Client;

import dk.lost_world.Game.State;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinMax implements Client {
    @Override
    public int takeTurn(State state) {
        Node minMaxResult = minMax(
            new Node(state, this),
            6,
            true
        );

        while (minMaxResult.parent != null && minMaxResult.parent.pitChosen != 0) {
            minMaxResult = minMaxResult.parent;
        }
        System.out.println("MinMax chooses: "+minMaxResult.pitChosen);
        return minMaxResult.pitChosen;
    }

    private static class Node {
        State state;
        int pitChosen;
        Node parent;
        Client me;

        public Node() {
        }


        public Node(State state) {
            this.state = new State(state);
        }

        public Node(State state, Client me) {
            this(state);
            this.me = me;
        }

        public Node(State state, Node parent, int pitChosen) {
            this(state);
            this.state.takeTurn(pitChosen);
            this.parent = parent;
            this.pitChosen = pitChosen;
            this.me = parent.me;
        }

        public boolean isTerminal() {
            return this.state.isDone();
        }

        public List<Node> getChildren() {
            return Stream.of(1,2,3,4,5,6)
                .filter(pitNum -> state.isValidNum(pitNum))
                .map(pitNum -> new Node(state, this, pitNum))
                .collect(Collectors.toList());
        }

        public int getScore() {
            if(this.state.getPlayerA().equals(this.me)) {
                return this.state.getPlayerAStore().getSeed() - this.state.getPlayerBStore().getSeed();
            }
            return this.state.getPlayerBStore().getSeed() - this.state.getPlayerAStore().getSeed();
        }

        @Override
        public String toString() {
            int numOfParents = 0;
            Node temp = this.parent;
            while (temp != null) {
                numOfParents++;
                temp = temp.parent;
            }
            return "Node{" +
                "pitChosen=" + pitChosen +
                ", parentScore=" + (parent != null ? parent.getScore() : null) +
                ", parentPit=" + (parent != null ? parent.pitChosen : null) +
                ", parents=" + numOfParents +
                ", Score=" + getScore() +
                '}';
        }
    }

    public static Node minScore() {
        return new Node() {
            @Override
            public int getScore() {
                return Integer.MIN_VALUE;
            }

            @Override
            public String toString() {
                return "MINSCORE";
            }
        };
    }

    public static Node maxScore() {
        return new Node() {
            @Override
            public int getScore() {
                return Integer.MAX_VALUE;
            }
        };
    }

    private Node minMax(Node node, int depth, boolean maximizingPlayer) {
        if(depth == 0 || node.isTerminal()) {
            return node;
        }

        Node bestValue, v;
        if(maximizingPlayer) {

            bestValue = minScore();

            for (Node child : node.getChildren()) {
                v = minMax(child, depth -1, false);
                bestValue = Math.max(bestValue.getScore(), v.getScore()) == bestValue.getScore() ? bestValue : v;
            }
            return bestValue;
        }
        else {
            bestValue = maxScore();

            for (Node child : node.getChildren()) {
                v = minMax(child, depth -1, true);
                bestValue = Math.min(bestValue.getScore(), v.getScore()) == bestValue.getScore() ? bestValue : v;
            }
            return bestValue;
        }
    }

    @Override
    public String toString() {
        return "MinMax{}";
    }
}
