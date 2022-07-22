package dvc.cabo.network;

import java.io.Serializable;

import dvc.cabo.logic.Game;

public class DataPacket implements Serializable {
    private static final long serialVersionUID = 89164L; // Just a 'random' UID.

    public final String info;
    public final Game game;

    public DataPacket(String info, Game game) {
	this.info = info;
	this.game = game;
    }

}
