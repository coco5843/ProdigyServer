package fr.cocoraid.prodigyserver.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import fr.cocoraid.prodigyserver.utils.ArmorstandSchematic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class InteractableSchematicProtocol {

    public InteractableSchematicProtocol(Plugin plugin) {

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player p = event.getPlayer();
                if (p != null) {
                    int id = packet.getIntegers().read(0);
                    ArmorstandSchematic.getSchematics().stream().filter(s -> s.foundArmorStand(id) && s.getAction() != null).findFirst().ifPresent(s -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            s.getAction().run(p,packet.getEntityUseActions().read(0));
                        });
                    });
                }

            }

        });
    }
}
