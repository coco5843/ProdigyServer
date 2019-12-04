package fr.cocoraid.prodigyserver.utils;

/**
 * Created by cocoraid on 25/01/2018.
 */

import com.comphenix.protocol.wrappers.EnumWrappers;
import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.nms.PacketArmorStand;
import net.minecraft.server.v1_13_R2.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ArmorstandSchematic {


    private static List<ArmorstandSchematic> schematics = new ArrayList<>();

    private static Map<String,List<TempArmorStand>> tempArmorStands = new HashMap<>();
    private static class TempArmorStand {
        Vector vector;
        EulerAngle euler;
        ItemStack item;
        boolean small;

        public TempArmorStand(Vector vector, EulerAngle angle, ItemStack item, boolean small ) {
            this.item = item;
            this.euler = angle;
            this.vector = vector;
            this.small = small;

        }
    }





    private String schematicName;
    private SchematicClick action;
    public void setAction(SchematicClick action) {
        this.action = action;
    }

    public SchematicClick getAction() {
        return action;
    }


    public ArmorstandSchematic(String schematic) {
        this.schematicName = schematic;
    }

    public static boolean loadFile(String schematicName) {
        tempArmorStands.put(schematicName,new ArrayList<>());
        try {
            InputStream in = ProdigyServer.getInstance().getResource("armorstand/" + schematicName);

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {

                String[] arr = line.split(" ", 6);
                boolean small = arr.length >= 6 && arr[5].equals("small") ? true : false;


                String itemname = arr[0];
                ItemStack item = new ItemStack(Material.valueOf(itemname.toUpperCase()));

                String posecrypted = arr[4].replace("Pose:{Head:[","").replace("]}","".replace("f",""));
                String[] pose = posecrypted.split(",", 4);
                EulerAngle euler = new EulerAngle(Double.valueOf(pose[0]), Double.valueOf(pose[1]),Double.valueOf(pose[2]));


                Vector vector = new Vector(Double.valueOf(arr[1].replace("(","").replace(",","")),
                        Double.valueOf(arr[2].replace(",","")),
                        Double.valueOf(arr[3].replace(")","")));

                tempArmorStands.get(schematicName).add(new TempArmorStand(vector,euler,item,small));
            }
            return true;
        }catch(IOException e) {
            e.printStackTrace();
        }

        return false;

    }


    public boolean isLoaded() {
      return tempArmorStands.containsKey(schematicName);
    }


    private List<PacketArmorStand> armorStands = new ArrayList<>();

    public ArmorstandSchematic paste(Location loc, int rotation) {
        if (!isLoaded())
            return null;

        schematics.add(this);
        Location middle = loc.clone();

        tempArmorStands.get(schematicName).forEach(temp -> {

            Vector v = temp.vector.clone();

            UtilMath.rotateAroundAxisY(v, Math.toRadians(-loc.getYaw() + rotation));
            Location l = middle.clone().add(v);
            if (temp.small) l.add(0, 0.8, 0);
            PacketArmorStand as = new PacketArmorStand(l);
            as.getArmorStand().setSmall(temp.small);
            as.setAsItemDisplayer(temp.item);
            as.spawn();

            as.rotate(new Vector3f((float)temp.euler.getX(),(float)temp.euler.getY(),(float)temp.euler.getZ()),loc.getYaw());
            armorStands.add(as);

        });

        return this;
    }






    @FunctionalInterface
    public interface SchematicClick
    {
        /**
         * Executes the desired action
         * on a player based upon implementation
         * @param player The player to run the action for
         */
        void run(Player player, EnumWrappers.EntityUseAction click);
    }


    public boolean foundArmorStand(int id) {
        return getArmorStands().stream().filter(as -> as.getArmorStand().getId() == id).findFirst().isPresent();
    }

    public void clear() {
        getArmorStands().forEach(a -> a.remove());
        getArmorStands().clear();
    }

    public List<PacketArmorStand> getArmorStands() {
        return armorStands;
    }

    public static List<ArmorstandSchematic> getSchematics() {
        return schematics;
    }
}
