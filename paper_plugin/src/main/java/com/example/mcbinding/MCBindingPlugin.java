package com.example.mcbinding;

import com.example.mcbinding.commands.BindQQCommand;
import com.example.mcbinding.commands.CheckBindCommand;
import com.example.mcbinding.commands.UnbindQQCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MCBindingPlugin extends JavaPlugin implements Listener {
    
    private Map<String, String> qqBindings = new HashMap<>();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File bindingsFile;
    
    @Override
    public void onEnable() {
        getLogger().info("MC Binding Plugin 已启用！");
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);
        
        // 注册命令
        getCommand("bindqq").setExecutor(new BindQQCommand(this));
        getCommand("unbindqq").setExecutor(new UnbindQQCommand(this));
        getCommand("checkbind").setExecutor(new CheckBindCommand(this));
        
        // 初始化绑定文件
        bindingsFile = new File(getDataFolder(), "bindings.json");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        loadBindings();
    }
    
    @Override
    public void onDisable() {
        getLogger().info("MC Binding Plugin 已禁用！");
        saveBindings();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        
        // 检查玩家是否已绑定QQ
        String qqId = getQQByMinecraft(playerName);
        if (qqId != null) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "✅ 你的QQ号已成功绑定到此Minecraft账号！");
        } else {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "⚠️ 你还没有绑定QQ号，使用 /bindqq <qq_number> 进行绑定");
        }
    }
    
    public void addBinding(String qqId, String minecraftName) {
        qqBindings.put(qqId, minecraftName);
        saveBindings();
    }
    
    public void removeBinding(String qqId) {
        qqBindings.remove(qqId);
        saveBindings();
    }
    
    public String getMinecraftByQQ(String qqId) {
        return qqBindings.get(qqId);
    }
    
    public String getQQByMinecraft(String minecraftName) {
        for (Map.Entry<String, String> entry : qqBindings.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(minecraftName)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public boolean isQQBound(String qqId) {
        return qqBindings.containsKey(qqId);
    }
    
    public boolean isMinecraftBound(String minecraftName) {
        return getQQByMinecraft(minecraftName) != null;
    }
    
    public Map<String, String> getAllBindings() {
        return new HashMap<>(qqBindings);
    }
    
    private void loadBindings() {
        try {
            if (bindingsFile.exists()) {
                try (FileReader reader = new FileReader(bindingsFile)) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> loaded = gson.fromJson(reader, Map.class);
                    if (loaded != null) {
                        qqBindings = loaded;
                    }
                }
            }
        } catch (IOException e) {
            getLogger().severe("加载绑定数据失败: " + e.getMessage());
        }
    }
    
    private void saveBindings() {
        try {
            try (FileWriter writer = new FileWriter(bindingsFile)) {
                gson.toJson(qqBindings, writer);
            }
        } catch (IOException e) {
            getLogger().severe("保存绑定数据失败: " + e.getMessage());
        }
    }
}