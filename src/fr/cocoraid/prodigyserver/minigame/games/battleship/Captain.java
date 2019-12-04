package fr.cocoraid.prodigyserver.minigame.games.battleship;

import fr.cocoraid.prodigyserver.utils.GlowEntity;
import fr.cocoraid.prodigyserver.utils.Head;
import fr.cocoraid.prodigyserver.utils.UtilItem;
import fr.cocoraid.prodigyserver.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.Map;

public class Captain {


    private Map<Player,BattleshipGame.PlayerData> data;

    private int life = 200;
    private ArmorStand captain;
    private boolean dead = false;
    private BattleshipTeam team;

    public Captain(BattleshipTeam team,Map<Player,BattleshipGame.PlayerData> data) {
        this.data = data;
        this.team = team;
        team.getCaptainLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.BEDROCK);
        team.getCaptainLocation().getBlock().setType(Material.AIR);
        this.captain = team.getCaptainLocation().getWorld().spawn(team.getCaptainLocation(),ArmorStand.class);
        captain.setGravity(false);
        captain.setCustomNameVisible(true);
        captain.setArms(true);
        captain.setBasePlate(false);
        captain.setLeggings(UtilItem.getColorArmor(Material.LEATHER_LEGGINGS,team.getColor()));
        captain.setChestplate(UtilItem.getColorArmor(Material.LEATHER_CHESTPLATE,team.getColor()));
        captain.setBoots(UtilItem.getColorArmor(Material.LEATHER_BOOTS,team.getColor()));
        captain.setHelmet(team == BattleshipTeam.PIRATE ? Head.BATTLESHIP_SPARROW.getHead() : Head.BATTLESHIP_CAPTAIN.getHead());
        captain.setCustomName(team.getName() + " Captain §a" + life);
    }

    public void damage(Player damager) {
        if(dead) return;
        if(damager.getGameMode() != GameMode.SURVIVAL || !data.containsKey(damager)) return;
        if(data.get(damager).getTeam() == team) return;

        damager.playSound(damager.getLocation(), Sound.ENTITY_GENERIC_HURT,1,1);
        damager.playSound(damager.getLocation(), Sound.ENTITY_VILLAGER_HURT,1,0);
        captain.getLocation().getWorld().spawnParticle(Particle.DAMAGE_INDICATOR,captain.getLocation().clone().add(0,1,0),5,0.1,0.1,0.1,0.1);
        life--;
        captain.setCustomName(team.getName() + " Captain §a" + life);

        if(life % 50 == 0 || life == 199) {
            data.keySet().stream().filter(p -> data.get(p).getTeam() == team).forEach(p -> {
                Utils.strike(captain.getLocation().add(0,5,0),p);
                p.playSound(p.getLocation(),Sound.BLOCK_END_PORTAL_SPAWN,1,0);
                p.sendTitle("","§cYour captain is attacked !",40,40,40);
                p.sendMessage(team.getName() + "! §cYour captain is beeing attacked inside your captain room (life " + life + ")");
                p.sendMessage(team.getName() + ": §bKill the assailant otherwise: no longer respawn & flag capture");
            });
        }

        if(life <= 0) {
            dead = true;
            captain.remove();

            data.keySet().forEach(cur -> {
                cur.playSound(cur.getLocation(),Sound.ENTITY_WITHER_SPAWN,1,0);
                cur.sendMessage("§cThe " + getTeam().getName() + " §ccaptain has die !");
            });
            captain.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE,captain.getLocation().clone().add(0,1,0),3,0.1,0.1,0.1,0.1);
            data.keySet().stream().filter(p -> data.get(p).getTeam() == team).forEach(p -> {
                p.sendTitle("§4Your captain has die ! ","§cYou can't respawn and capture the ennemy flag...",40,40,40);
            });
            if(team.getFlagCapturedPlayer() != null) {
                team.getFlagCapturedPlayer().getInventory().setHelmet(null);
                GlowEntity.removeGlow(team.getFlagCapturedPlayer());
                team.setFlagCapturedByPlayer(null);
            }
            team.setAllowRespawn(false);
            team.getBanner().getBlock().setType(Material.AIR);
        }
    }

    public ArmorStand getCaptain() {
        return captain;
    }

    public boolean isDead() {
        return dead;
    }



    public BattleshipTeam getTeam() {
        return team;
    }
}
