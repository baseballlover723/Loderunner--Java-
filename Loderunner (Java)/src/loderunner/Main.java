package loderunner;

//TODO make death handler
//TODO convert is in world
// implement gold

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import loderunner.GameData.Direction;

/**
 * @author Team #1, Philip, Sam, Chace
 */
public class Main {
	private static LodeWorld lodeWorld;
	private static int windowWidth;
	private static int windowHeight;
	private static float columnWidth;
	private static float rowHeight;
	private static Player player;
	private static JFrame frame;
	private static boolean paused;
	private static final int FRAME_RATE = 30
			;
	private static final int DELAY_MS = 1000 / FRAME_RATE;
	private static final int COUNTER_LIMIT = 5 * FRAME_RATE;
	private static GameData gameData;
	private static boolean levelWon = false;
	public static Sound music;
	private static StartPage startPage;
	private static String score1;
	private static String score2 = "";
	private static String score3 = "";
	public static String s1;
	public static String s2;
	public static String s3;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		paused = false;
		frame = new JFrame();
		music = new Sound();
		music.startBackground();
		int frameWidth = 1400;
		int frameHeight = 900;
		frame.setTitle("Lode Runner");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(500, 500));
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		gameData = new GameData();
		// startPage = new StartPage(frame);
		// deathScreen = new DeathScreen(frame);
		lodeWorld = new LodeWorld(frame);

		frame.setSize(frameWidth + 18, frameHeight + 45);
		frame.add(lodeWorld);

		player = GameData.getPlayer();
		HighScores highScores = new HighScores(frame);
		ArrayList<Integer> highScoreList = highScores.load();
		class keyListener implements KeyListener {

			// TODO, check to see if the move could be put in the game loop,
			// TODO check to see if out of bounds
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO fix, not moving
				// System.out.println(1);
				GameData.Direction direction = GameData.Direction.none;
				int keyCode = e.getKeyCode();
				switch (keyCode) {
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					if (player.onLadder()) {
						direction = GameData.Direction.up;
					} else {
						direction = GameData.Direction.none;
					}
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					direction = GameData.Direction.down;
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					direction = GameData.Direction.left;
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					direction = GameData.Direction.right;
					break;
				case KeyEvent.VK_C:
					direction = GameData.Direction.none;
					player.digRight((int) player.getAnchorPoint().getX(), (int) player
							.getAnchorPoint().getY());
					break;
				case KeyEvent.VK_X:
					direction = GameData.Direction.none;
					player.digDown((int) player.getAnchorPoint().getX(), (int) player
							.getAnchorPoint().getY());
					break;
				case KeyEvent.VK_Z:
					direction = GameData.Direction.none;
					player.digLeft((int) player.getAnchorPoint().getX(), (int) player
							.getAnchorPoint().getY());
					break;
				case KeyEvent.VK_P:
					paused = !paused;
					System.out.println(paused);
					break;
				case KeyEvent.VK_Q:
					System.exit(0);
					break;
				case KeyEvent.VK_PAGE_UP:
					paused = !paused;
					gameData.setNextLevel(GameData.getCurrentLevel() + 1);
					gameData.regenerateLevel();
					levelWon = false;
					paused = !paused;
					break;
				// only can go up level then down and then back up
				case KeyEvent.VK_PAGE_DOWN:
					paused = !paused;
					gameData.setNextLevel(0);
					gameData.regenerateLevel();
					levelWon = false;
					paused = !paused;
					break;

				default:
					direction = GameData.Direction.none;
				}
				if (!paused) {
					if (!player.isDead) {
						if (!player.isFalling()) {
							player.move(direction);
							// GameData.setNextLevel(GameData.getCurrentLevel());
						}
					}
				}

				// frame.repaint();

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				// System.out.println(2);

			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				// System.out.println(3);
			}

		}
		frame.addKeyListener(new keyListener());
		// GAME LOOP
		long start;
		long end;
		Iterator<Entry<Dimension, Integer>> itr;
		while (true) {
			// System.out.println(levelWon);
			if (player.onFinalLadder()) {
				GameData.setNextLevel(GameData.getCurrentLevel() + 1);
				gameData.regenerateLevel();
				levelWon = false;
			}
			// magical print statment, pause feature doesn't work without it
			if (!paused) {
				System.out.println();
			}
			if (!paused) {
				// System.out.println("gold remaining on level = " +
				// GameData.getWorldGold());
				// System.out.println("gold held by the player = " +
				// GameData.getPlayerGold());
				// System.out.println("Player Gold: " + GameData.getPlayerGold()
				// + " World Gold: "
				// + GameData.getWorldGold() + "levelwon = "+ levelWon);
				if (GameData.getPlayerGold() >= GameData.getWorldGold() && !levelWon) {
					GameData.setWin();
					// System.out.println("Win");
					levelWon = true;
				}
				player.update();
				if (player.isFalling()) {
					player.fall();
				}
				if (player.isDead) {
					System.out.println("DEAD");
					player.isDead= false;
					// GameData.setNextLevel(GameData.getCurrentLevel());
					// gameData.regenerateLevel();
					continue;
				}
				System.out.println(player.anchorPoint);
				start = System.nanoTime();
				// System.out.println("*");
				for (Guard guard : GameData.getGuardList()) {
					guard.update();
					if ((!guard.onRope() && !guard.inTemp()) && guard.isFalling()) {
						guard.fall();
					} else {
						long startTime = System.nanoTime();
						Direction direction = guard.AI.think();
						System.out.println("took " + (System.nanoTime() - startTime)/1_000_000.0 + " MS");
						System.out.println("Guard with go " + direction.toString());
						if (direction == Direction.none) {
							direction = guard.getLastMove();
						}
						guard.setLastMove(direction);
						// System.out.println("Direction to go is " +
						// direction);
						guard.move(direction);
						// System.out.println("Guard box = " +
						// guard.boundingRectangle.toString());
					}

				}
				// System.out.println("player box = " +
				// player.boundingRectangle.toString());
				// System.out.println();

				// lodeWorld.repaint();
				frame.repaint();
				itr = gameData.getTemps().entrySet().iterator();
				Entry<Dimension, Integer> dimension;
				int counter;
				while (itr.hasNext()) {
					dimension = itr.next();
					counter = dimension.getValue();
					if (counter < COUNTER_LIMIT) {
						dimension.setValue(counter + 1);
					} else {
						GameData.setBlock(dimension.getKey().width, dimension.getKey().height,
								GameData.BlockType.Brick);
						itr.remove();
					}
				}
				end = System.nanoTime();
				try {
					long delay = (DELAY_MS - (end - start) / 1000000);
					if (delay < 0) {
						Thread.sleep(0);
					} else {
						Thread.sleep(delay);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// reset();
		// int score = GameData.getScore();
		// highScores.addScore(score);
		// String s1 = new String();
		// String s2 = new String();
		// String s3 = new String();
		// int place = highScoreList.indexOf(score) + 1;
		// double percentile = place / (double) highScoreList.size();
		// System.out.println();
		// for (int k = 0; k < highScoreList.size(); k++) {
		// System.out.printf("%d: %d\n", k + 1, highScoreList.get(k));
		// }
		// System.out.printf("You got %d points!\n", score);
		// s1 = ("You scored:" + score);
		// System.out.printf("Thats good for %dth place\n", place);
		// s2 = (" and placed:" + place);
		// System.out.printf("Congratulations!!! ");
		// s3 = (" Congratulations!!! ");
		// if (place == 1) {
		// score1 = "You have the high score";
		// score2 = "You be winner!!!";
		// } else if (score == 0) {
		// score1 = "You are a complete failure at this game";
		//
		// } else if (place == 2) {
		// score1 = "You got second place!!!";
		//
		// } else if (place == 3) {
		// score1 = "You got Third place!!!";
		//
		// } else if (percentile < .1) {
		// score1 = "You placed in the top 10%!!!";
		// } else if (percentile < .25) {
		// score1 = "You placed in the top 25%!";
		// } else if (percentile < 0.5) {
		// score1 = "You placed in the top 50%";
		// score2 = "Hey, at least you don't suck at this game";
		// } else if (percentile <= 0.9) {
		// score1 = "You've unlocked \"Learning How To NOT Suck!\"";
		// } else {
		// score1 = "You are a complete failure at this game";
		// }
		// if (score == 0) {
		// score3 = "brah, can you even...";
		// }
	}

	public static void reset() {

		// JOptionPane.showMessageDialog(frame, "you died, well this sucks");
		// gameData.setLevelCount(gameData.getLevelCount());
		GameData.reset();
		HighScores highScores = new HighScores(frame);
		ArrayList<Integer> highScoreList = highScores.load();
		int score = GameData.getScore();
		highScores.addScore(score);
		String s1 = new String();
		String s2 = new String();
		String s3 = new String();
		int place = highScoreList.indexOf(score) + 1;
		double percentile = place / (double) highScoreList.size();
		System.out.println();
		for (int k = 0; k < highScoreList.size(); k++) {
			System.out.printf("%d: %d\n", k + 1, highScoreList.get(k));
		}
		System.out.printf("You got %d points!\n", score);
		s1 = ("You scored:" + score);
		System.out.printf("Thats good for %dth place\n", place);
		s2 = (" and placed:" + place);
		System.out.printf("Congratulations!!! ");
		s3 = (" Congratulations!!! ");
		if (place == 1) {
			score1 = "You have the high score";
			score2 = " You be winner!!!";
		} else if (score == 0) {
			score1 = "You are a complete failure at this game";

		} else if (place == 2) {
			score1 = "You got second place!!!";

		} else if (place == 3) {
			score1 = "You got Third place!!!";

		} else if (percentile < .1) {
			score1 = "You placed in the top 10%!!!";
			score1 = "You placed in the top 50%";
			score2 = " Hey, at least you don't suck at this game";
		} else if (percentile <= 0.9) {
			score1 = "You've unlocked \"Learning How To NOT Suck!\"";
		} else {
			score1 = "You are a complete failure at this game";
		}
		if (score == 0) {
			score3 = " brah, can you even...";
		}
		GameData.setScore(0);
		JOptionPane.showMessageDialog(frame, s1 + s2 + s3 + "\n" + score1 + score2 + score3);
		// gameData.setLevelCount(0);
		gameData.setNextLevel(gameData.getCurrentLevel());
		gameData.regenerateLevel();

	}
}
