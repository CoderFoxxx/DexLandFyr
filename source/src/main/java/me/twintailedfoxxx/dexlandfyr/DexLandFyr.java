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

package me.twintailedfoxxx.dexlandfyr;

import me.twintailedfoxxx.dexlandfyr.commands.*;
import me.twintailedfoxxx.dexlandfyr.objects.FyrConfiguration;
import me.twintailedfoxxx.dexlandfyr.objects.PlayerBlacklist;
import me.twintailedfoxxx.dexlandfyr.objects.Version;
import me.twintailedfoxxx.dexlandfyr.util.Commands;
import me.twintailedfoxxx.dexlandfyr.util.Message;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

@Mod(modid = DexLandFyr.MODID, name = DexLandFyr.NAME, version = DexLandFyr.VERSION, clientSideOnly = true)
public class DexLandFyr {
    public static final String MODID = "dexlandfyr";
    public static final String NAME = "DexLandFyr";
    public static final String VERSION = "1.3.2-RELEASE";
    public static final String MESSAGE_PREFIX = Message.formatColorCodes('&', "&8[&a&lDex&f&lLand&6&lФыр&8] &7");
    public static final String UPDATE_URL = "https://raw.githubusercontent.com/CoderFoxxx/DexLandFyr/1.12.2/versions/1.12.2/";

    public static DexLandFyr INSTANCE = null;
    public String commandPrefix = "";
    public File configFile;
    public static Logger LOGGER;
    public FyrConfiguration cfg;
    public Version version;
    public ArrayList<String> swearDictionary;
    public PlayerBlacklist blacklist;
    public boolean isEnabled;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException {
        INSTANCE = this;
        version = new Version(VERSION);
        swearDictionary = new ArrayList<>();
        configFile = new File(event.getModConfigurationDirectory() + File.separator + "dexlandfyr.cfg");
        cfg = new FyrConfiguration(configFile);
        if(!cfg.modEnabled.getBoolean()) {
            cfg.modEnabled.set(true);
            cfg.reload();
        }
        isEnabled = cfg.modEnabled.getBoolean();
        blacklist = new PlayerBlacklist();
        commandPrefix = cfg.commandPrefix.getString();
        LOGGER = event.getModLog();
        MinecraftForge.EVENT_BUS.register(new Events());

        URL path = getClass().getClassLoader().getResource("swear_dict.txt");
        assert path != null;
        FileUtils.copyURLToFile(path, new File(event.getModConfigurationDirectory() + File.separator +
                "swear_dict.txt"));
        Scanner scn = new Scanner(new File(event.getModConfigurationDirectory() + File.separator +
                "swear_dict.txt"));
        while (scn.hasNext()) {
            swearDictionary.add(scn.next());
        }
        scn.close();

        Commands.register(new HelpCommand());
        Commands.register(new CategoryCommand());
        Commands.register(new ToggleCommand());
        Commands.register(new SetCommandPrefixCommand());
        Commands.register(new ResetSettingsCommand());
        Commands.register(new SetStatCommand());
        Commands.register(new MultipleColorMessageCommand());
        Commands.register(new SetChatColorCommand());
        Commands.register(new SoundCommand());
        Commands.register(new UpdateCheckCommand());
        Commands.register(new IgnoreCommand());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("DexLandFyr запущен!");
    }
}
