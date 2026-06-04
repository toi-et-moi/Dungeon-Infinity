package dev.xkmc.dungeon_infinity.content.maze.chunkgen;

import net.minecraft.world.level.block.Rotation;

/**
 * bit 0-5: directional data
 * bit 6-10: boss room data
 * but 11-31: room type
 *
 */
public class CellInterpreter {

	public static boolean isBossRoom(int cell) {
		return ((cell >> 6) & 31) != 0;
	}

	public static int getBossRoom(int cell) {
		return cell >> 6;
	}

	public static int getOpenings(int cell) {
		return cell & 63;
	}

	public static int setBossRoom(int data) {
		return data << 6;
	}

	public static int getRoomTypeMask(int cell, int marker) {
		return (marker - 1) << 11;
	}

	public static int getRoomType(int cell) {
		return cell >> 11;
	}

	/**
	 * 0: illegal
	 * 1: room only
	 * 2: hallway only
	 * 3: both
	 */
	public static int getCellFlags(int cell) {
		if (isBossRoom(cell)) return 0;
		return switch (getTemplateType(cell)) {
			case 1, 6, 8, 9 -> 1;
			case 7 -> 2;
			case 2, 3, 4, 5 -> 3;
			default -> 0;
		};
	}

	public static final int SPECIAL = 1, HALLWAY = 2, ROOM = 3;

	/**
	 * 1: special
	 * 2: hallway
	 * 3: room
	 */
	public static int getRoomMarker(int cell, int flag) {
		return switch (flag) {
			case 1 -> ROOM;
			case 2 -> HALLWAY;
			default -> SPECIAL;
		};
	}


	/**
	 * 0: illegal
	 * 1: end
	 * 2: straight
	 * 3: corner
	 * 4: t way
	 * 5: cross
	 * 6: lower stairs
	 * 7: upper stairs
	 * 8: lower cross stairs
	 * 9: upper cross stairs
	 */
	public static int getTemplateType(int cell) {
		int open = getOpenings(cell);
		return switch (open) {
			case 1, 2, 4, 8 -> 1;
			case 3, 12 -> 2;
			case 5, 6, 9, 10 -> 3;
			case 7, 11, 13, 14 -> 4;
			case 15 -> 5;
			case 17, 18 -> 6;
			case 33, 34 -> 7;
			case 19, 20, 24, 28 -> 8;
			case 36, 40, 35, 44 -> 9;
			default -> 0;
		};
	}

	public static final CellInstance SKIP = new CellInstance("skip");
	public static final CellInstance MISSING = new CellInstance("missing");
	private static final CellInstance BOSS_CENTER = new CellInstance("boss/center");
	private static final CellInstance BOSS_SIDE = new CellInstance("boss/side");
	private static final CellInstance BOSS_CORNER = new CellInstance("boss/corner");
	private static final CellInstance END = new CellInstance("end");
	private static final CellInstance STRAIGHT = new CellInstance("straight");
	private static final CellInstance CORNER = new CellInstance("corner");
	private static final CellInstance T_WAY = new CellInstance("t_way");
	private static final CellInstance CROSS = new CellInstance("cross");
	private static final CellInstance STAIRS = new CellInstance("stairs");
	private static final CellInstance CROSS_STAIRS = new CellInstance("cross_stairs");

	public static boolean isHallway(int room) {
		return room == 1;
	}

	public static String getRoomName(int room) {
		return switch (room) {
			case 1 -> "path/";
			case 2 -> "room/";
			default -> "";
		};
	}

	public static CellInstance getTemplate(int cell) {
		if (isBossRoom(cell)) {
			int room = getBossRoom(cell);
			return switch (room) {
				case 1 -> BOSS_CORNER;
				case 2 -> BOSS_SIDE;
				case 3 -> BOSS_CORNER.with(Rotation.COUNTERCLOCKWISE_90);
				case 4 -> BOSS_SIDE.with(Rotation.CLOCKWISE_90);
				case 5 -> BOSS_CENTER;
				case 6 -> BOSS_SIDE.with(Rotation.COUNTERCLOCKWISE_90);
				case 7 -> BOSS_CORNER.with(Rotation.CLOCKWISE_90);
				case 8 -> BOSS_SIDE.with(Rotation.CLOCKWISE_180);
				case 9 -> BOSS_CORNER.with(Rotation.CLOCKWISE_180);
				default -> SKIP;
			};
		}
		int open = getOpenings(cell);
		var room = getRoomName(getRoomType(cell));
		var ans = switch (open) {
			case 1 -> END;
			case 2 -> END.with(Rotation.CLOCKWISE_180);
			case 3 -> STRAIGHT;
			case 4 -> END.with(Rotation.CLOCKWISE_90);
			case 5 -> CORNER.with(Rotation.CLOCKWISE_90);
			case 6 -> CORNER.with(Rotation.CLOCKWISE_180);
			case 7 -> T_WAY.with(Rotation.CLOCKWISE_90);
			case 8 -> END.with(Rotation.COUNTERCLOCKWISE_90);
			case 9 -> CORNER;
			case 10 -> CORNER.with(Rotation.COUNTERCLOCKWISE_90);
			case 11 -> T_WAY.with(Rotation.COUNTERCLOCKWISE_90);
			case 12 -> STRAIGHT.with(Rotation.CLOCKWISE_90);
			case 13 -> T_WAY;
			case 14 -> T_WAY.with(Rotation.CLOCKWISE_180);
			case 15 -> CROSS;

			case 17 -> STAIRS;
			case 18 -> STAIRS.with(Rotation.CLOCKWISE_180);
			case 19 -> CROSS_STAIRS;
			case 20 -> STAIRS.with(Rotation.CLOCKWISE_90);
			case 24 -> STAIRS.with(Rotation.COUNTERCLOCKWISE_90);
			case 28 -> CROSS_STAIRS.with(Rotation.CLOCKWISE_90);

			case 33, 34, 36, 40, 35, 44 -> SKIP;
			default -> MISSING;
		};
		if (open < 16) {
			ans = ans.room(room);
		}
		return ans;
	}

}
