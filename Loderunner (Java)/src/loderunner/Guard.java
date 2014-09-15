package loderunner;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import loderunner.GameData.BlockType;
import loderunner.GameData.Direction;

public class Guard extends SuperCharacter {

	private static Direction direction;
	private Color guardColor = Color.CYAN;
	protected double bodyLength;
	private double totalWidth;
	private double legLength;
	private double totalHeight;
	private double XHeadRad;
	private double YHeadRad;
	private double legAngle;
	private double bodyWidth;
	private double armAngle;
	private double armLength;
	private Color outlineColor;
	private Color color;
	private boolean hasGold;
	final static double ANCHOR_POINT_TO_ARM_DISTANCE_MULTIPLIER = .125;
	private boolean onRope;
	private boolean isFalling;
	private Sound music;
	protected AI AI;

	public Guard(int xAnchor, int yAnchor) {
		this.anchorPoint = new Point2D.Double(xAnchor - 0.5, yAnchor - 0.5);
		this.XMOVE_CONSTANT = 0.125;
		this.YMOVE_CONSTANT = 0.125;
		double pixelWidth = LodeWorld.getPixelWidth();
		double pixelHeight = LodeWorld.getPixelHeight();
		this.xProportion = 0.49;
		this.yProportion = 0.99;
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
		this.outlineColor = Color.black;
		this.color = Color.cyan;

		this.hasGold = false;
		this.music = new Sound();
		// super.update();

	}

	public static void setLastMove(Direction go) {
		direction = go;
	}
	public static Direction getLastMove() {
		return direction;
	}
	public void drawGuard(Graphics2D g2d) {
		double cellPixelWidth = LodeWorld.getPixelWidth();
		double cellPixelHeight = LodeWorld.getPixelHeight();
		updateValues(cellPixelWidth, cellPixelHeight);

		g2d.translate(this.anchorPoint.x * cellPixelWidth, this.anchorPoint.y * cellPixelHeight);

		// body done
		g2d.translate(0, -cellPixelHeight / 4);
		Rectangle2D body = new Rectangle2D.Double(-this.bodyWidth / 2, 0, this.bodyWidth,
				this.bodyLength);
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
			Rectangle2D leg = new Rectangle2D.Double(0, -cellPixelHeight / 40, this.legLength,
					cellPixelHeight / 20);
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
		if(this.isFalling || this.onRope){
			g2d.rotate(Math.PI);
			}
		g2d.translate(this.bodyWidth / 2, 0);
		Rectangle2D arm = new Rectangle2D.Double(0, -cellPixelWidth / 60, (this.armLength),
				cellPixelHeight / 30);
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

		g2d.translate(this.bodyWidth, this.ANCHOR_POINT_TO_ARM_DISTANCE_MULTIPLIER
				* cellPixelHeight);
		g2d.translate(-this.anchorPoint.getX() * cellPixelWidth, -this.anchorPoint.getY()
				* cellPixelHeight);

		// g2d.setColor(guardColor);
		// double cellPixelWidth = LodeWorld.getPixelWidth();
		// g2d.translate(this.anchorPoint.x, this.anchorPoint.y);
		//
		// g2d.translate(-cellPixelWidth / 20, -this.bodyLength / 2);
		// Rectangle2D body = new Rectangle2D.Double(0, 0, cellPixelWidth / 10,
		// this.bodyLength);
		// g2d.translate(cellPixelWidth / 20, this.bodyLength / 2);
		// g2d.translate(
		// 0,
		// -((this.bodyLength * this.ANCHOR_POINT_TO_ARM_DISTANCE_MULTIPLIER) +
		// cellPixelWidth * .1));
		//
		// g2d.rotate(((180 - 26.6) * Math.PI) / 180);
		// Rectangle2D arms = new Rectangle2D.Double(0, 0, this.totalWidth / 2,
		// cellPixelWidth / 10);
		// g2d.rotate(((26.6 * 2) * Math.PI) / 180);
		// g2d.draw(arms);
		// g2d.rotate(-((180 + (26.6 * 2)) * Math.PI) / 180);
		// g2d.translate(
		// 0,
		// ((this.bodyLength * this.ANCHOR_POINT_TO_ARM_DISTANCE_MULTIPLIER) +
		// cellPixelWidth * .1));
		// g2d.translate(-this.XHeadRad, -((this.YHeadRad * 2) +
		// (this.bodyLength / 2)));
		// Ellipse2D head = new Ellipse2D.Double(0, 0, this.XHeadRad * 2,
		// this.YHeadRad * 2);
		// g2d.setColor(Color.black);

	}

	private void updateValues(double pixelWidth, double pixelHeight) {
		// TODO Auto-generated method stub
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
	protected void handleDeath() {
		int counter = 0;
		if (this.hasGold) {
			while (GameData.getBlock(this.anchorPoint.getX(), this.anchorPoint.getY() - counter) == GameData.BlockType.nothing) {
				counter++;
			}
			GameData.setBlock((int) this.anchorPoint.getX(), (int) this.anchorPoint.getY() - counter - 1, GameData.BlockType.Gold);
		}
		Random rand = new Random();
		this.anchorPoint = new Point2D.Double(rand.nextInt(32) + 0.5, 0.5);

	}

	@Override
	protected void handleGold(int number) {
		if (!this.hasGold) {
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
			this.hasGold = true;
			music.startAlarm();
		}
	}

	protected boolean inTemp() {
		if ((this.upperLeftBlock == GameData.BlockType.Temp
				&& this.upperRightBlock == GameData.BlockType.Temp
				&& this.lowerLeftFootBlock == GameData.BlockType.Temp && this.lowerRightFootBlock == GameData.BlockType.Temp)) {
			GameData.setBlock((int) this.anchorPoint.getX(), (int) this.anchorPoint.getY(),
					BlockType.GuardInTemp);
			return true;
		} else {
			return false;
		}
	}

}
