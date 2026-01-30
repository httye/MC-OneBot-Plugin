"""
Minecraft OneBot Plugin for AstrBot - QQ-Minecraft Account Binding
"""
from astrbot.framework import Plugin
from astrbot.framework.context import AstrBotContext
from astrbot.framework.message import MessageEvent
from astrbot.framework.platform.account import AstrBotPlatformAccount
import asyncio
import logging
import json
import os
import websockets
import threading
from datetime import datetime

logger = logging.getLogger(__name__)

class MinecraftOneBotPlugin(Plugin):
    def __init__(self, context: AstrBotContext):
        super().__init__(context)
        self.name = "mc_onebot"
        self.description = "Minecraft OneBot 集成插件 - 支持QQ与Minecraft账号绑定"
        self.version = "0.3.0"
        self.author = "Your Name"
        self.bindings_file = "mc_bindings.json"
        self.bindings = self.load_bindings()
        self.websocket_server = None
        self.websocket_clients = set()
        self.websocket_enabled = True  # 从配置读取
        
    def load_bindings(self):
        """加载绑定数据"""
        if os.path.exists(self.bindings_file):
            try:
                with open(self.bindings_file, 'r', encoding='utf-8') as f:
                    return json.load(f)
            except Exception as e:
                logger.error(f"加载绑定数据失败: {e}")
                return {}
        return {}
    
    def save_bindings(self):
        """保存绑定数据"""
        try:
            with open(self.bindings_file, 'w', encoding='utf-8') as f:
                json.dump(self.bindings, f, ensure_ascii=False, indent=2)
        except Exception as e:
            logger.error(f"保存绑定数据失败: {e}")
    
    async def initialize(self):
        """插件初始化"""
        logger.info("Minecraft OneBot 插件正在初始化...")
        # 注册命令
        self.register_command(
            name="mc",
            description="Minecraft 相关命令",
            handler=self.handle_mc_command,
            aliases=["minecraft"]
        )
        self.register_command(
            name="bd",
            description="绑定Minecraft账号到QQ号",
            handler=self.handle_bind_command,
            aliases=["bind", "绑定"]
        )
        self.register_command(
            name="unbind",
            description="解绑Minecraft账号",
            handler=self.handle_unbind_command
        )
        self.register_command(
            name="checkbind",
            description="查看绑定状态",
            handler=self.handle_check_bind_command,
            aliases=["cb", "绑定查询"]
        )
        self.register_command(
            name="verifybind",
            description="验证绑定状态（服务器端）",
            handler=self.handle_verify_bind_command,
            aliases=["vb", "验证绑定"]
        )
        
        # 启动Websocket服务器
        if self.websocket_enabled:
            self.start_websocket_server()
    
    async def handle_mc_command(self, message: MessageEvent, context: AstrBotContext):
        """处理 mc 命令"""
        args = message.message.split()[1:]  # 获取命令参数
        
        if not args:
            return "Usage: /mc <command> [args]\n支持的命令: status, list, say, help"
        
        command = args[0].lower()
        
        if command == "status":
            return await self.get_server_status()
        elif command == "list":
            return await self.get_online_players()
        elif command == "say":
            if len(args) < 2:
                return "Usage: /mc say <message>"
            message_text = " ".join(args[1:])
            return await self.send_server_message(message_text)
        elif command == "help":
            return self.get_mc_help()
        else:
            return f"未知命令: {command}\n支持的命令: status, list, say, help"
    
    def get_mc_help(self):
        """获取MC命令帮助"""
        return """Minecraft 命令帮助:
/mc status - 查看服务器状态
/mc list - 查看在线玩家
/mc say <message> - 向服务器发送消息
/bd <minecraft_username> - 绑定Minecraft账号
/unbind - 解绑Minecraft账号
/checkbind - 查看绑定状态
/verifybind <qq_id> <minecraft_username> - 验证绑定状态（管理命令）"""
    
    async def handle_bind_command(self, message: MessageEvent, context: AstrBotContext):
        """处理绑定命令"""
        args = message.message.split()[1:]  # 获取命令参数
        
        if not args:
            return "Usage: /bd <minecraft_username>\n示例: /bd Steve\n用于绑定你的QQ号到Minecraft账号"
        
        mc_username = args[0].strip()
        qq_id = str(message.user_id)
        
        # 验证Minecraft用户名格式（基本验证）
        if not self.is_valid_minecraft_username(mc_username):
            return "无效的Minecraft用户名！用户名只能包含字母、数字和下划线，长度3-16个字符。"
        
        # 检查是否已绑定
        if qq_id in self.bindings:
            old_username = self.bindings[qq_id]
            return f"你已经绑定了Minecraft账号: {old_username}\n如需更换，请先使用 /unbind 命令解绑。"
        
        # 检查该Minecraft用户名是否已被其他QQ绑定
        for bound_qq, bound_username in self.bindings.items():
            if bound_username.lower() == mc_username.lower():
                return f"Minecraft账号 {mc_username} 已被其他QQ号绑定！"
        
        # 执行绑定
        self.bindings[qq_id] = mc_username
        self.save_bindings()
        
        # 通知Minecraft服务器
        await self.notify_server_bind(qq_id, mc_username)
        
        return f"✅ 绑定成功！\nQQ: {message.sender.nickname} ({qq_id})\nMinecraft: {mc_username}\n\n你可以使用 /checkbind 查看绑定状态。"
    
    async def handle_unbind_command(self, message: MessageEvent, context: AstrBotContext):
        """处理解绑命令"""
        qq_id = str(message.user_id)
        
        if qq_id not in self.bindings:
            return "你还没有绑定Minecraft账号！"
        
        mc_username = self.bindings[qq_id]
        del self.bindings[qq_id]
        self.save_bindings()
        
        # 通知Minecraft服务器
        await self.notify_server_unbind(qq_id, mc_username)
        
        return f"✅ 解绑成功！\nMinecraft账号 {mc_username} 已与你的QQ号解除绑定。"
    
    async def handle_check_bind_command(self, message: MessageEvent, context: AstrBotContext):
        """处理绑定状态查询命令"""
        qq_id = str(message.user_id)
        
        if qq_id in self.bindings:
            mc_username = self.bindings[qq_id]
            return f"✅ 绑定状态：\nQQ: {message.sender.nickname} ({qq_id})\nMinecraft: {mc_username}\n绑定时间: {self.get_bind_time(qq_id)}"
        else:
            return "❌ 你还没有绑定Minecraft账号！\n使用 /bd <minecraft_username> 进行绑定。"
    
    async def handle_verify_bind_command(self, message: MessageEvent, context: AstrBotContext):
        """处理绑定验证命令（管理命令）"""
        # 检查权限（这里简化为管理员权限检查）
        if not self.is_admin(message.user_id):
            return "❌ 权限不足！此命令仅限管理员使用。"
        
        args = message.message.split()[1:]
        if len(args) != 2:
            return "Usage: /verifybind <qq_id> <minecraft_username>"
        
        qq_id = args[0].strip()
        mc_username = args[1].strip()
        
        # 验证绑定
        if qq_id in self.bindings and self.bindings[qq_id].lower() == mc_username.lower():
            return f"✅ 验证成功！QQ {qq_id} 已绑定到 Minecraft {mc_username}"
        else:
            return f"❌ 验证失败！QQ {qq_id} 未绑定到 Minecraft {mc_username}"
    
    def is_valid_minecraft_username(self, username: str) -> bool:
        """验证Minecraft用户名格式"""
        if len(username) < 3 or len(username) > 16:
            return False
        # Minecraft用户名只能包含字母、数字和下划线
        import re
        return re.match(r'^[a-zA-Z0-9_]+$', username) is not None
    
    def is_admin(self, user_id: str) -> bool:
        """检查是否为管理员（简化实现）"""
        # 这里可以扩展为从配置文件读取管理员列表
        admin_ids = ["123456789"]  # 示例管理员QQ号
        return str(user_id) in admin_ids or str(user_id) == "10000"  # 机器人自己的ID
    
    def get_bind_time(self, qq_id: str) -> str:
        """获取绑定时间（简单实现）"""
        # 这里可以扩展为从绑定数据中存储时间
        return datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    async def notify_server_bind(self, qq_id: str, mc_username: str):
        """通知Minecraft服务器绑定信息"""
        try:
            message = {
                "type": "binding_success",
                "qq_id": qq_id,
                "minecraft_name": mc_username,
                "timestamp": datetime.now().isoformat()
            }
            await self.broadcast_to_clients(message)
        except Exception as e:
            logger.error(f"通知服务器绑定失败: {e}")
    
    async def notify_server_unbind(self, qq_id: str, mc_username: str):
        """通知Minecraft服务器解绑信息"""
        try:
            message = {
                "type": "binding_removed",
                "qq_id": qq_id,
                "minecraft_name": mc_username,
                "timestamp": datetime.now().isoformat()
            }
            await self.broadcast_to_clients(message)
        except Exception as e:
            logger.error(f"通知服务器解绑失败: {e}")
    
    def start_websocket_server(self):
        """启动Websocket服务器"""
        try:
            import websockets
            import asyncio
            
            async def websocket_handler(websocket, path):
                """处理Websocket连接"""
                self.websocket_clients.add(websocket)
                try:
                    async for message in websocket:
                        await self.handle_websocket_message(websocket, message)
                except websockets.exceptions.ConnectionClosed:
                    pass
                finally:
                    self.websocket_clients.discard(websocket)
            
            async def start_server():
                try:
                    self.websocket_server = await websockets.serve(
                        websocket_handler, "localhost", 8080
                    )
                    logger.info("Websocket服务器已启动，监听端口 8080")
                except Exception as e:
                    logger.error(f"Websocket服务器启动失败: {e}")
            
            # 在后台线程中运行异步服务器
            def run_server():
                asyncio.new_event_loop().run_until_complete(start_server())
            
            server_thread = threading.Thread(target=run_server, daemon=True)
            server_thread.start()
            
        except ImportError:
            logger.warning("websockets库未安装，Websocket功能将被禁用")
            self.websocket_enabled = False
        except Exception as e:
            logger.error(f"启动Websocket服务器失败: {e}")
            self.websocket_enabled = False
    
    async def handle_websocket_message(self, websocket, message):
        """处理Websocket消息"""
        try:
            import json
            data = json.loads(message)
            msg_type = data.get("type")
            
            if msg_type == "binding_request":
                # 处理来自Minecraft服务器的绑定请求
                qq_id = data.get("qq_id")
                mc_username = data.get("minecraft_name")
                await self.handle_server_binding_request(qq_id, mc_username)
            elif msg_type == "verification_request":
                # 处理验证请求
                qq_id = data.get("qq_id")
                mc_username = data.get("minecraft_name")
                await self.handle_verification_request(qq_id, mc_username, websocket)
        except Exception as e:
            logger.error(f"处理Websocket消息失败: {e}")
    
    async def handle_server_binding_request(self, qq_id: str, mc_username: str):
        """处理来自服务器的绑定请求"""
        # 检查是否已存在绑定
        if qq_id in self.bindings:
            old_username = self.bindings[qq_id]
            logger.warning(f"QQ {qq_id} 已绑定到 {old_username}，尝试绑定到 {mc_username}")
            return False
        
        # 检查Minecraft用户名是否已被其他QQ绑定
        for bound_qq, bound_username in self.bindings.items():
            if bound_username.lower() == mc_username.lower():
                logger.warning(f"Minecraft {mc_username} 已被QQ {bound_qq} 绑定")
                return False
        
        # 执行绑定
        self.bindings[qq_id] = mc_username
        self.save_bindings()
        logger.info(f"服务器绑定成功: QQ {qq_id} -> MC {mc_username}")
        return True
    
    async def handle_verification_request(self, qq_id: str, mc_username: str, websocket):
        """处理验证请求"""
        is_valid = (qq_id in self.bindings and
                   self.bindings[qq_id].lower() == mc_username.lower())
        
        response = {
            "type": "verification_response",
            "qq_id": qq_id,
            "minecraft_name": mc_username,
            "success": is_valid,
            "timestamp": datetime.now().isoformat()
        }
        await websocket.send(json.dumps(response, ensure_ascii=False))
    
    async def broadcast_to_clients(self, message: dict):
        """广播消息给所有连接的客户端"""
        if not self.websocket_clients:
            return
        
        import json
        message_str = json.dumps(message, ensure_ascii=False)
        disconnected_clients = []
        
        for client in self.websocket_clients.copy():
            try:
                await client.send(message_str)
            except Exception:
                disconnected_clients.append(client)
        
        # 移除断开连接的客户端
        for client in disconnected_clients:
            self.websocket_clients.discard(client)

def register(context: AstrBotContext):
    """插件注册函数"""
    plugin = MinecraftOneBotPlugin(context)
    return plugin