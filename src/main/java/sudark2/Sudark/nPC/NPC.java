package sudark2.Sudark.nPC;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static sudark2.Sudark.nPC.NPCManager.showTime;

public final class NPC extends JavaPlugin {

    static World main = null;
    static Set<UUID> displays = new HashSet<>();

    @Override
    public void onEnable() {
        main = Bukkit.getWorld("BEEF-DUNE");
        showTime(this);
        Bukkit.getPluginManager().registerEvents(new NPCManager(), this);
    }

    @Override
    public void onDisable() {
        displays.removeIf(uuid -> {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                entity.remove();
                return true;
            }
            return false;
        });

    }
}
