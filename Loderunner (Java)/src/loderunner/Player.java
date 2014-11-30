package loderunner;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import loderunner.GameData.BlockType;

public class Player extends SuperCharacter {
	protected boolean isDead;

	private double bodyWidth;
	private double bodyLength;
	private double totalWidth;
	private double legLength;
	protected double totalHeight;
	private double XHeadRad;
	private double YHeadRad;
	private double legAngle;
	private double armLength;
	private Color outlineColor;
	private Color color;
	private double armAngle;
	final static double ANCHOR_POINT_TO_ARM_DISTANCE_MULTIPLIER = .125;
	private boolean isFalling;
	private boolean onRope;
	private Sound music;
	private Zone zone;

	// TODO make sure it can support resizing
	// TODO needs a resize method that resizes everything with pixel width and
	// height
	public Player(int anchorX, int anchorY) {
		this.isDead = false;
		this.anchorPoint = new Point2D.Double(anchorX - 0.5, anchorY - 0.5);
		this.isPlayer = true;
		this.XMOVE_CONSTANT = 0.2;
		this.YMOVE_CONSTANT = 0.2;
		double pixelWidth = LodeWorld.getPixelWidth();
		double pixelHeight = LodeWorld.getPixelHeight();
		this.xProportion = .249;
		this.yProportion = .99;
		this.totalWidth = pixelWidth * this.xProportion;
		this.totalHeight = pixelHeight * this.yProportion;

		this.bodyWidth = pixelWidth / 20;
		this.bodyLength = pixelHeight * .375 * this.yProportion;
		this.XHeadRad = pixelWidth * .3 * this.xProportion;// .075;//.125;
		this.YHeadRad = pixelHeight * .125 * this.yProportion;
		this.legAngle = Math.PI / 2 - Math.atan(0.5 * pixelWidth / (1.5 * pixelHeight));
		double x = pixelWidth * this.xProportion;
		double y = pixelHeight * this.yProportion;
		this.legLength = Math.sqrt(x * x + 9 * y * y) / 8;

		this.armAngle = Math.PI / 2 - Math.atan(0.5 * pixelHeight / (1 * pixelWidth));
		this.armLength = Math.sqrt(x * x + 4 * y * y) / 8;
		this.outlineColor = Color.BLACK;
		this.color = Color.ORANGE;
		this.music = new Sound();

		// super.update();
	}

	public static double getArmToAnchorRatio() {
		return ANCHOR_POINT_TO_ARM_DISTANCE_MULTIPLIER;
	}

	// completely done
	public void drawPlayer(Graphics2D g2d) {
		double cellPixelWidth = LodeWorld.getPixelWidth();
		double cellPixelHeight = LodeWorld.getPixelHeight();
		updateValues(cellPixelWidth, cellPixelHeight);

		g2d.translate(this.anchorPoint.x * cellPixelWidth, this.anchorPoint.y * cellPixelHeight);

		// body done
		g2d.translate(0, -cellPixelHeight / 4);
		Rectangle2D body = new Rectangle2D.Double(-this.bodyWidth / 2, 0, this.bodyWidth, this.bodyLength);
		g2d.setColor(this.outlineColor);
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(body);
		g2d.setColor(this.color);
		g2d.fill(body);
		g2d.translate(0, cellPixelHeight / 4);

		g2d.translate(0, cellPixelHeight / 8);
		g2d.rotate(-Math.PI / 2);
		// legs done
		for (int k = -1; k < 2; k += 2) {
			g2d.setColor(this.outlineColor);

			g2d.rotate(-k * this.legAngle);
			Rectangle2D leg = new Rectangle2D.Double(0, -cellPixelHeight / 40, this.legLength, cellPixelHeight / 20);
			g2d.draw(leg);
			g2d.setColor(this.color);
			g2d.fill(leg);
			g2d.rotate(k * this.legAngle);
		}
		g2d.rotate(Math.PI / 2);
		// translate from legs point to arm point

		g2d.translate(-this.XHeadRad, -this.bodyLength - 2 * this.YHeadRad);
		Ellipse2D head = new Ellipse2D.Double(0, 0, 2 * this.XHeadRad, 2 * this.YHeadRad);
		g2d.setColor(this.outlineColor);
		g2d.draw(head);
		g2d.setColor(this.color);
		g2d.fill(head);
		g2d.translate(this.XHeadRad, (this.YHeadRad * 2)
				+ (cellPixelHeight * this.ANCHOR_POINT_TO_ARM_DISTANCE_MULTIPLIER));

		// arms show up, needs a little fine tuning
		if (this.isFalling || this.onRope) {
			g2d.rotate(Math.PI);
		}

		g2d.translate(this.bodyWidth / 2, 0);
		Rectangle2D arm = new Rectangle2D.Double(0, -cellPixelWidth / 60, (this.armLength), cellPixelHeight / 30);
		g2d.rotate(this.armAngle);
		g2d.setColor(this.outlineColor);
		g2d.draw(arm);
		g2d.setColor(this.color);
		g2d.fill(arm);
		g2d.rotate(-this.armAngle);
		g2d.translate(-this.bodyWidth, 0);

		g2d.rotate(Math.PI);
		g2d.rotate(-this.armAngle);
		g2d.setColor(this.outlineColor);
		g2d.draw(arm);
		g2d.setColor(this.color);
		g2d.fill(arm);

		g2d.rotate(this.armAngle);
		g2d.rotate(-Math.PI);
		if (this.isFalling || this.onRope) {
			g2d.rotate(-Math.PI);
		}

		g2d.translate(this.bodyWidth, this.ANCHOR_POINT_TO_ARM_DISTANCE_MULTIPLIER * cellPixelHeight);
		g2d.translate(-this.anchorPoint.getX() * cellPixelWidth, -this.anchorPoint.getY() * cellPixelHeight);

		// g2d.rotate(-((180 + (26.6 * 2)) * Math.PI) / 180);
		// g2d.translate(0, ((this.bodyLength *
		// this.ANCHOR_POINT_TO_ARM_DISTANCE_MULTIPLIER) + cellPixelWidth *
		// .1));
		// g2d.translate(-this.XHeadRad, -((this.YHeadRad * 2) +
		// (this.bodyLength / 2)));
		// Ellipse2D head = new Ellipse2D.Double(0, 0, this.XHeadRad * 2,
		// this.YHeadRad * 2);
	}

