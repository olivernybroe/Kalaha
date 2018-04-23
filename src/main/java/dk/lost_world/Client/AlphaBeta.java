package dk.lost_world.Client;

import dk.lost_world.Client.MinMax.Node;
import dk.lost_world.Game.State;

import static dk.lost_world.Client.MinMax.maxScore;
import static dk.lost_world.Client.MinMax.minScore;

public class AlphaBeta implements Client {
    int depth;

    public AlphaBeta(int depth) {
        this.depth = depth;
    }

    @Override
    public int takeTurn(State state) {
        System.out.println(state);
        Node minMaxResult = alphaBeta(
            new Node(
                state,
                this,
                state.getPlayerA().equals(this) ? state.getPlayerB() : state.getPlayerA()
            ),
            8,
            minScore(),
            maxScore(),
            true
        );

        while (minMaxResult.parent != null && minMaxResult.parent.pitChosen != 0) {
            minMaxResult = minMaxResult.parent;
        }
        System.out.println("AlphaBeta chooses: "+minMaxResult.pitChosen);
        return minMaxResult.pitChosen;
    }

    private Node alphaBeta(Node node, int depth, Node a, Node b, boolean maximizingPlayer) {
        if(depth == 0 || node.isTerminal()) {
            return node;
        }

        Node v, temp;
        if(maximizingPlayer) {
            v = minScore();
            for (Node child : node.getChildren()) {
                child.state.takeTurn(child.pitChosen);

                if(child.state.isExtraTurn()) {
                    temp = alphaBeta(child, depth -1,a, b, true);
                }
                else {
                    temp = alphaBeta(child, depth -1, a, b, false);
                }

                v = Math.max(v.getScore(), temp.getScore()) == temp.getScore() ? temp : v;
                a = Math.max(a.getScore(), v.getScore()) == v.getScore() ? v : a;
                if(b.getScore() <= a.getScore()) {
                    break;
                }
            }
            return v;
        }
        else {
            v = maxScore();
            for (Node child : node.getChildren()) {
                child.state.takeTurn(child.pitChosen);

                if(child.state.isExtraTurn()) {
                    temp = alphaBeta(child, depth -1,a, b, true);
                }
                else {
                    temp = alphaBeta(child, depth -1, a, b, false);
                }

                v = Math.min(v.getScore(), temp.getScore()) == temp.getScore() ? temp : v;
                v = Math.min(b.getScore(), v.getScore()) == v.getScore() ? v : b;
                if(b.getScore() <= a.getScore()) {
                    break;
                }
            }
            return v;
        }
    }

    @Override
    public String toString() {
        return "AlphaBeta{}";
    }
}
