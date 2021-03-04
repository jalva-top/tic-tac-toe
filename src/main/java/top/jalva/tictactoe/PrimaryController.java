package top.jalva.tictactoe;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import top.jalva.tictactoe.enums.GameLevel;
import top.jalva.tictactoe.enums.GameResult;
import top.jalva.tictactoe.enums.Player;
import top.jalva.tictactoe.service.Board;
import top.jalva.tictactoe.service.BoardPosition;
import top.jalva.tictactoe.service.CombinationsResolver;
import top.jalva.tictactoe.service.PlayerDriver;

public class PrimaryController {
	private static final String _LOW = "_low";
	private static final String DOT_PNG = ".png";
	private static final String DOT_JPG = ".jpg";	

	@FXML
	private GridPane gameBoard;

	@FXML
	private Button newGameButton;

	@FXML
	private ImageView resultImageView;

	@FXML
	private ImageView computerPic;
	@FXML
	private ImageView computerSign;

	@FXML
	private ImageView userPic;
	@FXML
	private ImageView userSign;

	@FXML
	private HBox gameLevelParent;

	@FXML
	private Label score;

	CombinationsResolver combinations;
	Board board;
	Player user;
	Player computer;
	PlayerDriver driver;
	final ObjectProperty<GameLevel> level = new SimpleObjectProperty<>(GameLevel.LOW);

	ImageView lastUserStep;
	final BooleanProperty gameIsOver = new SimpleBooleanProperty(false);
	final Map<BoardPosition, ImageView> positionImageMap = new HashMap<>();
	final Random random = new Random();

	Player currentPlayer;
	int robotsNumber = 12;

	int userScore;
	int computerScore;

	public void initialize() {
		userPic.setImage(getImage("user.png"));

		initBindings();
		initGameLevelToggleGroup();
		initGameBoard();
		startNewGame();
	}

	private void initBindings() {
		resultImageView.visibleProperty().bind(resultImageView.imageProperty().isNotNull());
		newGameButton.visibleProperty().bind(gameIsOver);

		computerPic.visibleProperty().bind(gameIsOver.not());
		computerSign.visibleProperty().bind(gameIsOver.not());

		userPic.visibleProperty().bind(gameIsOver.not());
		userSign.visibleProperty().bind(gameIsOver.not());
	}

	private void initGameLevelToggleGroup() {
		ToggleGroup tg = new ToggleGroup();

		for(GameLevel level:GameLevel.values()) {
			ToggleButton toggle = new ToggleButton(level.getLevelName());
			toggle.setToggleGroup(tg);
			toggle.setOnAction(e-> {
				toggle.setSelected(true);
				this.level.set(level);
				startNewGame();
			});
			toggle.setSelected(level == this.level.get());
			toggle.disableProperty().bind(resultImageView.visibleProperty().not());

			gameLevelParent.getChildren().add(toggle);
		}
	}

	private void initGameBoard() {
		gameBoard.getChildren().clear();
		gameBoard.visibleProperty().bind(resultImageView.visibleProperty().not());
		for(int row = 0; row < 3; row++) {
			for(int column = 0; column < 3; column++) {
				gameBoard.add(createImageView(row, column), column, row);
			}
		}
	}

	public EventHandler<? super KeyEvent> getKeyPressedListener() {
		return key -> BoardPosition.byKeyText(key.getText()).ifPresent(this::actSteps);
	}

	private ImageView createImageView(Integer row, Integer column) {
		BoardPosition position = BoardPosition.of(row, column);

		final ImageView iv = new ImageView(getImage(Player.EMPTY));	
		iv.setUserData(position);
		positionImageMap.put(position, iv);

		GridPane.setFillHeight(iv, true);
		GridPane.setFillWidth(iv, true);
		iv.setOnMousePressed(value-> handleMousePressed(value, position));

		return iv;
	}

	private void handleMousePressed(MouseEvent event, BoardPosition position) {
		if(!gameIsOver.get()) {
			if(event.isPrimaryButtonDown()) {
				actSteps(position);
			} else if(event.isSecondaryButtonDown()) {
				drawOrClearUserSign(position);
			}
		}
	}

	private void actSteps(BoardPosition newUserStep) {
		if(board.isEmpty(newUserStep)) {
			actUserStep(newUserStep);
			sleep(1, ()-> {
				actComputerStep();
				setResultIfNecessary();
			});
		}
	}

