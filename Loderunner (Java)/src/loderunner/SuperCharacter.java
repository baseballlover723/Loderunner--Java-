package loderunner;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;

import loderunner.GameData.BlockType;

abstract public class SuperCharacter {
	protected double XMOVE_CONSTANT;
	protected double YMOVE_CONSTANT;
	protected Point2D.Double anchorPoint;
	protected double xProportion;
	protected double yProportion;

	protected double xL;
	protected double xR;
	protected double yU;
	protected double yD;
	protected double yF;
	protected BlockType upperLeftBlock;
	protected BlockType upperRightBlock;
	protected BlockType lowerLeftBlock;
	protected BlockType lowerRightBlock;
	protected BlockType lowerLeftFootBlock;
	protected BlockType lowerRightFootBlock;
	protected Rectangle2D boundingRectangle;

	protected boolean isPlayer = false;

	// TODO, pass it starting point
	public SuperCharacter() {
		// instanstates a invalid bounding rectangle
		// bounding rectangle gets set to a valid rectangle in update
		this.boundingRectangle = new Rectangle2D.Double(-1, -1, -1, -1);
	}

	/**
	 * updates the character bounding box and the blocks at those points
	 */
	protected boolean update() {
		// if the character is in the center of a block, each bounding block
		// will be the same as the block at the anchor point
		// bounding box coordinates
		this.xL = (this.anchorPoint.getX() - this.xProportion / 2);
		this.xR = (this.anchorPoint.getX() + this.xProportion / 2);
		// top of the head
		this.yU = (this.anchorPoint.getY() - this.yProportion / 2);
		// just above feet
		this.yF = (this.anchorPoint.getY() + this.yProportion / 2);
		// just below feet
		this.yD = (this.anchorPoint.getY() + this.yProportion / 2 + 0.015);
		this.upperLeftBlock = GameData.getBlock(this.xL, this.yU);
		this.upperRightBlock = GameData.getBlock(this.xR, this.yU);
		this.lowerLeftFootBlock = GameData.getBlock(this.xL, this.yF);
		this.lowerRightFootBlock = GameData.getBlock(this.xR, this.yF);
		this.lowerLeftBlock = GameData.getBlock(this.xL, this.yD);
		this.lowerRightBlock = GameData.getBlock(this.xR, this.yD);
		this.boundingRectangle.setRect(this.xL, this.yU, this.xProportion, this.yProportion);

		this.updateZone();
		if (this.upperLeftBlock == GameData.BlockType.Gold) {
			this.handleGold(1);
		} else if (this.lowerLeftFootBlock == GameData.BlockType.Gold) {
			this.handleGold(2);
		} else if (this.upperRightBlock == GameData.BlockType.Gold) {
			this.handleGold(3);
		} else if (this.lowerRightFootBlock == GameData.BlockType.Gold) {
			this.handleGold(4);
		} else if (checkDead()) {
			this.handleDeath();
			System.out.println("I'm dead!!!!");
			return true;
		}
		return false;
	}

	abstract protected void updateZone();

	/**
	 * handles what happens when the character dies.
	 */
	abstract protected void handleDeath();

	/**
	 * checks to see if the character is dead by checking to see if the
	 * character intersects with any blocks not abstract because they are
	 * similar enough and I want to keep isXBlocked private
	 * 
	 * @return
	 */
	private boolean checkDead() {
		if (this.isDeathBlock(this.upperLeftBlock) || this.isDeathBlock(this.lowerLeftFootBlock)
				|| this.isDeathBlock(this.upperRightBlock) || this.isDeathBlock(this.lowerRightFootBlock)) {
			return true;
		} else {
			if (isPlayer) {
				for (Guard guard : GameData.getGuardList()) {
					if (this.boundingRectangle.intersects(guard.boundingRectangle)) {
						return true;
					}
				}
			}
			return false;
		}

	}

