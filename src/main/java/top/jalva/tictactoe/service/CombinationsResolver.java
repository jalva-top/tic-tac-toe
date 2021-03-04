package top.jalva.tictactoe.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import top.jalva.tictactoe.enums.GameResult;
import top.jalva.tictactoe.enums.Player;

public class CombinationsResolver {

	Board board;
	Player me;
	Player opponent;

	final Set<Set<BoardPosition>> combinations = new HashSet<>();

	public CombinationsResolver(Board board, Player player) {
		super();

		this.board = board;
		this.me = player;
		this.opponent = player.getOpponent();

		initCombinations();
	}

	private void initCombinations() {
		IntStream.range(0, 3).forEach(i-> {
			combinations.add(getRowWinnerCombinations(i));
			combinations.add(getColumnWinnerCombinations(i));
		});

		combinations.add(getStraightDiagonalWinnerCombinations());
		combinations.add(getReverseDiagonalWinnerCombinations());
	}

	public boolean gameIsOver() {
		return isWinner() || isLooser() || findEmptyPositions().isEmpty();
	}

	protected boolean isWinner(Player player) {
		Set<BoardPosition> playerPositions = board.getPlayerPositions(player);

		for(Set<BoardPosition> combination : combinations) {
			if(playerPositions.containsAll(combination)) {
				return true;
			}
		}

		return false;
	}

	public boolean isWinner() {
		return isWinner(me);
	}

	public boolean isLooser() {
		return isWinner(opponent);
	}

	public Optional<BoardPosition> findPositionToWin() {
		return findPositionToWin(me);
	}

	public Optional<BoardPosition> findOpponentPositionToWin() {
		return findPositionToWin(opponent);
	}

	protected Optional<BoardPosition> findPositionToWin(Player player) {		
		return findPositionsToWin(player, 1).stream().findAny();
	}

	private boolean notContainsOpponentPositions(Set<BoardPosition> combination, Set<BoardPosition> opponentsPositions) {
		return combination.parallelStream().noneMatch(opponentsPositions::contains);
	}

	public List<BoardPosition> findPositionsToWin() {		
		return findPositionsToWin(me, 2);
	}

	public List<BoardPosition> findStartCombinationPositions() {
		return findPositionsToWin(me, 3);
	}

	protected List<BoardPosition> findPositionsToWin(Player player, int stepNumber) {
		Set<BoardPosition> opponentsPositions = board.getPlayerPositions(player.getOpponent());

		final List<BoardPosition> result = new ArrayList<>();

		for(Set<BoardPosition> combination : combinations) {
			if(notContainsOpponentPositions(combination, opponentsPositions)) {
				List<BoardPosition> necessaryPositions = combination.stream().filter(board::isEmpty).collect(Collectors.toList());

				if(necessaryPositions.size() == stepNumber) {
					if(stepNumber == 1) {
						return necessaryPositions;
					} 
					
					result.addAll(necessaryPositions);
				}
			}
		}

		return result;
	}

	public List<BoardPosition> findEmptyPositions() {
		return board.findEmptyPositions();
	}

	public Set<BoardPosition> getWinnerCombination() {
		for(Set<BoardPosition> combination : combinations) {
			List<Player> players = combination.parallelStream().map(board::getPlayer).collect(Collectors.toList());
			long distinctCount = players.stream().distinct().count();

			if(distinctCount == 1 && players.size() == 3 && players.get(0) != Player.EMPTY) {
				return combination;
			}
		}

		return Collections.emptySet();
	}

	public Optional<GameResult> getGameResultFor(Player player) {
		if(isWinner(player)) {
			return Optional.of(GameResult.WIN);
		}

		if(isWinner(player.getOpponent())) {
			return Optional.of(GameResult.LOSE);
		}

		if(findEmptyPositions().isEmpty()) {
			return Optional.of(GameResult.DRAW);
		}

		return Optional.empty();
	}

	Set<BoardPosition> getRowWinnerCombinations(int row) {
		return IntStream.range(0, 3).mapToObj(column ->BoardPosition.of(row, column)).collect(Collectors.toUnmodifiableSet());
	}

	Set<BoardPosition> getColumnWinnerCombinations(int column) {
		return IntStream.range(0, 3).mapToObj(row -> BoardPosition.of(row, column)).collect(Collectors.toUnmodifiableSet());
	}

	Set<BoardPosition> getStraightDiagonalWinnerCombinations() {
		return IntStream.range(0, 3).mapToObj(index -> BoardPosition.of(index, index)).collect(Collectors.toUnmodifiableSet());
	}

	Set<BoardPosition> getReverseDiagonalWinnerCombinations() {
		return Arrays.asList(BoardPosition.of(0, 2),//
				BoardPosition.of(1, 1),//
				BoardPosition.of(2, 0))//
				.stream().collect(Collectors.toUnmodifiableSet());
	}

}
