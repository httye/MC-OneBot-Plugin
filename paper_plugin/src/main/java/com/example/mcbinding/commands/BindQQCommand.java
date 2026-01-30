package com.example.mcbinding.commands;

import com.example.mcbinding.MCBindingPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BindQQCommand implements CommandExecutor {
    
    private MCBindingPlugin plugin;
    
    public BindQQCommand(MCBindingPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "此命令只能由玩家执行！");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "用法: /bindqq <qq_number>");
            return true;
        }
        
        String qqNumber = args[0].trim();
        
        // 验证QQ号格式
        if (!isValidQQNumber(qqNumber)) {
            player.sendMessage(ChatColor.RED + "无效的QQ号格式！");
            return true;
        }
        
        String minecraftName = player.getName();
        
        // 检查该QQ号是否已被绑定
        if (plugin.isQQBound(qqNumber)) {
            player.sendMessage(ChatColor.RED + "此QQ号已被其他Minecraft账号绑定！");
            return true;
        }
        
        // 检查该Minecraft账号是否已被绑定
        if (plugin.isMinecraftBound(minecraftName)) {
            String oldQQ = plugin.getQQByMinecraft(minecraftName);
            player.sendMessage(ChatColor.RED + "你的Minecraft账号已被QQ号 " + oldQQ + " 绑定！");
            return true;
        }
        
        // 执行绑定
        plugin.addBinding(qqNumber, minecraftName);
        player.sendMessage(ChatColor.GREEN + "✅ 绑定成功！");
        player.sendMessage(ChatColor.GREEN + "QQ号: " + qqNumber);
        player.sendMessage(ChatColor.GREEN + "Minecraft: " + minecraftName);
        
        // 可选：通知AstrBot服务器绑定成功
        plugin.getLogger().info("玩家 " + minecraftName + " 已绑定QQ号 " + qqNumber);
        
        return true;
    }
    
    private boolean isValidQQNumber(String qqNumber) {
        try {
            long qq = Long.parseLong(qqNumber);
            return qq > 10000 && qq < 9999999999L; // 基本的QQ号范围验证
        } catch (NumberFormatException e) {
            return false;
        }
    }
}