	private boolean isDeathBlock(BlockType block) {
		if (block == GameData.BlockType.nothing || block == GameData.BlockType.Ladder
				|| block == GameData.BlockType.Rope || block == GameData.BlockType.Temp
				|| block == GameData.BlockType.Gold || block == GameData.BlockType.GuardInTemp
				|| block == GameData.BlockType.FinalLadder || block == GameData.BlockType.nextLevelPortal) { // if
																												// is
																												// nothing
																												// or
			return false;
		} else {
			return true;
		}
	}

	/**
	 * handles what to do when gold is touched, the number corresponds to which
	 * corner of the bounding box that the gold is touching so the location of
	 * the gold is know
	 * 
	 * @param number
	 */
	protected abstract void handleGold(int number);

	/**
	 * move the character in the direction if it can by a arbitraty constant
	 * 
	 * @param direction
	 */
	public void move(GameData.Direction direction) {
		if (!blocked(direction)) {
			if (direction.equals(GameData.Direction.left) || direction.equals(GameData.Direction.right)) {
				this.anchorPoint.x += direction.getValue() * XMOVE_CONSTANT;
			} else {
				this.anchorPoint.y += direction.getValue() * YMOVE_CONSTANT;
			}
		}
	}

	public void fall() {
		this.anchorPoint.y += GameData.Direction.falling.getValue() * YMOVE_CONSTANT;
	}

	/**
	 * returns the set anchorPoint
	 * 
	 * @return
	 */
	public Point2D getAnchorPoint() {
		return this.anchorPoint;
	}

	public void setAnchorPoint(double x, double y) {
		this.anchorPoint.x = x;
		this.anchorPoint.y = y;
	}

	// public boolean isInWorld() {
	// if (this.getAnchorPoint().getX() > LodeWorld.getPixelWidth()
	// || this.getAnchorPoint().getX() < 0) {
	//
	// return false;
	// }
	// if (this.getAnchorPoint().getY() > LodeWorld.getPixelHeight()
	// || this.getAnchorPoint().getY() < 0) {
	//
	// return false;
	// } else
	// return true;
	//
	// }
	//
	// public boolean isInWorld(Direction direction) {
	// if (direction.isHorizontal()) {
	// return isInWorldVertical(direction);
	// } else {
	// return isInWorldHorizontal(direction);
	//
	// }
	//
	// }
	//
	// public boolean isInWorldVertical(Direction direction) {
	// if (this.anchorPoint.getY() + (YMOVE_CONSTANT + this.yProportion / 2)
	// * direction.getValue() > LodeWorld.getPixelHeight()
	// || this.anchorPoint.getY() + (YMOVE_CONSTANT + this.yProportion / 2)
	// * direction.getValue() < 0) {
	//
	// return false;
	// }
	// return true;
	//
	// }
	//
	// public boolean isInWorldHorizontal(Direction direction) {
	// if (direction.isHorizontal()) {
	// if (this.anchorPoint.getX() + (XMOVE_CONSTANT + this.xProportion / 2)
	// * direction.getValue() > LodeWorld.getPixelWidth()
	// || this.anchorPoint.getX() + (XMOVE_CONSTANT + this.xProportion / 2)
	// * direction.getValue() < 0) {
	//
	// return false;
	// }
	// }
	// return true;
	//
	// }

