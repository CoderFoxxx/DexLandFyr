package me.twintailedfoxxx.dexlandfyr.commands;

import me.twintailedfoxxx.dexlandfyr.DexLandFyr;
import me.twintailedfoxxx.dexlandfyr.event.ClientChatEvent;
import me.twintailedfoxxx.dexlandfyr.objects.DLFCommand;
import me.twintailedfoxxx.dexlandfyr.util.Commands;
import me.twintailedfoxxx.dexlandfyr.util.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class IgnoreCommand extends DLFCommand {
    public IgnoreCommand() {
        super("ignore", "Управление чёрным списком игроков", "ignr");
    }

    public void execute(EntityPlayerSP player, ClientChatEvent event, String[] args) {
        if (args.length == 0) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Неверное использование команды! " +
                    "&8" + DexLandFyr.INSTANCE.commandPrefix + "&7&o" + getName()) + " " + Commands.argsToString(getArgs()));
        } else {
            switch (args[0]) {
                case "add":
                case "addplayer":
                    if (Minecraft.getMinecraft().theWorld.getPlayerEntityByName(args[1]) != null) {
                        if (DexLandFyr.INSTANCE.blacklist.addPlayer(args[1])) {
                            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&',
                                    "Вы добавили игрока &e" + args[1] + "&7 в чёрный список. Теперь Вы не будете видеть " +
                                            "сообщения этого игрока в чате."));
                        } else {
                            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Игрок &e" + args[1] +
                                    " &aуже &7находится в чёрном списке."));
                        }
                    } else Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&',
                            "&cОшибка: Игрока &e" + args[1] + "&7 нет в сети."));
                    break;
                case "remove":
                case "removeplayer":
                    DexLandFyr.INSTANCE.blacklist.removePlayer(args[1]);
                    Message.send(DexLandFyr.MESSAGE_PREFIX + "Вы удалили &e" + args[1] + "&7с чёрного списка.");
                    break;
                case "list":
                    Message.send(DexLandFyr.MESSAGE_PREFIX + "Список заблокированных игроков:");
                    DexLandFyr.INSTANCE.blacklist.listPlayers();
                    break;
            }
        }
    }

    @Override
    public String[] getArgs() {
        return new String[]{"addplayer&8/&eremoveplayer", "игрок"};
    }

    @Override
    public String getHoverText() {
        return Message.formatColorCodes('&',
                "&6Эта команда позволяет Вам добавлять игроков в чёрный список и удалять с него.\n" +
                        "\n" +
                        "&6Чтобы добавить игрока в чёрный список, используйте команду:\n" +
                        "&7" + DexLandFyr.INSTANCE.commandPrefix + getName() + " &badd &e[игрок]\n" +
                        "&6Чтобы удалить игрока с чёрного списка, используйте команду:\n" +
                        "&7" + DexLandFyr.INSTANCE.commandPrefix + getName() + " &bremove &e[игрок]\n" +
                        "&6Чтобы показать список заблокированных игроков, используйте команду:\n" +
                        "&7" + DexLandFyr.INSTANCE.commandPrefix + getName() + " &blist\n" +
                        "\n" +
                        "&7&oВы не будете видеть в чате сообщений, которые отправляет заблокированный Вами игрок." +
                        "\n" +
                        "&6&nНажмите, чтобы написать эту команду в чате!");
    }

    @Override
    public String getCommandSuggestText() {
        return DexLandFyr.INSTANCE.commandPrefix + getName() + "[addplayer/removeplayer] [игрок]";
    }
}
