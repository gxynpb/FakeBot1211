package me.fakebot;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.craftbukkit.CraftServer;

import java.util.*;

public class FakePlayerManager {
    private final Map<String, Player> fakePlayers = new HashMap<>();
    private final Plugin plugin = FakePlayerPlugin.getInstance();

    public void createFakePlayer(String name, Location loc) {
        if (fakePlayers.containsKey(name)) {
            plugin.getLogger().warning("假人 " + name + " 已存在，跳过创建");
            return;
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        Player fakePlayer = craftServer.createPlayer(profile);

        if (!fakePlayer.teleport(loc)) {
            plugin.getLogger().severe("假人 " + name + " 传送失败，位置无效");
            return;
        }

        PlayerLoginEvent loginEvent = new PlayerLoginEvent(fakePlayer, "localhost", null);
        loginEvent.setResult(PlayerLoginEvent.Result.ALLOWED);
        Bukkit.getPluginManager().callEvent(loginEvent);

        if (loginEvent.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            plugin.getLogger().severe("假人 " + name + " 登录被拦截: " + loginEvent.getKickMessage());
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            fakePlayer.spigot().respawn();
            fakePlayers.put(name, fakePlayer);
            plugin.getLogger().info("假人 " + name + " 创建成功");
        });
    }

    public void removeFakePlayer(String name) {
        Player fakePlayer = fakePlayers.get(name);
        if (fakePlayer == null) {
            plugin.getLogger().warning("假人 " + name + " 不存在，跳过删除");
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            fakePlayer.kickPlayer("Removed by FakeBot");
            fakePlayers.remove(name);
            plugin.getLogger().info("假人 " + name + " 已移除");
        });
    }

    public void removeAll() {
        new ArrayList<>(fakePlayers.keySet()).forEach(this::removeFakePlayer);
    }

    public List<String> getList() {
        return new ArrayList<>(fakePlayers.keySet());
    }

    public boolean exists(String name) {
        return fakePlayers.containsKey(name);
    }
}
