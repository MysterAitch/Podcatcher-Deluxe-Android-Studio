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

package com.podcatcher.deluxe.model.test;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.podcatcher.deluxe.R;
import com.podcatcher.deluxe.model.tags.RSS;
import com.podcatcher.deluxe.model.types.Genre;
import com.podcatcher.deluxe.model.types.Language;
import com.podcatcher.deluxe.model.types.Podcast;
import com.podcatcher.deluxe.model.types.Suggestion;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Creates JSON files for importing podcast suggestions to podcatcher-deluxe.com
 */
@SuppressWarnings("javadoc")
public class SuggestionImporter extends InstrumentationTestCase {

    public static final String TAG = "IMPORTER";

    public final void testCreateImportFile() {
        // final String url = "http://brainsciencepodcast.libsyn.com/rss";
        final Context context = getInstrumentation().getTargetContext();
        final List<Podcast> oldSuggestions = Utils.getExamplePodcasts(context, 25);
        //oldSuggestions.add(new Podcast(null, url));
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final List<Dummy> dummies = new ArrayList<>();

        for (Podcast podcast : oldSuggestions) {
            final SuggestionImport si = new SuggestionImport((Suggestion) podcast);

            Utils.loadAndWait(si);

            // Checks: episodes? Latest episode date?

            dummies.add(new Dummy(si));

            // Checks: All fields set?
        }

        final File podcastFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(podcastFolder, "suggestions.json"));
            stream.write(gson.toJson(dummies).getBytes());
        } catch (IOException e) {
            Log.w(TAG, "Failed to write JSON file.");
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "JSON starts with: " + gson.toJson(dummies));
    }

    private class SuggestionImport extends Suggestion {

        String keywords;
        String link;
        String language;
        Set<String> categories = new HashSet<>();

        public SuggestionImport(Suggestion podcast) {
            super(null, podcast.getUrl());

            // Carry over values
            votes = podcast.votes;
            added = podcast.added;
        }

        @Override
        protected void parse(XmlPullParser parser, String tagName) throws XmlPullParserException, IOException {
            Log.d(TAG, "TAGNAME: " + tagName);
            final Resources res = getInstrumentation().getTargetContext().getResources();

            if (RSS.LINK.equals(tagName) && link == null) // We only want the first one from the header
                link = normalizeUrl(parser.nextText());
            else if (RSS.DESCRIPTION.equals(tagName) && description == null)
                description = parser.nextText();
            else if ("keywords".equals(tagName) && keywords == null)
                keywords = parser.nextText();
            else if (RSS.LANGUAGE.equals(tagName) && language == null) {
                final String langCode = parser.nextText().substring(0, 2).toLowerCase(Locale.US);

                switch (langCode) {
                    case "en":
                        language = res.getStringArray(R.array.languages)[Language.ENGLISH.ordinal()];
                        break;
                    case "de":
                        language = res.getStringArray(R.array.languages)[Language.GERMAN.ordinal()];
                        break;
                    case "fr":
                        language = res.getStringArray(R.array.languages)[Language.FRENCH.ordinal()];
                        break;
                    case "es":
                        language = res.getStringArray(R.array.languages)[Language.SPANISH.ordinal()];
                        break;
                    default:
                        Log.w(TAG, "Unknown language: " + langCode);
                }
            } else if (RSS.CATEGORY.equals(tagName)) {
                String category = parser.getAttributeValue("", "text");
                if (category == null || category.length() < 2)
                    category = parser.nextText();

                try {
                    category = category.replace("&amp;", "&").split("/")[0];

                    final Genre genre = Genre.forLabel(category);
                    categories.add(res.getStringArray(R.array.genres)[genre.ordinal()]);
                } catch (IllegalArgumentException iae) {
                    // pass
                    Log.w(TAG, "Unknown category: " + category);
                } catch (Throwable th) {
                    // pass
                    Log.w(TAG, "Bad category: " + category);
                }
            }
        }
    }

    private class Dummy {

        private String title;
        private String keywords;
        private String feed;
        private String logo;
        private String site;
        private String description;
        private Object[] category;
        private String language;
        private String type;
        private boolean explicit;
        private int votes;
        private String added;
        private String path;

        public Dummy(SuggestionImport si) {
            this.title = si.getName();
            this.keywords = si.keywords;
            this.feed = si.getUrl().replace("http://", "feed://");
            this.logo = si.getLogoUrl();
            this.site = si.link != null && si.link.length() > 5 ? si.link : null;
            this.description = si.getDescription();
            this.language = si.language;
            this.category = si.categories.toArray();
            try {
                final String typeString = si.getEpisodes().get(0).getMediaType().split("/")[0];
                this.type = typeString.substring(0, 1).toUpperCase(Locale.ENGLISH) +
                        typeString.substring(1, 5).toLowerCase(Locale.ENGLISH);
            } catch (Throwable th) {
                Log.w(TAG, "Cannot get media type from episodes");
            }
            this.votes = si.votes + (si.isFeatured() ? 10 : 0);
            this.explicit = si.isExplicit();
            this.path = title.replace(" ", "-");
            this.added = si.added == null ? new SimpleDateFormat("dd MMM yyyy", Locale.US).format(new Date()) : si.added;
        }
    }
}
