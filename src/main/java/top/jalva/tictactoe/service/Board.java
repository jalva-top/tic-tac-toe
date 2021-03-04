package top.jalva.tictactoe.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import top.jalva.tictactoe.enums.Player;

public class Board {
	
	private static final int GAME_DIMENSION = 3;
	private static final String DELIMETER = "\n----------\n";
	Map<BoardPosition, Player> data;
	
	public Board() {
		super();	
		data = new HashMap<>(GAME_DIMENSION * GAME_DIMENSION);
		
		for(int row = 0; row < GAME_DIMENSION; row++) {
			for(int column = 0; column < GAME_DIMENSION; column++) {
				data.put(BoardPosition.of(row, column), Player.EMPTY);
			}
		}
	}
	
	public boolean isEmpty(BoardPosition pos) {
		return getPlayer(pos) == Player.EMPTY;
	}
	
	public Player getPlayer(BoardPosition pos) {
		return data.get(pos);
	}

	public void set(BoardPosition pos, Player player) {
		
		if(data.get(pos) != null && data.get(pos) != Player.EMPTY) {
			throw new IllegalArgumentException("BoardPosition is not empty: " + pos + "\n" + this);
		}
		
		data.put(pos, player);
	}
	
	public Set<BoardPosition> getPlayerPositions(Player player) {		
		return data.entrySet().stream().filter(e-> e.getValue() == player).map(Entry::getKey).collect(Collectors.toSet());
	}
	
	public List<BoardPosition> findEmptyPositions() {
		return getPlayerPositions(Player.EMPTY).stream().collect(Collectors.toList());
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(BoardPosition key : data.keySet()) {
			builder.append(data.get(key).asString());
			if(key.getColumn() == GAME_DIMENSION - 1) {
				builder.append(DELIMETER);
			} else {
				builder.append(" | ");
			}
		}
		
		return builder.toString();
	}
}