	private void setResultIfNecessary() {
		Optional<GameResult> result = combinations.getGameResultFor(user);

		if(result.isPresent()) {
			gameIsOver.set(true);
			handleGameResult(result.get());
		}
	}

	private void handleGameResult(GameResult result) {
		Set<BoardPosition> winnerCombinations = combinations.getWinnerCombination();
		redraw(winnerCombinations);
		handleScore(result);
		newGameButton.setDisable(true);
		sleep(2, ()-> {
			resultImageView.setImage(getImage(result));
			newGameButton.setDisable(false);
		});
	}

	private void handleScore(GameResult result) {
		if(userScore > 99 || computerScore > 99) {
			userScore = computerScore = 0;
		}

		if(result == GameResult.WIN) {
			userScore++;
		} else if (result == GameResult.LOSE){
			computerScore++;
		}

		score.setText(userScore + " : " + computerScore);
	}

	private void redraw(Set<BoardPosition> winnerCombinations) {
		for(Entry<BoardPosition, ImageView> entry : positionImageMap.entrySet()) {
			BoardPosition position = entry.getKey();
			if(!winnerCombinations.contains(position)) {
				ImageView iv = entry.getValue();
				setImage(iv, board.getPlayer(position), _LOW);
			}
		}
	}


	private void sleep(int seconds, Runnable onSucceeded) {
		Task<Void> sleeper = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(seconds * 1000);
				} catch (InterruptedException e) {
				}
				return null;
			};
		};

		sleeper.setOnSucceeded(value-> Platform.runLater(onSucceeded));
		new Thread(sleeper).start();
	}

	private void drawOrClearUserSign(BoardPosition position) {
		if(board.isEmpty(position)) {
			ImageView iv = positionImageMap.get(position);
			if(lastUserStep == iv) {
				setImage(iv, Player.EMPTY);
				lastUserStep = null;
			} else if(lastUserStep == null) {
				setImage(iv, user, _LOW);
				lastUserStep = iv;
			}
		}
	}

	private void actUserStep(BoardPosition position) {
		if(currentPlayer == user) {
			board.set(position, user);
			setImage(positionImageMap.get(position), user);
			lastUserStep = null;
			currentPlayer = user.getOpponent();
		}
	}

	private void actComputerStep() {
		if(currentPlayer == computer) {
			Optional<BoardPosition> computerStep = driver.nextStep();
			if(computerStep.isPresent() && board.isEmpty(computerStep.get())) {	
				ImageView iv = positionImageMap.get(computerStep.get());
				board.set(computerStep.get(), computer);
				setImage(iv, computer);
			}
			currentPlayer = computer.getOpponent();
		}
	}

	private void setImage(ImageView iv, Player currentPlayer, String postfix) {
		iv.setImage(null);
		iv.setImage(getImage(currentPlayer, postfix));
	}

	private void setImage(ImageView iv, Player currentPlayer) {
		iv.setImage(getImage(currentPlayer));
	}

	public Image getImage(Player player, String postfix) {
		return getImage( player.name() + postfix + DOT_PNG);
	}

	public Image getImage(Player player) {
		return getImage(player, "");
	}

	public Image getImage(GameResult result) {
		return getImage(result.name() + DOT_JPG);
	}

	public Image getImage(String name) {
		return new Image(//
				getClass()//
				.getResource("image/" + name)//
				.toExternalForm());
	}

	void setRandomRobot() {
		int robotNumber = random.nextInt(robotsNumber);
		computerPic.setImage(getImage(getRobotFileName(robotNumber)));	
	}

	private String getRobotFileName(int robotNumber) {
		return "robot_" + (robotNumber + 1) + DOT_PNG;
	}

	@FXML
	void newGameButtonOnAction(ActionEvent event) {
		startNewGame();
	}

	private void startNewGame() {
		selectCurrentLevelToggle();
		setRandomRobot();
		gameIsOver.set(false);
		positionImageMap.values().forEach(iv-> setImage(iv, Player.EMPTY));
		board = new Board();
		user = Player.X;

		initComputer();
	}

	private void selectCurrentLevelToggle() {

	}

	private void initComputer() {
		resultImageView.setImage(null);
		user = Player.getRamdomPlayer();
		currentPlayer = Player.X;
		computer = user.getOpponent();
		userSign.setImage(getImage(user));
		computerSign.setImage(getImage(computer));
		combinations = new CombinationsResolver(board, computer);		
		driver = new PlayerDriver(level.get(), board, computer, combinations);

		if(computer == Player.X) {
			actComputerStep();
		}
	}
}
