package top.jalva.tictactoe;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

	private static Scene scene;
	private static FXMLLoader fxmlLoader;

	@Override
	public void start(Stage stage) throws IOException {
		scene = new Scene(loadFXML("primary"), 600, 740);
		stage.setTitle("Tic-Tac-Toe");
		stage.setScene(scene);

		EventHandler<? super KeyEvent> handler = ((PrimaryController)fxmlLoader.getController()).getKeyPressedListener();
		scene.addEventHandler(KeyEvent.KEY_PRESSED, handler);

		stage.show();
	}

	static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
	}

	private static Parent loadFXML(String fxml) throws IOException {	
		URL url = App.class.getResource(fxml + ".fxml");
		
		if(url == null) {
			url = App.class.getResource("/" + fxml + ".fxml");
		}
		
		if(url == null) {
			url = App.class.getClassLoader().getResource(fxml + ".fxml");
		}
		
		fxmlLoader = new FXMLLoader(url);        
		return fxmlLoader.load();
	}

	public static void main(String[] args) {
		launch();
	}

}