package net.azisaba.nitroplate.command;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundClearPrefixMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CommandClearPrefix implements TabExecutor {
    static @Nullable Player getTarget(String commandName, CommandSender sender, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage("This command cannot be executed by console.");
            return null;
        }
        if (args.length == 0 || (sender instanceof Player && !sender.hasPermission("nitroplate." + commandName + ".others"))) {
            return (Player) sender;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return null;
        }
        return target;
    }

    static void clearPrefix(@NotNull CommandSender sender, @NotNull Player target, boolean global) {
        net.azisaba.azipluginmessaging.api.entity.Player apiPlayer = AziPluginMessagingProvider.get().getPlayerAdapter(Player.class).get(target);
        PacketSender packetSender = AziPluginMessagingProvider.get().getServer().getPacketSender();
        ProxyboundClearPrefixMessage message = new ProxyboundClearPrefixMessage(apiPlayer, global, false);
        Protocol.P_CLEAR_PREFIX.sendPacket(packetSender, message);
        if (sender instanceof Player && "ja_jp".equalsIgnoreCase(((Player) sender).getLocale())) {
            sender.sendMessage(ChatColor.GREEN + "Prefixをリセットしました。");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Prefix has been cleared.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target = getTarget("clearprefix", sender, args);
        if (target == null) return true;
        clearPrefix(sender, target, false);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.hasPermission("nitroplate.clearprefix.others") && args.length == 1) {
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
