package top.jalva.tictactoe.enums;

import java.util.Arrays;
import java.util.Random;

public enum Player {	
	EMPTY,X,O;
	
	final static Random random = new Random();

	public Player getOpponent() {
		if(this == Player.EMPTY) {
			throw new IllegalArgumentException("Illegal player");
		}

		if(this == Player.X) {
			return Player.O;
		}
		
		return Player.X;
	}
	
	public String asString() {
		if(this == EMPTY) {
			return " ";
		}
		
		return name();
	}
	
	public static Player getRamdomPlayer() {
		return Arrays.asList(Player.X, Player.O).get(random.nextInt(2));
	}
}
