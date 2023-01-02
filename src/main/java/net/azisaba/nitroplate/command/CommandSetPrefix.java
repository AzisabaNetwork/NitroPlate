package net.azisaba.nitroplate.command;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundSetPrefixMessage;
import net.azisaba.nitroplate.NitroPlate;
import net.azisaba.nitroplate.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.InvalidArgumentException;
import xyz.acrylicstyle.util.StringReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandSetPrefix implements TabExecutor {
    static void setPrefix(@NotNull Player player, @NotNull String rawPrefix, boolean global) {
        StringReader reader = StringReader.create(rawPrefix);
        reader.skipWhitespace();
        String prefix;
        try {
            prefix = reader.readQuotableString('\\', '"');
        } catch (InvalidArgumentException e) {
            player.sendMessage(Util.toString(e));
            return;
        }
        String strip = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefix));
        if (prefix.length() > 150 || strip.length() > 16) {
            if ("ja_jp".equalsIgnoreCase(player.getLocale())) {
                player.sendMessage(ChatColor.RED + "Prefixが長すぎます。");
            } else {
                player.sendMessage(ChatColor.RED + "Prefix is too long.");
            }
            return;
        }
        if (!player.hasPermission("nitroplate.setprefix.bypass")) {
            boolean isAllowed = !strip.toLowerCase().contains("admin");
            if (ChatColor.translateAlternateColorCodes('&', prefix).contains("§k")) {
                isAllowed = false;
            }
            if (strip.contains("Mod") || strip.contains("Mgr") || strip.contains("Dev")) {
                isAllowed = false;
            }
            if (strip.toLowerCase().contains("[mod]") || strip.toLowerCase().contains("[mgr]") || strip.toLowerCase().contains("[dev]")) {
                isAllowed = false;
            }
            if (strip.matches("\\d+円皿")) {
                isAllowed = false;
            }
            if ((global || NitroPlate.preventRankPrefix) && strip.contains("Rank")) {
                isAllowed = false;
            }
            if (!isAllowed) {
                if ("ja_jp".equalsIgnoreCase(player.getLocale())) {
                    player.sendMessage(ChatColor.RED + "Prefixに使用できない文字が含まれています。");
                } else {
                    player.sendMessage(ChatColor.RED + "Prefix contains invalid characters.");
                }
                return;
            }
        }
        net.azisaba.azipluginmessaging.api.entity.Player apiPlayer = AziPluginMessagingProvider.get().getPlayerAdapter(Player.class).get(player);
        ProxyboundSetPrefixMessage message = new ProxyboundSetPrefixMessage(apiPlayer, global, prefix + " ");
        Protocol.P_SET_PREFIX.sendPacket(AziPluginMessagingProvider.get().getServer().getPacketSender(), message);
        if ("ja_jp".equalsIgnoreCase(player.getLocale())) {
            player.sendMessage(ChatColor.GREEN + "Prefixを設定しました。");
        } else {
            player.sendMessage(ChatColor.GREEN + "Prefix has been set.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command cannot be executed by console.");
            return true;
        }
        setPrefix((Player) sender, String.join(" ", args), false);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return suggest(sender, String.join(" ", args));
    }

    static @NotNull List<String> suggest(@NotNull CommandSender sender, @NotNull String rawPrefix) {
        if (!(sender instanceof Player)) return Collections.emptyList();
        if (rawPrefix.isEmpty()) {
            return Collections.emptyList();
        }
        if (rawPrefix.startsWith("\"") && !rawPrefix.endsWith("\"")) {
            rawPrefix += "\"";
        }
        StringReader reader = StringReader.create(rawPrefix);
        reader.skipWhitespace();
        String prefix;
        try {
            prefix = reader.readQuotableString('\\', '"');
        } catch (InvalidArgumentException e) {
            return Arrays.asList(Util.toString(e).split("\n"));
        }
        String colored = ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', prefix);
        return Collections.singletonList(colored + " " + ((Player) sender).getDisplayName());
    }
}
