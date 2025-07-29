package sudark2.Sudark.nPC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCManager implements Listener {
    private final Plugin plugin;
    private final Map<UUID, NPCData> npcMap = new HashMap<>();

    // NPC数据类
    public static class NPCData {
        Location location;
        String name;
        String profession;

        public NPCData(Location location, String name, String profession) {
            this.location = location;
            this.name = name;
            this.profession = profession;
        }
    }

    public NPCManager(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startNPCChecker();
    }

    // 创建村民NPC
    public void spawnNPC(Location location, String name, String profession) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomName(name);
        villager.setCustomNameVisible(true);
        villager.setProfession(Villager.Profession.valueOf(profession));
        villager.setInvulnerable(true);
        villager.setAI(false); // 禁用AI，防止移动

        // 存储NPC信息
        NPCData data = new NPCData(location, name, profession);
        npcMap.put(villager.getUniqueId(), data);
    }

    // 定时检查NPC状态
    private void startNPCChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                npcMap.entrySet().forEach(entry -> {
                    UUID uuid = entry.getKey();
                    NPCData data = entry.getValue();
                    Entity entity = Bukkit.getEntity(uuid);
                    if (entity == null || !entity.isValid()) {
                        // NPC不可用，重新创建
                        spawnNPC(data.location, data.name, data.profession);
                        npcMap.remove(uuid); // 移除旧UUID
                    } else {
                        // 更新朝向
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
                });
            }
        }.runTaskTimer(plugin, 0L, 10L); // 每0.5秒检查一次
    }

    // 防止NPC移动
    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent event) {
        if (npcMap.containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    // 防止NPC被破坏
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (npcMap.containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}