package net.azisaba.nitroplate.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CommandSetGlobalPrefix implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command cannot be executed by console.");
            return true;
        }
        CommandSetPrefix.setPrefix((Player) sender, String.join(" ", args), true);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.singletonList(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
    }
}
