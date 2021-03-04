package top.jalva.tictactoe.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import top.jalva.tictactoe.enums.GameLevel;
import top.jalva.tictactoe.enums.Player;

public class PlayerDriver {

	GameLevel gameLevel;
	Board board;
	Player player;
	CombinationsResolver combinations;
	Random random;

	public PlayerDriver(GameLevel gameLevel, Board board, Player player, CombinationsResolver combinations) {
		super();
		this.gameLevel = gameLevel;
		this.board = board;
		this.player = player;
		this.combinations = combinations;
		random = new Random();
	}

	public Optional<BoardPosition> nextStep() {
		if(!combinations.gameIsOver()) {
			Optional<BoardPosition> mostImportantPosition = getMostImportantPositionForNextStep();

			if(mostImportantPosition.isPresent()) {
				return mostImportantPosition;
			}

			List<BoardPosition> positions = getAccessiblePositions();

			if(!positions.isEmpty()) {
				return Optional.of(getRandom(positions));
			}
		}

		return Optional.empty();
	}

	Optional<BoardPosition> getMostImportantPositionForNextStep() {
		final List<Supplier<Optional<BoardPosition>>> suppliers = getPositionSuppliers();
		
		for(var supplier : suppliers) {
			if(gameLevel.takeIntoAccount()) {
				Optional<BoardPosition> position = supplier.get();
				
				if(position.isPresent()) {
					return position;
				}
			}
		}
		
		return Optional.empty();
	}

	private List<Supplier<Optional<BoardPosition>>> getPositionSuppliers() {
		final List<Supplier<Optional<BoardPosition>>> suppliers = new ArrayList<>();
		suppliers.add(combinations::findPositionToWin);
		suppliers.add(combinations::findOpponentPositionToWin);
		return suppliers;
	}

	private BoardPosition getRandom(List<BoardPosition> positions) {
		return positions.get(random.nextInt(positions.size()));
	}

	List<BoardPosition> getAccessiblePositions() {				
		for(var supplier : getPositionListSuppliers()) {
			List<BoardPosition> positions = supplier.get();

			if(!positions.isEmpty()) {
				return positions;
			}
		}
		return Collections.emptyList();
	}

	private List<Supplier<List<BoardPosition>>> getPositionListSuppliers() {
		final List<Supplier<List<BoardPosition>>> suppliers = new ArrayList<>();

		if(gameLevel == GameLevel.LOW) {
			suppliers.add(combinations::findPositionsToWin);
			suppliers.add(combinations::findStartCombinationPositions);
		}
		suppliers.add(combinations::findEmptyPositions);

		return suppliers;
	}
}


