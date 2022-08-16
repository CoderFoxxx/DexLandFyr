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
import me.twintailedfoxxx.dexlandfyr.objects.Pair;
import me.twintailedfoxxx.dexlandfyr.objects.Version;
import org.apache.logging.log4j.Level;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

public class VersionChecker implements Callable<Pair<String, Boolean>> {
    public Pair<String, Boolean> call() {
        boolean isLatest = true;
        String latest;
        Pair<String, Boolean> pair = null;

        try {
            InputStream str = new URL(DexLandFyr.UPDATE_URL + "version").openStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = str.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            latest = result.toString("UTF-8");
            Version remoteVersion = new Version(latest);
            if (remoteVersion.isGreaterThan(DexLandFyr.INSTANCE.version)) {
                isLatest = false;
                DexLandFyr.logger.log(Level.INFO, "There is an update for the mod. Please, update it to the latest version.");
            }

            pair = new Pair<>(latest, isLatest);
            str.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pair;
    }
}