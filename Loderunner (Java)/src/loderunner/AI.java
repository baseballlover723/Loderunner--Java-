package loderunner;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import loderunner.GameData.Direction;

public class AI {
	private Guard guard;
	private Player player;
	private ArrayList<Zone> zones;
	private Zone zone;
	private Zone playerZone;
	private HashMap<ArrayList<Zone>, Integer> paths;

	public AI(Guard guard, Player player) {
		this.guard = guard;
		this.player = player;
		this.zones = GameData.getZones();
	}

	protected Direction think() {

		this.zone = GameData.inWhatZone((int) this.guard.anchorPoint.getX(),
				(int) this.guard.anchorPoint.getY());
		if (this.zone == null) {
			return Direction.none;
		}
		this.playerZone = GameData.inWhatZone((int) this.player.getAnchorPoint().getX(),
				(int) this.player.getAnchorPoint().getY());
		if (this.playerZone == null) {
			if (this.guard.anchorPoint.getX() < this.player.anchorPoint.getX()) {
				return Direction.right;
			} else {
				return Direction.left;
			}
		}
		this.paths = new HashMap<ArrayList<Zone>, Integer>();
		if (this.zone == this.playerZone) {
			if (this.guard.anchorPoint.getX() > this.player.anchorPoint.getX()) {
				return Direction.left;
			}
			if (this.guard.anchorPoint.getX() < this.player.anchorPoint.getX()) {
				return Direction.right;
			}
			if (this.guard.anchorPoint.getY() > this.player.anchorPoint.getY()) {
				return Direction.up;
			}
			if (this.guard.anchorPoint.getY() < this.player.anchorPoint.getY()) {
				return Direction.down;
			}
		}
		Double location = new Point2D.Double(this.guard.anchorPoint.getX(),
				this.guard.anchorPoint.getY());

		this.helper(location, this.zone, 0, new ArrayList<Zone>());
		// for (Node node : this.zone.nodes) {
		// int length;
		// ArrayList<Zone> path = new ArrayList<Zone>();
		// // if (this.zone.tall) {
		// Double location = new Point2D.Double(this.guard.anchorPoint.getX(),
		// this.guard.anchorPoint.getY());
		// if (node.down != null) {
		// length = distance(location, node);
		// System.out.println("length to nearest node "+node+" is "+ length);
		// path.add(node.down.zoneToGo);
		// helper(nodeToPoint(node), node.down.zoneToGo, length, path);
		// }
		// if (node.up != null) {
		// length = distance(location, node);
		// path.add(node.up.zoneToGo);
		// helper(nodeToPoint(node), node.up.zoneToGo, length, path);
		// }
		// if (node.left != null) {
		// length = distance(location, node);
		// path.add(node.left.zoneToGo);
		// helper(nodeToPoint(node), node.left.zoneToGo, length, path);
		// }
		// if (node.right != null) {
		// length = distance(location, node);
		// path.add(node.right.zoneToGo);
		// helper(nodeToPoint(node), node.right.zoneToGo, length, path);
		// }
		//
		// }
		// }
		int smallest = 99999;
		ArrayList<Zone> smallestPath = new ArrayList<Zone>();
		for (Entry<ArrayList<Zone>, Integer> path : this.paths.entrySet()) {
			// System.out.printf("length = " + path.getValue() + " path = ");
			for (Zone zone : path.getKey()) {
				// System.out.printf(zone.id + " ");
			}
			// System.out.println();
			if (path.getValue() <= smallest) {
				smallest = path.getValue();
				smallestPath = path.getKey();
			}
		} 
//		System.out.println("smallest value = " + smallest);
//		System.out.println("first block to go to is: " + smallestPath.get(0).id);
		for (Zone zone : smallestPath) {
//			 System.out.printf(zone.id + " ");
		}
		// System.out.println();
//		System.out.println("smallestPath.get(0) = " + smallestPath.get(0).id);
		try {
		Direction directionToGo = directionToZone(smallestPath.get(0));
		return directionToGo;
		} catch (IndexOutOfBoundsException e) {
			if (this.guard.anchorPoint.getX() < this.player.anchorPoint.getX()) {
				return Direction.right;
			} else {
				return Direction.left;
			}
			
		}

	}

