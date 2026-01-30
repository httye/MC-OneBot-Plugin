package com.example.mcbinding.commands;

import com.example.mcbinding.MCBindingPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckBindCommand implements CommandExecutor {
    
    private MCBindingPlugin plugin;
    
    public CheckBindCommand(MCBindingPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "此命令只能由玩家执行！");
            return true;
        }
        
        Player player = (Player) sender;
        String minecraftName = player.getName();
        
        // 检查绑定状态
        String qqNumber = plugin.getQQByMinecraft(minecraftName);
        if (qqNumber != null) {
            player.sendMessage(ChatColor.GREEN + "✅ 绑定状态：");
            player.sendMessage(ChatColor.GREEN + "Minecraft: " + minecraftName);
            player.sendMessage(ChatColor.GREEN + "QQ号: " + qqNumber);
        } else {
            player.sendMessage(ChatColor.YELLOW + "❌ 你还没有绑定QQ号！");
            player.sendMessage(ChatColor.YELLOW + "使用 /bindqq <qq_number> 进行绑定。");
        }
        
        return true;
    }
}