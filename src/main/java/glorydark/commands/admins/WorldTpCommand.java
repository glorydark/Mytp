package glorydark.commands.admins;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import glorydark.BaseAPI;
import glorydark.MainClass;

import static glorydark.BaseAPI.getLang;

public class WorldTpCommand extends Command {
    public WorldTpCommand(){
        super("w","Multi-world Teleport Command","/w 世界名");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            if(MainClass.checktrust((Player) commandSender,false)) {
                switch (strings.length){
                    case 1:
                        Level level = Server.getInstance().getLevelByName(strings[0]);
                        if (level != null) {
                            BaseAPI.worldteleport((Player) commandSender, level);
                        } else {
                            commandSender.sendMessage(getLang("Tips", "world_is_not_loaded"));
                        }
                        break;
                    case 2:
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if(player != null){
                            level = Server.getInstance().getLevelByName(strings[0]);
                            if (level != null) {
                                BaseAPI.worldteleport(player, level);
                            } else {
                                commandSender.sendMessage(getLang("Tips", "world_is_not_loaded"));
                            }
                        }else{
                            commandSender.sendMessage("玩家不在线！");
                        }
                        break;
                    default:
                        commandSender.sendMessage("使用提示：/w 世界名 或者 /w 世界名 玩家名");
                        break;
                }
            }else{
                commandSender.sendMessage("您没有权限！");
            }
        }else{
            if(strings.length == 2){
                Player player = Server.getInstance().getPlayer(strings[1]);
                if(player != null){
                    Level level = Server.getInstance().getLevelByName(strings[0]);
                    if (level != null) {
                        BaseAPI.worldteleport(player, level);
                    } else {
                        commandSender.sendMessage(getLang("Tips", "world_is_not_loaded"));
                    }
                }else{
                    commandSender.sendMessage("玩家不在线！");
                }
            }else{
                commandSender.sendMessage("使用提示：/w 世界名 玩家名");
            }
        }
        return true;
    }
}
