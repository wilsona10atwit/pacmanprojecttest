package PacManCS;


import javafx.fxml.FXML;

import java.io.*;
import javafx.geometry.Point2D;
import java.util.*;


public class PacManMechanics {
	public enum cellValue {
		EMPTY, PACDOT, POWERPELLET, WALL, G1SPAWN, G2SPAWN, PACMANSPAWN
	};

	public enum Movement {
		UP, DOWN, LEFT, RIGHT, STOP
	}

	@FXML
	private int rowCount;
	@FXML
	private int collumnCount;
	private cellValue[][] grid;
	private int score;
	private int level;
	private int pelletCount;
	private static int lives;
	private static boolean gameOver;
	private static boolean youWin;
	private static boolean powerPelletMode;
	private Point2D pacLoc;
	private Point2D pacVel;
	private Point2D g1Loc;
	private Point2D g1Vel;
	private Point2D g2Loc;
	private Point2D g2Vel;
	private static Movement lastMovement;
	private static Movement currMovement;

	public PacManMechanics() {
		this.newGame();
	}

	public void loadLevel(String fName) {
		File file = new File(fName);
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			Scanner lScanner = new Scanner(line);
			while (lScanner.hasNext()) {
				lScanner.next();
				collumnCount++;
			}
			rowCount++;
		}
		collumnCount = collumnCount / rowCount;
		Scanner s2 = null;
		try {
			s2 = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		grid = new cellValue[rowCount][collumnCount];
		int row = 0;
		int pacRow = 0;
		int pacCollumn = 0;
		int g1Row = 0;
		int g2Row = 0;
		int g1Collumn = 0;
		int g2Collumn = 0;
		while (s2.hasNextLine()) {
			int collumn = 0;
			String line = s2.nextLine();
			Scanner ls2 = new Scanner(line);
			while (ls2.hasNext()) {
				String val = ls2.next();
				cellValue thisVal;
				if (val.equals("W")) {
					thisVal = cellValue.WALL;
					pelletCount++;
				} else if (val.equals("S")) {
					thisVal = cellValue.PACDOT;
					pelletCount++;
				} else if (val.equals("B")) {
					thisVal = cellValue.POWERPELLET;
					pelletCount++;
				} else if (val.equals("1")) {
					thisVal = cellValue.G1SPAWN;
					g1Row = row;
					g1Collumn = collumn;
				} else if (val.equals("2")) {
					thisVal = cellValue.G2SPAWN;
					g2Row = row;
					g2Collumn = collumn;
				} else if (val.equals("P")) {
					thisVal = cellValue.PACMANSPAWN;
					pacRow = row;
					pacCollumn = collumn;
				} else {
					thisVal = cellValue.EMPTY;
				}
				grid[row][collumn] = thisVal;
				collumn++;
			}
			row++;
		}
		pacLoc = new Point2D(pacRow, pacCollumn);
		pacVel = new Point2D(0, 0);
		g1Loc = new Point2D(g1Row, g1Collumn);
		g2Loc = new Point2D(g2Row, g2Collumn);
		g1Vel = new Point2D(-1, 0);
		g2Vel = new Point2D(-1, 0);
		lastMovement = Movement.STOP;
		currMovement = Movement.STOP;
	}

