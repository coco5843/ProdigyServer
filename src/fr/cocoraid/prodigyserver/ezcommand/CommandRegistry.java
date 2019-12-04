package fr.cocoraid.prodigyserver.ezcommand;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;

public class CommandRegistry {

    @SafeVarargs
    public static void register(EzCommand... cmds) {
        for(EzCommand cmd : cmds) {
            ((CraftServer) Bukkit.getServer()).getCommandMap().register(cmd.getName(), cmd);
        }
    }   

}
