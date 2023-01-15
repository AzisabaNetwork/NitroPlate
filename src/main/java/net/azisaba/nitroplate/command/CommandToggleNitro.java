package net.azisaba.nitroplate.command;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CommandToggleNitro implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target = CommandClearPrefix.getTarget("togglenitro", sender, args);
        if (target == null) return true;
        net.azisaba.azipluginmessaging.api.entity.Player apiPlayer = AziPluginMessagingProvider.get().getPlayerAdapter(Player.class).get(target);
        PacketSender packetSender = AziPluginMessagingProvider.get().getServer().getPacketSender();
        Protocol.P_TOGGLE_NITRO_SARA.sendPacket(packetSender, new PlayerMessage(apiPlayer));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.hasPermission("nitroplate.togglenitro.others") && args.length == 1) {
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
