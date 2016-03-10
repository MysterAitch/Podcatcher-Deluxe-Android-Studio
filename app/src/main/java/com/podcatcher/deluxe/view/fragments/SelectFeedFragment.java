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

package com.podcatcher.deluxe.view.fragments;

import com.podcatcher.deluxe.R;
import com.podcatcher.deluxe.model.PodcastManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A dialog that presents feed alternatives (@link Suggestion#getFeeds()) to the user
 * and allows for picking one of the options.
 * The activity that shows this needs to implement the {@link SelectFeedDialogListener}.
 */
public class SelectFeedFragment extends DialogFragment {

    /**
     * The feeds list
     */
    private Map<String, String> feeds = new LinkedHashMap<>();
    /**
     * The feed list adapter
     */
    private RecyclerView.Adapter<FeedListItemHolder> adapter = new FeedListAdapter();

    /**
     * The listener we report back to
     */
    private SelectFeedDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Make sure our listener is present
        try {
            this.listener = (SelectFeedDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SelectFeedDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.select_feed, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final RecyclerView feedList = (RecyclerView) view.findViewById(R.id.feed_list);

        feedList.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        feedList.setHasFixedSize(true);
        feedList.setLayoutManager(new LinearLayoutManager(getActivity()));
        feedList.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        // We close the fragment here, it needs no lifecycle
        dismiss();
    }

    /**
     * Set the list of feed alternatives to show.
     *
     * @param feedAlternatives The map.
     */
    public void setFeeds(Map<String, String> feedAlternatives) {
        if (feedAlternatives != null)
            this.feeds = feedAlternatives;

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    /**
     * Interface definition for a callback to be invoked when the user picks a feed.
     */
    public interface SelectFeedDialogListener {

        /**
         * Called on listener when podcast feed alternative is picked.
         *
         * @param podcastUrl Podcast URL for the feed selected.
         */
        void onFeedSelected(String podcastUrl);
    }

    /**
     * The list adapter, nothing special here
     */
    private class FeedListAdapter extends RecyclerView.Adapter<FeedListItemHolder> {

        private FeedListAdapter() {
            setHasStableIds(true);
        }

        @Override
        public FeedListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FeedListItemHolder(LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.select_feed_item, parent, false));
        }

        @Override
        public void onBindViewHolder(FeedListItemHolder holder, int position) {
            holder.show(feeds.keySet().toArray()[position].toString(),
                    feeds.values().toArray()[position].toString());
        }

        @Override
        public int getItemCount() {
            return feeds.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    /**
     * The item view holder
     */
    private class FeedListItemHolder extends RecyclerView.ViewHolder {

        /**
         * The feed URL the holder currently represents
         */
        private String url;

        // The views held
        private ImageView iconView;
        private TextView labelView;
        private TextView captionView;

        private FeedListItemHolder(View itemView) {
            super(itemView);

            iconView = (ImageView) itemView.findViewById(R.id.feed_type);
            labelView = (TextView) itemView.findViewById(R.id.feed_label);
            captionView = (TextView) itemView.findViewById(R.id.feed_label_caption);

            // When a feed is picked, alert listener and close the dialog
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onFeedSelected(url);
                    dismiss();
                }
            });
        }

        private void show(String label, String url) {
            this.url = url;

            // Disable if feed is already on the podcast list
            final boolean disabled = PodcastManager.getInstance().findPodcastForUrl(url) != null;
            itemView.setEnabled(!disabled);
            labelView.setEnabled(!disabled);
            captionView.setEnabled(!disabled);
            iconView.setEnabled(!disabled);

            // We expect the label to look like "Feed name (Size, MIME type),
            // e.g. "Premium (60MB, audio/mp3)"
            if (label.matches(".+[(][0-9]+MB (audio|video)/.+[)]$")) {
                // Leaves "60MB, audio/mp3)" as last index
                final String[] parts = label.trim().split("[(]");
                // Leaves "60MB, audio/mp3" as meta
                final String meta = parts[parts.length - 1].substring(0, parts[parts.length - 1].length() - 1);
                // Array with ["60MB", "audio/mp3"]
                final String[] metaParts = meta.split(" ");
                // Label needs all the rest that is not meta
                labelView.setText(label.substring(0, label.length() - (meta.length() + 2)).trim());

                captionView.setVisibility(View.VISIBLE);
                captionView.setText(getString(R.string.suggestion_file_size, metaParts[0]));

                iconView.setVisibility(View.VISIBLE);

                if (metaParts[1].toLowerCase(Locale.US).startsWith("video"))
                    iconView.setImageResource(R.drawable.ic_media_video);
                else if (metaParts[1].toLowerCase(Locale.US).startsWith("audio"))
                    iconView.setImageResource(R.drawable.ic_media_audio);
                else
                    iconView.setImageResource(0);
            } else {
                labelView.setText(label);
                captionView.setVisibility(View.GONE);
                iconView.setVisibility(View.GONE);
            }
        }
    }

    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {

        private final Drawable divider;

        public SimpleDividerItemDecoration(Context context) {
            // Use the list view divider drawable
            final TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
            divider = a.getDrawable(0);
            a.recycle();
        }

        @Override
        public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(canvas);
            }
        }
    }
}
