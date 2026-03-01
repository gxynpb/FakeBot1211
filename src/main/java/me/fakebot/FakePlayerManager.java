package me.fakebot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;

import java.util.*;

public class FakePlayerManager {
    private final Map<String, Player> fakePlayers = new HashMap<>();
    private final Plugin plugin = FakePlayerPlugin.getInstance();

    public void createFakePlayer(String name, Location loc) {
        if (fakePlayers.containsKey(name)) {
            plugin.getLogger().warning("假人 " + name + " 已存在，跳过创建");
            return;
        }

        // 使用 Bukkit API 创建 PlayerProfile，彻底避免 authlib 依赖
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), name);
        // 使用 Paper API 创建假人
        Player fakePlayer = Bukkit.createPlayer(profile);

        // 传送假人到指定位置，并检查是否成功
        if (!fakePlayer.teleport(loc)) {
            plugin.getLogger().severe("假人 " + name + " 传送失败，位置无效");
            return;
        }

        // 触发登录事件，让服务器和其他插件正确识别假人
        PlayerLoginEvent loginEvent = new PlayerLoginEvent(fakePlayer, "localhost", null);
        loginEvent.setResult(PlayerLoginEvent.Result.ALLOWED);
        Bukkit.getPluginManager().callEvent(loginEvent);

        if (loginEvent.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            plugin.getLogger().severe("假人 " + name + " 登录被拦截: " + loginEvent.getKickMessage());
            return;
        }

        // 主线程安全地将假人加入世界
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

        // 主线程安全地移除假人
        Bukkit.getScheduler().runTask(plugin, () -> {
            fakePlayer.kickPlayer("Removed by FakeBot");
            fakePlayers.remove(name);
            plugin.getLogger().info("假人 " + name + " 已移除");
        });
    }

    public void removeAll() {
        // 遍历副本，避免在迭代时修改原集合
        new ArrayList<>(fakePlayers.keySet()).forEach(this::removeFakePlayer);
    }

    public List<String> getList() {
        return new ArrayList<>(fakePlayers.keySet());
    }

    public boolean exists(String name) {
        return fakePlayers.containsKey(name);
    }
}
