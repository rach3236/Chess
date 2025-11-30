package datamodel;

import java.util.ArrayList;

public record Games(ArrayList<GameData> games) {
    public int size() {
        return games.size();
    }
}
