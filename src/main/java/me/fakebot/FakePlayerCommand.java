package me.fakebot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FakePlayerCommand implements CommandExecutor {
    private final FakePlayerManager m = FakePlayerPlugin.getInstance().getManager();

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p) || !p.isOp()) {
            s.sendMessage("§c无权限");
            return true;
        }
        if (a.length == 0) {
            p.sendMessage("§e/bot create 名字 | remove 名字 | list");
            return true;
        }
        switch(a[0].toLowerCase()) {
            case "create" -> {
                if (a.length > 1) {
                    m.createFakePlayer(a[1], p.getLocation());
                    p.sendMessage("§a创建成功");
                }
            }
            case "remove" -> {
                if (a.length > 1) {
                    m.removeFakePlayer(a[1]);
                    p.sendMessage("§c删除成功");
                }
            }
            case "list" -> p.sendMessage("§a假人: " + m.getList());
        }
        return true;
    }
}
