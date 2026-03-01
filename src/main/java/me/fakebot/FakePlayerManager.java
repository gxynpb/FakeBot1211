package me.fakebot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.*;

public class FakePlayerManager {
    private final Map<String, Player> fakePlayers = new HashMap<>();

    public void createFakePlayer(String name, Location loc) {
        if (fakePlayers.containsKey(name)) return;

        // 创建游戏档案
        var profile = Bukkit.createProfile(UUID.randomUUID(), name);
        // 使用 Paper API 创建假人
        Player fakePlayer = Bukkit.createPlayer(profile);

        // 传送假人到指定位置
        fakePlayer.teleport(loc);

        // 触发登录事件，让服务器正确识别假人
        var loginEvent = new PlayerLoginEvent(fakePlayer, PlayerLoginEvent.Result.ALLOWED, null);
        Bukkit.getPluginManager().callEvent(loginEvent);

        // 将假人加入世界
        fakePlayer.spigot().respawn();
        fakePlayers.put(name, fakePlayer);
    }

    public void removeFakePlayer(String name) {
        Player fakePlayer = fakePlayers.get(name);
        if (fakePlayer != null) {
            fakePlayer.kickPlayer("Removed by admin");
            fakePlayers.remove(name);
        }
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
