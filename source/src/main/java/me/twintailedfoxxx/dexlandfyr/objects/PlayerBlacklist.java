package me.twintailedfoxxx.dexlandfyr.objects;

import me.twintailedfoxxx.dexlandfyr.DexLandFyr;
import me.twintailedfoxxx.dexlandfyr.util.Message;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerBlacklist {
    private ArrayList<String> blacklistedPlayers;
    private FyrConfiguration conf = DexLandFyr.INSTANCE.cfg;

    public PlayerBlacklist() {
        this.blacklistedPlayers = new ArrayList<>(Arrays.asList(conf.playerBlacklist.getStringList()));
    }

    public boolean addPlayer(String name) {
        if (!blacklistedPlayers.contains(name)) {
            blacklistedPlayers.add(name);
            conf.playerBlacklist.set(blacklistedPlayers.toArray(new String[0]));
            conf.reload();
            return true;
        } else return false;
    }

    public void removePlayer(String name) {
        blacklistedPlayers.remove(name);
        conf.playerBlacklist.set(blacklistedPlayers.toArray(new String[0]));
        conf.reload();
    }

    public boolean has(String name) {
        return blacklistedPlayers.contains(name);
    }

    public void listPlayers() {
        for (int i = 0; i < blacklistedPlayers.size(); i++) {
            Message.send(Message.formatColorCodes('&',
                    "   &a" + (i + 1) + " &c&l" + blacklistedPlayers.get(i)));
        }
    }
}
