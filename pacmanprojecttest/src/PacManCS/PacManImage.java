package PacManCS;
import javafx.fxml.FXML;



import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import PacManCS.PacManMechanics.cellValue;
public class PacManImage extends Group {
	public final static double CELL_WIDTH = 20.0;
	@FXML private int rowCount;
	@FXML private int collumnCount;
	private ImageView[][] cellView;
	private Image blueGhost;
	private Image cyanGhost;
	private Image orangeGhost;
	private Image pacmanDown;
	private Image pacmanLeft;
	private Image pacmanRight;
	private Image pacmanUp;
	private Image pinkGhost;
	private Image redGhost;
	private Image pacDot;
	private Image powerPellet;
	private Image pacWall;
	
	public PacManImage() {
		this.blueGhost = new Image(getClass().getResourceAsStream("/pictures/blueGhost.gif"));
		this.cyanGhost = new Image(getClass().getResourceAsStream("/pictures/cyanGhost.gif"));
		this.orangeGhost = new Image(getClass().getResourceAsStream("/pictures/orangeGhost.gif"));
		this.pacmanDown = new Image(getClass().getResourceAsStream("/pictures/pacmanDown.gif"));
		this.pacmanLeft = new Image(getClass().getResourceAsStream("/pictures/pacmanLeft.gif"));
		this.pacmanRight = new Image(getClass().getResourceAsStream("/pictures/pacmanRight.gif"));
		this.pacmanUp = new Image(getClass().getResourceAsStream("/pictures/pacmanUp.gif"));
		this.pacDot = new Image(getClass().getResourceAsStream("/pictures/pacDot.png"));
		this.powerPellet = new Image(getClass().getResourceAsStream("/pictures/powerPellet"));
		this.pacWall = new Image(getClass().getResourceAsStream("/pictures/pacWall.jpg"));
	}
	private void initializeGrid() {
		if (this.rowCount > 0 && this.collumnCount > 0) {
			this.cellView = new ImageView[this.rowCount][this.collumnCount];
			for (int i = 0; i < this.rowCount; i++) {
				for(int j = 0; j < this.collumnCount; j++) {
					ImageView iv = new ImageView();
					iv.setX((double) j * CELL_WIDTH);
					iv.setY((double) i * CELL_WIDTH);
					iv.setFitWidth(CELL_WIDTH);
					iv.setFitHeight(CELL_WIDTH);
					this.cellView[i][j] = iv;
					this.getChildren().add(iv);
				}
			}
		}
	}
	public void update (PacManMechanics mechanics) {
		assert ((mechanics.getRowCount() == this.rowCount) && (mechanics.getCollumnCount() == this.collumnCount));
		for (int i = 0; i < this.rowCount; i++) {
			for(int j = 0; j < this.collumnCount; j++) {
				cellValue cv = mechanics.getCellValue(i,j);
				if(cv == cellValue.WALL) {
					this.cellView[i][j].setImage(this.pacWall);
				}
				else if (cv == cellValue.POWERPELLET) {
					this.cellView[i][j].setImage(this.powerPellet);
				}
				else if (cv == cellValue.PACDOT) {
					this.cellView[i][j].setImage(this.pacDot);
				}
				else {
					this.cellView[i][j].setImage(null);
				}
				if (i == mechanics.getPacLoc().getX() && j == mechanics.getPacLoc().getY() && (PacManMechanics.getLastMovement()== PacManMechanics.Movement.RIGHT || PacManMechanics.getLastMovement() == PacManMechanics.Movement.STOP)) {
					this.cellView[i][j].setImage(this.pacmanRight);
				}
				else if (i == mechanics.getPacLoc().getX() && j == mechanics.getPacLoc().getY() && (PacManMechanics.getLastMovement()== PacManMechanics.Movement.LEFT)){
					this.cellView[i][j].setImage(this.pacmanLeft);
				}
				else if (i == mechanics.getPacLoc().getX() && j == mechanics.getPacLoc().getY() && (PacManMechanics.getLastMovement()== PacManMechanics.Movement.UP)){
					this.cellView[i][j].setImage(this.pacmanUp);
				}
				else if (i == mechanics.getPacLoc().getX() && j == mechanics.getPacLoc().getY() && (PacManMechanics.getLastMovement()== PacManMechanics.Movement.DOWN)){
					this.cellView[i][j].setImage(this.pacmanDown);
				}
				if (PacManMechanics.isPowerPelletMode() && (Controller.getPowerPelletModeCounter() == 6 || Controller.getPowerPelletModeCounter() == 4 || Controller.getPowerPelletModeCounter() == 2)) {
					if (i == mechanics.getG1Loc().getX() && j == mechanics.getG1Loc().getY()) {
						this.cellView[i][j].setImage(this.pinkGhost);
					}
					if (i == mechanics.getG2Loc().getX() && j == mechanics.getG2Loc().getY()) {
						this.cellView[i][j].setImage(this.orangeGhost);
					}
				}
				else if (PacManMechanics.isPowerPelletMode()) {
					if (i == mechanics.getG1Loc().getX() && j == mechanics.getG1Loc().getY()) {
						this.cellView[i][j].setImage(this.blueGhost);
					}
					if (i == mechanics.getG2Loc().getX() && j == mechanics.getG2Loc().getY()) {
						this.cellView[i][j].setImage(this.blueGhost);
					}
				}
				else {
					if (i == mechanics.getG1Loc().getX() && j == mechanics.getG1Loc().getY()) {
						this.cellView[i][j].setImage(this.pinkGhost);
					}
				}
				if (i == mechanics.getG2Loc().getX() && j == mechanics.getG2Loc().getY()) {
					this.cellView[i][j].setImage(this.orangeGhost);
				}
			}
		}
	}
	public int getRowCount() {
		return this.rowCount;
	}
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
		this.initializeGrid();
	}
	public int getCollumnCount() {
		return this.collumnCount;
	}
	public void setCollumnCount(int collumnCount) {
		this.collumnCount = collumnCount;
		this.initializeGrid();
	}
}
