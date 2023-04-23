package glorydark.gui;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import glorydark.BaseAPI;
import glorydark.MainClass;
import glorydark.theme.MainWindowDIYAPI;
import glorydark.utils.Request;
import glorydark.utils.RequestType;
import me.onebone.economyapi.EconomyAPI;

import java.util.*;

import static glorydark.BaseAPI.buildButton;
import static glorydark.BaseAPI.getLang;

public class GuiMainAPI {
    
    public static void showMainMenu(Player player) {
        if(!player.isOnline()){ return; }
        FormWindowSimple form = new FormWindowSimple(getLang("MainMenu","title"), getLang("MainMenu","content").replace("%player%",player.getName()).replace("%money%",String.valueOf(EconomyAPI.getInstance().myMoney(player))));
        MainWindowDIYAPI.optimizedMainWindow(form, player);
        GuiListener.showFormWindow(player, form, GuiType.MainMenu);
    }
    public static void showWarpsMenu(Player player) { //传送点系统
        if(!player.isOnline()){ return; }
        FormWindowSimple form = new FormWindowSimple(getLang("Warp_TeleportMenu","title"), getLang("Warp_TeleportMenu","content"));
        Config warpconfig = new Config(MainClass.path+"/warps.yml",Config.YAML);
        if(warpconfig.get("list") != null) {
            List<String> warplist = new ArrayList<>(warpconfig.getStringList("list"));
            for (String wpn : warplist) {
                if(getLang("Warp_TeleportMenu",wpn+"_name").equals("Key Not Found!")) {
                    form.addButton(buildButton(wpn, getLang("Warp_TeleportMenu", wpn + "_picpath")));
                }else{
                    form.addButton(buildButton(getLang("Warp_TeleportMenu",wpn+"_name"), getLang("Warp_TeleportMenu", wpn + "_pic_path")));
                }
            }
        }
        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
        GuiListener.showFormWindow(player, form, GuiType.WarpMenu);
    }
    public static void showTeleportMainMenu(Player player){
        if(!player.isOnline()){ return; }
        FormWindowSimple form = new FormWindowSimple(getLang("Teleport_Main","title"), getLang("Teleport_Main","content"));
        form.addButton(buildButton(getLang("Teleport_Main","button1_text"),getLang("Teleport_Main","button1_pic_path")));
        form.addButton(buildButton(getLang("Teleport_Main","button2_text"),getLang("Teleport_Main","button2_pic_path")));
        form.addButton(buildButton(getLang("Teleport_Main","button3_text") + getLang("Teleport_Main", "button3_text_dynamic").replace("%count%", String.valueOf(MainClass.undealtRequests.getOrDefault(player.getName(), new ArrayList<>()).stream().filter(request -> !request.isExpired()).count())), getLang("Teleport_Main","button3_pic_path")));
        GuiListener.showFormWindow(player, form, GuiType.TeleportMainMenu);
    }

    public static void showTeleportQueneListMenu(Player player){
        List<Request> requests = new ArrayList<>(MainClass.undealtRequests.getOrDefault(player.getName(), new ArrayList<>()));
        for (Iterator<Request> it = requests.stream().iterator(); it.hasNext(); ) {
            Request request = it.next();
            if (request.isExpired()) {
                requests.remove(request);
            }
        }
        MainClass.undealtRequests.put(player.getName(), requests); //移除过期！
        FormWindowSimple form = new FormWindowSimple(getLang("Teleport_Quene","title"), getLang("Teleport_Quene","content"));
        for(Request request: requests){
            switch (request.requestType){
                case Invitation:
                    form.addButton(new ElementButton(getLang("Teleport_Quene","prefix_sender_active").replace("%time%", String.valueOf(60 - (System.currentTimeMillis() - request.millis)/1000))));
                    break;
                case WaitForInvitation:
                    form.addButton(new ElementButton(getLang("Teleport_Quene","prefix_sender_passive").replace("%time%", String.valueOf(60 - (System.currentTimeMillis() - request.millis)/1000))));
                    break;
            }
        }
        GuiListener.showFormWindow(player, form, GuiType.TeleportQueneMenu);
    }

