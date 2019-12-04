package fr.cocoraid.prodigyserver.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class UtilMath {

	public static double offset(Location a, Location b) {
		return offset(a.toVector(), b.toVector());
	}

	public static double offset(Vector a, Vector b) {
		return a.subtract(b).length();
	}

	public static final Random random = new Random(System.nanoTime());

	public static float randomRange(float min, float max) {
		return min + (float)Math.random() * (max - min);
	}

	public static int randomRange(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public static double randomRange(double min, double max) {
		return Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min);
	}

	public static double arrondi(double A, int B) {
		return (double) ( (int) (A * Math.pow(10, B) + .5)) / Math.pow(10, B);
	}

	public static int getRandomWithExclusion(int start, int end, int... exclude) {
		int rand = start + random.nextInt(end - start + 1 - exclude.length);
		for (int ex : exclude) {
			if (rand < ex) {
				break;
			}
			rand++;
		}
		return rand;
	}

	public static float getLookAtYaw(Vector motion) {
		double deltaX = motion.getX();
		double deltaZ = motion.getZ();
		double yaw = Math.atan2(deltaX, deltaZ);
		return (float) yaw - 90;

	}

	public static List<Block> getInRadius(Location loc, double radius)
	{
		List<Block> blockList = new ArrayList<>();
		int iR = (int)radius + 1;

		for (int x = -iR; x <= iR; x++) {
			for (int z = -iR; z <= iR; z++)
				for (int y = -iR; y <= iR; y++)
				{
					Block curBlock = loc.getBlock().getRelative(x, y, z);
					double offset = UtilMath.offset(loc, curBlock.getLocation());
					if (offset <= radius)
						blockList.add(curBlock);
				}
		}
		return blockList;
	}


	public static List<Block> getInXRadius(Location loc, double radius, double xRadius) {
		List<Block> blockList = new ArrayList<>();
		int iR = (int) radius + 1;
		int iX = (int) xRadius + 1;

		for (int x = -iX; x <= iX; x++) {
			for (int z = -iR; z <= iR; z++) {
				Block curBlock = loc.getWorld().getBlockAt(
						(int) (loc.getX() + x), (int) (loc.getY()),
						(int) (loc.getZ() + z));
				double offset = UtilMath.offset(loc, curBlock.getLocation()
						.add(0.5D, 0.5D, 0.5D));
				if (offset <= radius)
					blockList.add(curBlock);
			}
		}
		return blockList;
	}

	public static List<Block> getIn2DRadius(Location loc, double radius) {
		List<Block> blockList = new ArrayList<>();
		int iR = (int) radius + 1;

		for (int x = -iR; x <= iR; x++) {
			for (int z = -iR; z <= iR; z++) {
				Block curBlock = loc.getWorld().getBlockAt(
						(int) (loc.getX() + x), (int) (loc.getY()),
						(int) (loc.getZ() + z));
				double offset = UtilMath.offset(loc, curBlock.getLocation()
						.add(0.5D, 0.5D, 0.5D));
				if (offset <= radius)
					blockList.add(curBlock);
			}
		}
		return blockList;
	}

	public static boolean elapsed(long from, long required) {
		return System.currentTimeMillis() - from > required;
	}
	
	/**
	 * Un vecteur de bump (projection)
	 * @param entity
	 * @param from
	 * @param power
	 * @return
	 */
	public static Vector getBumpVector(Entity entity, Location from, double power) {
		Vector bump = entity.getLocation().toVector().subtract(from.toVector()).normalize();
		if(Double.isNaN(bump.getX()))
			bump.setX(0);
		if(Double.isNaN(bump.getZ()))
			bump.setZ(0);
		bump.multiply(power);
		return bump;
	}

	/**
	 * Un vecteur de pull (attraction)
	 * @param entity
	 * @param
	 * @param power
	 * @return
	 */
	public static Vector getPullVector(Entity entity, Location to, double power) {
		Vector pull = to.toVector().subtract(entity.getLocation().toVector()).normalize();
		pull.multiply(power);
		return pull;
	}

	/**
	 *
	 * @param location
	 * @param distance
	 * @return
	 */
	public static List<Player> getClosestPlayersFromLocation(Location location, double distance) {
		List<Player> result = new ArrayList<>();
		double d2 = distance * distance;
		for (Player player : location.getWorld().getPlayers()) {
			if (player.getLocation().add(0, 0.85D, 0).distanceSquared(location) <= d2) {
				result.add(player);
			}
		}
		return result;
	}

	/**
	 * Projette l'entité à partir d'une location
	 * @param entity
	 * @param from projeté à partir de
	 * @param power multiplicateur de puissance
	 */
	public static void bumpEntity(Entity entity, Location from, double power) {
		entity.setVelocity(getBumpVector(entity, from, power));
	}

	/**
	 * Projette l'entité
	 * @param entity
	 * @param from
	 * @param power
	 * @param fixedY fix le Y 
	 */
	public static void bumpEntity(Entity entity, Location from, double power, double fixedY) {
		Vector vector = getBumpVector(entity, from, power);
		vector.setY(fixedY);
		entity.setVelocity(vector);
	}

	/**
	 * Attire l'entité vers une location
	 * @param entity
	 * @param to attiré vers
	 * @param power multiplicateur de puissance
	 */
	public static void pullEntity(Entity entity, Location to, double power) {
		entity.setVelocity(getPullVector(entity, to, power));
	}

	/**
	 * Attiré l'entité
	 * @param entity
	 * @param from
	 * @param power
	 * @param fixedY fix le Y
	 */
	public static void pullEntity(Entity entity, Location from, double power, double fixedY) {
		Vector vector = getPullVector(entity, from, power);
		vector.setY(fixedY);
		entity.setVelocity(vector);
	}

	/**
	 * Vecteur qui s'update autour de l'axe X avec un angle
	 * @param v
	 * @param angle
	 * @return
	 */
	public static final Vector rotateAroundAxisX(Vector v, double angle) {
		double y, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		y = v.getY() * cos - v.getZ() * sin;
		z = v.getY() * sin + v.getZ() * cos;
		return v.setY(y).setZ(z);
	}

	/**
	 * Vecteur qui s'update autour de l'axe Y avec un angle
	 * @param v
	 * @param angle
	 * @return
	 */
	public static final Vector rotateAroundAxisY(Vector v, double angle) {
		double x, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		x = v.getX() * cos + v.getZ() * sin;
		z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}

	/**
	 * Vecteur qui s'update autour de l'axe Z avec un angle
	 * @param v
	 * @param angle
	 * @return
	 */
	public static final Vector rotateAroundAxisZ(Vector v, double angle) {
		double x, y, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		x = v.getX() * cos - v.getY() * sin;
		y = v.getX() * sin + v.getY() * cos;
		return v.setX(x).setY(y);
	}

	/**
	 * 
	 * @param v
	 * @param angleX
	 * @param angleY
	 * @param angleZ
	 * @return
	 */
	public static final Vector rotateVector(Vector v, double angleX, double angleY, double angleZ) {
		rotateAroundAxisX(v, angleX);
		rotateAroundAxisY(v, angleY);
		rotateAroundAxisZ(v, angleZ);
		return v;
	}

	public static Vector rotate(Vector v, Location l) {
		double yaw = l.getYaw() / 180 * Math.PI;
		double pitch = l.getPitch() / 180 * Math.PI;

		v = rotateAroundAxisX(v, pitch);
		v = rotateAroundAxisY(v, -yaw);
		return v;
	}


	/**
	 * Util convert
	 * @return f
	 */
	public static byte toPackedByte(float f) {
		return (byte) ((int) (f * 256.0F / 360.0F));
	}

	public static Vector getRandomVector() {
		double x, y, z;
		x = random.nextDouble() * 2 - 1;
		y = random.nextDouble() * 2 - 1;
		z = random.nextDouble() * 2 - 1;

		return new Vector(x, y, z).normalize();
	}


	public static Vector getRandomCircleVector() {
		double rnd, x, z, y;
		rnd = random.nextDouble() * 2 * Math.PI;
		x = Math.cos(rnd);
		z = Math.sin(rnd);
		y = Math.sin(rnd);

		return new Vector(x, y, z);
	}

	public static final BlockFace[] axis = { BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST };
	public static final byte[] axisByte = { 3 , 4, 2, 5 };


	public static Vector getRandomVectorline() {

		int min = -5;
		int max = 5;
		int rz = (int)(Math.random()*(max - min) + min);
		int rx = (int)(Math.random()*(max - min) + min);

		double miny = -5;
		double maxy = -1;
		double ry = (Math.random()*(maxy - miny) + miny);

		return new Vector(rx, ry, rz).normalize();
	}

	public static Vector rotate(Vector v, double yaw, double pitch) {

		if (pitch < 0)
			pitch += 360.;
		if (yaw < 0)
			yaw += 360.;

		double ay = Math.toRadians(-yaw);
		double ax = Math.toRadians(pitch);

		double sin = Math.sin(ax);
		double cos = Math.cos(ax);
		Vector ret = new Vector(v.getX(), v.getY() * cos - v.getZ() * sin, v.getY() * sin + v.getZ() * cos);


		sin = Math.sin(ay);
		cos = Math.cos(ay);
		return new Vector(ret.getX() * cos + ret.getZ() * sin, ret.getY(), ret.getX() * -sin + ret.getZ() * cos);
	}

	public static final Vector rotateVector(Vector v, float yawDegrees, float pitchDegrees) {
		double yaw = Math.toRadians(-1 * (yawDegrees + 90));
		double pitch = Math.toRadians(-pitchDegrees);

		double cosYaw = Math.cos(yaw);
		double cosPitch = Math.cos(pitch);
		double sinYaw = Math.sin(yaw);
		double sinPitch = Math.sin(pitch);

		double initialX, initialY, initialZ;
		double x, y, z;

		// Z_Axis rotation (Pitch)
		initialX = v.getX();
		initialY = v.getY();
		x = initialX * cosPitch - initialY * sinPitch;
		y = initialX * sinPitch + initialY * cosPitch;

		// Y_Axis rotation (Yaw)
		initialZ = v.getZ();
		initialX = x;
		z = initialZ * cosYaw - initialX * sinYaw;
		x = initialZ * sinYaw + initialX * cosYaw;

		return new Vector(x, y, z);
	}

	public static LinkedList<Vector> createCircle(double radius, double particleamount) {
		double amount = radius * particleamount;
		double inc = (2 * Math.PI) / amount;
		LinkedList<Vector> vecs = new LinkedList<>();
		for (int i = 0; i < amount; i++) {
			double angle = i * inc;
			double x = radius * Math.cos(angle);
			double z = radius * Math.sin(angle);
			Vector v = new Vector(x, 0, z);
			vecs.add(v);
		}
		return vecs;
	}


}
