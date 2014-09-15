package loderunner;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//Stores data for current game
public class GameData {
	private static int score = 0;
	BufferedReader reader;
	private int gold;
	private int numEnemies;
	private int goldRemaining;
	private String file;
	private int numberOfGuards;
	private String ERROR_STRING = "I don't know how this error occured";
	private int errorPlayerCounter;
	private ArrayList<Node> nodes;
	private static Guard[] guardList;
	private static Player player;
	private static ArrayList<Zone> zones;
	private static int worldGoldCount = 0;
	private static int NextLevel=1;
	private static int currentLevel=0;
	private String firstLevel = "src/NewLodeLevel.txt";
	private String secondLevel = "src/LodeLevel2.txt";
	private String thirdLevel = "src/LodeLevel3.txt";
	private static Sound music;
	private static GameData.BlockType[][] gameState;
	private static HashMap<Dimension, Integer> temps;
	private static int playerGoldCount = 0;

	public GameData() {
		temps = new HashMap<Dimension, Integer>();
		gameState = new GameData.BlockType[34][23];
		player = new Player(-1,-1);
		this.file = this.firstLevel;
		this.populateMap();
		this.music = new Sound();
	}

	public static enum BlockType {
		nothing(0), Brick(1), Board(2), Ladder(3), Rope(4), FinalLadder(5), Temp(6), Gold(7), GuardInTemp(
				8), nextLevelPortal(9);

		private int value;

		private BlockType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	/**
	 * direction enum, lets you select the different direction you can move
	 * 
	 * @author rosspa
	 * 
	 */
	public static enum Direction {
		left(-1), up(-1), none(0), right(1), down(1), falling(0.5);

		private double value;

		private Direction(double value) {
			this.value = value;
		}

		public double getValue() {
			return value;
		}

