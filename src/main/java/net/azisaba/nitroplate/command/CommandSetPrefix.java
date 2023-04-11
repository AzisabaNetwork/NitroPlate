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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandSetPrefix implements TabExecutor {
    private static final Set<String> STRIP_CONTAINS_DISALLOW = new HashSet<>(Arrays.asList(
            "Member", "Builder", "YouTuber", "Youtuber", "Mod", "Mgr", "Dev", "ゲーミング", "鯖主", "[Broadcast]"
    ));

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
        String strip =
                ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefix))
                        .replace('À', 'A').replace('Á', 'A');
        if (prefix.length() > 150 || strip.length() > 16) {
            if ("ja_jp".equalsIgnoreCase(player.getLocale())) {
                player.sendMessage(ChatColor.RED + "Prefixが長すぎます。");
            } else {
                player.sendMessage(ChatColor.RED + "Prefix is too long.");
            }
            return;
        }
        if (!player.hasPermission("nitroplate.setprefix.bypass")) {
            boolean isDisallowed = strip.toLowerCase().contains("admin") ||
                    strip.toLowerCase().contains("abmin") ||
                    ChatColor.translateAlternateColorCodes('&', prefix).contains("§k") ||
                    strip.toLowerCase().contains("owner") ||
                    strip.toLowerCase().contains("[member]") ||
                    strip.toLowerCase().contains("[builder]") ||
                    strip.toLowerCase().contains("[youtuber]") ||
                    strip.toLowerCase().contains("[mod]") ||
                    strip.toLowerCase().contains("[mgr]") ||
                    strip.toLowerCase().contains("[dev]") ||
                    strip.startsWith("●") ||
                    strip.matches(".*(100|500|1000|2000|5000|10000|20000|50000|100000)円皿.*") ||
                    (global || NitroPlate.preventRankPrefix) && strip.contains("Rank") ||
                    STRIP_CONTAINS_DISALLOW.stream().anyMatch(strip::contains);
            if (isDisallowed) {
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
