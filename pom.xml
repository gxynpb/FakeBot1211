package me.fakebot;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer; // 👉 注意：包名需与 MC 版本匹配（如 v1_21_R1 对应 1.21.1）

import java.util.*;

public class FakePlayerManager {
    private final Map<String, Player> fakePlayers = new HashMap<>();
    private final Plugin plugin = FakePlayerPlugin.getInstance();

    public void createFakePlayer(String name, Location loc) {
        if (fakePlayers.containsKey(name)) {
            plugin.getLogger().warning("假人 " + name + " 已存在，跳过创建");
            return;
        }

        // 1. 创建 GameProfile（身份凭证）
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);

        // 2. 通过 CraftServer（CraftBukkit 实现）访问 NMS 层，创建 ServerPlayer（假玩家）
        Player fakePlayer = null;
        try {
            CraftServer craftServer = (CraftServer) Bukkit.getServer();
            net.minecraft.server.MinecraftServer nmsServer = craftServer.getServer();
            net.minecraft.server.level.PlayerList playerList = nmsServer.getPlayerList();
            
            // 👇 调用 NMS 方法创建 ServerPlayer（核心：通过 NMS 生成假玩家）
            net.minecraft.world.entity.player.Player nmsPlayer = playerList.createPlayer(profile, "FakeBot"); 
            fakePlayer = nmsPlayer.getBukkitEntity(); // 转换为 Bukkit 层的 Player 对象

            // 3. 传送假人到指定位置
            if (!fakePlayer.teleport(loc)) {
                plugin.getLogger().severe("假人 " + name + " 传送失败，位置无效");
                return;
            }

            // 4. 触发 PlayerLoginEvent，模拟真实登录流程
            PlayerLoginEvent loginEvent = new PlayerLoginEvent(
                Bukkit.getOfflinePlayer(profile.getId()), // 离线玩家关联（用于事件触发）
                "localhost", // 客户端地址（可自定义，如玩家 IP）
                null
            );
            loginEvent.setResult(PlayerLoginEvent.Result.ALLOWED); // 允许登录
            Bukkit.getPluginManager().callEvent(loginEvent);

            if (loginEvent.getResult() != PlayerLoginEvent.Result.ALLOWED) {
                plugin.getLogger().severe("假人 " + name + " 登录被拦截: " + loginEvent.getKickMessage());
                return;
            }

            // 5. 主线程安全地将假人加入世界（避免异步问题）
            Bukkit.getScheduler().runTask(plugin, () -> {
                fakePlayer.spigot().respawn(); // 确保玩家“重生”（加载数据、显示到世界）
                fakePlayers.put(name, fakePlayer);
                plugin.getLogger().info("假人 " + name + " 创建成功");
            });

        } catch (Exception e) {
            plugin.getLogger().severe("创建假人 " + name + " 时发生错误: " + e.getMessage());
            e.printStackTrace(); // 打印堆栈，便于调试
        }
    }

    // ... 其他方法（removeFakePlayer、removeAll 等）保持不变 ...
}
