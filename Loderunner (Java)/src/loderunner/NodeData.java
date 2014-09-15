package loderunner;

import loderunner.GameData.Direction;

public class NodeData {
	protected Zone zoneToGo;
	protected Direction direction;
	protected int fallLength;

	public NodeData(Zone zoneToGo, Direction direction, int fallLength) {
		this.zoneToGo = zoneToGo;
		this.direction = direction;
		this.fallLength = fallLength;
		
	}
}