    public static void showTeleportMenu(Player player, int type) {
        switch (type){
            // If type equals 0, it means to teleport player to others.
            case 0:
                if (!player.isOnline()) {
                    return;
                }
                FormWindowSimple form = new FormWindowSimple(getLang("Teleport_ToPlayer","title"), getLang("Teleport_ToPlayer","content"));
                Map<UUID, Player> pl = player.getServer().getOnlinePlayers();
                List<Player> list = new ArrayList<>(pl.values());
                for (Player p : list) {
                    form.addButton(new ElementButton(p.getName()));
                }
                GuiListener.showFormWindow(player, form, GuiType.TeleportInitiativeMenu);
                break;
            // Instead, if type equals 1, it means the opposite.
            case 1:
                if (!player.isOnline()) {
                    return;
                }
                FormWindowSimple form1 = new FormWindowSimple(getLang("Teleport_PlayerToYou","title"), getLang("Teleport_PlayerToYou","content"));
                Map<java.util.UUID, Player> pl1 = player.getServer().getOnlinePlayers();
                List<Player> list1 = new ArrayList<>(pl1.values());
                for (Player p : list1) {
                    form1.addButton(new ElementButton(p.getName()));
                }
                GuiListener.showFormWindow(player, form1, GuiType.TeleportPassiveMENU);
                break;
            default:
                break;
        }
    }

    public static void showTeleportAcceptMenu(Player asker, Player player, int type, Boolean free) {
        Config worldBanned = new Config(MainClass.path+"/config.yml",Config.YAML);
        if(worldBanned.getStringList("禁止玩家传送世界").contains(asker.getLevel().getName()) || worldBanned.getStringList("禁止玩家传送世界").contains(player.getLevel().getName())) {
            asker.sendMessage(getLang("Tips", "one_party_tp_forbidden"));
            return;
        }
        List<Request> requests = MainClass.undealtRequests.getOrDefault(player.getName(), new ArrayList<>());
        for (Iterator<Request> it = requests.stream().iterator(); it.hasNext(); ) {
            Request request = it.next();
            if(request.isExpired()){
                requests.remove(request);
            }
        }
        MainClass.undealtRequests.put(player.getName(), requests); //移除过期！
        if(requests.size() > 0 && requests.stream().anyMatch(request -> request.getSender().equals(asker.getName()))){
            FormWindowSimple formsetting = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","had_invited_just_now"));
            formsetting.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            GuiListener.showFormWindow(asker,formsetting, GuiType.ErrorMenu);
            return;
        }
        if(!player.isOnline()){
            FormWindowSimple formsetting = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","player_undefined"));
            formsetting.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            GuiListener.showFormWindow(asker,formsetting, GuiType.ErrorMenu);
            return;
        }
        Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
        switch (type){
            case 0:
                if(!free) {
                    double cost = config.getDouble("传送邀请花费");
                    if (EconomyAPI.getInstance().myMoney(asker) < cost) {
                        asker.sendMessage(getLang("Tips", "short_of_money"));
                        return;
                    }
                    EconomyAPI.getInstance().reduceMoney(asker, cost);
                }
                if(MainClass.undealtRequests.getOrDefault(player.getName(), new ArrayList<>()).size() == 0){
                    showPlayerDealInvitationMenu(player, asker.getName(), RequestType.WaitForInvitation);
                }else{
                    requests.add(new Request(asker.getName(), player.getName(), RequestType.WaitForInvitation));
                    MainClass.undealtRequests.put(player.getName(), requests);
                    player.sendMessage(getLang("Tips", "wait_passive_in_quene").replace("%player%", asker.getName()));
                    asker.sendMessage(getLang("Tips", "wait_in_quene").replace("%player%", player.getName()));
                }
                break;
            case 1:
                if(!free) {
                    double cost = config.getDouble("传送邀请花费");
                    if (EconomyAPI.getInstance().myMoney(asker) < cost) {
                        asker.sendMessage(getLang("Tips", "short_of_money"));
                        return;
                    }
                    EconomyAPI.getInstance().reduceMoney(asker, cost);
                }
                if(MainClass.undealtRequests.getOrDefault(player.getName(), new ArrayList<>()).size() == 0){
                    showPlayerDealInvitationMenu(player, asker.getName(), RequestType.Invitation);
                }else{
                    requests.add(new Request(asker.getName(), player.getName(), RequestType.Invitation));
                    MainClass.undealtRequests.put(player.getName(), requests);
                    player.sendMessage(getLang("Tips", "wait_active_in_quene").replace("%player%", asker.getName()));
                    asker.sendMessage(getLang("Tips", "wait_in_quene").replace("%player%", player.getName()));
                }
                break;
            default:
                break;
        }
    }

