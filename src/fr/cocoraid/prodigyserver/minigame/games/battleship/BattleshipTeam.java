package fr.cocoraid.prodigyserver.minigame.games.battleship;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public enum BattleshipTeam {

    PIRATE(ChatColor.BLACK,Color.WHITE,"§7Pirate", ShipPoints.piratePoints,ShipPoints.pirateCannons,-56,65,-11,-90, new Vector(-56,65,31),new Vector(-56,66,1),new Vector(-56,62,-34),new Vector(-56,54,46), new ArrayList<>())
    ,CONQUISTADOR(ChatColor.GOLD,Color.ORANGE,"§6Conquistador",ShipPoints.conquistadorsPoints,ShipPoints.conquistadorsCannons, 0,65,30, 90, new Vector(0,65,-13),new Vector(0,66,17),new Vector(0,62,52),new Vector(0,54,-28), new ArrayList<>());


    private ChatColor chatColor;
    private Player flagCaptured;
    private boolean allowRespawn = true;
    private boolean matDestroyed = false;
    private String name;
    private Location[] spawnPoints;
    private Location[] cannonPoints;
    private Location powderChest;
    private Location spawnTeam;
    private Location banner;
    private Location captain;
    private Location mat;
    private Color color;
    private List<Item> list;

    BattleshipTeam(ChatColor chatColor,Color color, String name, Vector[] spawnPoints, Vector[] cannonPoints, double x, double y, double z, float yaw, Vector powderChest,Vector mat,Vector captain,Vector banner, List<Item> list) {
        World w = Bukkit.getWorld("battleship");
        spawnTeam = new Location(w,x,y,z,yaw,0);
        this.color = color;
        this.chatColor = chatColor;
        this.name = name;
        this.list = list;
        this.mat = new Location(w,mat.getX() ,mat.getY(),mat.getZ());
        this.powderChest = new Location(w,powderChest.getX() + 0.5,powderChest.getY() + 0.5,powderChest.getZ() + 0.5);
        this.banner = new Location(w,banner.getX() + 0.5,banner.getY() + 0.5,banner.getZ() + 0.5);
        this.captain = new Location(w,captain.getX() + 0.5,captain.getY() ,captain.getZ() + 0.5, this.name.equalsIgnoreCase("PIRATE") ? spawnTeam.getYaw() - 90 : spawnTeam.getYaw() + 90, 0);
        this.spawnPoints = new Location[spawnPoints.length];
        this.cannonPoints = new Location[cannonPoints.length];

        int k = 0;
        for (Vector spawnPoint : spawnPoints) {
            this.spawnPoints[k] = new Location(w, spawnPoint.getX(),spawnPoint.getY(),spawnPoint.getZ(),spawnTeam.getYaw(),0);
            k++;
        }
        k = 0;
        for (Vector cannonPoint : cannonPoints) {
            this.cannonPoints[k] = new Location(w, cannonPoint.getX(),cannonPoint.getY() - 0.7,cannonPoint.getZ(), spawnTeam.getYaw(),0);
            k++;
        }

    }

    public Location getBanner() {
        return banner;
    }

    public Location getMat() {
        return mat;
    }

    public void setMatDestroyed(boolean matDestroyed) {
        this.matDestroyed = matDestroyed;
    }

    public boolean isMatDestroyed() {
        return matDestroyed;
    }

    public List<Item> getList() {
        return list;
    }

    public Location getSpawnTeam() {
        return spawnTeam;
    }


    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Location getPowderChest() {
        return powderChest;
    }

    public Location[] getSpawnPoints() {
        return spawnPoints;
    }

    public Location[] getCannonPoints() {
        return cannonPoints;
    }

    public Location getCaptainLocation() {
        return captain;
    }

    public boolean isFlagCaptured() {
        return flagCaptured != null;
    }

    public void setFlagCapturedByPlayer(Player flagCaptured) {
        this.flagCaptured = flagCaptured;
    }

    public Player getFlagCapturedPlayer() {
        return flagCaptured;
    }

    public boolean isAllowRespawn() {
        return allowRespawn;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public void setAllowRespawn(boolean allowRespawn) {
        this.allowRespawn = allowRespawn;
    }



    private static class ShipPoints {


        private static Vector[] conquistadorsPoints = new Vector[]{
                new Vector(-4, 65, 10), new Vector(0, 65, 12), new Vector(3, 65, 10), new Vector(6, 65, 1), new Vector(6, 65, -6), new Vector(4, 65, -10),
                new Vector(0, 65, -10), new Vector(-4, 65, -10), new Vector(-33, 71, 12), new Vector(-7, 65, -6)
        };

        private static Vector[] piratePoints = new Vector[]{
                new Vector(-52, 65, 8), new Vector(-56, 65, 6), new Vector(-59, 65, 8), new Vector(-62, 65, 17), new Vector(-62, 65, 24), new Vector(-60, 65, 28),
                new Vector(-56, 65, 28), new Vector(-52, 65, 28), new Vector(-49, 65, 24)
        };


        private static Vector[] conquistadorsCannons = new Vector[]{
                new Vector(-8, 65, -8), new Vector(-8, 65, -3), new Vector(-7, 65, 4),new Vector(-7, 65, 9),new Vector(-7, 65, 14),
                new Vector(-8, 61, -21), new Vector(-9, 61, -15), new Vector(-9, 61, -9),new Vector(-9, 61, -3),new Vector(-9, 61, 3),new Vector(-9, 61, 9),new Vector(-8, 61, 15),new Vector(-8, 61, 21),new Vector(-8, 61, 27),new Vector(-8, 61, 33),
                new Vector(-10, 57, -21), new Vector(-9, 57, -15), new Vector(-9, 57, -9),new Vector(-9, 57, -3),new Vector(-9, 57, 3),new Vector(-9, 57, 9),new Vector(-8, 57, 15),new Vector(-8, 57, 21),new Vector(-8, 57, 27),new Vector(-8, 57, 33),
        };

        private static Vector[] pirateCannons = new Vector[]{
                new Vector(-48, 65, 26), new Vector(-48, 65, 21), new Vector(-49, 65, 14),new Vector(-49, 65, 9),new Vector(-49, 65, 4),
                new Vector(-48, 61, 39), new Vector(-47, 61, 33), new Vector(-47, 61, 27),new Vector(-47, 61, 21),new Vector(-47, 61, 15),new Vector(-47, 61, 9),new Vector(-48, 61, 3),new Vector(-48, 61, -3),new Vector(-48, 61, -9),new Vector(-48, 61, -15),
                new Vector(-46, 57, 39), new Vector(-46, 57, 33), new Vector(-45, 57, 27),new Vector(-45, 57, 21),new Vector(-45, 57, 15),new Vector(-46, 57, 9),new Vector(-46, 57, 3),new Vector(-46, 57, -3),new Vector(-47, 57, -9),new Vector(-47, 57, -15)
        };

    }

}
