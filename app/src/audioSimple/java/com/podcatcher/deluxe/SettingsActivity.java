/**
 * Copyright 2012-2016 Kevin Hausmann
 *
 * This file is part of Podcatcher Deluxe.
 *
 * Podcatcher Deluxe is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Podcatcher Deluxe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Podcatcher Deluxe. If not, see <http://www.gnu.org/licenses/>.
 */

package com.podcatcher.deluxe;

import com.podcatcher.deluxe.view.fragments.SettingsFragment;

import android.os.Bundle;

/**
 * Update settings activity.
 */
public class SettingsActivity extends BaseActivity {

    /**
     * The flag for the first run dialog
     */
    public static final String KEY_FIRST_RUN = "first_run";

    /**
     * The select all podcast on start-up preference key
     */
    public static final String KEY_SELECT_ALL_ON_START = "select_all_on_startup";
    /**
     * The preference key for the auto download flag
     */
    public static final String KEY_AUTO_DOWNLOAD = "auto_download";
    /**
     * The preference key for the auto delete flag
     */
    public static final String KEY_AUTO_DELETE = "auto_delete";
    /**
     * The key for the download folder preference
     */
    public static final String KEY_DOWNLOAD_FOLDER = "download_folder";

    /**
     * Setting key for the sync receive field
     */
    public static final String KEY_SYNC_RECEIVE = "receive_controller";
    /**
     * Setting key for the sync field
     */
    public static final String KEY_SYNC_ACTIVE = "enabled_sync_controllers";
    /**
     * Setting key for the last sync field
     */
    public static final String KEY_LAST_SYNC = "last_full_sync";

    /**
     * Tag to find the add suggestion dialog fragment under
     */
    private static final String SETTINGS_DIALOG_TAG = "settings_dialog";
    /**
     * The settings fragment we display
     */
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.preferences);

        // Create and show suggestion fragment
        if (savedInstanceState == null) {
            // Create the fragment to show
            this.settingsFragment = new SettingsFragment();
            // Display the fragment as the main content.
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, settingsFragment, SETTINGS_DIALOG_TAG)
                    .commit();
        } else
            this.settingsFragment = (SettingsFragment)
                    getFragmentManager().findFragmentByTag(SETTINGS_DIALOG_TAG);
    }
}
