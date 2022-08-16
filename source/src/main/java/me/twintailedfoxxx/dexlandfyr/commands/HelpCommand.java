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

package me.twintailedfoxxx.dexlandfyr.commands;

import me.twintailedfoxxx.dexlandfyr.DexLandFyr;
import me.twintailedfoxxx.dexlandfyr.objects.DLFCommand;
import me.twintailedfoxxx.dexlandfyr.objects.MessageAction;
import me.twintailedfoxxx.dexlandfyr.util.Commands;
import me.twintailedfoxxx.dexlandfyr.util.Message;
import net.minecraft.client.entity.EntityPlayerSP;
import me.twintailedfoxxx.dexlandfyr.event.ClientChatEvent;

public class HelpCommand extends DLFCommand {
    public HelpCommand() {
        super("help", "Показывает это сообщение помощи.", "h", "hlp");
    }

    @Override
    public void execute(EntityPlayerSP player, ClientChatEvent event, String[] args) {
        showHelp();
    }

    @Override
    public String getHoverText() {
        return Message.formatColorCodes('&', "&6Эта команда показывает это сообщение помощи");
    }

    @Override
    public String getCommandSuggestText() {
        return DexLandFyr.INSTANCE.commandPrefix + "help";
    }

    public static void showHelp() {
        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&a&lDex&f&lLand&6&lФыр &7Сделано с " +
                "&c♥ &7от &6&lTwinTailedFo&f&lxxx&7. &6&lФыр!"));
        Message.send(Message.formatColorCodes('&', "&d&lЧтобы узнать больше информации о команде, наведите на неё курсор."));
        for (DLFCommand command : Commands.commands)
            Message.send("  " + Message.formatColorCodes('&', "&7" + DexLandFyr.INSTANCE.commandPrefix + "&o" +
                            command.getName() + " " + Commands.argsToString(command.getArgs()) + " &8- &b" +
                            command.getDescription() + " &7&o(Алиасы: " + command.getAliases() + ")"),
                    MessageAction.SUGGEST_COMMAND, command.getCommandSuggestText(), command.getHoverText());
        Message.send(Message.formatColorCodes('&',
                "   &e&lFAQ по категориям:\n" +
                        "       &e• &fkill &8- &3категория сообщений о убийстве Вашего оппонента;\n" +
                        "       &e• &fvoidkill &8- &3категория сообщений о убийстве Вашего оппонента после его смерти в бездне;\n" +
                        "       &e• &fbed &8- &3категория сообщений о сломанных кроватях Ваших оппонентов;\n" +
                        "       &e• &fownbed &8- &3категория сообщений о сломанной кровати Вашей команды;\n" +
                        "       &e• &gamestart &8- &3категория сообщений о начале игры;\n" +
                        "       &e• &fgameend &8- &3категория сообщений о конце игры;\n" +
                        "       &e• &fdeath &8- &3категория сообщений о Вашей смерти от рук другого игрока;\n" +
                        "       &e• &fvoiddeath &8- &3категория сообщений о Вашей смерти в бездне (игрок не имел содействие в этом);\n" +
                        "       &e• &fswearreply &8- &3категория сообщений, которые будут отправляться в ответ нецензурным словам.\n" +
                        "   &e&lFAQ по переменным:\n" +
                        "       &e• &f&l{ingame_kill_count} &8- &3показывает количество убийств в идущей игре;\n" +
                        "       &e• &f&l{total_kill_count} &8- &3показывает общее количество убийств (если Вы установили " +
                        "эту переменную при помощи " + DexLandFyr.INSTANCE.commandPrefix + "setstat);\n" +
                        "       &e• &f&l{ingame_beds_count} &8- &3показывает количетсво сломанных кроватей в идущей игре;\n" +
                        "       &e• &f&l{total_beds_count} &8- &3показывает общее количество сломанных кроватей (если Вы " +
                        "установили эту переменную при помощи " + DexLandFyr.INSTANCE.commandPrefix + "setstat);\n" +
                        "       &e• &f&l{ingame_death_count} &8- &3показывает количество смертей в идущей игре;\n" +
                        "       &e• &f&l{player} &8- &3ник умершего от Вас игрока;\n" +
                        "       &e• &f&l{player_team_color} &8- &3цвет команды умершего от Вас игрока;\n" +
                        "       &e• &f&l{killer} &8- &3ник убившего Вас игрока;\n" +
                        "       &e• &f&l{killer_team_color} &8- &3цвет команды убившего Вас игрока;\n" +
                        "       &e• &f&l{dc} &8- &3цвет, установленный с помощью " + DexLandFyr.INSTANCE.commandPrefix + "setcc;\n" +
                        "       &e• &f&l{gameTimer} &8- &3время игры, которое выводится только после игры."));
    }
}