package net.azisaba.nitroplate;

import net.azisaba.nitroplate.command.CommandClearGlobalPrefix;
import net.azisaba.nitroplate.command.CommandClearPrefix;
import net.azisaba.nitroplate.command.CommandSetGlobalPrefix;
import net.azisaba.nitroplate.command.CommandSetPrefix;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class NitroPlate extends JavaPlugin {
    public static boolean preventRankPrefix = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        preventRankPrefix = getConfig().getBoolean("prevent-rank-prefix", false);
        Bukkit.getPluginCommand("setprefix").setExecutor(new CommandSetPrefix());
        Bukkit.getPluginCommand("setglobalprefix").setExecutor(new CommandSetGlobalPrefix());
        Bukkit.getPluginCommand("clearprefix").setExecutor(new CommandClearPrefix());
        Bukkit.getPluginCommand("clearglobalprefix").setExecutor(new CommandClearGlobalPrefix());
    }
}
