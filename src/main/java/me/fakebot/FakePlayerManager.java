package me.fakebot;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import com.mojang.authlib.GameProfile;
import java.util.*;

public class FakePlayerManager {
    private final Map<String, ServerPlayer> fakePlayers = new HashMap<>();

    public void createFakePlayer(String name, Location loc) {
        if(fakePlayers.containsKey(name)) return;
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        ServerPlayer sp = new ServerPlayer(
            ((CraftServer)Bukkit.getServer()).getServer(),
            ((CraftWorld)loc.getWorld()).getHandle(),
            profile
        );
        sp.setPos(loc.getX(), loc.getY(), loc.getZ());
        ((CraftServer)Bukkit.getServer()).getServer().getPlayerList().addPlayer(sp);
        fakePlayers.put(name, sp);
    }

    public void removeFakePlayer(String name) {
        ServerPlayer sp = fakePlayers.get(name);
        if(sp != null) {
            sp.connection.disconnect("Remove");
            fakePlayers.remove(name);
        }
    }

    public void removeAll() {
        new ArrayList<>(fakePlayers.keySet()).forEach(this::removeFakePlayer);
    }

    public List<String> getList() { return new ArrayList<>(fakePlayers.keySet()); }
    public boolean exists(String name) { return fakePlayers.containsKey(name); }
}
