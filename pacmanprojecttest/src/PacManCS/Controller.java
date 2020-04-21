package PacManCS;
import javafx.fxml.FXML;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import java.util.Timer;
import java.util.TimerTask;
public class Controller implements EventHandler<KeyEvent> {
	final private static double FPS = 5.0;
	@FXML private Label scoreLabel;
	@FXML private Label livesLabel;
	@FXML private Label levelLabel;
	@FXML private Label gameOverLabel;
	@FXML private PacManImage pacManImage;
	
	private Timer timer;
	private static int powerPelletModeCounter;
	private boolean pause;
	private PacManMechanics pacManMechanics;
	private static final String [] levelFiles = {"src/levels/level1.txt", "src/levels/level2.txt", "src/levels/level3.txt"};
	public Controller() {
		this.pause=false;
	}
	public void initalize() {
		String f = Controller.getLevelFile(0);
		this.pacManMechanics = new PacManMechanics();
		this.update(PacManMechanics.Movement.STOP);
		powerPelletModeCounter=25;
		this.startTimer();
	}
	private void startTimer() {
		this.timer = new java.util.Timer();
		TimerTask tT = new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						update(PacManMechanics.getCurrMovement());
					}
				});
			}
		};
		long frameTimeInMS = (long)(1000.0 / FPS);
		this.timer.schedule(tT, 0, frameTimeInMS);
	}
	private void update(PacManMechanics.Movement movement) {
		this.pacManMechanics.move(movement);
		this.pacManImage.update(pacManMechanics);
		this.scoreLabel.setText(String.format("Score: %d", this.pacManMechanics.getScore()));
		this.levelLabel.setText(String.format("Level: %d", this.pacManMechanics.getLevel()));
		this.livesLabel.setText(String.format("Lives: %d", this.pacManMechanics.getLives()));
		if (PacManMechanics.gameOver()) {
			this.gameOverLabel.setText(String.format("GAME OVER!"));
			pause();
		}
		if(PacManMechanics.isYouWin()) {
			this.gameOverLabel.setText(String.format("YOU WIN!"));
		}
		if (PacManMechanics.isPowerPelletMode()) {
			powerPelletModeCounter--;
		}
		if (powerPelletModeCounter == 00 && PacManMechanics.isPowerPelletMode()) {
			PacManMechanics.setPowerPelletMode(false);
		}
	}
	public void handle (Scene scene) {
		scene.setOnKeyPressed(ke -> {
		boolean keyRecognized = true;
		KeyCode keycode = ke.getCode();
		PacManMechanics.Movement movement = PacManMechanics.Movement.STOP;
		if(keycode.equals(KeyCode.LEFT)) {
			movement = PacManMechanics.Movement.LEFT;
		}
		else if (keycode.equals(KeyCode.RIGHT)) {
			movement = PacManMechanics.Movement.RIGHT;
		}
		else if (keycode.equals(KeyCode.UP)) {
			movement = PacManMechanics.Movement.UP;
		}
		else if (keycode.equals(KeyCode.DOWN)) {
			movement = PacManMechanics.Movement.DOWN;
		}
		else if(keycode.equals(KeyCode.P)) {
			pause();
			this.pacManMechanics.newGame();
			this.gameOverLabel.setText(String.format(""));
			pause = false;
			this.startTimer();
		}
		else {
			keyRecognized = false;
		}
		if(keyRecognized) {
			ke.consume();
			pacManMechanics.setCurrMovement(movement);
		}
		else {
			
		}
		});
	}
	public void pause() {
		this.timer.cancel();
		this.pause = true;
	}
	public double getBoardWidth() {
		return PacManImage.CELL_WIDTH * this.pacManImage.getCollumnCount();
	}
	public double getBoardHeight() {
		return PacManImage.CELL_WIDTH * this.pacManImage.getRowCount();
	}
	public static void setPowerPelletModeCounter() {
		powerPelletModeCounter=25;
	}
	public static int getPowerPelletModeCounter() {
		return powerPelletModeCounter;
	}
	public static String getLevelFile(int x) {
        return levelFiles[x];
    }
	public boolean getPaused() {
		return pause;
	}
	@Override
	public void handle(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
