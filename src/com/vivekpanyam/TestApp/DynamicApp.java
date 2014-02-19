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
import java.lang.reflect.Method;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import dalvik.system.DexClassLoader;


public class DynamicApp {


    public static Context ctx;
    public static ClassLoader classLoader;

    /**
     * Init the DynamicApp with an APK file
     *
     * @param a An activity in the currently running application
     * @param filename The path to the APK file to load
     */
    public static void init(Activity a, String filename) {
        try{
            //Get Application Context
            ctx = a.getApplicationContext();

            //If there is an update, launch that
            File update = new File(ctx.getFilesDir(), "update_done.apk");
            if (update.exists()) {
                update.renameTo(new File(ctx.getFilesDir(), filename));
            }

            //Load new package; extract it from assets if necessary
            String file = Utils.loadDexFile(a, ctx, filename);

            // Check signatures
            Signature[] mySignatures = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES).signatures;

            // Get package info of new package
            PackageInfo newInfo = Utils.getAPKInfo(file, ctx);
            Signature[] newSignatures = newInfo.signatures;

            boolean sigMatch = false;

            // This is fine because the arrays are going to be pretty small
            for (Signature signature : mySignatures) {
                for (Signature signature2 : newSignatures) {
                    if (signature.equals(signature2)) {
                        sigMatch = true;
                        break;
                    }
                }
            }

            if (!sigMatch) {
                return;
            }

            //Get version of new package
            int version = newInfo.versionCode;

            //Get Dispatcher of other package and init it.
            Class<?> evolve = Class.forName("com.vivekpanyam.evolve.Evolve", false, DynamicApp.classLoader);


            //Load resources
            Class<?>[] parameterTypes = new Class[1];
            parameterTypes[0] = java.lang.String.class;
            Method m = evolve.getDeclaredMethod("loadResources", parameterTypes);
            m.setAccessible(true);

            Object[] parameters = new Object[1];
            parameters[0] = file;
            m.invoke(evolve, parameters);

            //Init Dispatcher
            Class<?>[] parameterTypes2 = new Class[3];
            parameterTypes2[0] = Activity.class;
            parameterTypes2[1] = Integer.class;
            parameterTypes2[2] = DexClassLoader.class;
            Method m2 = evolve.getDeclaredMethod("init", parameterTypes2);
            m2.setAccessible(true);

            Object[] parameters2 = new Object[3];
            parameters2[0] = a;
            parameters2[1] = version;
            parameters2[2] = DynamicApp.classLoader;
            m2.invoke(evolve, parameters2);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
