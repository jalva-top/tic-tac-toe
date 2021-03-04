package top.jalva.tictactoe.enums;

import java.util.Random;

public enum GameLevel {
	LOW(4, "1"), MID(3, "2"), HIGH(1, "3");
	
	int core;
	String levelName;
	Random random = new Random();

	private GameLevel(int core, String levelName) {
		this.core = core;
		this.levelName = levelName;
	}
	
	public boolean takeIntoAccount() {
		int randomInt = random.nextInt(core);
		return randomInt == 0 || randomInt == 1;
	}

	public String getLevelName() {
		return levelName;
	}
}
