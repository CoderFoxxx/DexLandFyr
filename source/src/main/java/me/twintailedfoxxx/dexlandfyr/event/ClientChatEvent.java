// Credit goes to: ayleafs
// https://github.com/ayleafs

package me.twintailedfoxxx.dexlandfyr.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ClientChatEvent extends Event {
    public String message;

    public ClientChatEvent(String message) {
        this.message = message;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}