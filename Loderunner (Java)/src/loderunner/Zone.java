package loderunner;

import java.util.ArrayList;

import loderunner.GameData.BlockType;

public class Zone {
	protected int x1;
	protected int y1;
	protected int x2;
	protected int y2;
	protected BlockType block;
	protected int length;
	protected boolean tall;
	protected ArrayList<Node> nodes;
	protected int id;

	public Zone(int x1, int y1, int length, int id, BlockType block) {
		this.id = id;
		System.out.println(this.id);
		this.x1 = x1;
		this.y1 = y1;
		this.length = length;
		if (block == GameData.BlockType.Ladder) {
			this.tall = true;
			this.y2 = this.y1 + length;
			this.x2 = this.x1;
		} else {
			this.tall = false;
			this.x2 = this.x1 + length;
			this.y2 = y1;
		}
		this.block = block;
		if (this.block == GameData.BlockType.Brick) {
			this.y1--;
			this.y2--;
		}
		this.nodes = new ArrayList<Node>();
		// if (this.x1 != 0) {
		// this.nodes.add(new Node(this.x1 - 1, this.y1))
		// }
	}

	protected void addNode(Node node) {
		this.nodes.add(node);
	}

	protected boolean isInZone(int x, int y) {
		if (this.tall) {
			if (x == this.x1 && y <= this.y2 && y >= this.y1 - 1) {
				return true;
			} else {
				return false;
			}
		} else {
			if (y == this.y1 && x <= this.x2 && x >= this.x1) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public String toString() {
		return this.id + "";
	}
}
