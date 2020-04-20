package PacManCS;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;
public class PacManMain extends Application {
	@Override 
	public void start(Stage mainStage) throws Exception{
		FXMLLoader load = new FXMLLoader(getClass().getResource("PacMan.fxml"));
		Parent root = load.load();
		mainStage.setTitle("PacMan");
		Controller controller = load.getController();
		root.setOnKeyPressed(controller);
		double sceneWidth = controller.getBoardWidth() + 20.0;
		double sceneHeight = controller.getBoardHeight() + 100.0;
		mainStage.setScene(new Scene(root,sceneWidth, sceneHeight));
		mainStage.show();
		root.requestFocus();
	}
	public static void main(String [] args) {
		launch(args);
	}
}
