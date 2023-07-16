package glorydark.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBed;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import glorydark.BaseAPI;
import glorydark.MainClass;

import java.util.TimerTask;

import static glorydark.BaseAPI.getLang;

public class EventListener implements Listener {

    @EventHandler
    public void PlayerRespawnEvent(PlayerRespawnEvent event){
        if(event.getPlayer().getGamemode() == 1 || event.getPlayer().getGamemode() == 3){ return; }
        if(!event.isFirstSpawn()) {
            Player player = event.getPlayer();
            if (player == null) {
                return;
            }
            Config mainCfg = new Config(MainClass.path+"/config.yml",Config.YAML);
            if(mainCfg.exists("是否使用快捷工具") && mainCfg.getBoolean("是否使用快捷工具",true)) {
                Item convenience = new Item(mainCfg.getInt("快捷工具ID", 347));
                convenience.setLore(getLang("Convenient_Tool","lore"));
                String nametag = getLang("Convenient_Tool","nametag");
                convenience.setCustomName(nametag);
                convenience.setDamage(0);
                if(!event.getPlayer().getInventory().getContents().values().stream().filter(item -> item.getCustomName().equals(nametag)).sorted().findAny().isPresent()) {
                    event.getPlayer().getInventory().addItem(convenience);
                    event.getPlayer().sendMessage(getLang("Tips", "given_convenient_tool"));
                }
            }
            if((Boolean) BaseAPI.getDefaultConfig("死亡回主城")) {
                if (BaseAPI.getDefaultConfig("主城坐标") != null && !BaseAPI.getDefaultConfig("主城坐标").equals("null")) {
                    String pos = (String) BaseAPI.getDefaultConfig("主城坐标");
                    String[] poses = pos.split(":");
                    String levelname = poses[3];
                    if (Server.getInstance().getLevelByName(levelname) != null) {
                        double x = Double.parseDouble(poses[0]);
                        double y = Double.parseDouble(poses[1]);
                        double z = Double.parseDouble(poses[2]);
                        event.setRespawnPosition(new Position(x, y, z, player.getServer().getLevelByName(levelname)));
                        player.teleport(new Position(x, y, z, player.getServer().getLevelByName(levelname)), null);
                        player.sendMessage(getLang("Tips", "back_to_spawnpoint"));
                    } else {
                        player.sendMessage(getLang("Tips", "world_is_not_loaded"));
                    }
                }
            }else{
                Config config = new Config(MainClass.path+"/player/"+player.getName()+".yml");
                if (config.exists("spawnpoint")) {
                    String levelname = config.getString("spawnpoint.level");
                    if (Server.getInstance().getLevelByName(levelname) != null) {
                        double x = config.getDouble("spawnpoint.x");
                        double y = config.getDouble("spawnpoint.y");
                        double z = config.getDouble("spawnpoint.z");
                        event.setRespawnPosition(new Location(x, y, z, player.getServer().getLevelByName(levelname)));
                        player.teleport(new Position(x, y, z, player.getServer().getLevelByName(levelname)), null);
                        player.sendMessage(getLang("Tips", "back_to_spawnpoint"));
                    } else {
                        player.sendMessage(getLang("Tips", "world_is_not_loaded"));
                    }
                }else{
                    if(MainClass.bedFeature && config.exists("bedSpawn")){
                        String levelname = config.getString("bedSpawn.level");
                        if (Server.getInstance().getLevelByName(levelname) != null) {
                            double x = config.getDouble("bedSpawn.x");
                            double y = config.getDouble("bedSpawn.y");
                            double z = config.getDouble("bedSpawn.z");
                            Location location = new Location(x, y, z, player.getServer().getLevelByName(levelname));
                            if(location.getLevelBlock().getId() == Block.BED_BLOCK){
                                event.setRespawnPosition(location);
                                player.teleport(new Position(x, y, z, player.getServer().getLevelByName(levelname)), null);
                                player.sendMessage(getLang("Tips", "back_to_spawnpoint"));
                            }else{
                                config.remove("bedSpawn");
                                config.save();
                                player.sendMessage(new TranslationContainer("tile.bed.notValid"));
                            }
                        }else{
                            player.sendMessage(new TranslationContainer("tile.bed.notValid"));
                            event.setRespawnPosition(player.getSpawn());
                            player.teleport(player.getSpawn(), null);
                        }
                    }else {
                        event.setRespawnPosition(player.getSpawn());
                        player.teleport(player.getSpawn(), null);
                    }
                }
            }
            if (!MainClass.godPlayer.contains(player)) {
                MainClass.godPlayer.add(player);
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        MainClass.godPlayer.remove(player);
                        player.sendMessage(getLang("Tips", "god_effect_dissolve"));
                    }
                };
                MainClass.timer.schedule(timerTask, 3000);
                player.sendMessage(getLang("Tips", "god_effect_given"));
            }
        }
    }

    @EventHandler
    public void PlayerLocallyInitializedEvent(PlayerLocallyInitializedEvent event){
        if (event.getPlayer() == null) {
            return;
        }
        Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
        if(config.exists("是否使用快捷工具") && config.getBoolean("是否使用快捷工具",true)) {
            Item convenience = new Item(config.getInt("快捷工具ID", 347));
            convenience.setLore(getLang("Convenient_Tool","lore"));
            String nametag = getLang("Convenient_Tool","nametag");
            convenience.setCustomName(nametag);
            convenience.setDamage(0);
            if(!event.getPlayer().getInventory().getContents().values().stream().filter(item -> item.getCustomName().equals(nametag)).sorted().findAny().isPresent()) {
                event.getPlayer().getInventory().addItem(convenience);
                event.getPlayer().sendMessage(getLang("Tips", "given_convenient_tool"));
            }
        }
        if (config.getBoolean("是否进服回城", false)) {
            if (BaseAPI.getDefaultConfig("主城坐标") != null && !BaseAPI.getDefaultConfig("主城坐标").equals("null")) {
                String pos = (String) BaseAPI.getDefaultConfig("主城坐标");
                String[] poses = pos.split(":");
                double x = Double.parseDouble(poses[0]);
                double y = Double.parseDouble(poses[1]);
                double z = Double.parseDouble(poses[2]);
                String levelname = poses[3];
                if (Server.getInstance().getLevelByName(levelname) != null) {
                    event.getPlayer().teleport(new Location(x, y, z, Server.getInstance().getLevelByName(levelname)));
                } else {
                    event.getPlayer().sendMessage(getLang("Tips", "world_is_not_loaded"));
                }
            }
            event.getPlayer().sendMessage(getLang("Tips", "back_to_lobby"));
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if(MainClass.bedFeature){
            Block block = event.getBlock();
            if(block.getId() == Block.BED_BLOCK){
                if(block.getLocation().distance(player.getLocation()) > 1.5D){
                    player.sendMessage(new TranslationContainer("tile.bed.tooFar"));
                }else {
                    if (canSafelyFallAsleep(player)) {
                        Config config = new Config(MainClass.path + "/player/" + player.getName() + ".yml", Config.YAML);
                        config.set("bedSpawn.x", block.getX());
                        config.set("bedSpawn.y", block.getY());
                        config.set("bedSpawn.z", block.getZ());
                        config.set("bedSpawn.level", block.getLevel().getName());
                        config.save();
                        player.sendMessage(new TranslationContainer("tile.bed.respawnSet"));
                        onActivate((BlockBed) block, player);
                    } else {
                        player.sendMessage(new TranslationContainer("tile.bed.notSafe"));
                    }
                }
                event.setCancelled(true);
                return;
            }
        }
        Config config = new Config(MainClass.path + "/config.yml", Config.YAML);
        if (config.exists("是否使用快捷工具") && config.getBoolean("是否使用快捷工具")) {
            if (!event.getAction().equals(PlayerInteractEvent.Action.PHYSICAL)) {
                Item i = player.getInventory().getItemInHand();
                if (i.getId() == 347 && i.getCustomName().equals(getLang("Convenient_Tool", "nametag"))) {
                    player.getServer().dispatchCommand(player, "mytp open");
                }
            }
        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event){
        Config config = new Config(MainClass.path + "/config.yml", Config.YAML);
        if (!config.getBoolean("快捷工具丢弃", true)) {
            Player player = event.getPlayer();
            Item i = player.getInventory().getItemInHand();
            if (i.getId() == 347 && i.getCustomName().equals(getLang("Convenient_Tool", "nametag"))) {
                event.getPlayer().sendActionBar(getLang("Convenient_Tool","unable_to_drop"));
                event.setCancelled(true);
            }
        }
    }

    public void onActivate(BlockBed blockBed, Player player) {
        if (blockBed.level.getDimension() != 1 && blockBed.level.getDimension() != 2) {
            int time = blockBed.getLevel().getTime() % 24000;
            boolean isNight = time >= 14000 && time < 23000;
            if (player != null && !isNight) {
                player.sendMessage(new TranslationContainer("tile.bed.noSleep"));
            } else {
                Block blockNorth = blockBed.north();
                Block blockSouth = blockBed.south();
                Block blockEast = blockBed.east();
                Block blockWest = blockBed.west();
                Object b;
                if ((blockBed.getDamage() & 8) == 8) {
                    b = this;
                } else if (blockNorth.getId() == blockBed.getId() && (blockNorth.getDamage() & 8) == 8) {
                    b = blockNorth;
                } else if (blockSouth.getId() == blockBed.getId() && (blockSouth.getDamage() & 8) == 8) {
                    b = blockSouth;
                } else if (blockEast.getId() == blockBed.getId() && (blockEast.getDamage() & 8) == 8) {
                    b = blockEast;
                } else {
                    if (blockWest.getId() != blockBed.getId() || (blockWest.getDamage() & 8) != 8) {
                        if (player != null) {
                            player.sendMessage(new TranslationContainer("tile.bed.notValid"));
                        }
                        return;
                    }
                    b = blockWest;
                }

                if (b instanceof Vector3 && player != null && !player.sleepOn((Vector3) b)) {
                    player.sendMessage(new TranslationContainer("tile.bed.occupied"));
                }
            }
        } else {
            CompoundTag tag = EntityPrimedTNT.getDefaultNBT(blockBed).putShort("Fuse", 0);
            new EntityPrimedTNT(blockBed.level.getChunk(blockBed.getFloorX() >> 4, blockBed.getFloorZ() >> 4), tag);
        }
    }

    public boolean canSafelyFallAsleep(Player player){
        for(Entity entity: player.getLevel().getEntities()){
            if(entity instanceof EntityMob && entity.distance(player.getLocation()) < 10D){
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event){
        if(event.getEntity() == null){return; }
        Config playerconfig = new Config(MainClass.path+"/player/"+ event.getEntity().getName()+".yml",Config.YAML);
        playerconfig.set("lastdeath.x",event.getEntity().getX());
        playerconfig.set("lastdeath.y",event.getEntity().getY());
        playerconfig.set("lastdeath.z",event.getEntity().getZ());
        playerconfig.set("lastdeath.level",event.getEntity().getLevel().getName());
        playerconfig.save();
        MainClass.godPlayer.remove(event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerDamageEvent(EntityDamageEvent event){
        if (event.getEntity() == null) {
            return;
        }
        if(event.getEntity() instanceof Player) {
            if (MainClass.godPlayer.contains(((Player) event.getEntity()).getPlayer())){
                event.getEntity().setHealth(event.getEntity().getMaxHealth());
                event.setCancelled(true);
            }
        }
    }
}
