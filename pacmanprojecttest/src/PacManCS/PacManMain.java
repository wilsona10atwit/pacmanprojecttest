package PacManCS;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class PacManMain extends Application {
	public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PacMan.fxml"));
		Parent root = loader.load();
		primaryStage.setTitle("PacMan");
		Controller controller = loader.getController();
		root.setOnKeyPressed(controller);
		double sceneWidth = controller.getBoardWidth() + 20.0;
		double sceneHeight = controller.getBoardHeight() + 100.0;
		primaryStage.setScene(new Scene(root,sceneWidth, sceneHeight));
		primaryStage.show();
		root.requestFocus();
	}
	public static void main(String [] args) {
		launch(args);
	}
}
