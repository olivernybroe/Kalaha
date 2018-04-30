package dk.lost_world.Client;

import dk.lost_world.Client.MinMax.Node;
import dk.lost_world.Game.State;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static dk.lost_world.Client.MinMax.maxScore;
import static dk.lost_world.Client.MinMax.minScore;

public class AlphaBeta implements Client {
    private int depth;
    private Stack<Integer> tempMoves = new Stack<>();

    public AlphaBeta(int depth) {
        this.depth = depth;
    }

    @Override
    public int takeTurn(State state) {
        System.out.println(state);
        if(state.isExtraTurn()) {
            try {
                TimeUnit.SECONDS.sleep(1); // Add sleep so it doesn't explode terminal
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("AlphaBeta chooses: "+tempMoves.peek());
            return tempMoves.pop();
        }
        else {
            System.out.println("AlphaBeta is calculating...");
            tempMoves.empty();
        }
        long startTime = System.nanoTime();

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
        tempMoves.push(minMaxResult.pitChosen);

        while (minMaxResult.parent != null && minMaxResult.parent.pitChosen != 0) {
            minMaxResult = minMaxResult.parent;
            tempMoves.push(minMaxResult.pitChosen);
        }

        long endTime = System.nanoTime();

        long duration = ((endTime - startTime) / 1000000) / 1000;
        System.out.println("Took "+duration+"s to calculate");

        System.out.println("AlphaBeta chooses: "+minMaxResult.pitChosen);
        return tempMoves.pop();
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
                    temp = alphaBeta(child, depth -1,a, b, false);
                }
                else {
                    temp = alphaBeta(child, depth -1, a, b, true);
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