    public static void showPlayerDealInvitationMenu(Player player, String sender, RequestType type){
        FormWindowModal modal;
        List<Request> requests = MainClass.undealtRequests.getOrDefault(player.getName(), new ArrayList<>());
        List<Request> expired = new ArrayList<>();
        for(Request request: requests){
            if(request.isExpired()){
                expired.add(request);
            }
        }
        requests.removeAll(expired); //移除过期！
        switch (type) {
            case WaitForInvitation: //请求你邀请他
                Request request = new Request(sender, player.getName(), RequestType.WaitForInvitation);
                requests.add(request);
                MainClass.undealtRequests.put(player.getName(), requests);
                MainClass.directRequest.put(player.getName(), request);
                modal = new FormWindowModal(getLang("Teleport_ToPlayerAccept", "title"), getLang("Teleport_ToPlayerAccept", "content_sender_passive").replace("%player%", sender), getLang("Teleport_ToPlayerAccept", "button_accept"), getLang("Teleport_ToPlayerAccept", "button_decline"));
                GuiListener.showFormWindow(player, modal, GuiType.AcceptListPassiveMENU);
                break;
            case Invitation: //邀请您
                Request request1 = new Request(sender, player.getName(), RequestType.Invitation);
                requests.add(request1);
                MainClass.undealtRequests.put(player.getName(), requests);
                MainClass.directRequest.put(player.getName(), request1);
                modal = new FormWindowModal(getLang("Teleport_ToPlayerAccept", "title"), getLang("Teleport_ToPlayerAccept", "content_sender_active").replace("%player%", sender), getLang("Teleport_ToPlayerAccept", "button_accept"), getLang("Teleport_ToPlayerAccept", "button_decline"));
                GuiListener.showFormWindow(player, modal, GuiType.AcceptListInitiativeMENU);
                break;
        }
    }

