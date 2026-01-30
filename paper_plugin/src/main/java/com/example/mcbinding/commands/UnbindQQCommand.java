package com.example.mcbinding.commands;

import com.example.mcbinding.MCBindingPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnbindQQCommand implements CommandExecutor {
    
    private MCBindingPlugin plugin;
    
    public UnbindQQCommand(MCBindingPlugin plugin) {
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
        
        // 检查是否已绑定
        String qqNumber = plugin.getQQByMinecraft(minecraftName);
        if (qqNumber == null) {
            player.sendMessage(ChatColor.RED + "你还没有绑定QQ号！");
            return true;
        }
        
        // 执行解绑
        plugin.removeBinding(qqNumber);
        player.sendMessage(ChatColor.GREEN + "✅ 解绑成功！");
        player.sendMessage(ChatColor.GREEN + "QQ号: " + qqNumber + " 已与你的Minecraft账号解除绑定。");
        
        return true;
    }
}