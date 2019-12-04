package fr.cocoraid.prodigyserver.nms;


import fr.cocoraid.prodigyserver.utils.UtilMath;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class PacketArmorStand extends PacketEntity {

    private Location location;
    private EntityArmorStand armorStand;
    private PacketPlayOutEntityEquipment equip;
    private ItemStack head;
    private String title;
    public PacketArmorStand(Location location) {
        super(location);
        this.location = location;
        armorStand = new EntityArmorStand(((CraftWorld)location.getWorld()).getHandle());
        armorStand.setLocation(location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
        this.entity = armorStand;

    }

    public PacketArmorStand setAsNameDisplayer(String displayName) {
        this.title = displayName;
        armorStand.setCustomName(new ChatComponentText(displayName));
        armorStand.setCustomNameVisible(true);
        armorStand.setInvisible(true);
        return this;
    }

    public PacketArmorStand setAsItemDisplayer(ItemStack item) {
        armorStand.setInvisible(true);
        this.head = item;
        equip = new PacketPlayOutEntityEquipment(armorStand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(item));
        return this;
    }


    public void rotate(Vector3f angle, float yaw) {
        location.setYaw(yaw);
        armorStand.setHeadPose(angle);
        PacketPlayOutEntity.PacketPlayOutEntityLook yawRotation = new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.getId(), UtilMath.toPackedByte(yaw),(byte) 0, true);
        sendPacket(new PacketPlayOutEntityMetadata(armorStand.getId(),armorStand.getDataWatcher(),true));
        sendPacket(yawRotation);
    }

    public void rotate(float yaw) {
        PacketPlayOutEntity.PacketPlayOutEntityLook rotate = new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.getId(), UtilMath.toPackedByte(yaw),(byte)0,true);
        sendPacket(rotate);
        this.location.setYaw(yaw);
    }

    public void updateName(String name) {
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(new ChatComponentText(name));
        PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(armorStand.getId(),armorStand.getDataWatcher(),true);
        sendPacket(meta);
    }

    public void removeName() {
        armorStand.setCustomName(new ChatComponentText(""));
        armorStand.setCustomNameVisible(false);
        PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(armorStand.getId(),armorStand.getDataWatcher(),true);
        sendPacket(meta);
    }

    public void updateHead(ItemStack item) {
        this.head = item;
        equip = new PacketPlayOutEntityEquipment(armorStand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(item));
        sendPacket(equip);
    }


    @Override
    public void spawn() {
        super.spawn();
        if(equip != null)
            sendPacket(equip);
    }


    public EntityArmorStand getArmorStand() {
        return armorStand;
    }

    public String getTitle() {
        return title;
    }

    public ItemStack getHead() {
        return head;
    }

    public boolean isSmall() {
        return armorStand.isSmall();
    }
}
