package sudark2.Sudark.nPC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static sudark2.Sudark.nPC.NPCManager.villagers;

public final class NPC extends JavaPlugin {

    static World main = null;

    @Override
    public void onEnable() {
        initialise(this);
    }

    public void initialise(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                main = Bukkit.getWorld("BEEF-DUNE");
                main.getEntities().forEach(entity -> {
                    if(entity.isCustomNameVisible()){
                        entity.remove();
                    }
                });

                NPCManager npcManager = new NPCManager(plugin);
                npcManager.spawnNPC(new Location(main, 5, 82, 1), "§e农民", Villager.Profession.FARMER);
                npcManager.spawnNPC(new Location(main, 5, 82, -5), "§e铁匠铺", Villager.Profession.ARMORER);
                npcManager.spawnNPC(new Location(main, 4, 82, 33), "§e染铺夫人", Villager.Profession.LEATHERWORKER);
                npcManager.spawnNPC(new Location(main, 4, 82, 39), "§e建材商", Villager.Profession.MASON);
            }
        }.runTaskLater(this, 5 * 20L);
    }

    @Override
    public void onDisable() {
        villagers.forEach(Entity::remove);
    }
}
