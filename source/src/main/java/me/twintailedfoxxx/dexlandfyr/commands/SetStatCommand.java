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
import me.twintailedfoxxx.dexlandfyr.objects.FyrConfiguration;
import me.twintailedfoxxx.dexlandfyr.util.Commands;
import me.twintailedfoxxx.dexlandfyr.util.Message;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.ClientChatEvent;

public class SetStatCommand extends DLFCommand {
    private final FyrConfiguration conf = DexLandFyr.INSTANCE.cfg;

    public SetStatCommand() {
        super("setstat", "Установить статистику. Она устанавливается ОДИН раз.",
                "setstatistic", "stat", "sstat");
    }

    @Override
    public String[] getArgs() {
        return new String[]{"kills&8/&ebeds", "&eколичество-со-статистики"};
    }

    @Override
    public void execute(EntityPlayerSP player, ClientChatEvent event, String[] args) {
        if (args.length == 0) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Неверное использование команды! &8" + DexLandFyr.INSTANCE.commandPrefix + "&7&o" + getName()) + " " + Commands.argsToString(getArgs()));
        } else {
            switch (args[0].toLowerCase()) {
                case "kills":
                    if (conf.totalKills.getInt() == 0) {
                        int overallKills = Integer.parseInt(args[1]);
                        conf.totalKills.set(overallKills);
                        conf.reload();
                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы установили счетчик убийств на значение: &e" + overallKills));
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Вы уже установили количество общих убийств.");
                    }
                    break;
                case "beds":
                    if (conf.totalBeds.getInt() == 0) {
                        int overallBeds = Integer.parseInt(args[1]);
                        conf.totalBeds.set(overallBeds);
                        conf.reload();
                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы установили счетчик кроватей на значение: &e" + overallBeds));
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Вы уже установили количество сломанных кроватей.");
                    }
                    break;
                default:
                    Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Неизвестный тип статистики."));
            }
        }
    }

    @Override
    public String getHoverText() {
        return Message.formatColorCodes('&',
                "&6Эта команда позволяет установить значения для переменных 'Количество убийств за всё время' и " +
                        "'Количество сломанных кроватей за всё время'. " +
                        "\n" +
                        "\n" +
                        "&6Пример выполнения команды:\n" +
                        "\n" +
                        "&7&oНа таблице справа отображены данные:\n" +
                        "   &fУбийств: &e&l6,740\n" +
                        "   &fСмертей: &e1,550\n" +
                        "   &fK/D: &e4.35\n" +
                        "   &fИгр: &e750\n" +
                        "   &fПобед: &e340\n" +
                        "   &fСломанных кроватей: &e&l1,810\n" +
                        "\n" +
                        "&9&oКоманды для установки переменных будут выглядеть следующим образом:\n" +
                        "&7" + DexLandFyr.INSTANCE.commandPrefix + "setstat &ekills &e&l6740\n" +
                        "&7" + DexLandFyr.INSTANCE.commandPrefix + "setstat &ebeds &e&l1810" +
                        "\n" +
                        "&c&lВНИМАНИЕ! &7Значение переменной устанавливается ТОЛЬКО ОДИН РАЗ!\n" +
                        "&6&nНажмите, чтобы написать эту команду в чате!");
    }

    @Override
    public String getCommandSuggestText() {
        return DexLandFyr.INSTANCE.commandPrefix + getName() + " [kills/beds] [количество]";
    }
}
