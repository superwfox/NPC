package sudark2.Sudark.nPC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

import static sudark2.Sudark.nPC.NPC.displays;
import static sudark2.Sudark.nPC.NPC.main;

public class NPCManager implements Listener {
    static Location shop1 = p(124, 66, -1);
    static Location shop2 = p(124, 66, 41);
    static Location shop3 = p(158, 66, -1);
    static Location shop4 = p(158, 66, 41);

    static Set<Location> settlements = Set.of(
            p(107, 63, 9), p(107, 63, 31),
            p(141, 63, 9), p(141, 63, 31),
            p(175, 63, 9), p(175, 63, 31)
    );

    static Transformation huge = new Transformation(
            new Vector3f(0, 5f, 0),            // 平移（translation）向量
            new Quaternionf(),  // 旋转（rotation），默认不旋转
            new Vector3f(6f, 6f, 6f), // 缩放（scale）到 1
            new Quaternionf()                // 旋转中心点（leftRotation），默认不旋转
    );

    static Transformation drama = new Transformation(
            new Vector3f(0, 7f, 0),            // 平移（translation）向量
            new Quaternionf(),  // 旋转（rotation），默认不旋转
            new Vector3f(8f, 7f, 8f), // 缩放（scale）到 1
            new Quaternionf()                // 旋转中心点（leftRotation），默认不旋转
    );

    public static void showTime(Plugin plugin) {
        reGenerate();
        new BukkitRunnable() {
            @Override
            public void run() {

                displays.forEach(display -> {
                    rotateDisplay((ItemDisplay) Bukkit.getEntity(display), 1.2f);
                });

            }
        }.runTaskTimer(plugin, 0, 20);
    }

    @EventHandler
    public void onDisplayDead(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() && event.getMessage().equals("regenerate")) {
            reGenerate();
            Bukkit.broadcastMessage("NPCs have been regenerated!");
        }
    }

    public static void reGenerate() {
        main.getEntities().forEach(
                entity -> {
                    if (entity instanceof ItemDisplay)
                        entity.remove();
                });
        createShop(shop1, Material.STONECUTTER);
        createShop(shop2, Material.ORANGE_GLAZED_TERRACOTTA);
        createShop(shop3, Material.BARREL);
        createShop(shop4, Material.BLAST_FURNACE);
        for (Location settlement : settlements) {
            if (getBanner(settlement) != null) {
                ItemDisplay display = (ItemDisplay) settlement.getWorld().spawnEntity(settlement, EntityType.ITEM_DISPLAY);
                display.setItemStack(getBanner(settlement));
                display.setTransformation(drama);
                display.setInterpolationDelay(0);
                displays.add(display.getUniqueId());
            }
        }
    }

    public static void createShop(Location loc, Material m) {
        ItemDisplay item = (ItemDisplay) loc.getWorld().spawnEntity(loc, EntityType.ITEM_DISPLAY);
        item.setItemStack(new ItemStack(m));
        item.setTransformation(huge);
        item.setInterpolationDelay(0);
        displays.add(item.getUniqueId());
    }

    public static void rotateDisplay(ItemDisplay display, float radiansPerSecond) {
        if (display == null) return;
        Transformation tf = display.getTransformation();
        tf.getLeftRotation()
                .rotateLocalY(radiansPerSecond / 20f);  // 每tick 增加	(弧度/秒 ÷ 20)

        display.setInterpolationDuration(20);
        display.setInterpolationDelay(0);
        display.setTransformation(tf);

    }


    public static Location p(int x, int y, int z) {
        return new Location(main, x, y, z);
    }

    public static ItemStack getBanner(Location loc) {
        Block block = loc.getBlock();

        if (block.getType().toString().endsWith("_BANNER")) {
            Banner banner = (Banner) block.getState();

            ItemStack item = new ItemStack(block.getType());
            BannerMeta meta = (BannerMeta) item.getItemMeta();

            meta.setPatterns(banner.getPatterns());
            item.setItemMeta(meta);

            return item;
        }
        return null;
    }
}