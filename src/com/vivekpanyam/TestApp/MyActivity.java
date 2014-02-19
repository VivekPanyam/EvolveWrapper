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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Init App with the APK file specified
        // This APK file must be in the assets directory
        DynamicApp.init(this, "MyActualApp.apk");

        /* Start App by calling this activity. It is the entry point for the new
         * application.
         *
         * THIS ACTIVITY MUST BE DECLARED IN THE ANDROID MANIFEST
         */
        startApp("com.example.MainActivity");

        //Close this activity so the user doesn't see it when they close the app.
        finish();
    }

    /**
     * Helper function that starts an activity given a class name
     *
     * @param className The class name of the Activity to start
     */
    public void startApp(String className) {
        try {
            Intent intent = new Intent(this, Class.forName(className, false, DynamicApp.classLoader));
            System.out.println("Starting App With " + className);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
