package loderunner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import loderunner.GameData.BlockType;
import loderunner.GameData.Direction;

@SuppressWarnings("serial")
public class LodeWorld extends JComponent {

	private static GameData.BlockType[][] gameState;// lose lose
	private static int cellNumHeight;
	private static int cellNumWidth;
	private static double cellPixelHeight;
	private static double cellPixelWidth;
	private JFrame frame;
	private static Player player;

	
	
	// TODO: add a list of superCharacters

	public LodeWorld(JFrame frame) {
		gameState = GameData.getGameState();
		this.frame = frame;
		this.setDoubleBuffered(true);
		this.setSize(this.frame.getWidth(), this.frame.getHeight());
		cellNumHeight = 22;
		cellNumWidth = 32;
		cellPixelWidth = (this.getWidth()) / cellNumWidth;
		cellPixelHeight = (this.getHeight()) / cellNumHeight;
		player = GameData.getPlayer();
	}

	// come up with better graphics, ex) rope looking like rope, ladder looking
	// like ladder,
	// need to have bricks look different then boards, but not just different
	// color rectangles
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setBackground(Color.BLACK);
		// g2.setColor(Color.RED);

		cellPixelWidth = (double) (this.getWidth()) / cellNumWidth;
		cellPixelHeight = (double) (this.getHeight()) / cellNumHeight;
		for (int k = 1; k < cellNumWidth + 1; k++) {
			for (int i = 1; i < cellNumHeight + 1; i++) {
				// System.out.println(i + " " + k);
				BlockType block = GameData.getBlock(k - 1, i - 1);
				if (block == GameData.BlockType.Board) {
					g2.setColor(Color.darkGray);
					g2.fillRect((int) ((k - 1) * cellPixelWidth),
							(int) ((i - 1) * cellPixelHeight), (int) cellPixelWidth,
							(int) cellPixelHeight);
					g2.drawRect((int) ((k - 1) * cellPixelWidth),
							(int) ((i - 1) * cellPixelHeight), (int) cellPixelWidth,
							(int) cellPixelHeight);

				}
				if (block == GameData.BlockType.Brick) {
					g2.setColor(Color.red);
					g2.fillRect((int) ((k - 1) * cellPixelWidth),
							(int) ((i - 1) * cellPixelHeight), (int) cellPixelWidth,
							(int) cellPixelHeight);
					g2.drawRect((int) ((k - 1) * cellPixelWidth),
							(int) ((i - 1) * cellPixelHeight), (int) cellPixelWidth,
							(int) cellPixelHeight);
					// System.out.println("he: "+(k*cellPixelWidth));
				}
				if (block == GameData.BlockType.nothing) {
					g2.setColor(Color.black);
					g2.fillRect((int) ((k - 1) * cellPixelWidth),
							(int) ((i - 1) * cellPixelHeight), (int) cellPixelWidth,
							(int) cellPixelHeight);
					g2.drawRect((int) ((k - 1) * cellPixelWidth),
							(int) ((i - 1) * cellPixelHeight), (int) cellPixelWidth,
							(int) cellPixelHeight);

				}
				if (block == GameData.BlockType.FinalLadder) {
					g2.setColor(Color.black);
					g2.fillRect((int) ((k - 1) * cellPixelWidth),
							(int) ((i - 1) * cellPixelHeight), (int) cellPixelWidth,
							(int) cellPixelHeight);
					g2.drawRect((int) ((k - 1) * cellPixelWidth),
							(int) ((i - 1) * cellPixelHeight), (int) cellPixelWidth,
							(int) cellPixelHeight);

				}
				if (block == GameData.BlockType.Rope) {
					g2.setColor(Color.BLACK);
					
					Rectangle2D rope = new Rectangle2D.Double( ((k - 1) * cellPixelWidth),
							 ((i - 1) * cellPixelHeight),  cellPixelWidth,
							 cellPixelHeight);
					g2.draw(rope);
					g2.fill(rope);
					g2.setColor(Color.WHITE);
					Rectangle2D actualRope = new Rectangle2D.Double( ((k - 1) * cellPixelWidth),
							 ((i - 1) * cellPixelHeight+(cellPixelHeight/10)),  cellPixelWidth,
							 cellPixelHeight/20);
					g2.draw(actualRope);
					g2.fill(actualRope);
				}
				if (block == GameData.BlockType.Ladder) {
					g2.setColor(Color.black);
					Rectangle2D ladderBack = new Rectangle2D.Double( ((k - 1) * cellPixelWidth),
							 ((i - 1) * cellPixelHeight),  cellPixelWidth,
							 cellPixelHeight);
					g2.draw(ladderBack);
					g2.fill(ladderBack);
					g2.setColor(Color.white);
					Rectangle2D ladderSideL = new Rectangle2D.Double( ((k - 1) * cellPixelWidth),
							 ((i - 1) * cellPixelHeight),  cellPixelWidth/10,
							 cellPixelHeight);
					Rectangle2D ladderSideR = new Rectangle2D.Double( ((k - 1) * cellPixelWidth+(9*cellPixelWidth/10)),
							 ((i - 1) * cellPixelHeight),  cellPixelWidth/10,
							 cellPixelHeight);
					g2.draw(ladderSideL);
					g2.fill(ladderSideL);
					g2.draw(ladderSideR);
					g2.fill(ladderSideR);
					Rectangle2D ladderStep1 = new Rectangle2D.Double( ((k - 1) * cellPixelWidth),
							 ((i - 1) * cellPixelHeight+(3*cellPixelHeight/4)),  cellPixelWidth,
							 cellPixelHeight/15);
					Rectangle2D ladderStep2 = new Rectangle2D.Double( ((k - 1) * cellPixelWidth),
							 ((i - 1) * cellPixelHeight+(cellPixelHeight/4)),  cellPixelWidth,
							 cellPixelHeight/15);
					g2.draw(ladderStep1);
					g2.fill(ladderStep1);
					g2.draw(ladderStep2);
					g2.fill(ladderStep2);
				}
				if (block == GameData.BlockType.Gold) {
					g2.setColor(Color.black);
					g2.fillRect((int) ((k - 1) * cellPixelWidth),
							(int) ((i - 1) * cellPixelHeight), (int) (cellPixelWidth + 1),
							(int) (cellPixelHeight));
					g2.setColor(Color.yellow);
					int number = 11;
					for (int j = 0; j < number; j++) {
						Rectangle2D rectangle = new Rectangle2D.Double((k - 1) * cellPixelWidth + j
								* cellPixelWidth / (2 * number), (i - 1) * cellPixelHeight
								+ (number - j - 1) * cellPixelHeight / number, (number - j)
								* cellPixelWidth / number, cellPixelHeight / number);
						g2.draw(rectangle);
						g2.fill(rectangle);
					}
					// g2.fillRect((int) ((k-1)*cellPixelWidth + cellPixelWidth
					// / 5), (int) (i
					// * cellPixelHeight + 2 * cellPixelHeight / 5),
					// (int) (3 * cellPixelWidth / 5), (int) (3 *
					// cellPixelHeight / 5));
				}
			}
		}
		player.drawPlayer(g2);
		for (Guard guard : GameData.getGuardList()) {
			guard.drawGuard(g2);
		}
		for (int k = 0; k<32;k++) {
			g2.fillRect((int) (k * cellPixelWidth), 0, 1, this.getHeight());
		}
		for (int k = 0; k<22;k++) {
			g2.fillRect(0,(int) (k * cellPixelHeight), this.getWidth(), 1);
		}


	}

	public static double getPixelHeight() {
		return cellPixelHeight;
	}

	public static double getPixelWidth() {
		return cellPixelWidth;
	}

	// print out the list

}
