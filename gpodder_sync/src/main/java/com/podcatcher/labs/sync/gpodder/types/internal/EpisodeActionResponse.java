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
package com.podcatcher.labs.sync.gpodder.types.internal;

import com.podcatcher.labs.sync.gpodder.types.EpisodeAction;

import java.util.List;

/**
 * Internal POJO for a response to gpodder.net's /episodes
 */
public class EpisodeActionResponse extends TimestampResponse {

    private List<EpisodeAction> actions;

    public List<EpisodeAction> getActions() {
        return actions;
    }

    public void setActions(List<EpisodeAction> actions) {
        this.actions = actions;
    }
}
