/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.volumio2.internal.mapping;

import static org.openhab.binding.volumio2.Volumio2BindingConstants.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.PlayPauseType;
import org.eclipse.smarthome.core.library.types.RawType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Patrick Sernetz - Initial Contribution
 */
public class Volumio2Data {

    private static final Logger log = LoggerFactory.getLogger(Volumio2Data.class);

    private String title = "";
    private boolean titleDirty;

    private String album = "";
    private boolean albumDirty;

    private String artist = "";
    private boolean artistDirty;

    private int volume = 0;
    private boolean volumeDirty;

    private String state = "";
    private boolean stateDirty;

    private String trackType = "";
    private boolean trackTypeDirty;

    private String position = "";
    private boolean positionDirty;

    private byte[] coverArt;
    private String coverArtUrl;
    private boolean coverArtDirty;

    private boolean repeat = false;
    private boolean repeatDirty;

    private boolean random = false;
    private boolean randomDirty;

    public void update(JSONObject jsonObject) throws JSONException {

        if (jsonObject.has(CHANNEL_TITLE)) {
            setTitle(jsonObject.getString(CHANNEL_TITLE));
        } else {
            setTitle("");
        }

        if (jsonObject.has(CHANNEL_ALBUM)) {
            setAlbum(jsonObject.getString(CHANNEL_ALBUM));
        } else {
            setAlbum("");
        }

        if (jsonObject.has(CHANNEL_VOLUME)) {
            setVolume(jsonObject.getInt(CHANNEL_VOLUME));
        } else {
            setVolume(0);
        }

        if (jsonObject.has(CHANNEL_ARTIST)) {
            setArtist(jsonObject.getString(CHANNEL_ARTIST));
        } else {
            setArtist("");
        }

        /* Special */
        if (jsonObject.has("status")) {
            setState(jsonObject.getString("status"));
        } else {
            setState("pause");
        }

        if (jsonObject.has(CHANNEL_TRACK_TYPE)) {
            setTrackType(jsonObject.getString(CHANNEL_TRACK_TYPE));
        } else {
            setTrackType("");
        }

        if (jsonObject.has(CHANNEL_COVER_ART)) {
            setCoverArt(jsonObject.getString(CHANNEL_COVER_ART));
        } else {
            setCoverArt(null);
        }

        if (jsonObject.has(CHANNEL_PLAY_RANDOM) && !jsonObject.isNull(CHANNEL_PLAY_RANDOM)) {
            setRandom(jsonObject.getBoolean(CHANNEL_PLAY_RANDOM));
        } else {
            setRandom(false);
        }

        if (jsonObject.has(CHANNEL_PLAY_REPEAT) && !jsonObject.isNull(CHANNEL_PLAY_REPEAT)) {
            setRepeat(jsonObject.getBoolean(CHANNEL_PLAY_REPEAT));
        } else {
            setRepeat(false);
        }

    }

    public StringType getTitle() {
        return new StringType(title);
    }

    public void setTitle(String title) {
        if (!title.equals(this.title)) {
            this.title = title;
            this.titleDirty = true;
        } else {
            this.titleDirty = false;
        }
    }

    public StringType getAlbum() {
        return new StringType(album);
    }

    public void setAlbum(String album) {

        if (album.equals("null")) {
            album = "";
        }

        if (!album.equals(this.album)) {
            this.album = album;
            this.albumDirty = true;
        } else {
            this.albumDirty = false;
        }
    }

    public StringType getArtist() {
        return new StringType(artist);
    }

    public void setArtist(String artist) {

        if (artist.equals("null")) {
            artist = "";
        }

        if (!artist.equals(this.artist)) {
            this.artist = artist;
            this.artistDirty = true;
        } else {
            this.artistDirty = false;
        }
    }

    public PercentType getVolume() {
        return new PercentType(volume);
    }

    public void setVolume(int volume) {
        if (volume != this.volume) {
            this.volume = volume;
            this.volumeDirty = true;
        } else {
            this.volumeDirty = false;
        }
    }

    public void setState(String state) {
        if (state != this.state) {
            this.state = state;
            this.stateDirty = true;
        } else {
            this.stateDirty = false;
        }
    }

    public PlayPauseType getState() {

        PlayPauseType playPauseStatus = null;

        switch (state) {
            case "play":
                playPauseStatus = PlayPauseType.PLAY;
                break;
            case "pause":
                playPauseStatus = PlayPauseType.PAUSE;
                break;
            default:
                playPauseStatus = PlayPauseType.PAUSE;
        }

        return playPauseStatus;
    }

    public void setTrackType(String trackType) {
        if (trackType != this.trackType) {
            this.trackType = trackType;
            this.trackTypeDirty = true;
        } else {
            this.trackTypeDirty = false;
        }
    }

    public StringType getTrackType() {
        return new StringType(trackType);
    }

    public void setPosition(String position) {
        if (position != this.position) {
            this.position = position;
            this.positionDirty = true;
        } else {
            this.positionDirty = false;
        }
    }

    public void setCoverArt(String coverArtUrl) {

        if (coverArtUrl != this.coverArtUrl) {
            // TODO: Only handle images with complete uri atm.
            if (!coverArtUrl.startsWith("http")) {
                return;
            }

            try {
                URL url = new URL(coverArtUrl);
                URLConnection connection = url.openConnection();
                coverArt = IOUtils.toByteArray(connection.getInputStream());

            } catch (IOException ioe) {
                coverArt = null;
            }
            this.coverArtDirty = true;
        } else {
            this.coverArtDirty = false;
        }

    }

    public RawType getCoverArt() {
        if (coverArt == null) {
            return new RawType();
        } else {
            return new RawType(coverArt);
        }

    }

    public OnOffType getRandom() {
        return (random == true) ? OnOffType.ON : OnOffType.OFF;
    }

    public void setRandom(boolean val) {
        if (val != this.random) {
            this.random = val;
            this.randomDirty = true;
        } else {
            this.randomDirty = false;
        }
    }

    public OnOffType getRepeat() {
        return (repeat == true) ? OnOffType.ON : OnOffType.OFF;
    }

    public void setRepeat(boolean val) {
        if (val != this.repeat) {
            this.repeat = val;
            this.repeatDirty = true;
        } else {
            this.repeatDirty = false;
        }
    }

    public StringType getPosition() {
        return new StringType(position);
    }

    public boolean isPositionDirty() {
        return positionDirty;
    }

    public boolean isStateDirty() {
        return stateDirty;
    }

    public boolean isTitleDirty() {
        return titleDirty;
    }

    public boolean isAlbumDirty() {
        return albumDirty;
    }

    public boolean isArtistDirty() {
        return artistDirty;
    }

    public boolean isVolumeDirty() {
        return volumeDirty;
    }

    public boolean isTrackTypeDirty() {
        return trackTypeDirty;
    }

    public boolean isCoverArtDirty() {
        return coverArtDirty;
    }

    public boolean isRandomDirty() {
        return randomDirty;
    }

    public boolean isRepeatDirty() {
        return repeatDirty;
    }

}
