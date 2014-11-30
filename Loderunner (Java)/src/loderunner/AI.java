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
	private ArrayList<Zone> smallestPath;
	private int smallest;

	public AI(Guard guard, Player player) {
		this.guard = guard;
		this.player = player;
		this.zones = GameData.getZones();
	}

	protected Direction think() {
		// still having issues
		// but its better
		// guards are hopping like their on crack

		this.zone = this.guard.currentZone;
		if (this.zone == null) {
			return Direction.none;
		}
		System.out.println("guard is in zone " + this.zone.id);
		this.playerZone = this.player.getZone();
		this.paths = new HashMap<ArrayList<Zone>, Integer>();
		if (this.zone == this.playerZone) {
			System.out.println("player loctaion = " + this.player.anchorPoint.toString());
			System.out.println("guard location = " + this.guard.anchorPoint.toString());
			if (!this.zone.tall) {
				if (this.guard.anchorPoint.getX() > this.player.anchorPoint.getX()) {
					return Direction.left;
				}
				if (this.guard.anchorPoint.getX() < this.player.anchorPoint.getX()) {
					return Direction.right;
				}
			}
			if (this.guard.anchorPoint.getY() > this.player.anchorPoint.getY()) {
				return Direction.up;
			}
			if (this.guard.anchorPoint.getY() < this.player.anchorPoint.getY()) {
				return Direction.down;
			}
		}
		Double location = new Point2D.Double(this.guard.anchorPoint.getX(), this.guard.anchorPoint.getY());

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
		this.smallest = Integer.MAX_VALUE;
		this.smallestPath = new ArrayList<Zone>();
		for (Entry<ArrayList<Zone>, Integer> path : this.paths.entrySet()) {
			System.out.printf("length = " + path.getValue() + " path = ");
			for (Zone zone : path.getKey()) {
				System.out.printf(zone.id + " ");
			}
			System.out.println();
			if (path.getValue() <= this.smallest) {
				this.smallest = path.getValue();
				this.smallestPath = path.getKey();
			}
		}
		System.out.println();
		System.out.println("smallest value = " + smallest);
		if (smallest == Integer.MAX_VALUE) {
			System.out.println();
		}
//		System.out.println("first block to go to is: " + smallestPath.get(0).id);
		for (Zone zone : this.smallestPath) {
			System.out.printf(zone.id + " ");
		}
		System.out.println();
		System.out.println("next zone to go = " + smallestPath.get(0).id);
		if (this.zone.id == 9) {
			System.out.println();
		}
		try {
			Direction directionToGo = directionToZone(this.smallestPath.get(0));
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
		for (Node node : this.zone.nodes) {
			// TODO
			// may need to check if inside node idk though
			if (node.down != null && node.down.zoneToGo == zone) {
				return directionToNode(node, Direction.down, 0, -1);
			}
			if (node.up != null && node.up.zoneToGo == zone) {
				return directionToNode(node, Direction.up, 0, 1);
			}
			if (node.left != null && node.left.zoneToGo == zone) {
				return directionToNode(node, Direction.left, -1, 0);
			}
			if (node.right != null && node.right.zoneToGo == zone) {
				return directionToNode(node, Direction.right, 1, 0);
			}
		}
		// if (this.zone.tall) {
		// for (Node node : this.zone.nodes) {
		// if ((node.down != null && node.down.zoneToGo == zone) || (node.up !=
		// null && node.up.zoneToGo == zone)
		// || (node.left != null && node.left.zoneToGo == zone)
		// || (node.right != null && node.right.zoneToGo == zone)) {
		// if (node.getY() > this.guard.anchorPoint.getY()) {
		// return Direction.down;
		// } else {
		// return Direction.up;
		// }
		// }
		// }
		// } else {
		// for (Node node : this.zone.nodes) {
		// // TODO
		// // may need to check if inside node idk though
		// if (node.down != null && node.down.zoneToGo == zone) {
		// return Direction.down;
		// }
		// if (node.up != null && node.up.zoneToGo == zone) {
		// return this.directionToNode(node);
		// // return Direction.up;
		// }
		// if (node.left != null && node.left.zoneToGo == zone) {
		// return Direction.left;
		// }
		// if (node.right != null && node.right.zoneToGo == zone) {
		// return Direction.right;
		// }
		// // if (node.getX() > this.guard.anchorPoint.getX()) {
		// // return Direction.right;
		// // } else {
		// // return Direction.left;
		// // }
		// // }
		// }
		// }
		throw new RuntimeException("AI didn't return a direction");

	}

	private Direction directionToNode(Node node, Direction nodeDirection, int deltaX, int deltaY) {
		if (node.isInside(this.guard, this.zone.tall)) {
			// inside node
			return nodeDirection;
		}
		if (this.zone.tall) {
			if (node.getY() + deltaY > this.guard.anchorPoint.getY()) {
				return Direction.down;
			} else {
				return Direction.up;
			}
		} else {
			if (node.getX() + deltaX < this.guard.anchorPoint.getX()) {
				return Direction.left;
			} else {
				return Direction.right;
			}
		}
	}

	private Point2D nodeToPoint(Node node) {
		return new Point2D.Double(node.getX(), node.getY());
	}

	// change to queue?
	private double helper(Point2D location, Zone zone, double length, ArrayList<Zone> path) {
//		System.out.println(length);
		if (zone == this.playerZone) {
			// if (zone.id == 12 && path.size() == 1) {
			// System.out.println();
			// }
			// length += (int) Math.max(Math.abs(location.getX() -
			// this.player.anchorPoint.getX()),
			if (zone.tall) {
				length += Math.abs(location.getY() - this.player.anchorPoint.getY());
			} else {
				length += (int) Math.abs(location.getX() - this.player.anchorPoint.getX());
			}
			this.paths.put(path, (int)length);
			return length;
		}
		if (path.size() > 25) {
			return 0;
		}
		if (this.zone != null) {
			if (!this.zone.nodes.isEmpty()) {
				for (Node node : zone.nodes) {
					if (node.left != null && node.left.zoneToGo.id != zone.id && !path.contains(node.left.zoneToGo)) {
						length += distance(location, node);
						ArrayList<Zone> newPath = clone(path);
						newPath.add(node.left.zoneToGo);
						// this.helper(nodeToPoint(node), node.left.zoneToGo,
						// length
						// + node.left.fallLength, newPath);
						this.helper(nodeToPoint(node), node.left.zoneToGo, length, newPath);
					}
					if (node.right != null && node.right.zoneToGo.id != zone.id && !path.contains(node.right.zoneToGo)) {
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
					if (node.down != null && node.down.zoneToGo.id != zone.id && !path.contains(node.down.zoneToGo)) {
						length += distance(location, node);
						ArrayList<Zone> newPath = clone(path);
						newPath.add(node.down.zoneToGo);
						this.helper(nodeToPoint(node), node.down.zoneToGo, length + node.down.fallLength, newPath);
					}
					if (node.up != null && node.up.zoneToGo.id != zone.id && !path.contains(node.up.zoneToGo)) {
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

	private double distance(Point2D location, Node node2) {
		return Math.max(Math.abs(location.getX() - node2.getX()+0.5), Math.abs(location.getY() - node2.getY()+0.5));
	}

	private ArrayList<Zone> clone(ArrayList<Zone> path) {
		ArrayList<Zone> newPath = new ArrayList<Zone>();
		for (Zone zone : path) {
			newPath.add(zone);
		}
		return newPath;
	}
}
