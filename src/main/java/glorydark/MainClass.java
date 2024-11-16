package glorydark;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import glorydark.commands.admins.ManageCommand;
import glorydark.commands.admins.WorldListCommand;
import glorydark.commands.admins.WorldTpCommand;
import glorydark.commands.players.*;
import glorydark.event.EventListener;
import glorydark.gui.GuiListener;
import glorydark.gui.GuiType;
import glorydark.utils.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import static glorydark.BaseAPI.*;

public class MainClass extends PluginBase implements Listener {
    public static String path = null;
    public static MainClass plugin;
    public static Timer timer = new Timer();
    public static List<Player> godPlayer = new ArrayList<>();
    public static HashMap<Player, String> editTeleportPoint = new HashMap<>();
    public static HashMap<String, List<Request>> undealtRequests = new HashMap<>();

    public static HashMap<String, Request> directRequest = new HashMap<>();

    public static boolean bedFeature = false;

    public static Boolean addtrust(String pn) {
        Config trustlist = new Config(path + "/trust.yml", Config.YAML);
        List<String> arrayList = new ArrayList<>(trustlist.getStringList("list"));
        if (arrayList.contains(pn)) {
            return false;
        } else {
            arrayList.add(pn);
            trustlist.set("list", arrayList);
            trustlist.save();
            return true;
        }
    }

    public static Boolean removetrust(String pn) {
        Config trustlist = new Config(path + "/trust.yml", Config.YAML);
        List<String> arrayList = new ArrayList<>(trustlist.getStringList("list"));
        if (arrayList.contains(pn)) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).equals(pn)) {
                    arrayList.remove(i);
                    trustlist.set("list", arrayList);
                    trustlist.save();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static void tp(Player asker, Player player) { //将第一个参数传送到第二个参数
        if (!player.isOnline()) {
            return;
        }
        if (!asker.isOnline()) {
            return;
        }
        Level level = player.getLevel();
        if (!asker.getServer().isLevelGenerated(level.getName())) {
            player.sendMessage(getLang("Tips", "world_is_not_loaded"));
            return;
        }
        asker.teleport(player.getPosition().getLocation());
        asker.sendMessage(getLang("Tips", "teleport_to_player").replace("%player%", player.getName()));
    }

    public static boolean checktrust(Player p, Boolean openGui) {
        Config trustlist = new Config(path + "/trust.yml", Config.YAML);
        List<String> arrayList = new ArrayList<>(trustlist.getStringList("list"));
        if (arrayList.contains(p.getName())) {
            return true;
        } else {
            if (openGui) {
                FormWindowSimple form = new FormWindowSimple(getLang("Tips", "menu_default_title"), getLang("Tips", "operation_is_not_authorized"));
                form.addButton(buildButton(getLang("Tips", "menu_button_return_text"), getLang("Tips", "menu_button_return_pic_path")));
                GuiListener.showFormWindow(p, form, GuiType.ErrorMenu);
            }
            return false;
        }
    }

    @Override
    public void onLoad() {
        this.getLogger().info("DEssential Onloaded!");
        path = this.getDataFolder().getPath();
        plugin = this;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this); // 注册Event
        this.getServer().getPluginManager().registerEvents(new GuiListener(), this); // 注册菜单监听Event
        this.getServer().getPluginManager().registerEvents(new EventListener(), this); // 注册事件监听Event
        this.getServer().getCommandMap().register("", new ManageCommand());
        this.getServer().getCommandMap().register("", new WildCommand());
        this.getServer().getCommandMap().register("", new WorldListCommand());
        this.getServer().getCommandMap().register("", new WorldTpCommand());
        this.getServer().getCommandMap().register("", new homeCommand());
        this.getServer().getCommandMap().register("", new tpaCommand());
        this.getServer().getCommandMap().register("", new tpaHereCommand());
        this.getServer().getCommandMap().register("", new tpInvitationListCommand());
        this.getLogger().info("DEssential Enabled!");
        this.saveResource("config.yml", false);
        this.saveResource("lang.yml", false);
        bedFeature = (boolean) getDefaultConfig("开启自带床特性");
        loadLevel();
    }

    public void loadLevel() {
        for (String worldName : getWorlds()) {
            if (!this.getServer().isLevelLoaded(worldName)) {
                if (this.getServer().isLevelGenerated(worldName)) {
                    this.getLogger().info("地图加载中，地图名:" + worldName);
                    this.getServer().loadLevel(worldName);
                }
            }
        }
    }

    public ArrayList<String> getWorlds() {
        ArrayList<String> worlds = new ArrayList<>();
        File file = new File(this.getServer().getFilePath() + "/worlds");
        File[] s = file.listFiles();
        if (s != null) {
            for (File file1 : s) {
                if (file1.isDirectory()) {
                    worlds.add(file1.getName());

                }
            }
        }
        return worlds;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DEssential Disabled!");
    }

    /*
    public void a() {
        for(File file: new File(Server.getInstance().getDataPath()+"/players/").listFiles()){
            if(file.getName().endsWith(".dat")){
                String uuid = file.getName().replace(".dat", "");
                this.getLogger().info(Server.getInstance().getOfflinePlayer(UUID.fromString(uuid)).getName());
            }
        }
    }
     */
}