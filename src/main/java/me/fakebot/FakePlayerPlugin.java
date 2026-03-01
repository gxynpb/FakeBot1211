package me.fakebot;

import org.bukkit.plugin.java.JavaPlugin;

public class FakePlayerPlugin extends JavaPlugin {
    private static FakePlayerPlugin instance;
    private FakePlayerManager manager;

    @Override
    public void onEnable() {
        instance = this;
        manager = new FakePlayerManager();
        getCommand("fakeplayer").setExecutor(new FakePlayerCommand());
        getLogger().info("§aFakeBot 1.21.10 加载成功 | 指令 /bot");
    }

    @Override
    public void onDisable() {
        manager.removeAll();
    }

    public static FakePlayerPlugin getInstance() { return instance; }
    public FakePlayerManager getManager() { return manager; }
}
