package com.example.mcbinding.websocket;

import com.example.mcbinding.MCBindingPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.WebSocket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URI;
import java.util.logging.Logger;

public class BindingWebSocketClient extends WebSocketClient {
    
    private MCBindingPlugin plugin;
    private Logger logger;
    private Gson gson = new Gson();
    private boolean connected = false;
    
    public BindingWebSocketClient(URI serverUri, MCBindingPlugin plugin) {
        super(serverUri);
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }
    
    @Override
    public void onOpen(ServerHandshake handshake) {
        logger.info("已连接到AstrBot Websocket服务器");
        connected = true;
    }
    
    @Override
    public void onMessage(String message) {
        try {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String type = json.get("type").getAsString();
            
            if ("binding_request".equals(type)) {
                handleBindingRequest(json);
            } else if ("verification_request".equals(type)) {
                handleVerificationRequest(json);
            }
        } catch (Exception e) {
            logger.severe("处理Websocket消息失败: " + e.getMessage());
        }
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("与AstrBot Websocket服务器断开连接: " + reason);
        connected = false;
    }
    
    @Override
    public void onError(Exception ex) {
        logger.severe("Websocket错误: " + ex.getMessage());
    }
    
    public boolean isConnected() {
        // 使用更安全的连接状态检查方式
        WebSocket.READYSTATE readyState = this.getReadyState();
        return connected && readyState == WebSocket.READYSTATE.OPEN;
    }
    
    public void sendBindingSuccess(String qqId, String minecraftName) {
        if (!isConnected()) {
            return;
        }
        
        try {
            JsonObject message = new JsonObject();
            message.addProperty("type", "binding_success");
            message.addProperty("qq_id", qqId);
            message.addProperty("minecraft_name", minecraftName);
            message.addProperty("timestamp", System.currentTimeMillis());
            
            this.send(message.toString());
        } catch (WebsocketNotConnectedException e) {
            logger.warning("Websocket未连接，无法发送绑定成功消息");
        }
    }
    
    public void sendBindingRemoved(String qqId, String minecraftName) {
        if (!isConnected()) {
            return;
        }
        
        try {
            JsonObject message = new JsonObject();
            message.addProperty("type", "binding_removed");
            message.addProperty("qq_id", qqId);
            message.addProperty("minecraft_name", minecraftName);
            message.addProperty("timestamp", System.currentTimeMillis());
            
            this.send(message.toString());
        } catch (WebsocketNotConnectedException e) {
            logger.warning("Websocket未连接，无法发送解绑消息");
        }
    }
    
    public void sendVerificationResponse(String qqId, String minecraftName, boolean success) {
        if (!isConnected()) {
            return;
        }
        
        try {
            JsonObject message = new JsonObject();
            message.addProperty("type", "verification_response");
            message.addProperty("qq_id", qqId);
            message.addProperty("minecraft_name", minecraftName);
            message.addProperty("success", success);
            message.addProperty("timestamp", System.currentTimeMillis());
            
            this.send(message.toString());
        } catch (WebsocketNotConnectedException e) {
            logger.warning("Websocket未连接，无法发送验证响应");
        }
    }
    
    private void handleBindingRequest(JsonObject json) {
        String qqId = json.get("qq_id").getAsString();
        String minecraftName = json.get("minecraft_name").getAsString();
        
        // 在服务器端执行绑定
        plugin.addBinding(qqId, minecraftName);
        
        // 发送成功响应
        sendBindingSuccess(qqId, minecraftName);
    }
    
    private void handleVerificationRequest(JsonObject json) {
        String qqId = json.get("qq_id").getAsString();
        String minecraftName = json.get("minecraft_name").getAsString();
        
        // 验证绑定是否有效
        String storedMinecraft = plugin.getMinecraftByQQ(qqId);
        boolean isValid = storedMinecraft != null && storedMinecraft.equals(minecraftName);
        
        sendVerificationResponse(qqId, minecraftName, isValid);
    }
}