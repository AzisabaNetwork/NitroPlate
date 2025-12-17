package net.azisaba.nitroplate.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.InvalidArgumentException;

public class Util {
    public static @NotNull String toString(@NotNull InvalidArgumentException e) {
        String error = ChatColor.RED + "Invalid syntax: " + e.getMessage();
        if (e.getContext() == null) {
            return error;
        }
        try {
            StringBuilder sb = new StringBuilder(error);
            sb.append("\n");
            String prev = e.getContext().peekWithAmount(-Math.min(e.getContext().index(), 15));
            StringBuilder next = new StringBuilder(e.getContext().peekWithAmount(Math.min(e.getContext().readableCharacters(), Math.max(15, e.getLength()))));
            if (next.length() == 0) {
                for (int i = 0; i < e.getLength(); i++) {
                    next.append(' ');
                }
            }
            sb.append(ChatColor.WHITE).append(prev);
            String left = next.substring(0, e.getLength());
            String right = next.substring(e.getLength(), next.length());
            sb.append(ChatColor.RED).append(ChatColor.UNDERLINE).append(left);
            sb.append(ChatColor.WHITE).append(right);
            return sb.toString();
        } catch (Exception ex) {
            return error;
        }
    }
}
