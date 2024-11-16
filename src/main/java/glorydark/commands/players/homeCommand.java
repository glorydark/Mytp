package glorydark.commands.players;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import glorydark.BaseAPI;
import glorydark.gui.GuiMainAPI;

public class homeCommand extends Command {
    public homeCommand() {
        super("home", "", "/home");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender instanceof Player) {
            GuiMainAPI.showHomeMainMenu((Player) commandSender);
        } else {
            commandSender.sendMessage(BaseAPI.getLang("Tips", "plz_use_it_in_game"));
        }
        return true;
    }
}
