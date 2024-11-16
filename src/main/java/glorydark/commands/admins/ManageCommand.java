package glorydark.commands.admins;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.Config;
import glorydark.MainClass;
import glorydark.gui.GuiMainAPI;

public class ManageCommand extends Command {
    public ManageCommand() {
        super("mytp", "Mytp", "/mytp help");
    }

    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "setmaxhome":
                case "设置家最大数量":
                    if (sender.isPlayer() && !MainClass.checktrust((Player) sender, false)) {
                        return true;
                    }
                    if (args.length != 3) {
                        sender.sendMessage("用法: /mytp setmaxhome(或'设置家最大数量') playername count");
                        return false;
                    }
                    if (Server.getInstance().lookupName(args[1]).isPresent()) {
                        Config pconfig = new Config(MainClass.path + "/player/" + args[1] + ".yml", Config.YAML);
                        pconfig.set("max_homes", Integer.valueOf(args[2]));
                        pconfig.save();
                        sender.sendMessage("设置玩家" + args[1] + "最大家数量为" + args[2]);
                    } else {
                        sender.sendMessage("Player Not Found!");
                    }
                    break;
                case "help":
                    Config config = new Config(MainClass.path + "/config.yml", Config.YAML);
                    if (config.exists("帮助")) {
                        if (!config.getStringList("帮助").isEmpty()) {
                            for (String s : config.getStringList("帮助")) {
                                sender.sendMessage(s);
                            }
                        }
                    } else {
                        sender.sendMessage("Incomplete config.yml!");
                    }
                    break;
                case "open":
                    if (sender.isPlayer()) {
                        GuiMainAPI.showMainMenu(sender.getServer().getPlayer(sender.getName()));
                        Config config1 = new Config(MainClass.path + "/config.yml", Config.YAML);
                        if (config1.getBoolean("是否启用打开音效", true)) {
                            sender.getServer().getPlayer(sender.getName()).getLevel().addSound(sender.getServer().getPlayer(sender.getName()), Sound.RANDOM_LEVELUP);
                        }
                    } else {
                        sender.sendMessage("请在游戏内使用本指令!");
                    }
                    break;
                case "addtrust":
                case "添加白名单":
                    if (!(sender.isPlayer())) {
                        if (args.length == 2) {
                            if (MainClass.addtrust(args[1])) {
                                sender.sendMessage("给予玩家【" + args[1] + "】白名单成功!");
                            } else {
                                sender.sendMessage("该玩家已经拥有白名单了！");
                            }
                        } else {
                            sender.sendMessage("请填写玩家名字!");
                        }
                    } else {
                        sender.sendMessage("请在控制台使用本指令!");
                    }
                    break;
                case "removetrust":
                case "删除白名单":
                    if (!(sender.isPlayer())) {
                        if (args.length == 2) {
                            if (MainClass.removetrust(args[1])) {
                                sender.sendMessage("移除玩家【" + args[1] + "】白名单成功!");
                            } else {
                                sender.sendMessage("该玩家没有白名单");
                            }
                        } else {
                            sender.sendMessage("请填写玩家名字!");
                        }
                    } else {
                        sender.sendMessage("请在控制台使用本指令!");
                    }
                    break;
            }
            return true;
        }
        return false;
    }
}