	private Direction directionToZone(Zone zone) {
		if (this.zone.tall) {
			for (Node node : this.zone.nodes) {
				if ((node.down != null && node.down.zoneToGo == zone)
						|| (node.up != null && node.up.zoneToGo == zone)
						|| (node.left != null && node.left.zoneToGo == zone)
						|| (node.right != null && node.right.zoneToGo == zone)) {
					if (node.getY() > this.guard.anchorPoint.getY()) {
						return Direction.down;
					} else {
						return Direction.up;
					}
				}
			}
		} else {
			for (Node node : this.zone.nodes) {
				if (node.down != null && node.down.zoneToGo == zone) {
					return Direction.down;
				}
				if (node.up != null && node.up.zoneToGo == zone) {
					return Direction.up;
				}
				if (node.left != null && node.left.zoneToGo == zone) {
					return Direction.left;
				}
				if (node.right != null && node.right.zoneToGo == zone) {
					return Direction.right;
				}
				// if (node.getX() > this.guard.anchorPoint.getX()) {
				// return Direction.right;
				// } else {
				// return Direction.left;
				// }
				// }
			}
		}
		return null;

	}

	private Point2D nodeToPoint(Node node) {
		return new Point2D.Double(node.getX(), node.getY());
	}

	private int helper(Point2D location, Zone zone, int length, ArrayList<Zone> path) {
		if (zone == this.playerZone) {
			// length += (int) Math.max(Math.abs(location.getX() -
			// this.player.anchorPoint.getX()),
			// Math.abs(location.getY() - this.player.anchorPoint.getY()));
			length += (int) Math.abs(location.getX() - this.player.anchorPoint.getX());
			this.paths.put(path, length);
			return 0;
		}
		if (path.size() > 25) {
			return 0;
		}
		if (this.zone != null) {
			if (!this.zone.nodes.isEmpty()) {
				for (Node node : zone.nodes) {
					if (node.left != null && node.left.zoneToGo.id != zone.id
							&& !path.contains(node.left.zoneToGo)) {
						length += distance(location, node);
						ArrayList<Zone> newPath = clone(path);
						newPath.add(node.left.zoneToGo);
						// this.helper(nodeToPoint(node), node.left.zoneToGo,
						// length
						// + node.left.fallLength, newPath);
						this.helper(nodeToPoint(node), node.left.zoneToGo, length, newPath);
					}
					if (node.right != null && node.right.zoneToGo.id != zone.id
							&& !path.contains(node.right.zoneToGo)) {
						if (zone.id == 7) {
							// System.out.println("7 right = " +
							// node.right.zoneToGo.id + " length before = "+
							// length);
						}
						length += distance(location, node);
						ArrayList<Zone> newPath = clone(path);
						newPath.add(node.right.zoneToGo);
						if (zone.id == 7) {
							// System.out.println("7 right = " +
							// node.right.zoneToGo.id + " length after = "+
							// length);
						}
						// this.helper(nodeToPoint(node), node.right.zoneToGo,
						// length
						// + node.right.fallLength, newPath);
						this.helper(nodeToPoint(node), node.right.zoneToGo, length, newPath);
					}
					if (node.down != null && node.down.zoneToGo.id != zone.id
							&& !path.contains(node.down.zoneToGo)) {
						length += distance(location, node);
						ArrayList<Zone> newPath = clone(path);
						newPath.add(node.down.zoneToGo);
						this.helper(nodeToPoint(node), node.down.zoneToGo, length
								+ node.down.fallLength, newPath);
					}
					if (node.up != null && node.up.zoneToGo.id != zone.id
							&& !path.contains(node.up.zoneToGo)) {
						length += distance(location, node);
						ArrayList<Zone> newPath = clone(path);
						newPath.add(node.up.zoneToGo);
						this.helper(nodeToPoint(node), node.up.zoneToGo, length, newPath);
					}
				}
			}
		}
		return 0;
	}

	private int distance(Point2D location, Node node2) {
		return (int) Math.max(Math.abs(location.getX() - node2.getX()),
				Math.abs(location.getY() - node2.getY()));
	}

	private ArrayList<Zone> clone(ArrayList<Zone> path) {
		ArrayList<Zone> newPath = new ArrayList<Zone>();
		for (Zone zone : path) {
			newPath.add(zone);
		}
		return newPath;
	}
}
