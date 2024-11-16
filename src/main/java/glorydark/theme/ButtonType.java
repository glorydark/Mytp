package glorydark.theme;

public enum ButtonType {

    WARP("warp", 0),
    TELEPORT("teleport", 1),
    MYSETTINGS("mysettings", 2),
    HOME("home", 3),
    WORLD("world", 4),
    DEATHSPAWN("deathspawn", 5),
    WILD("wild", 6),
    TELEPORTALL("teleportall", 7),
    WARPSETTINGS("warpsettings", 8),
    GLOBALSETTINGS("globalsettings", 9),
    WORLDOP("worldop", 10),
    UNDEFINED("none", -1);

    private final String type;
    private final int originId;

    private ButtonType(String type, int originId) {
        this.type = type;
        this.originId = originId;
    }

    public static ButtonType getTypeByString(String string) {
        for (ButtonType buttonType : ButtonType.values()) {
            if (buttonType.getType().equals(string)) {
                return buttonType;
            }
        }
        return UNDEFINED;
    }

    public int getOriginId() {
        return originId;
    }

    public String getType() {
        return type;
    }
}
