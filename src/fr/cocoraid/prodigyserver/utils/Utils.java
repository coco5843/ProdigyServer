package fr.cocoraid.prodigyserver.utils;

import net.minecraft.server.v1_13_R2.*;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Utils {

    public static void broadcastAdmin(String message) {
        Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("ps.admin")).forEach(cur -> cur.sendMessage(message));
    }


    public static void setBlockFast(Material m, Location location, boolean update) {
        BlockData data = m.createBlockData();
        Block nmsBlock = IRegistry.BLOCK.getOrDefault(new MinecraftKey(data.getMaterial().name().toLowerCase()));
        BlockPosition bp = new BlockPosition(location.getX(),location.getY(),location.getZ());
        ((CraftWorld)location.getWorld()).getHandle().setTypeAndData(bp, nmsBlock.getBlockData(),2);
        location.getBlock().setBlockData(data);
        if(update) {
            if (location.getBlock().getType() == Material.JUKEBOX) return;
            location.getBlock().getState().update(true, false);
        }
    }


    public static void strike(Location location, List<Player> players) {
        WorldServer ws = ((CraftWorld) location.getWorld()).getHandle();
        EntityLightning el = new EntityLightning(ws,location.getX(),location.getY(),location.getZ(), true , false);
        PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(el);
        players.forEach(cur -> {
            ((CraftPlayer)cur).getHandle().playerConnection.sendPacket(packet);
        });
    }


    public static void strike(Location location, Player player) {
        WorldServer ws = ((CraftWorld) location.getWorld()).getHandle();
        EntityLightning el = new EntityLightning(ws,location.getX(),location.getY(),location.getZ(), true , false);
        PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(el);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }


}
