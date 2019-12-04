package fr.cocoraid.prodigyserver.minigame.games.pizzaspleef;

import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.minigame.games.MiniGame;
import fr.cocoraid.prodigyserver.utils.UtilMath;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PizzaSpleefGame extends MiniGame   {


    private Player goldPlayer;
    private boolean goldenReady = false;

    private static ItemStack specialPotion = new ItemStack(Material.SPLASH_POTION);
    private static ItemStack superPickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
    static {
        superPickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED,10);
        ItemMeta meta = superPickaxe.getItemMeta();
        meta.setDisplayName("§3Super Pickaxe");
        superPickaxe.setItemMeta(meta);

        PotionMeta potionMeta = (PotionMeta) specialPotion.getItemMeta();
        potionMeta.setDisplayName("§cSpecial Potion");
        potionMeta.setBasePotionData(new PotionData(PotionType.UNCRAFTABLE, false,false));
        potionMeta.setColor(Color.RED);
        specialPotion.setItemMeta(potionMeta);

    }


    public PizzaSpleefGame() {
        super("§6Pizza§cSpleef");
        setFallgame();
    }

    @Override
    public void start() {
        super.start();

        players.forEach(p -> p.getInventory().setItem(0,superPickaxe));
    }

    @Override
    public void update() {
        super.update();
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        if(!players.contains(e.getPlayer())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void breakblock(BlockBreakEvent e) {
        if(!players.contains(e.getPlayer())) return;
        if(e.getBlock().getType() == Material.GOLD_BLOCK && e.getBlock().hasMetadata("golden")) return;

        if(e.getPlayer().getInventory().getItemInMainHand() == null) return;
        if(!e.getPlayer().getInventory().getItemInMainHand().equals(superPickaxe)) return;

        Player p = e.getPlayer();
        e.setDropItems(false);
        switch(e.getBlock().getType()) {
            case OBSIDIAN:
                p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASEDRUM,1,0);
                players.stream().filter(cur -> !p.equals(cur)).forEach(cur -> cur.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20 * 5,1,false,false,false)));
                Bukkit.broadcastMessage("§5The player " + e.getPlayer().getName() + " has blinded everyone !");
                break;
            case LIME_WOOL:
                p.playSound(p.getLocation(),Sound.ENTITY_IRON_GOLEM_ATTACK,1,0);
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,20 * 15, 4,false,false,false));
                sendBarMessage(p,"§aYou just got jump boost !");
                break;
            case RED_WOOL:
                p.playSound(p.getLocation(),Sound.ITEM_SHIELD_BREAK,1,2);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20 * 10, 2,false,false,false));
                sendBarMessage(p,"§cYou just got speed boost !");
                break;
            case WHITE_STAINED_GLASS:
                p.playSound(p.getLocation(),Sound.BLOCK_PUMPKIN_CARVE,1,0);
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,20 * 10, 1,false,false,false));
                sendBarMessage(p,"§fYou are now invisible");
                break;
            case SOUL_SAND:
                p.playSound(p.getLocation(),Sound.ENTITY_DOLPHIN_ATTACK,1,0);
                sendBarMessage(p,"§cYou just got a special potion for your ennemies");
                p.getInventory().addItem(specialPotion);
                break;
            case GOLD_BLOCK:
                goldPlayer = p;
                p.setExp(1);
                goldenReady = true;
                p.getInventory().getItemInMainHand().setType(Material.GOLDEN_PICKAXE);
                sendBarMessage(p,"§6" + p.getName() + " is now a gold player ! Be careful :O");
                p.getWorld().playSound(p.getLocation(),Sound.ENTITY_ENDER_DRAGON_GROWL,1,1);
                break;
        }

    }

    @EventHandler
    public void pvp(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof  Player && !players.contains(e.getEntity())) return;
        e.setDamage(0);
    }

    @EventHandler
    public void itemdamage(PlayerItemDamageEvent e) {
        if(!players.contains(e.getPlayer())) return;
        e.setCancelled(true);

    }

    @EventHandler
    public void otherDamage(EntityDamageEvent e) {
        if(!players.contains(e.getEntity())) return;

        if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void splash(PotionSplashEvent e) {
        if(!e.getEntity().getWorld().equals(gameWorld.getSpawnPoint().getWorld())) return;
        if(e.getPotion().getItem().equals(specialPotion)) {
            e.getAffectedEntities().forEach(cur -> {
                cur.addPotionEffect(new PotionEffect(UtilMath.randomRange(0,1) == 0 ? PotionEffectType.SLOW_DIGGING : PotionEffectType.SLOW,20 * 10, 2, false , false, false));
            });
        }
    }

    @Override
    public void setSpectator(Player p) {
        super.setSpectator(p);
        if(goldPlayer != null && goldPlayer.equals(p))
            goldPlayer = null;
    }

    @EventHandler
    public void goldenInteract(PlayerInteractEvent e) {
        if(!players.contains(e.getPlayer())) return;

        if(e.getItem() != null && goldPlayer != null && e.getPlayer().equals(goldPlayer) && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(!e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(superPickaxe.getItemMeta().getDisplayName())) return;

            Location l = e.getPlayer().getLocation();
            l.setPitch(0);
            goldenPower(e.getPlayer(), e.getClickedBlock(), l.getDirection());
        }
    }

    private void goldenCooldownPower(Player p) {
        goldenReady  = false;
        p.setExp(0);
        new BukkitRunnable() {
            float exp = 0F;
            @Override
            public void run() {
                p.setExp(exp);
                exp+=0.005F;
                if(exp >= 1) {
                    this.cancel();
                    p.setExp(1);
                    p.playSound(p.getLocation(),Sound.BLOCK_TRIPWIRE_ATTACH,1,0);
                    goldenReady = true;
                }
            }
        }.runTaskTimer(ProdigyServer.getInstance(), 1, 1);

    }

    private void goldenPower(Player p, Block start, Vector v) {

        if(!goldenReady) {
            p.playSound(p.getLocation(),Sound.ENTITY_BLAZE_HURT,1,0F);
            return;
        }
        goldenCooldownPower(p);

        Location loc = start.getLocation();
        List<Block> golden = new ArrayList<>();
        loc.getWorld().playSound(loc,Sound.BLOCK_ANVIL_HIT,1f,0F);

        new BukkitRunnable() {
            int max = 20;
            int maxairtime = 5;
            @Override
            public void run() {
                loc.add(v);
                max--;
                if(loc.getBlock().getType() == Material.AIR)
                    maxairtime--;
                if (max <= 0 || maxairtime <= 0) {
                    this.cancel();
                    golden.forEach(b -> {
                        b.setType(Material.AIR);
                    });
                    start.getWorld().playSound(start.getLocation(),Sound.ENTITY_WITHER_HURT,0.1f,1F);
                    return;
                }

                if(!golden.contains(loc.getBlock())) {
                    loc.getBlock().setType(Material.GOLD_BLOCK);
                    loc.getBlock().setMetadata("golden",new FixedMetadataValue(ProdigyServer.getInstance(),"golden"));
                    golden.add(loc.getBlock());
                    loc.getWorld().playEffect(loc,Effect.STEP_SOUND, Material.GOLD_BLOCK);
                }
            }
        }.runTaskTimer(ProdigyServer.getInstance(), 0, 0);
    }


}
