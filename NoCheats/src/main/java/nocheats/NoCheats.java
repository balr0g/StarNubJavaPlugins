package nocheats;


import nocheats.AntiGreif.Tiles;
import org.starnub.plugins.Plugin;

public class NoCheats extends Plugin {

    private volatile Tiles tiles;

    @Override
    public void onPluginEnable() {
        tiles =  new Tiles();
        tiles.registerTileGroupDamage();
    }

    @Override
    public void onPluginDisable() {

    }
}
