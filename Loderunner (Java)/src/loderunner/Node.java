package loderunner;

import java.util.ArrayList;
import java.util.HashMap;

import loderunner.GameData.BlockType;
import loderunner.GameData.Direction;

public class Node {
	protected int x;
	protected int y;
	protected NodeData left;
	protected NodeData down;
	protected NodeData up;
	protected NodeData right;
	// protected ArrayList<NodeData> zonesCanGo;
	private BlockType block;
	private Zone zone;
	protected boolean canGoDown;
	protected boolean delete;

	public Node(int x, int y, Zone zone, boolean canGoDown) {
		this.canGoDown = canGoDown;
		this.zone = zone;
		this.x = x;
		this.y = y;
		this.block = GameData.getBlock(this.x, this.y);
		if (this.x + 1 < 32) {
			this.right = checkRight();
		}
		if (this.x - 1 >= 0) {
			this.left = checkLeft();
		}
		if (this.canGoDown) {
			this.down = checkDown();
		}
		if (this.block == BlockType.Ladder) {
			this.up = checkUp();
		}

		// this.zonesCanGo = new ArrayList<NodeData>();
		// isValid(this.up);
		// isValid(this.down);
		// isValid(this.left);
		// isValid(this.right);
	}

	// private void isValid(NodeData nodeData) {
	// if (nodeData != null) {
	// // if (newZone.id != this.zone.id) {
	// System.out.println(this.x + " " + this.y + " " + nodeData.direction);
	// this.zonesCanGo.add(nodeData);
	// GameData.setBlock(this.x, this.y, BlockType.Gold);
	// // }
	// }

	// }
	@Override
	public String toString() {
		String string = "x: " + this.x + " y: " + this.y + " ";
		if (this.down != null) {
			string += "down ";
		}
		if (this.up != null) {
			string += "up ";
		}
		if (this.left != null) {
			string += "left ";
		}
		if (this.right != null) {
			string += "right ";
		}
		return string;
	}

	private NodeData checkUp() {
		Zone zoneToReturn = GameData.inWhatZone(this.x, this.y - 1, this.zone.id);
		if (zoneToReturn == null) {
			return null;
		}
		return new NodeData(zoneToReturn, Direction.up, 0);
	}

	private NodeData checkLeft() {
		Zone zoneToReturn;
		if (!GameData.isXBlocked(GameData.getBlock(this.x - 1, this.y)) && (!this.zone.isInZone(this.x - 1, this.y))) {
			zoneToReturn = GameData.inWhatZone(this.x - 1, this.y, this.zone.id);
			if (zoneToReturn != null) {
				return new NodeData(zoneToReturn, Direction.left, 0);
			} else if (!this.zone.tall) {
				int counter = 1;
				while (true) {
					// System.out.println(this.x + " " + (this.y + counter));
					zoneToReturn = GameData.inWhatZone(this.x - 1, this.y + counter, this.zone.id);
					if (zoneToReturn != null) {
						return new NodeData(zoneToReturn, Direction.left, counter);
					}
					counter++;
				}
			}
		}
		return null;
	}

	private NodeData checkRight() {
		Zone zoneToReturn;
		if (!GameData.isXBlocked(GameData.getBlock(this.x + 1, this.y)) && (!this.zone.isInZone(this.x + 1, this.y))) {
			zoneToReturn = GameData.inWhatZone(this.x + 1, this.y, this.zone.id);
			if (zoneToReturn != null) {
				return new NodeData(zoneToReturn, Direction.right, 0);
			} else if (!this.zone.tall) {
				int counter = 1;
				while (true) {
					// System.out.println(this.x + " " + (this.y + counter));
					zoneToReturn = GameData.inWhatZone(this.x + 1, this.y + counter, this.zone.id);
					if (zoneToReturn != null) {
						return new NodeData(zoneToReturn, Direction.right, counter);
					}
					counter++;
				}
			}
		}
		return null;
	}

	private NodeData checkDown() {
		int counter = 0;
		Zone newZone;
		while (true) {
			// System.out.println(this.x + " " + (this.y + counter));
			newZone = GameData.inWhatZone(this.x, this.y + counter, this.zone.id);
			if (newZone != null) {
				break;
			}
			counter++;
		}
		return new NodeData(newZone, Direction.down, counter);
	}

	protected double getY() {
		return this.y + 0.5;
	}

	protected double getX() {
		return this.x + 0.5;
	}

	public boolean isInside(SuperCharacter character, boolean isZoneTall) {
		if (!isZoneTall) {
			// not on ladder
			// must have both left and right inside
			if ((int) character.xL == this.x && (int) character.xR == this.x) {
				// needs to have either of top or bottom be in node
				if ((int) character.yF == this.y || (int) character.yU == this.y) {
					System.out.println("inside node");
					return true;
				}
			}
		} else {
			// on ladder
			// must have both head and feet inside
			if ((int) character.yF == this.y && (int) character.yU == this.y) {
				// needs to have either of left or right be in node
				if ((int) character.xL == this.x || (int) character.xR == this.x) {
					System.out.println("inside node");
					return true;
				}
			}
		}
		return false;
	}
}
