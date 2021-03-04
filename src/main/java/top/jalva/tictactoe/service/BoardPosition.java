package top.jalva.tictactoe.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javafx.util.Pair;

public class BoardPosition implements Comparable<BoardPosition> {
	private static final Map<Integer, BoardPosition> indexMap = new ConcurrentHashMap<>();
	private static final Map<Pair<Integer, Integer>, String> rowColumnKeyMap = new ConcurrentHashMap<>();
	
	static {
		rowColumnKeyMap.put(new Pair<>(0,0), "7");
		rowColumnKeyMap.put(new Pair<>(0,1), "8");
		rowColumnKeyMap.put(new Pair<>(0,2), "9");
		
		rowColumnKeyMap.put(new Pair<>(1,0), "4");
		rowColumnKeyMap.put(new Pair<>(1,1), "5");
		rowColumnKeyMap.put(new Pair<>(1,2), "6");
		
		rowColumnKeyMap.put(new Pair<>(2,0), "1");
		rowColumnKeyMap.put(new Pair<>(2,1), "2");
		rowColumnKeyMap.put(new Pair<>(2,2), "3");	
	}
	
	int row;
	int column;

	private BoardPosition(int row, int column) {
		super(); 
		if(row > 2 || column > 2) {
			throw new IllegalArgumentException("Row/column index cannot be greater then two: [" + row + ", " + column + "]");
		}

		if(row < 0 || column < 0) {
			throw new IllegalArgumentException("Row/column index cannot be less then zero: [" + row + ", " + column + "]");
		}

		this.row = row;
		this.column = column;
	}
	
	public static BoardPosition of(int row, int column) {
		int index = BoardPosition.sortIndex(row, column);
		
		BoardPosition boardPosition = BoardPosition.indexMap.get(index);
		
		if(boardPosition == null) {
			boardPosition = new BoardPosition(row, column);
			BoardPosition.indexMap.put(index, boardPosition);			
		}
		
		return boardPosition;
	}

	public static Optional<BoardPosition> byKeyText(String keyText) {	
		Pair<Integer, Integer> pos = rowColumnKeyMap.entrySet()//
				.stream().filter(e-> e.getValue().equals(keyText)).findAny().orElseThrow().getKey();
		return Optional.of(BoardPosition.of(pos.getKey(), pos.getValue()));
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}

		if(this == obj) {
			return true;
		}

		if(obj instanceof BoardPosition) {
			BoardPosition that = (BoardPosition) obj;

			return this.sortIndex() == that.sortIndex();
		}

		return false;
	}

	@Override
	public int hashCode() {
		return BoardPosition.sortIndex(row, column);
	}

	private int sortIndex() {
		return BoardPosition.sortIndex(row, column);
	}
	
	private static int sortIndex(int row, int column) {
		return (row + 1) * 10 + column;
	}

	@Override
	public String toString() {
		return "[" + row + ", " + column + "]";
	}

	@Override
	public int compareTo(BoardPosition o) {		
		return Integer.compare(this.sortIndex(), o.sortIndex());
	}
}