	public void newGame() {
		PacManMechanics.gameOver = false;
		PacManMechanics.youWin = false;
		PacManMechanics.powerPelletMode = false;
		this.pelletCount = 0;
		rowCount = 0;
		collumnCount = 0;
		this.score = 0;
		this.level = 1;
		PacManMechanics.lives = 3;
		this.loadLevel(Controller.getLevelFile(0));
	}
	public void nextLevel() {
		if (this.levelComplete()) {
			this.level++;
			rowCount = 0;
			collumnCount = 0;
			youWin=false;
		}
		try {
			this.loadLevel(Controller.getLevelFile(level-1));
		}
		catch (ArrayIndexOutOfBoundsException e) {
			youWin=true;
			gameOver=true;
			level--;
		}
		
	}
	public void movePacman(Movement movement) {
		Point2D potPacVel = changeVel(movement);
		Point2D potPacLoc = pacLoc.add(potPacVel);
		potPacLoc = setGoingOffscreenNewLoc(potPacLoc);
		if(movement.equals(lastMovement)) {
			if(grid[(int)potPacLoc.getX()][(int)potPacLoc.getY()] == cellValue.WALL) {
				pacVel = changeVel(Movement.STOP);
				setLastMovement(Movement.STOP);
			}
			else { 
				pacVel = potPacVel;
				pacLoc = potPacLoc;
			}
		}
		else {
			if(grid[(int) potPacLoc.getX()][(int) potPacLoc.getY()] == cellValue.WALL) {
				potPacVel = changeVel(lastMovement);
				potPacLoc = pacLoc.add(potPacVel);
				if (grid[(int) potPacLoc.getX()][(int) potPacLoc.getY()] == cellValue.WALL) {
					pacVel = changeVel(Movement.STOP);
					setLastMovement(Movement.STOP);
				}
				else {
					pacVel = changeVel(lastMovement);
					pacLoc = pacLoc.add(pacVel);
				}
			}
			else {
				pacVel = potPacVel;
				pacLoc = potPacLoc;
				setLastMovement(movement);
			}
		}
	}
	public void moveGhosts() {
		Point2D[] g1Move = moveAGhost(g1Vel, g1Loc);
		Point2D[] g2Move = moveAGhost(g2Vel, g2Loc);
		g1Vel = g1Move[0];
		g2Vel = g2Move[0];
		g1Loc = g1Move[1];
		g2Loc = g2Move[1];
	}
	public Point2D[] moveAGhost(Point2D vel, Point2D loc) {
		Random generator = new Random();
		if(!powerPelletMode) {
			if(loc.getY() == pacLoc.getY()) {
				if(loc.getX() > pacLoc.getX()) {
					vel = changeVel(Movement.UP);
				}
				else {
					vel = changeVel(Movement.DOWN);
				}
				Point2D potLoc = loc.add(vel);
				potLoc = setGoingOffscreenNewLoc(potLoc);
				while (grid[(int) potLoc.getX()][(int) potLoc.getY()] == cellValue.WALL) {
					int random = generator.nextInt(4);
					Movement movement = intToMove(random);
					vel = changeVel(movement);
					potLoc = loc.add(vel);
				}
				loc = potLoc;
			}
			else if(loc.getX() == pacLoc.getX()) {
				if (loc.getY() > pacLoc.getY()) {
					vel = changeVel(Movement.LEFT);
				}
				else {
					vel = changeVel(Movement.RIGHT);
				}
				Point2D potLoc = loc.add(vel);
				potLoc = setGoingOffscreenNewLoc(potLoc);
				while (grid[(int) potLoc.getX()][(int) potLoc.getY()] == cellValue.WALL) {
					int random = generator.nextInt(4);
					Movement movement = intToMove(random);
					vel = changeVel(movement);
					potLoc=loc.add(vel);
				}
				loc = potLoc;
			}
			else { 
				Point2D potLoc = loc.add(vel);
				potLoc = setGoingOffscreenNewLoc(potLoc);
				while (grid[(int) potLoc.getX()][(int) potLoc.getY()] == cellValue.WALL) {
					int random = generator.nextInt(4);
					Movement movement = intToMove(random);
					vel = changeVel(movement);
					potLoc = loc.add(vel);
				}
				loc=potLoc;
			}
		}
		if(powerPelletMode) {
			if(loc.getY() == pacLoc.getY()) {
				if(loc.getX() > pacLoc.getX()) {
					vel = changeVel(Movement.DOWN);
				}
				else {
					vel = changeVel(Movement.UP);
				}
				Point2D potLoc = loc.add(vel);
				potLoc = setGoingOffscreenNewLoc(potLoc);
				while (grid[(int) potLoc.getX()][(int) potLoc.getY()] == cellValue.WALL) {
					int random = generator.nextInt(4);
					Movement movement = intToMove(random);
					vel = changeVel(movement);
					potLoc = loc.add(vel);
				}
				loc=potLoc;
			}
			else if(loc.getX() == pacLoc.getX()) {
				if(loc.getY() > pacLoc.getY()) {
					vel = changeVel(Movement.RIGHT);
				}
				else {
					vel = changeVel(Movement.LEFT);
				}
				Point2D potLoc = loc.add(vel);
				potLoc = setGoingOffscreenNewLoc(potLoc);
				while (grid[(int) potLoc.getX()][(int) potLoc.getY()] == cellValue.WALL) {
					int random = generator.nextInt(4);
					Movement movement = intToMove(random);
					vel = changeVel(movement);
					potLoc = loc.add(vel);
				}
				loc=potLoc;
			}
			else {
				Point2D potLoc = loc.add(vel);
				potLoc = setGoingOffscreenNewLoc(potLoc);
				while (grid[(int) potLoc.getX()][(int) potLoc.getY()] == cellValue.WALL) {
					int random = generator.nextInt(4);
					Movement movement = intToMove(random);
					vel = changeVel(movement);
					potLoc = loc.add(vel);
				}
				loc=potLoc;
			}
		}
		Point2D[] move = {vel,loc};
		return move;
	}
	public Point2D setGoingOffscreenNewLoc(Point2D objLoc) {
		if(objLoc.getY() >= collumnCount) {
			objLoc = new Point2D(objLoc.getX(), 0);
		}
		if(objLoc.getY() < 0) {
			objLoc = new Point2D(objLoc.getX(), collumnCount -1);
		}
		return objLoc;
	}
	public Movement intToMove(int x) {
		if (x==0) {
			return Movement.LEFT;
		}
		else if (x==1) {
			return Movement.RIGHT;
		}
		else if(x==2) {
			return Movement.UP;
		}
		else {
			return Movement.DOWN;
		}
	}
	public void pacSpawn() {
		for (int i = 0; i < this.rowCount; i++) {
			for (int j = 0; j < this.collumnCount; j++) {
				if (grid[i][j] == cellValue.PACMANSPAWN) {
					pacLoc = new Point2D(i, j);
				}
			}
		}
		pacVel = new Point2D(0,0);
	}
	public void g1Spawn() {
		for (int k = 0; k < this.rowCount; k++) {
			for (int l = 0; l < this.collumnCount; l++) {
				if (grid[k][l] == cellValue.G1SPAWN) {
					g1Loc = new Point2D(k, l);
				}
			}
		}
		g1Vel = new Point2D(-1,0);
	}
	public void g2Spawn() {
		for (int m = 0; m < this.rowCount; m++) {
			for (int n = 0; n < this.collumnCount; n++) {
				if (grid[m][n] == cellValue.G2SPAWN) {
					g2Loc = new Point2D(m, n);
				}
			}
		}
		g2Vel = new Point2D(-1,0);
	}
	public void move(Movement move) {
		this.movePacman(move);
		cellValue pacLocCV=grid[(int) pacLoc.getX()][(int) pacLoc.getY()];
		if(pacLocCV == cellValue.PACDOT) {
			grid[(int) pacLoc.getX()][(int) pacLoc.getY()] = cellValue.EMPTY;
			pelletCount--;
			score+=10;
		}
		if (pacLocCV == cellValue.POWERPELLET) {
			grid[(int) pacLoc.getX()][(int) pacLoc.getY()] = cellValue.EMPTY;
			pelletCount--;
			score+=50;
			powerPelletMode = true;
			Controller.setPowerPelletModeCounter();
		}
		if (powerPelletMode) {
			if(pacLoc.equals(g1Loc)) {
				g1Spawn();
				score+=100;
			}
			if (pacLoc.equals(g2Loc)) {
				g2Spawn();
				score+=100;
			}
		}
		else {
			if (pacLoc.equals(g1Loc)) {
				lives--;
				checkGameOver();
				if(lives < 1) {
					gameOver();
				}
				else {
					pacSpawn();
				}
			}
				if (pacLoc.equals(g2Loc)) {
					lives--;
					checkGameOver();
					if(lives < 1) {
						gameOver();
					}
					else {
						pacSpawn();
					}
				}
				if(this.levelComplete()) {
					pacVel = new Point2D(0,0);
					nextLevel();
				}
			}
		}
	public Point2D changeVel(Movement movement){
        if(movement == Movement.LEFT){
            return new Point2D(0,-1);
        }
        else if(movement == Movement.RIGHT){
            return new Point2D(0,1);
        }
        else if(movement == Movement.UP){
            return new Point2D(-1,0);
        }
        else if(movement == Movement.DOWN){
            return new Point2D(1,0);
        }
        else{
            return new Point2D(0,0);
        }
    }
	public static boolean isPowerPelletMode() {
		return powerPelletMode;
	}
	public static void setPowerPelletMode(boolean powerPelletMode) {
		PacManMechanics.powerPelletMode=powerPelletMode;
	}
	public static boolean isYouWin() {
		return youWin;
	}
	public boolean levelComplete () {
		return this.pelletCount == 0;
	}
	public static boolean checkGameOver() {
		--lives;
		return gameOver();
	}
	public static boolean gameOver() {
		return gameOver = true;
	}
	public cellValue getCellValue(int row, int collumn) {
		assert ((row >= 0 && row < this.grid.length) && (collumn >=0 && collumn < this.grid[0].length));
		return this.grid[row][collumn];
	}
	public static Movement getCurrMovement() {
		return currMovement;
	}
	public void setCurrMovement(Movement move) {
		currMovement = move;
	}
	public static Movement getLastMovement() {
		return lastMovement;
	}
	public void setLastMovement(Movement move) {
		lastMovement = move;
	}
	public int getLives() {
		return lives;
	}
	public void setLives(int lives) {
		PacManMechanics.lives=lives;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score=score;
	}
	public void addScore(int points) {
		this.score+=points;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel (int level) {
		this.level = level;
	}
	public int getPelletCount() {
		return pelletCount;
	}
	public void setPelletCount(int pelletCount) {
		this.pelletCount = pelletCount;
	}
	public int getRowCount() {
		return rowCount;
	}
	public int getCollumnCount() {
		return collumnCount;
	}
	public void setRowCount(int rowCount) {
		this.rowCount=rowCount;
	}
	public void setCollumnCount(int collumnCount) {
		this.collumnCount=collumnCount;
	}
	public Point2D getPacLoc() {
		return pacLoc;
	}
	public Point2D getG1Loc() {
		return g1Loc;
	}
	public Point2D getG2Loc() {
		return g2Loc;
	}
	public void setPacLoc(Point2D pacLoc) {
		this.pacLoc=pacLoc;
	}
	public void setG1Loc(Point2D g1Loc) {
		this.g1Loc = g1Loc;
	}
	public void setG2Loc(Point2D g2Loc) {
		this.g2Loc=g2Loc;
	}
	public Point2D getPacVel() {
		return pacVel;
	}
	public Point2D getG1Vel() {
		return g1Vel;
	}
	public Point2D getG2Vel() {
		return g2Vel;
	}
	public void setPacVel(Point2D pacVel) {
		this.pacVel=pacVel;
	}
	public void setG1Vel (Point2D g1Vel) {
		this.g1Vel=g1Vel;
	}
	public void setG2Vel(Point2D g2Vel) {
		this.g2Vel=g2Vel;
	}
}
