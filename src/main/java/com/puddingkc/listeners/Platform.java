package com.puddingkc.listeners;

import com.puddingkc.FishAnything;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * @author Coaixy
 * @createTime 2024-08-20
 * @packageName com.puddingkc.listeners
 */


public class Platform implements Listener {
    private final FishAnything plugin;
    // 平台默认高度
    private final int y = 10;

    public Platform(FishAnything plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // 判断是否首次进入服务器
        if (isFirstJoin(player.getUniqueId().toString())) {
            // 创建新的platformId
            int platformId = plugin.getConfig().getInt("nextPlatformIndex");
            plugin.getConfig().set("nextPlatformIndex", platformId + 1);
            plugin.getConfig().set("player." + player.getUniqueId() + ".platformId", platformId);
            // 生成平台
            World world = plugin.getServer().getWorld("world");
            if (world == null) return;
            Location platformLocation = generatePlatformLocation(world);
            generatePlatform(platformLocation);
            // 保存玩家信息
            plugin.getConfig().set("player." + player.getUniqueId() + ".platformX", platformLocation.getX());
            plugin.getConfig().set("player." + player.getUniqueId() + ".platformZ", platformLocation.getZ());
            player.teleport(platformLocation.add(1, 4, 1));
            plugin.saveConfig();
        }
    }

    @EventHandler
    public void backPlatform(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        double x = plugin.getConfig().getDouble("player." + player.getUniqueId() + ".platformX");
        double z = plugin.getConfig().getDouble("player." + player.getUniqueId() + ".platformZ");
        // 防止传送失败
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                safeTeleport(player, new Location(player.getWorld(), x, y, z), 10);
            }
        };
        runnable.runTaskLater(plugin, 5);
    }

    public boolean isSafeLocation(Location loc) {
        Block block = loc.getBlock();
        Block blockAbove = block.getRelative(0, 1, 0);
        Block blockBelow = block.getRelative(0, -1, 0);

        // 检查玩家站立的位置是否安全（例如，方块不为空气）
        if (block.getType() != Material.AIR) {
            return false;
        }

        // 检查头部位置是否安全
        if (blockAbove.getType() != Material.AIR) {
            return false;
        }

        // 检查脚下的位置是否有固体方块
        return blockBelow.getType().isSolid();
    }

    public boolean safeTeleport(Player player, Location targetLocation, int maxHeightOffset) {
        Location safeLocation = findSafeLocation(targetLocation, maxHeightOffset);
        player.teleport(safeLocation);
        return true;
    }

    public Location findSafeLocation(Location originalLocation, int maxHeightOffset) {
        Location safeLocation = originalLocation.clone();

        // 向左搜索安全地点
        for (int i = 0; i <= maxHeightOffset; i++) {
            safeLocation.add(0, 0, i);
            if (isSafeLocation(safeLocation)) {
                return safeLocation;
            }
            safeLocation.subtract(0, i, 0); // 复原到原始位置
        }

        // 向右搜索安全地点
        for (int i = 1; i <= maxHeightOffset; i++) {
            safeLocation.subtract(i, 0, 0);
            if (isSafeLocation(safeLocation)) {
                return safeLocation;
            }
            safeLocation.add(0, i, 0); // 复原到原始位置
        }

        // 如果没有找到安全位置，返回原始位置
        return originalLocation;
    }

    private Location generatePlatformLocation(World world) {
        int distance = plugin.getConfig().getInt("platformDistance", 30);
        Location spawnLocation = world.getSpawnLocation();
        int x = (int) spawnLocation.getX();
        int z = (int) spawnLocation.getZ();
        Random random = new Random();
        while (new Location(world, x, y, z).getBlock().getType() != Material.AIR) {
            if (random.nextBoolean()) {
                x += distance;
            } else {
                z += distance;
            }
        }
        return new Location(world, x, y, z);
    }

    private void generatePlatform(Location loc) {
        int size = 4;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Block block = loc.clone().add(i, 0, j).getBlock();
                if (i == 1 && j == 1) {
                    block.setType(Material.WATER);
                    block.getLocation().clone().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
                } else if (i == 1 && j == 2) {
                    block.setType(Material.WATER);
                    block.getLocation().clone().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
                } else if (i == 2 && j == 1) {
                    block.setType(Material.WATER);
                    block.getLocation().clone().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
                } else if (i == 2 && j == 2) {
                    block.setType(Material.WATER);
                    block.getLocation().clone().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
                } else {
                    block.setType(Material.BEDROCK);
                }
            }
        }
    }

    private boolean isFirstJoin(String uuid) {
        ConfigurationSection playerSection = plugin.getConfig().getConfigurationSection("player");
        if (playerSection == null) return true;
        for (String s : playerSection.getKeys(false)) {
            if (s.equals(uuid)) {
                return false;
            }
        }
        return true;
    }
}