	/**
	 * checks to see if the character can move in the direction
	 * 
	 * @param direction
	 * @return
	 */
	public boolean blocked(GameData.Direction direction) {
		if (direction.isHorizontal()) {
			return GameData.isXBlocked(GameData.getBlock(
					(this.anchorPoint.getX() + (XMOVE_CONSTANT + this.xProportion / 2) * direction.getValue()),
					(this.anchorPoint.getY() - this.yProportion / 2)))
					|| GameData.isXBlocked(GameData.getBlock(
							(this.anchorPoint.getX() + (XMOVE_CONSTANT + this.xProportion / 2) * direction.getValue()),
							(this.anchorPoint.getY() + this.yProportion / 2)));
		} else {
			if (direction == GameData.Direction.up) {
				return GameData.isYBlocked(GameData.getBlock((this.anchorPoint.getX() + this.xProportion / 2),
						(this.anchorPoint.getY() + (YMOVE_CONSTANT + this.yProportion / 2) * direction.getValue())),
						direction)
						|| GameData.isYBlocked(
								GameData.getBlock(
										(this.anchorPoint.getX() - this.xProportion / 2),
										(this.anchorPoint.getY() + (YMOVE_CONSTANT + this.yProportion / 2)
												* direction.getValue())), direction)
						|| GameData.isYBlocked(
								GameData.getBlock((this.anchorPoint.getX() + this.xProportion / 2),
										(this.anchorPoint.getY() - (this.yProportion / 2) * direction.getValue())),
								direction)
						|| GameData.isYBlocked(
								GameData.getBlock((this.anchorPoint.getX() - this.xProportion / 2),
										(this.anchorPoint.getY() - (this.yProportion / 2) * direction.getValue())),
								direction);
			}
			return GameData.isYBlocked(GameData.getBlock((this.anchorPoint.getX() + this.xProportion / 2),
					(this.anchorPoint.getY() + (YMOVE_CONSTANT + this.yProportion / 2) * direction.getValue())),
					direction)
					|| GameData.isYBlocked(
							GameData.getBlock(
									(this.anchorPoint.getX() - this.xProportion / 2),
									(this.anchorPoint.getY() + (YMOVE_CONSTANT + this.yProportion / 2)
											* direction.getValue())), direction);
		}
	}

	/**
	 * checks to see if the character is falling, i.e. has nothing blocks under
	 * him
	 * 
	 * @return
	 */
	protected boolean isFalling() {
		if (this.onRope()) {
			return false;
		}
		if ((this.upperLeftBlock == GameData.BlockType.nothing || this.upperLeftBlock == GameData.BlockType.Temp
				|| this.upperLeftBlock == GameData.BlockType.Gold || this.upperLeftBlock == GameData.BlockType.Rope)
				&& (this.upperRightBlock == GameData.BlockType.nothing
						|| this.upperRightBlock == GameData.BlockType.Temp
						|| this.upperRightBlock == GameData.BlockType.Gold || this.upperRightBlock == GameData.BlockType.Rope)
				&& (this.lowerLeftBlock == GameData.BlockType.nothing || this.lowerLeftBlock == GameData.BlockType.Temp
						|| this.lowerLeftBlock == GameData.BlockType.Gold || this.lowerLeftBlock == GameData.BlockType.Rope)
				&& (this.lowerRightBlock == GameData.BlockType.nothing
						|| this.lowerRightBlock == GameData.BlockType.Temp
						|| this.lowerRightBlock == GameData.BlockType.Gold || this.lowerRightBlock == GameData.BlockType.Rope)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks to see if the character is on a ladder it checks by seeing if ANY
	 * of the 4 points of the bounding box are in contact with a ladder
	 * 
	 * @return
	 */
	protected boolean onLadder() {
		if (this.upperLeftBlock == GameData.BlockType.Ladder || this.upperRightBlock == GameData.BlockType.Ladder
				|| this.lowerLeftFootBlock == GameData.BlockType.Ladder
				|| this.lowerRightFootBlock == GameData.BlockType.Ladder) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean onFinalLadder() {
		if (this.upperLeftBlock == GameData.BlockType.nextLevelPortal
				|| this.upperRightBlock == GameData.BlockType.nextLevelPortal
				|| this.lowerLeftFootBlock == GameData.BlockType.nextLevelPortal
				|| this.lowerRightFootBlock == GameData.BlockType.nextLevelPortal) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks to see if the character is on a rope
	 * 
	 * @return
	 */
	protected boolean onRope() {
		if ((this.upperLeftBlock == GameData.BlockType.Rope && this.lowerLeftFootBlock == GameData.BlockType.Rope)
				|| (this.upperRightBlock == GameData.BlockType.Rope && this.lowerRightFootBlock == GameData.BlockType.Rope)) {
			return true;
		} else {
			return false;
		}
	}
}
