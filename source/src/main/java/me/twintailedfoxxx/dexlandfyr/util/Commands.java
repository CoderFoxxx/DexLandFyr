/*
 * MIT License
 *
 * Copyright (c) 2022 Nick "CoderFoxxx", "TwinTailedFoxxx" Moonshine
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.twintailedfoxxx.dexlandfyr.util;

import me.twintailedfoxxx.dexlandfyr.DexLandFyr;
import me.twintailedfoxxx.dexlandfyr.objects.DLFCommand;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.ClientChatEvent;

import java.util.ArrayList;

public class Commands {
    public static final ArrayList<DLFCommand> commands = new ArrayList<>();

    public static boolean execute(String[] args, ClientChatEvent e, EntityPlayerSP player) {
        String chatCommandName = (e.getMessage().split(" ")[0]).replace(DexLandFyr.INSTANCE.commandPrefix, "")
                .toLowerCase();
        for (DLFCommand command : commands) {
            if ((chatCommandName.equals(command.getName())) || command.getAliases().contains(chatCommandName)) {
                command.execute(player, e, args);
                return true;
            }
        }
        return false;
    }

    public static String argsToString(String[] args) {
        if (args.length != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String arg : args)
                stringBuilder.append(Message.formatColorCodes('&', "&d[&e" + arg + "&d]")).append(" ");
            return stringBuilder.toString();
        }
        return "";
    }

    public static void register(DLFCommand command) {
        if (!commands.contains(command)) commands.add(command);
    }
}
