package sudark2.Sudark.nPC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class NPCManager implements Listener {
    private final Plugin plugin;
    public static Set<Villager> villagers = new HashSet<>();

    public NPCManager(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startNPCChecker();
    }

    // 创建村民NPC
    public void spawnNPC(Location location, String name, Villager.Profession profession) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location.clone().add(0.5, 0, 0.5), EntityType.VILLAGER);
        villager.setCustomName(name);
        villager.setCustomNameVisible(true);
        villager.setProfession(profession);
        villager.setInvulnerable(true);
        villager.setAI(false);
        villager.addScoreboardTag("npc");

        // 存储NPC信息
        villagers.add(villager);
    }

    // 定时检查NPC状态
    private void startNPCChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (villagers.isEmpty()) return;
                for (Villager entity : villagers) {
                    if (entity != null) {
                        entity.getNearbyEntities(10, 10, 10).stream()
                                .filter(e -> e instanceof org.bukkit.entity.Player)
                                .findFirst()
                                .ifPresent(player -> {
                                    Location playerLoc = player.getLocation();
                                    Location entityLoc = entity.getLocation();
                                    entityLoc.setDirection(playerLoc.toVector().subtract(entityLoc.toVector()));
                                    entity.teleport(entityLoc);
                                });
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 10L); // 每0.5秒检查一次
    }

    // 防止NPC被破坏
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Villager v) {
            if (entity.getScoreboardTags().contains("npc")) {
                event.setCancelled(true);
            }
        }
    }
}