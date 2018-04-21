package dk.lost_world.Game.Artifacts;

import dk.lost_world.Client.Client;

public class Store extends Pit {
    public Store(Client owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Store{" +
            "seed=" + seed +
            ", owner=" + owner +
            '}';
    }
}
