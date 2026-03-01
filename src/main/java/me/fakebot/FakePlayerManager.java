package me.fakebot;

import io.papermc.paper.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.FakePlayer;
import org.bukkit.entity.Player;
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

        // 1. 创建 PlayerProfile（Paper API 标准方式）
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), name);
        // 2. 使用 Paper API 创建 User
        User user = Bukkit.createUser(profile);
        // 3. 创建 FakePlayer（明确类型，避免编译错误）
        FakePlayer fakePlayer = user.createPlayer(loc.getWorld());

        // 4. 传送假人到指定位置
        if (!fakePlayer.teleport(loc)) {
            plugin.getLogger().severe("假人 " + name + " 传送失败，位置无效");
            return;
        }

        // 5. 主线程安全地将假人加入世界
        Bukkit.getScheduler().runTask(plugin, () -> {
            fakePlayer.spawn(); // Paper API 专用方法，将假人加入世界
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
            fakePlayer.kick(Component.text("Removed by FakeBot")); // 使用 Adventure API 处理文本
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
