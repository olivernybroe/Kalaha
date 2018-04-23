package dk.lost_world.Client;

import dk.lost_world.Game.State;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinMax implements Client {
    int depth;

    public MinMax(int depth) {
        this.depth = depth;
    }

    @Override
    public int takeTurn(State state) {
        System.out.println(state);
        Node minMaxResult = minMax(
            new Node(
                state,
                this,
                state.getPlayerA().equals(this) ? state.getPlayerB() : state.getPlayerA()
            ),
            this.depth,
            true
        );

        while (minMaxResult.parent != null && minMaxResult.parent.pitChosen != 0) {
            minMaxResult = minMaxResult.parent;
        }
        System.out.println("MinMax chooses: "+minMaxResult.pitChosen);
        return minMaxResult.pitChosen;
    }

    public static class Node {
        State state;
        int pitChosen;
        Node parent;
        Client me;
        Client opponent;

        public Node() {
        }


        public Node(State state) {
            this.state = new State(state);
        }

        public Node(State state, Client me, Client opponent) {
            this(state);
            this.me = me;
            this.opponent = opponent;
        }

        public Node(State state, Node parent, int pitChosen) {
            this(state);
            this.parent = parent;
            this.pitChosen = pitChosen;
            this.me = parent.me;
            this.opponent = parent.opponent;
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

            @Override
            public String toString() {
                return "MAXSCORE";
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
                child.state.takeTurn(child.pitChosen);
                if(child.state.isExtraTurn()) {
                    v = minMax(child, depth -1, true);
                }
                else {
                    v = minMax(child, depth -1, false);
                }
                bestValue = Math.max(bestValue.getScore(), v.getScore()) == bestValue.getScore() ? bestValue : v;
            }
            return bestValue;
        }
        else {
            bestValue = maxScore();

            for (Node child : node.getChildren()) {
                child.state.takeTurn(child.pitChosen);
                if(child.state.isExtraTurn()) {
                    v = minMax(child, depth -1, false);
                }
                else {
                    v = minMax(child, depth -1, true);
                }
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
