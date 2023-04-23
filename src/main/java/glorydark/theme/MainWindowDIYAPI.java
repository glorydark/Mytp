package glorydark.theme;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;
import glorydark.BaseAPI;
import glorydark.MainClass;

public class MainWindowDIYAPI {
    public static void optimizedMainWindow(FormWindowSimple form, Player player){
        Config lang = BaseAPI.getAllLang();
        for(int i = 1; i<=11; i++){
            String fuc = "MainMenu.button"+i+"_func";
            boolean display = lang.getBoolean("MainMenu.button"+i+"_show");
            if(display){
                if(i >= 8 && !MainClass.checktrust(player,false)){ continue; }
                form.addButton(BaseAPI.buildButton(BaseAPI.getLang("MainMenu","button"+i+"_text"),BaseAPI.getLang("MainMenu","button"+i+"_pic_path")));
            }
        }
    }

    public static int getRealFunction(ElementButton button){
        Config lang = BaseAPI.getAllLang();
        for(int i = 0; i<11; i++){
            String fuc = BaseAPI.getLang("MainMenu", "button"+i+"_func");
            String text = BaseAPI.getLang("MainMenu", "button"+i+"_text");
            if(text.equals(button.getText())){
                return ButtonType.getTypeByString(fuc).ordinal();
            }
        }
        return -1;
    }
}