	// public void dig();

	private void updateValues(double pixelWidth, double pixelHeight) {
		// Zone zone = GameData.inWhatZone((int) this.anchorPoint.getX(),
		// (int) this.anchorPoint.getY());
		// // System.out.println(zone.id);
		// if (zone != null) {
		// for (Node node : zone.nodes) {
		// if (node.x == (int) this.anchorPoint.getX()
		// && node.y == (int) this.anchorPoint.getY()) {
		// if (node.down != null) {
		// System.out.println(node.down.direction);
		// }
		// if (node.up != null) {
		// System.out.println(node.up.direction);
		// }
		// if (node.right != null) {
		// System.out.println(node.right.direction);
		// }
		// if (node.left != null) {
		// System.out.println(node.left.direction);
		// }
		// System.out.println();
		// }
		// }
		// }
		this.bodyWidth = pixelWidth / 20;
		this.bodyLength = pixelHeight * .375 * this.yProportion;
		this.XHeadRad = pixelWidth * .3 * this.xProportion;// .075;//.125;
		this.YHeadRad = pixelHeight * .125 * this.yProportion;
		this.legAngle = Math.PI - Math.atan(0.5 * pixelWidth / (1.5 * pixelHeight));
		double x = pixelWidth * this.xProportion;
		double y = pixelHeight * this.yProportion;
		this.legLength = Math.sqrt(x * x + 9 * y * y) / 8;
		this.armAngle = Math.PI / 2 - Math.atan(0.5 * pixelWidth / (1 * pixelHeight));
		this.armLength = Math.sqrt(x * x + 4 * y * y) / 8;
		this.isFalling = this.isFalling();
		this.onRope = this.onRope();
	}
	
	@Override
	protected void updateZone() {
		this.zone = GameData.inWhatZone((int) this.anchorPoint.getX(), (int) this.anchorPoint.getY());
		if (this.zone != null) {
			System.out.println("player is in zone " + this.zone.id);
		}
		if (this.zone == null) {
			// try left side
			this.zone = GameData.inWhatZone((int) this.xL, (int) this.getAnchorPoint().getY());
			if (this.zone == null) {
				// still null, try the right side
				this.zone = GameData.inWhatZone((int) this.xR, (int) this.getAnchorPoint().getY());
				if (this.zone == null) {
					// still null, player is falling
					this.zone = GameData.getZoneUnderFallingPlayer();
					// if (this.guard.anchorPoint.getX() <
					// this.anchorPoint.getX()) {
					// return Direction.right;
					// } else {
					// return Direction.left;
					// }
				}
			}
		}

	}

	public void digRight(int x, int y) {
		int xToDig = x + 1;
		int yToDig = y + 1;
		dig(xToDig, yToDig);
	}

	public void digDown(int x, int y) {
		int xToDig = x;
		int yToDig = y + 1;
		dig(xToDig, yToDig);
	}

	public void digLeft(int x, int y) {
		int xToDig = x - 1;
		int yToDig = y + 1;
		dig(xToDig, yToDig);

	}

	private void dig(int x, int y) {
		if (GameData.getBlock(x, y).equals(GameData.BlockType.Brick)) {
			GameData.setBlock(x, y, GameData.BlockType.Temp);
			GameData.getTemps().put(new Dimension(x, y), 0);
		}

	}

	@Override
	protected void handleGold(int number) {
		double x, y;
		if (number < 3) {
			x = this.xL;
		} else {
			x = this.xR;
		}
		if (number % 2 == 0) {
			y = this.yF;
		} else {
			y = this.yU;
		}
		GameData.setBlock((int) x, (int) y, GameData.BlockType.nothing);
		GameData.incrementPlayerGold();
		GameData.increment();
		System.out.println("score = " + GameData.getScore());
		music.startGold();

	}

	@Override
	protected void handleDeath() {
		this.isDead = true;
		music.startDie();
		Main.reset();
	}
	
	/**
	 * returns the zone the player is in, or if the player is falling, the zone he will land on
	 * @return
	 */
	public Zone getZone() {
		return this.zone;
	}
}
