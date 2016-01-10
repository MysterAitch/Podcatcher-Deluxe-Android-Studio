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

package com.podcatcher.deluxe.adapters;

import com.podcatcher.deluxe.R;
import com.podcatcher.deluxe.model.types.MediaType;
import com.podcatcher.deluxe.view.fragments.AddSuggestionFragment;

import android.content.Context;

/**
 * Adapter for the media type spinner in the suggestion dialog.
 */
public class MediaTypeSpinnerAdapter extends SuggestionFilterSpinnerAdapter {

    /**
     * Create new adapter.
     *
     * @param context The current context.
     */
    public MediaTypeSpinnerAdapter(Context context) {
        super(context);

        // Put all types into the value map where they are sorted by language
        // because we are using the corresponding resources as keys
        for (int index = 0; index < MediaType.values().length; index++) {
            final String key = resources.getStringArray(R.array.types)[index];
            values.put(key, MediaType.values()[index]);
        }
    }

    @Override
    public Object getItem(int position) {
        if (position == 0)
            return AddSuggestionFragment.FILTER_WILDCARD;
        else
            return values.values().toArray()[position - 1];
    }

    @Override
    public int getCount() {
        return MediaType.values().length + 1;
    }
}