		/**
		 * checks to see if the direction is horizontal or not
		 * 
		 * @return
		 */
		public boolean isHorizontal() {
			if (this == Direction.left || this == Direction.right) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static Guard[] getGuardList() {
		return guardList;
	}

	/**
	 * This method draws the escape route for the player
	 */
	public static void setWin() {
		System.out.println("saldkjfkajsfdlkdsaflja");
		for (int k = 0; k < 32; k++) {
			for (int i = 0; i < 19; i++) {
//				System.out.print("k: " + k + "i: " + i + " ");
				if (gameState[k][i] == BlockType.FinalLadder) {
					gameState[k][i] = BlockType.Ladder;
					System.out.println(k + " " + i);
				}
			}
		}
		music.startWin();

	}

	/*
	 * sets score to new score, used for bonus points and reset.
	 */
	public static void setScore(int newScore) {
		score = newScore;
	}

	/*
	 * adds 1 to score
	 */
	public static void increment() {
		score++;
		System.out.println(score);
	}

	public static void incrementPlayerGold() {
		playerGoldCount++;
		System.out.println("Player gold count: " + playerGoldCount + " ");

	}

	/*
	 * returns current score
	 */
	public static int getScore() {
		return score;
	}

	/*
	 * returns the remaining amount of gold on the level
	 */
	public static int getPlayerGold() {
		return playerGoldCount;
	}

	public static int getWorldGold() {
		return worldGoldCount;
	}

	public static HashMap<Dimension, Integer> getTemps() {
		return temps;
	}

	public static BlockType[][] getGameState() {
		return gameState;
	}

	public void generateNextLevel() {

		this.file = "src/LodeLevel2.txt";
	}

	// generate nodes here
	private void populateMap() {
		int guardCounter = 0;
		this.errorPlayerCounter = 0;
		zones = new ArrayList<Zone>();
		nodes = new ArrayList<Node>();
		try {
			reader = new BufferedReader(new FileReader(this.file));
			String text;
			int tempBlock = 0;
			int rowCount = 0;
			while ((text = reader.readLine()) != null) {
				int currentColumn = 0;
				for (int k = 0; k < text.length(); k++) {
					if (currentColumn == 0 && rowCount == 0) {
						this.numberOfGuards = Character.getNumericValue(text.charAt(k));
						guardList = new Guard[this.numberOfGuards];
						System.out.print("s3");
						currentColumn++;
					} else {
						System.out.print("s4");
						tempBlock = Character.getNumericValue(text.charAt(k));
						if (tempBlock == 16) {
							tempBlock = 0;

							if (guardCounter >= this.numberOfGuards) {
								ERROR_STRING = String.format(
										"\nyo dawg, you be lying to my face,\n"
												+ "you said there were only %d guards,\n"
												+ "but a bunch more showed up,\n"
												+ "don't be pulling that shit on me again brah\n",
										this.numberOfGuards);
								throw new IllegalArgumentException();
							}
							guardList[guardCounter] = new Guard(currentColumn, rowCount);
							guardCounter++;
						} else if (tempBlock == 25) {
							tempBlock = 0;
							this.errorPlayerCounter++;
							player.setAnchorPoint(currentColumn, rowCount-0.5);
						} else if (tempBlock == 7) {
							worldGoldCount++;
						}

						gameState[currentColumn][rowCount] = reverseBlockType(tempBlock);
						currentColumn++;
					}
				}
				rowCount++;
				if (rowCount > 22) {
					break;
				}

			}
			if (this.errorPlayerCounter == 0) {
				ERROR_STRING = String.format("yo dawg, where'd you go???\n"
						+ "I can't see you anywhere!!!\n" + "you better get your ass down here\n"
						+ "before I have to have my guys get your little cracker ass");
				throw new IllegalArgumentException();

			}
			if (this.errorPlayerCounter > 1) {
				ERROR_STRING = String.format("Whoa whoa whoa dawg, we gots a problem,\n"
						+ "someone be trying to impersonate you!\n"
						+ "my guys be saying that there be more then one playa,\n"
						+ "and this town ain't big enough for more then one playa,\n"
						+ "so take care of that shit and come back\n");
				throw new IllegalArgumentException();
			}

			if (guardCounter < this.numberOfGuards) {
				ERROR_STRING = String.format("\nyo dawg, you said there were %d guards,\n"
						+ "but I only counted %d guards, count better brah\n", this.numberOfGuards,
						guardCounter);
				throw new IllegalArgumentException();
			}
			for (Guard guard : guardList) {
				guard.AI = new AI(guard, player);
			}
			this.loadZones();
			this.loadNodes();
			this.refineNodes();
			this.printNodes();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println(ERROR_STRING);
			System.exit(1);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void printNodes() {
		for (Zone zone : zones) {
			for (Node node : zone.nodes) {
//				setBlock(node.x, node.y, BlockType.Gold);
				System.out.println(node.toString());
			}
			System.out.println();
		}
	}

	private void refineNodes() {
		for (Zone zone : zones) {
			if (zone.block == BlockType.Rope) {
				Node leftNode;
				Node currentNode;
				Node rightNode;
				leftNode = zone.nodes.get(0);
				currentNode = zone.nodes.get(1);
				rightNode = zone.nodes.get(2);
				for (int k = 1; k < zone.nodes.size() - 1; k++) {
					if (leftNode.down.zoneToGo.id == currentNode.down.zoneToGo.id
							&& currentNode.down.zoneToGo.id == rightNode.down.zoneToGo.id) {
						currentNode.delete = true;
					} else {
						currentNode.delete = false;
					}
					leftNode = currentNode;
					currentNode = rightNode;
					rightNode = zone.nodes.get(k + 1);
				}
				Iterator<Node> itr = zone.nodes.iterator();
				while (itr.hasNext()) {
					Node blah = itr.next();
					if (blah.delete) {
						itr.remove();
					}
				}
//				for (Node node : zone.nodes) {
//					if(node.delete) {
//						node.down = null;
//					}
//				}
			}
		}

	}

	public static Zone inWhatZone(int x, int y) {
		for (Zone zone : zones) {
			if (zone.isInZone(x, y)) {
				return zone;
			}
		}
		return null;
	}

	public static Zone inWhatZone(int x, int y, int id) {
		for (Zone zone : zones) {
			if (zone.isInZone(x, y)) {
				if (zone.id != id) {
					return zone;
				}
			}
		}
		return null;
	}

	private void loadNodes() {
		for (Zone zone : zones) {
			if (zone.tall) {
				checkTall(zone);
			} else {
				boolean boo;
				if (zone.block == BlockType.Rope) {
					boo = true;
				} else {
					boo = false;
				}
				checkFlat(zone, boo);
			}
		}
	}

	private void checkFlat(Zone zone, boolean boo) {
		System.out.println(zone.id);
		if (zone.id == 23) {
			System.out.println(3);
		}
		// if (zone.x1 - 1 >= 0) {
		// Node head = new Node(zone.x1 - 1, zone.y1, zone, true);
		// if (!head.zonesCanGo.isEmpty()) {
		// zone.addNode(head);
		// }
		// }
		for (int k = 0; k <= zone.length; k++) {
			Node node = new Node(zone.x1 + k, zone.y1, zone, boo);
			if (node.down != null || node.up != null || node.left != null || node.right != null) {
				zone.addNode(node);
			}
		}
		// if (zone.x2 + 1 < 32) {
		// Node tail = new Node(zone.x2 + 1, zone.y1, zone, true);
		// if(!tail.zonesCanGo.isEmpty()) {
		// zone.addNode(tail);
		// }
		// }
		System.out.println("done");
		System.out.println();
	}

	private void checkTall(Zone zone) {
		if (zone.y1 - 1 >= 0) {
			Node head = new Node(zone.x1, zone.y1 - 1, zone, false);
			if (head.down != null || head.up != null || head.left != null || head.right != null) {
				zone.addNode(head);
			}
		}
		for (int k = 0; k <= zone.length; k++) {
			Node node = new Node(zone.x1, zone.y1 + k, zone, false);
			if (node.down != null || node.up != null || node.left != null || node.right != null) {
				zone.addNode(node);
			}
		}
		// if (zone.y2 + 1 < 20) {
		// Node tail = new Node(zone.x1, zone.y2 + 1, zone, true);
		// }
		System.out.println("done");
		System.out.println();

	}

	private void loadZones() {
		this.findPlatforms();
		this.findLadders();
	}

	private void findLadders() {
		int counter = zones.size();
		Zone zone;
		int x, y;
		x = -1;
		while (x < 31) {
			x++;
			y = -1;
			while (y < 20) {
				y++;
				BlockType currentCell = this.getCell(x, y);
				if (currentCell == BlockType.Ladder) {
					int startX = x;
					int startY = y;
					int length = this.checkHowTall(x, y);
					zone = new Zone(startX, startY, length, counter, currentCell);
					zones.add(zone);
					y += zone.length;
					counter++;
				}
			}
		}
	}

	private void findPlatforms() {
		Zone zone;
		int counter = 0;
		int x, y = -1;
		while (y < 20) {
			y++;
			x = -1;
			while (x < 31) {
				x++;
				BlockType currentCell = this.getCell(x, y);
				if (currentCell == BlockType.Board || currentCell == BlockType.Brick
						|| currentCell == BlockType.Rope) {
					int startX = x;
					int startY = y;
					int length = this.checkHowLong(x, y);
					zone = new Zone(startX, startY, length, counter, currentCell);
					zones.add(zone);
					// System.out.println(zone.x1 + " " + zone.x2);
					// System.out.println(zone.y1 + " " + zone.y2);
					// System.out.println();
					x += zone.length;
					counter++;
				}
			}
		}
		// System.out.println(zones.size());
	}

	private int checkHowTall(int x, int y) {
		int count = 0;
		BlockType currentBlock = this.getCell(x, y);
		BlockType nextBlock = this.getCell(x, y + 1);
		while (nextBlock == currentBlock) {
			if (y + count + 1 >= 20) {
				break;
			}
			count++;
			currentBlock = nextBlock;
			nextBlock = this.getCell(x, y + 1 + count);
		}
		return count;
	}

	private int checkHowLong(int x, int y) {
		int count = 0;
		BlockType currentBlock = this.getCell(x, y);
		BlockType nextBlock = this.getCell(x + 1, y);
		if (nextBlock == BlockType.Board) {
			nextBlock = BlockType.Brick;
		}
		while (nextBlock == currentBlock) {
			if (x + count + 1 >= 32) {
				break;
			}
			count++;
			currentBlock = nextBlock;
			nextBlock = this.getCell(x + count + 1, y);
			if (nextBlock == BlockType.Board) {
				nextBlock = BlockType.Brick;
			}
		}
		return count;
	}

	public BlockType reverseBlockType(int type) {
		if (type == 0) {
			return BlockType.nothing;
		}
		if (type == 1) {
			return BlockType.Brick;
		}
		if (type == 2) {
			return BlockType.Board;
		}
		if (type == 3) {
			return BlockType.Ladder;
		}
		if (type == 4) {
			return BlockType.Rope;
		}
		if (type == 5) {
			return BlockType.FinalLadder;
		}
		if (type == 6) {
			return BlockType.Temp;
		}
		if (type == 7) {
			return BlockType.Gold;
		}
		if (type == 9)
			return BlockType.nextLevelPortal;
		return null;

	}

	public BlockType getCell(int x, int y) {
		return gameState[x + 1][y + 1];
	}

	/**
	 * checks to see if the block is eligible to be moved through if moving in
	 * the x axis
	 * 
	 * @param block
	 * @return
	 */
	public static boolean isXBlocked(BlockType block) {
		if (block == BlockType.nothing || block == BlockType.Ladder || block == BlockType.Rope
				|| block == BlockType.Temp || block == BlockType.Gold
				|| block == BlockType.FinalLadder || block==BlockType.nextLevelPortal) { // if is nothing or
			return false; // ladder, or rope, return false, else return true;
		} else {
			return true;
		}
	}

	/**
	 * checks to see if the block is eligible to be moved through if moving in
	 * the y axis
	 * 
	 * @param block
	 * @return
	 */
	// possibly make abstract
	public static boolean isYBlocked(BlockType block, GameData.Direction direction) {
		// can go up if block is a ladder or nothing
		// can allow for nothing to be a valid block to not be blocked by
		// because the game won't let you go up unless you are on a ladder
		if (direction == GameData.Direction.up) {
			if (block == BlockType.Ladder || block == BlockType.nothing
					|| block == BlockType.FinalLadder || block == BlockType.nextLevelPortal) {
				return false;
			} else {
				return true;
			}
			// can go down through nothing, ladders, gold, and temps
		} else if (block == BlockType.nothing || block == BlockType.Ladder
				|| block == BlockType.Temp || block == BlockType.Gold
				|| block == BlockType.nextLevelPortal) {
			// return false, else true
			return false;
		} else {
			return true;
		}
	}

	public static BlockType setBlock(int x, int y, GameData.BlockType block) {
		return gameState[x + 1][y + 1] = block;
	}

	// convert to take double and cast here
	public static BlockType getBlock(double x, double y) {
		int getX = (int) (x + 1);
		int getY = (int) (y + 1);
		return gameState[getX][getY];

	}

	public static Player getPlayer() {
		return player;
	}
	public static void setCurrentLevel(int num)
	{
		
		currentLevel=num;
	}
	public static int getCurrentLevel()
	{
		
		return currentLevel;
	}
	public static void setNextLevel(int num)
	{
		
		NextLevel=num;
	}
	public static int getNextLevel()
	{
		
		return NextLevel;
	}
	public void regenerateLevel() {
		currentLevel=NextLevel;
		if(currentLevel==0 || player.isDead)
		{
			currentLevel=NextLevel;
			worldGoldCount=0;

			this.file=firstLevel;
			this.populateMap();
			//currentLevel++;
			
		}
		if(currentLevel==1)
		{
			worldGoldCount=0;
			this.file=secondLevel;
			this.populateMap();
			currentLevel++;
			
			
		}
		if(currentLevel==2)
		{
			worldGoldCount=0;
			this.file=secondLevel;
			this.populateMap();
			currentLevel++;			
		}
	}

	public static ArrayList<Zone> getZones() {
		return zones;
	}

}
