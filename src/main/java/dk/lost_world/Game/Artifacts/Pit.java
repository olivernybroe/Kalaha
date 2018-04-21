package dk.lost_world.Game.Artifacts;

import dk.lost_world.Client.Client;

public abstract class Pit implements Cloneable{
    protected int seed = 0;
    protected Client owner;

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int removeAllSeeds() {
        int seed = this.getSeed();
        this.setSeed(0);
        return seed;
    }

    public void incrementSeed(int incrementWith) {
        this.setSeed(this.getSeed()+incrementWith);
    }

    public void incrementSeed() {
        this.incrementSeed(1);
    }

    public Client getOwner() {
        return owner;
    }

    public boolean isEmpty() {
        return this.getSeed() == 0;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
