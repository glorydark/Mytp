package glorydark;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import glorydark.gui.GuiListener;
import glorydark.gui.GuiType;
import me.onebone.economyapi.EconomyAPI;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class BaseAPI {
    public static Config getAllLang(){
        return new Config(MainClass.path+"/lang.yml");
    }

    public static String getLang(String key, String subKey){
        Map<String, Object> map = getAllLang().getAll();
        Map<String, Object> map1 = (Map<String, Object>) map.get(key);
        if(map1.get(subKey) != null){
            return (String) map1.get(subKey);
        }else{
            return "Key Not Found!";
        }
    }

    public static Integer getWorldPlayerLimit(String level){
        Config cfg = new Config(MainClass.path+"/limit.yml",Config.YAML);
        if(cfg.exists(level)){
            return cfg.getInt(level);
        }else{
            return 99999;
        }
    }

    /*
    public static String getLang(String key){
        return getAllLang().getString(key,"Value Not Found!");
    }

     */

    public static ElementButton buildButton(String name,String path){
        //Debug: Server.getInstance().getLogger().alert(path);
        String[] splits = path.split(":",2);
        if(splits.length >=2){
            switch (splits[0]){
                case "path":
                    return new ElementButton(name,new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH,splits[1]));
                case "url":
                    return new ElementButton(name,new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL,splits[1]));
            }
        }
        return new ElementButton(name);
    }

    public static ElementButtonImageData buildIcon(String path){
        //Debug: Server.getInstance().getLogger().alert(path);
        String[] splits = path.split(":",2);
        if(splits.length >=2){
            switch (splits[0]){
                case "path":
                    return new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH,splits[1]);
                case "url":
                    return new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL,splits[1]);
            }
        }
        return null;
    }

    public static int rand(int min, int max) {
        if (min == max) {
            return max;
        }

        return min + new Random(System.currentTimeMillis()).nextInt(max - min);
    }

    public static void wild(Player p, Boolean free) {
        if(((List<String>)BaseAPI.getDefaultConfig(("禁止随机传送世界"))).contains(p.getLevel().getName())) {
            FormWindowSimple returnForm = new FormWindowSimple(getLang("Tips", "menu_default_title"), getLang("Tips", "world_not_allowed"));
            returnForm.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            GuiListener.showFormWindow(p, returnForm, GuiType.ErrorMenu);
            return;
        }
        if(!free) {
            double cost = (double) BaseAPI.getDefaultConfig("随机传送花费");
            if (cost != 0d) {
                if (EconomyAPI.getInstance().myMoney(p) < cost) {
                    p.sendMessage(getLang("Tips", "short_of_money"));
                    return;
                }
                EconomyAPI.getInstance().reduceMoney(p, cost);
            }
        }
        Location location = getSafePos(p);
        if(location == null) {
            p.sendMessage(getLang("Tips","wild_failed"));
            return;
        }
        if (p.teleport(location)) {
            p.sendMessage(getLang("Tips","wild_success"));
        } else {
            p.sendMessage(getLang("Tips","wild_failed"));
        }
    }

    public static Location getSafePos(Player p){

        Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
        Position pos;
        if(p.getLevel().getName().equals("nether")) {
            pos = p.getLevel().getSafeSpawn(new Vector3(rand(config.getInt("wild_minX"), config.getInt("wild_maxX")), 120, rand(config.getInt("wild_minZ"), config.getInt("wild_maxZ"))));
            if(pos.y <= 32){
                return null;
            }
        }else{
            pos = p.getLevel().getSafeSpawn(new Vector3(rand(config.getInt("wild_minX"), config.getInt("wild_maxX")), 250, rand(config.getInt("wild_minZ"), config.getInt("wild_maxZ"))));
        }
        for(int i =0; i< 250; i++){
            pos.setY(pos.getFloorY() - 1);
            if(pos.getLevelBlock().isSolid()){
                if(p.getLevel().getName().equals("nether")){
                    p.getLevel().setBlock(pos.add(0, 1, 0), Block.get(0));
                    p.getLevel().setBlock(pos.add(0, 2, 0), Block.get(0));
                }
                return new Location(pos.getFloorX()+0.5, pos.getFloorY() + 2, pos.getFloorZ()+0.5, p.getLevel());
            }
        }
        if(p.getLevel().getName().equals("nether")){
            p.getLevel().setBlock(pos.add(0, 1, 0), Block.get(0));
            p.getLevel().setBlock(pos.add(0, 2, 0), Block.get(0));
        }
        return new Location(pos.getFloorX()+0.5, pos.getFloorY() + 2, pos.getFloorZ()+0.5, p.getLevel());
    }

    public static Object getDefaultConfig(String key){
        Config cfg = new Config(MainClass.path+"/config.yml",Config.YAML);
        return cfg.get(key);
    }

    public static void setDefaultConfig(String key, Object o){
        Config cfg = new Config(MainClass.path+"/config.yml",Config.YAML);
        cfg.set(key, o);
        cfg.save();
    }

    public static void removeDefaultConfig(String key){
        Config cfg = new Config(MainClass.path+"/config.yml",Config.YAML);
        cfg.remove(key);
        cfg.save();
    }

    public static void setLangConfig(String key, Object o){
        Config cfg = new Config(MainClass.path+"/lang.yml",Config.YAML);
        cfg.set(key, o);
        cfg.save();
    }

    public static void teleportToDeathPoint(Player p, Boolean free){
        Config playerconfig = new Config(MainClass.path+"/player/"+ p.getName()+".yml",Config.YAML);
        if(!free) {
            double cost = (double) BaseAPI.getDefaultConfig("返回死亡点花费");
            if (cost != 0d) {
                if (EconomyAPI.getInstance().myMoney(p) < cost) {
                    p.sendMessage(getLang("Tips", "short_of_money"));
                    return;
                }
                EconomyAPI.getInstance().reduceMoney(p, cost);
            }
        }
        if(playerconfig.exists("lastdeath")){
            p.sendMessage(getLang("Tips","on_teleporting"));
            double x = playerconfig.getDouble("lastdeath.x");
            double y = playerconfig.getDouble("lastdeath.y");
            double z = playerconfig.getDouble("lastdeath.z");
            Level level = p.getServer().getLevelByName(playerconfig.getString("lastdeath.level"));
            if(level != null) {
                p.teleportImmediate(new Location(x, y, z, level));
            }else{
                p.sendMessage(getLang("Tips","world_is_not_loaded"));
            }
        }else{
            FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","deathpoint_not_exist"));
            form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            GuiListener.showFormWindow(p, form, GuiType.ErrorMenu);
        }
    }

    public static void worldteleport(Player p, Level level){
        if(level == null){
            p.sendMessage(getLang("Tips","world_is_not_loaded"));
            return;
        }
        if(p.getServer().getLevels().containsValue(level)) {
            Position spawnpos = level.getSpawnLocation();
            p.teleportImmediate(spawnpos.getLocation());
            p.sendMessage(getLang("Tips","on_teleporting"));
        }else{
            p.sendMessage(getLang("Tips","world_is_not_loaded"));
        }
    }
}