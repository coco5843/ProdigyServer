package fr.cocoraid.prodigyserver.lobby.event;

import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.lobby.LobbyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

public class CancelEvent implements Listener {

    private LobbyManager lm = ProdigyServer.getInstance().getLobbyManager();

    @EventHandler
    public void breakblock(BlockBreakEvent e) {
        if(!lm.getWorld().equals(e.getBlock().getWorld())) return;
        if(e.getPlayer().hasPermission("ps.admin")) return;
            e.setCancelled(true);

    }

    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent e) {
        if(!lm.getWorld().equals(e.getPlayer().getWorld())) return;
        if(e.getPlayer().hasPermission("ps.admin")) return;

        e.setCancelled(true); }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;
        if(e.getEntity() instanceof Player && ((Player)e.getEntity()).hasPermission("ps.admin")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if(!lm.getWorld().equals(e.getPlayer().getWorld())) return;
        if(e.getPlayer().hasPermission("ps.admin")) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;
        if(e.getEntity() instanceof Player && ((Player)e.getEntity()).hasPermission("ps.admin")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(!lm.getWorld().equals(e.getBlock().getWorld())) return;
        if(e.getPlayer().hasPermission("ps.admin")) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if(!lm.getWorld().equals(e.getPlayer().getWorld())) return;
        if(e.getPlayer().hasPermission("ps.admin")) return;
        e.setCancelled(true);

    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if(!lm.getWorld().equals(e.getPlayer().getWorld())) return;
        if(e.getPlayer().hasPermission("ps.admin")) return;

        e.setCancelled(true);
    }


    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;

        if(e.getCause() != EntityDamageEvent.DamageCause.VOID
                && e.getCause() != EntityDamageEvent.DamageCause.SUICIDE)
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByBlock(EntityDamageByBlockEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;

        if(e.getCause() != EntityDamageEvent.DamageCause.VOID
                && e.getCause() != EntityDamageEvent.DamageCause.SUICIDE)
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;

        if(e.getCause() != EntityDamageEvent.DamageCause.VOID
                && e.getCause() != EntityDamageEvent.DamageCause.SUICIDE)
            e.setCancelled(true);
    }


    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent e) {
        if(!lm.getWorld().equals(e.getPlayer().getWorld())) return;

        e.setCancelled(true);
        e.getPlayer().updateInventory();
    }




    @EventHandler
    public void onEntityBlockPlace(EntityChangeBlockEvent e) {
        if(!lm.getWorld().equals(e.getBlock().getWorld())) return;

        e.setCancelled(true);
    }
    @EventHandler
    public void onEntityBlockForm(EntityBlockFormEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;
        e.setCancelled(true);
    }
    @EventHandler
    public void onEntityCreatePortal(EntityCreatePortalEvent e) {
        e.setCancelled(true);
    }
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        if(!lm.getWorld().equals(e.getBlock().getWorld())) return;

        e.setCancelled(true);
    }
    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        if(!lm.getWorld().equals(e.getIgnitingBlock().getWorld())) return;

        e.setCancelled(true);
    }
    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        if(!lm.getWorld().equals(e.getPlayer().getWorld())) return;

        e.setCancelled(true);
    }
    @EventHandler
    public void onHangingBreak(HangingBreakEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;

        e.setCancelled(true);
    }
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent e) {
        if(!lm.getWorld().equals(e.getRemover().getWorld())) return;

        e.setCancelled(true);
    }
    @EventHandler
    public void onHangingPlace(HangingPlaceEvent e) {
        if(!lm.getWorld().equals(e.getPlayer().getWorld())) return;

        e.setCancelled(true);
    }
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;

        e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerLeashEntity(PlayerLeashEntityEvent e) {
        if(!lm.getWorld().equals(e.getPlayer().getWorld())) return;
        e.setCancelled(true);
    }
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;

        e.setCancelled(true); }
    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;

        e.setCancelled(true); }
    @EventHandler
    public void onEntityPortal(EntityPortalEvent e) {
        e.setCancelled(true); }

    @EventHandler
    public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent e) {
        e.setCancelled(true); }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(!lm.getWorld().equals(e.getEntity().getWorld())) return;

        e.setKeepInventory(true);
        e.setDeathMessage(null);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if(e.toWeatherState())
            e.setCancelled(true);
    }




}
