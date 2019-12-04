package fr.cocoraid.prodigyserver.nms;

import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public abstract class PacketEntity {

    protected EntityLiving entity;
    protected Location location;
    public PacketEntity(Location location) {
        this.location = location;
    }

    public void teleport(Location location) {
        this.location = location;
        fakeTeleport(location);
    }

    public void fakeTeleport(Location location) {
        entity.setLocation(location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
        PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(entity);
        sendPacket(teleport);
    }



    public void spawn() {
        PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(entity);
        sendPacket(spawn);
    }

    public void remove() {
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entity.getId());
        sendPacket(destroy);
    }

    protected void sendPacket(Packet packet) {
        Bukkit.getOnlinePlayers().stream().filter(p -> p.getWorld().equals(location.getWorld())).forEach(p -> {
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
        });
    }

    public Location getLocation() {
        return location;
    }
}
