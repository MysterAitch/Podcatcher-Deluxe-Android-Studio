/** Copyright 2012-2015 Kevin Hausmann
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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.podcatcher.deluxe.R;
import com.podcatcher.deluxe.model.PodcastManager;
import com.podcatcher.deluxe.model.types.Genre;
import com.podcatcher.deluxe.model.types.Language;
import com.podcatcher.deluxe.model.types.MediaType;
import com.podcatcher.deluxe.model.types.Suggestion;
import com.podcatcher.deluxe.view.SuggestionListItemViewHolder;
import com.podcatcher.deluxe.view.fragments.AddSuggestionFragment.AddSuggestionListener;

import java.util.List;

/**
 * Adapter for the suggestion list/grid.
 */
public class SuggestionListAdapter extends RecyclerView.Adapter<SuggestionListItemViewHolder> {

    /**
     * Handler for button call backs
     */
    protected final AddSuggestionListener listener;
    /**
     * The list our data resides in
     */
    protected List<Suggestion> list;

    /**
     * The currently selected language
     */
    private Language selectedLanguage;
    /**
     * The currently selected content category
     */
    private Genre selectedGenre;
    /**
     * The currently selected media type
     */
    private MediaType selectedType;

    /**
     * Create new adapter.
     *
     * @param suggestions List of podcasts (suggestions) to wrap.
     * @param listener    Call back for the add button to attach.
     */
    public SuggestionListAdapter(List<Suggestion> suggestions, AddSuggestionListener listener) {
        this.list = suggestions;
        this.listener = listener;

        setHasStableIds(true);
    }

    /**
     * Update the adapter on the current filter settings.
     *
     * @param selectedLanguage Language filter set, <code>null</code> for "none"
     * @param selectedGenre    Category filter set, <code>null</code> for "none"
     * @param selectedType     Media type filter set, <code>null</code> for "none"
     */
    public void setFilterConfiguration(Language selectedLanguage, Genre selectedGenre, MediaType selectedType) {
        this.selectedLanguage = selectedLanguage;
        this.selectedGenre = selectedGenre;
        this.selectedType = selectedType;
    }

    @Override
    public SuggestionListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new SuggestionListItemViewHolder(LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.suggestion_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(SuggestionListItemViewHolder holder, int position) {
        holder.show(list.get(position), listener, selectedLanguage, selectedGenre, selectedType);
    }

    @Override
    public void onViewAttachedToWindow(SuggestionListItemViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        // This makes sure the holder can react when "its" podcast is actually added
        PodcastManager.getInstance().addChangePodcastListListener(holder);
    }

    @Override
    public void onViewDetachedFromWindow(SuggestionListItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        // Make sure the holder is removed to prevent leaking
        PodcastManager.getInstance().removeChangePodcastListListener(holder);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