    public static void showPlayerSettingMenu(Player player) {
        Config pconfig = new Config(MainClass.path+"/player/"+player.getName()+".yml",Config.YAML);
        FormWindowCustom form = new FormWindowCustom(getLang("PersonalSetting","title"));
        form.addElement(new ElementToggle(getLang("PersonalSetting","toggle1_text"),pconfig.getBoolean("自动接受传送请求")));
        Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
        if(config.getBoolean("开启设置重生点",false)) {
            form.addElement(new ElementToggle(getLang("PersonalSetting", "toggle2_text"), false));
        }
        GuiListener.showFormWindow(player, form, GuiType.SettingMenu);
    }
    public static void showMainSettingMenu(Player player) {
        /* if(!MainClass.checktrust(player,true)){ return; } */
        Config pconfig = new Config(MainClass.path+"/config.yml",Config.YAML);
        FormWindowCustom form = new FormWindowCustom(getLang("SystemSettingMenu","title"));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input1_text"),pconfig.getString("传送邀请花费")));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input2_text"),pconfig.getString("设置家花费")));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input3_text"),pconfig.getString("设置重生点花费")));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input4_text"),pconfig.getString("最多同时存在家")));
        form.addElement(new ElementToggle(getLang("SystemSettingMenu","toggle5_text"),pconfig.getBoolean("强制回主城")));
        form.addElement(new ElementToggle(getLang("SystemSettingMenu","toggle6_text"),pconfig.getBoolean("开启设置重生点")));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input7_text"),pconfig.getString("返回死亡点花费")));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input8_text"),pconfig.getString("随机传送花费")));
        GuiListener.showFormWindow(player, form, GuiType.MainSETTINGMENU);
    }
    public static void showWarpSettingMenu(Player player) {
        /* if(!MainClass.checktrust(player,true)){ return; } */
        Config warpconfig = new Config(MainClass.path+"/warps.yml",Config.YAML);
        FormWindowSimple form = new FormWindowSimple(getLang("Warp_SettingSelectMenu","title"),getLang("Warp_SettingSelectMenu","content"));
        if(warpconfig.get("list") != null) {
            List<String> warplist = new ArrayList<>(warpconfig.getStringList("list"));
            for (String wpn : warplist) {
                form.addButton(new ElementButton(wpn));
            }
        }
        GuiListener.showFormWindow(player, form, GuiType.WarpsSettingMenu);
    }
    public static void showHomeMainMenu(Player player){
        FormWindowSimple form = new FormWindowSimple(getLang("Home_Main","title"),getLang("Home_Main","content"));
        form.addButton(buildButton(getLang("Home_Main","button1_text"),getLang("Home_Main","button1_pic_path")));
        form.addButton(buildButton(getLang("Home_Main","button2_text"),getLang("Home_Main","button2_pic_path")));
        form.addButton(buildButton(getLang("Home_Main","button3_text"),getLang("Home_Main","button3_pic_path")));
        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
        GuiListener.showFormWindow(player, form, GuiType.HomeMainMenu);
    }
    public static void showHomeTeleportMenu(Player player){
        FormWindowSimple form = new FormWindowSimple(getLang("Home_TeleportMenu","title"),getLang("Home_TeleportMenu","content"));
        Config hc = new Config(MainClass.path+"/homes/"+player.getName()+".yml",Config.YAML);
        List<String> arr = new ArrayList<>(hc.getStringList("list"));
        for(String n : arr){
            Config pointc = new Config(MainClass.path+"/homes/"+player.getName()+"/"+n+".yml",Config.YAML);
            String intro = pointc.getString("简介");
            form.addButton(new ElementButton("名称:"+n+"\n简介:"+intro));
        }
        GuiListener.showFormWindow(player, form, GuiType.HomeTeleportMenu);
    }
    public static void showHomeCreateMenu(Player player, Boolean free){
        if(!((List<String>) BaseAPI.getDefaultConfig("设置家世界")).contains(player.level.getName())){
            FormWindowSimple returnForm = new FormWindowSimple(getLang("Tips", "menu_default_title"), getLang("Tips", "world_not_allowed"));
            returnForm.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            GuiListener.showFormWindow(player, returnForm, GuiType.ErrorMenu);
            return;
        }
        FormWindowCustom form = new FormWindowCustom(getLang("Home_CreateMenu","title"));
        form.addElement(new ElementInput(getLang("Home_CreateMenu","input1_text"),getLang("Home_CreateMenu","input1_tip")));
        form.addElement(new ElementInput(getLang("Home_CreateMenu","input2_text"),getLang("Home_CreateMenu","input2_tip")));
        if(free){
            GuiListener.showFormWindow(player, form, GuiType.HomeCreateMenuForFree);
        }else{
            GuiListener.showFormWindow(player, form, GuiType.HomeCreateMenu);
        }
    }
    public static void showWarpManageSelectMenu(Player player){
        FormWindowSimple form = new FormWindowSimple(getLang("Warp_MainMenu","title"),getLang("Warp_MainMenu","content"));
        form.addButton(buildButton(getLang("Warp_MainMenu","button1_text"),getLang("Warp_MainMenu","button1_pic_path")));
        form.addButton(buildButton(getLang("Warp_MainMenu","button2_text"),getLang("Warp_MainMenu","button2_pic_path")));
        form.addButton(buildButton(getLang("Warp_MainMenu","button3_text"),getLang("Warp_MainMenu","button3_pic_path")));
        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
        GuiListener.showFormWindow(player, form, GuiType.WarpsSettingManageMenu);
    }
    public static void showHomeDeleteMenu(Player player){
        Config warpscfg = new Config(MainClass.path+"/homes/"+player.getName()+".yml",Config.YAML);
        List<String> arr = new ArrayList<>(warpscfg.getStringList("list"));
        if(arr.size() >= 1) {
            FormWindowCustom form = new FormWindowCustom(getLang("Home_DeleteMenu","title"));
            form.addElement(new ElementDropdown(getLang("Home_DeleteMenu", "dropdown_title"), arr));
            GuiListener.showFormWindow(player, form, GuiType.HomeDeleteMenu);
        }else{
            FormWindowSimple returnForm = new FormWindowSimple(getLang("Tips", "menu_default_title"), getLang("Tips", "have_no_home"));
            returnForm.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            GuiListener.showFormWindow(player, returnForm, GuiType.ErrorMenu);
        }
    }
    public static void showWarpsSettingDownMenu(Player player, String name) {
        /* if(!MainClass.checktrust(player,true)){ return; } */
        MainClass.editTeleportPoint.put(player,name);
        Config warpscfg = new Config(MainClass.path+"/warps/"+ name +".yml",Config.YAML);
        if(!warpscfg.exists("x") || !warpscfg.exists("y") || !warpscfg.exists("z") || !warpscfg.exists("world") || !warpscfg.exists("state")){
            FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","settings_error"));
            form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            GuiListener.showFormWindow(player, form, GuiType.ErrorMenu);
            return;
        }
        FormWindowCustom form = new FormWindowCustom(getLang("Warp_SettingMenu","title"));
        form.addElement(new ElementLabel(name));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input1_title"),warpscfg.getString("name")));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input2_title"),String.valueOf(warpscfg.getDouble("x"))));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input3_title"),String.valueOf(warpscfg.getDouble("y"))));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input4_title"),String.valueOf(warpscfg.getDouble("z"))));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input5_title"),warpscfg.getString("world")));
        form.addElement(new ElementToggle(getLang("Warp_SettingMenu","toggle6_title"),warpscfg.getBoolean("state")));
        String[] strings = warpscfg.getString("title").split(":");
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input7_title"),strings[0]));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input8_title"),strings[1]));
        GuiListener.showFormWindow(player, form, GuiType.WarpSettingDownMenu);
    }
    public static void showWarpsCreateMenu(Player player) {
        /* if(!MainClass.checktrust(player,true)){ return; } */
        FormWindowCustom form = new FormWindowCustom(getLang("Warp_CreateMenu","title"));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input1_text"),getLang("Warp_CreateMenu","input1_tip")));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input2_text"),getLang("Warp_CreateMenu","input2_tip")));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input3_text"), String.valueOf(player.getX())));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input4_text"),String.valueOf(player.getY())));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input5_text"),String.valueOf(player.getZ())));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input6_text"),player.getLevel().getName()));
        form.addElement(new ElementToggle(getLang("Warp_CreateMenu","toggle7_text"),false));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input8_text"),getLang("Warp_CreateMenu","input8_tip")));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input9_text"),getLang("Warp_CreateMenu","input9_tip")));
        GuiListener.showFormWindow(player, form, GuiType.WarpsSettingCreateMenu);
    }
    public static void showWarpsDeleteMenu(Player player) {
        Config warpscfg = new Config(MainClass.path+"/warps.yml",Config.YAML);
        /* if(!MainClass.checktrust(player,true)){ return; } */
        FormWindowCustom form = new FormWindowCustom(getLang("Warp_DeleteMenu","title"));
        List<String> arr = new ArrayList<>(warpscfg.getStringList("list"));
        form.addElement(new ElementDropdown(getLang("Warp_DeleteMenu","dropdown_title"),arr));
        GuiListener.showFormWindow(player, form, GuiType.WarpsSettingDeleteMenu);
    }
    public static void showWorldListMenu(Player player){
        Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
        List<String> whiteworld = new ArrayList<>(config.getStringList("世界白名单"));
        FormWindowSimple form = new FormWindowSimple(getLang("World_TeleportMenu","title"),getLang("World_TeleportMenu","content"));
        for (Level level : Server.getInstance().getLevels().values()) {
            String levelName = level.getName();
            if (getLang("World_TeleportMenu", levelName + "_name").equals("Key Not Found!")) {
                if(!whiteworld.contains(levelName)){
                    if(MainClass.checktrust(player, false) || levelName.equals(Server.getInstance().getDefaultLevel().getName())){
                        Integer max = BaseAPI.getWorldPlayerLimit(levelName);
                        int now = level.getPlayers().values().size();
                        if(now >= max) {
                            form.addButton(buildButton(levelName + "\n§l§c[ 已爆满 ] [ 人数:"+max+"/"+max+" ]", getLang("World_TeleportMenu", levelName + "_picpath")));
                        }else {
                            form.addButton(buildButton(levelName + "\n§l§3[ 可进入 ] [ 人数:"+now+"/"+max+" ]", getLang("World_TeleportMenu", levelName + "_picpath")));
                        }
                    }else{
                        form.addButton(buildButton(levelName + "\n" + TextFormat.GRAY + "§l[ 未开放 ]", getLang("World_TeleportMenu", levelName + "_picpath")));
                    }
                }else{
                    Integer max = BaseAPI.getWorldPlayerLimit(levelName);
                    int now = level.getPlayers().values().size();
                    if(now >= max) {
                        form.addButton(buildButton(levelName + "\n" + "§l§c[ 已爆满 ] [ 人数:"+max+"/"+max+" ]", getLang("World_TeleportMenu", levelName + "_picpath")));
                    }else {
                        form.addButton(buildButton(levelName + "\n" + "§l§3[ 可进入 ] [ 人数:"+now+"/"+max+" ]", getLang("World_TeleportMenu", levelName + "_picpath")));
                    }
                }
            } else {
                if(!whiteworld.contains(levelName)){
                    if(MainClass.checktrust(player, false) || level.getName().equals(Server.getInstance().getDefaultLevel().getName())){
                        Integer max = BaseAPI.getWorldPlayerLimit(levelName);
                        int now = level.getPlayers().values().size();
                        if(now >= max) {
                            form.addButton(buildButton(getLang("World_TeleportMenu", levelName + "_name") + "\n" + TextFormat.RED + "[ 已爆满 ] [ 人数:"+max+"/"+max+" ]", getLang("World_TeleportMenu", levelName + "_picpath")));
                        }else {
                            form.addButton(buildButton(getLang("World_TeleportMenu", levelName + "_name") + "\n" + TextFormat.GREEN + "[ 可进入 ] [ 人数:"+now+"/"+max+" ]", getLang("World_TeleportMenu", levelName + "_picpath")));
                        }
                    }else{
                        form.addButton(buildButton(getLang("World_TeleportMenu", levelName + "_name")  + "\n" + TextFormat.GRAY + "[ 未开放 ]", getLang("World_TeleportMenu", levelName + "_picpath")));
                    }
                }else{
                    Integer max = BaseAPI.getWorldPlayerLimit(levelName);
                    int now = level.getPlayers().values().size();
                    if(now >= max) {
                        form.addButton(buildButton(getLang("World_TeleportMenu", levelName + "_name") + "\n" + TextFormat.RED + "[ 已爆满 ] [ 人数:"+max+"/"+max+" ]", getLang("World_TeleportMenu", levelName + "_picpath")));
                    }else {
                        form.addButton(buildButton(getLang("World_TeleportMenu", levelName + "_name") + "\n" + TextFormat.GREEN + "[ 可进入 ] [ 人数:"+now+"/"+max+" ]", getLang("World_TeleportMenu", levelName + "_picpath")));
                    }
                }
            }
        }
        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
        GuiListener.showFormWindow(player, form, GuiType.WorldTeleportMenu);
    }
    public static void showAllWorldTeleportMenu(Player player){
        /* if(!MainClass.checktrust(player,true)){ return; } */
        FormWindowSimple form = new FormWindowSimple(getLang("World_TeleportMenu","title"),getLang("World_TeleportMenu","content"));
        for(Level level: Server.getInstance().getLevels().values()){
            form.addButton(new ElementButton(level.getName()));
        }
        GuiListener.showFormWindow(player,form, GuiType.ManagerWorldMenu);
    }
}
