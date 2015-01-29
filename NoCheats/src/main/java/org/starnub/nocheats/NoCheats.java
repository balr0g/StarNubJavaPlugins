package org.starnub.nocheats;


import org.starnub.starnubserver.pluggable.Plugin;

public class NoCheats extends Plugin {

    private volatile Tiles tiles;

    @Override
    public void onEnable() {
        tiles =  new Tiles();
        tiles.registerTileGroupDamage();
    }

    @Override
    public void onDisable() {

    }
}
