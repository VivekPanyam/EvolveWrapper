/*    Copyright (C) 2014 Vivek Panyam.
 *
 *    This program is free software: you can redistribute it and/or  modify
 *    it under the terms of the GNU Affero General Public License, version 3,
 *    as published by the Free Software Foundation.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.vivekpanyam.TestApp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;


import dalvik.system.DexClassLoader;

public class Utils {


    public static PackageInfo getAPKInfo(String path, Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SIGNATURES);
        return info;
    }


    /**
     * Load the classes.dex file from an APK or JAR file.
     *
     * @param a An activity in the currently running application
     * @param context The application context for the currently running application
     * @param file The name of the APK or JAR file to load (ex: myapp.apk)
     *
     * @return The path to the APK file
     */
    public static String loadDexFile(Activity a, Context context, String file) {
        try {
            File f = new File(context.getFilesDir(),file);

            //If the file doesn't exist, create it from the bundled one in assets
            if (!f.exists()) {
                copyAsset(context, file);
            }
            System.out.println(f.getAbsolutePath());
            DexClassLoader dcl = new DexClassLoader(
                    f.getAbsolutePath(),
                    context.getCacheDir().getAbsolutePath(),
                    null,
                    context.getClassLoader());

            DynamicApp.classLoader = dcl;

            return f.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static void copyAsset(Context ctx, String filename)
    {
        AssetManager assetManager = ctx.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try
        {
            in = assetManager.open(filename);
            out = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }

}
