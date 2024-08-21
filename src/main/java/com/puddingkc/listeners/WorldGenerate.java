package com.puddingkc.listeners;

import com.puddingkc.FishAnything;
import org.bukkit.Bukkit;
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

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author Coaixy
 * @createTime 2024-08-20
 * @packageName com.puddingkc.listeners
 */


public class WorldGenerate implements Listener {
    private final FishAnything plugin;
    private final int y = 30;

    public WorldGenerate(FishAnything plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isFirstJoin(player.getUniqueId().toString())) {
            player.sendMessage("欢迎加入服务器，这是你第一次加入服务器，正在为你生成平台");
            // 记录平台ID
            int platformId = plugin.getConfig().getInt("nextPlatformIndex");
            plugin.getConfig().set("nextPlatformIndex", platformId + 1);
            plugin.getConfig().set("player." + player.getUniqueId() + ".platformId", platformId);
            World world = plugin.getServer().getWorld("world");
            if (world == null) return;
            Location platformLocation = generatePlatformLocation(world);
            generatePlatform(platformLocation);
            plugin.getConfig().set("player." + player.getUniqueId() + ".platformX", platformLocation.getX());
            plugin.getConfig().set("player." + player.getUniqueId() + ".platformZ", platformLocation.getZ());
            player.teleport(platformLocation.add(0, 2, 0));
            plugin.saveConfig();
        }
    }

    @EventHandler
    public void backPlatform(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        double x = plugin.getConfig().getDouble("player." + player.getUniqueId() + ".platformX");

        double z = plugin.getConfig().getDouble("player." + player.getUniqueId() + ".platformZ");

        player.sendMessage("正在传送回你的平台 " + x + " " + y + " " + z);
        player.setRespawnLocation(new Location(player.getWorld(), x, y + 2, z));

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(new Location(player.getWorld(), x, y + 2, z));
            }
        };
        runnable.runTaskLater(plugin, 5);
    }

    private Location generatePlatformLocation(World world) {
        Location spawnLocation = world.getSpawnLocation();
        int x = (int) spawnLocation.getX();
        int z = (int) spawnLocation.getZ();
        Random random = new Random();
        while (new Location(world, x, y, z).getBlock().getType() != Material.AIR) {
            if (random.nextBoolean()) {
                x += 15;
            } else {
                z += 15;
            }
        }
        Location platformLocation = new Location(world, x, y, z);
        return platformLocation;
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
