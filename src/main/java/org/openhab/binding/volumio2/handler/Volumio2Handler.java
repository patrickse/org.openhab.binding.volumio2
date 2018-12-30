/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.volumio2.handler;

import static org.openhab.binding.volumio2.Volumio2BindingConstants.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.NextPreviousType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.PlayPauseType;
import org.eclipse.smarthome.core.library.types.RewindFastforwardType;
import org.eclipse.smarthome.core.library.types.StopMoveType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.UnDefType;
import org.json.JSONException;
import org.json.JSONObject;
import org.openhab.binding.volumio2.internal.Volumio2Service;
import org.openhab.binding.volumio2.internal.mapping.Volumio2Data;
import org.openhab.binding.volumio2.internal.mapping.Volumio2Events;
import org.openhab.binding.volumio2.internal.mapping.Volumio2ServiceTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * The {@link Volumio2Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Patrick Sernetz - Initial Contribution
 */
public class Volumio2Handler extends BaseThingHandler {

    private static Logger log = LoggerFactory.getLogger(Volumio2Handler.class);
    private Volumio2Service volumio;
    private Volumio2Data state = new Volumio2Data();

    public Volumio2Service getVolumio() {
        return volumio;
    }

    public Volumio2Handler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        log.debug("channelUID: {}", channelUID);

        if (volumio == null) {
            log.debug("Ignoring command " + channelUID.getId() + " = " + command + " because device is offline.");
            if (ThingStatus.ONLINE.equals(getThing().getStatus())) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "device is offline");
            }
            return;
        }

        try {
            switch (channelUID.getId()) {
                case CHANNEL_PLAYER:
                    handlePlaybackCommands(command);
                    break;
                case CHANNEL_VOLUME:
                    handleVolumeCommand(command);
                    break;

                case CHANNEL_ARTIST:
                    break;
                case CHANNEL_ALBUM:
                    break;
                case CHANNEL_TRACK_TYPE:
                    break;
                /**
                 * case CHANNEL_COVER_ART:
                 * if (command instanceof RefreshType) {
                 * volumio.getState();
                 * }
                 * break;
                 **/
                case CHANNEL_TITLE:
                    break;

                case CHANNEL_PLAY_RADIO_STREAM:
                    if (command instanceof StringType) {
                        final String uri = ((StringType) command).toFullString();
                        volumio.replacePlay(uri, "Radio", Volumio2ServiceTypes.WEBRADIO);
                    }
                    break;

                case CHANNEL_PLAY_URI:
                    if (command instanceof StringType) {
                        final String uri = ((StringType) command).toFullString();
                        volumio.replacePlay(uri, "URI", Volumio2ServiceTypes.WEBRADIO);
                    }
                    break;

                case CHANNEL_PLAY_FILE:
                    if (command instanceof StringType) {
                        final String uri = ((StringType) command).toFullString();
                        volumio.replacePlay(uri, "", Volumio2ServiceTypes.MPD);
                    }
                    break;

                case CHANNEL_PLAY_PLAYLIST:
                    if (command instanceof StringType) {
                        final String playlistName = ((StringType) command).toFullString();
                        volumio.playPlaylist(playlistName);
                    }
                    break;
                case CHANNEL_CLEAR_QUEUE:
                    if (command instanceof OnOffType) {
                        if (command == OnOffType.ON) {
                            volumio.clearQueue();
                            // Make it feel like a toggle button ...
                            updateState(channelUID, OnOffType.OFF);
                        }
                    }
                    break;
                case CHANNEL_PLAY_RANDOM:
                    if (command instanceof OnOffType) {
                        boolean enableRandom = (command == OnOffType.ON) ? true : false;
                        volumio.setRandom(enableRandom);
                    }
                    break;
                case CHANNEL_PLAY_REPEAT:
                    if (command instanceof OnOffType) {
                        boolean enableRepeat = (command == OnOffType.ON) ? true : false;
                        volumio.setRepeat(enableRepeat);
                    }
                    break;
                case "REFRESH":
                    log.debug("Called Refresh");
                    volumio.getState();
                    break;
                case CHANNEL_SYSTEMCOMMAND:
                    if (command instanceof StringType) {
                        sendSystemCommand(command);
                        updateState(CHANNEL_SYSTEMCOMMAND, UnDefType.UNDEF);
                    } else if (RefreshType.REFRESH == command) {
                        updateState(CHANNEL_SYSTEMCOMMAND, UnDefType.UNDEF);
                    }
                    break;
                default:
                    log.error("Unknown channel: {}", channelUID.getId());
            }

        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }

    }

    private void sendSystemCommand(Command command) {
        if (command instanceof StringType) {
            volumio.sendSystemCommand(command.toString());
            updateState(CHANNEL_SYSTEMCOMMAND, UnDefType.UNDEF);
        } else if (command.equals(RefreshType.REFRESH)) {
            updateState(CHANNEL_SYSTEMCOMMAND, UnDefType.UNDEF);
        }
    }

    /**
     * Set all channel of thing to UNDEF during connection.
     */
    private void clearChannels() {
        for (Channel channel : getThing().getChannels()) {
            updateState(channel.getUID(), UnDefType.UNDEF);
        }
    }

    private void handleVolumeCommand(Command command) {

        if (command instanceof PercentType) {
            volumio.setVolume((PercentType) command);
        } else if (command instanceof RefreshType) {
            volumio.getState();
        } else {
            log.error("Command is not handled");
        }

    }

    private void handlePlaybackCommands(Command command) {

        if (command instanceof PlayPauseType) {

            PlayPauseType playPauseCmd = (PlayPauseType) command;

            switch (playPauseCmd) {
                case PLAY:
                    volumio.play();
                    break;
                case PAUSE:
                    volumio.pause();
                    break;
            }
        } else if (command instanceof StopMoveType) {

            if (command instanceof StopMoveType) {
                StopMoveType stopMoveType = (StopMoveType) command;

                switch (stopMoveType) {
                    case STOP:
                        volumio.stop();
                        break;
                    default:
                        break;
                }
            }
        } else if (command instanceof NextPreviousType) {

            NextPreviousType nextPreviousType = (NextPreviousType) command;

            switch (nextPreviousType) {
                case PREVIOUS:
                    volumio.previous();
                    break;
                case NEXT:
                    volumio.next();
                    break;
            }

        } else if (command instanceof RewindFastforwardType) {

            RewindFastforwardType fastforwardType = (RewindFastforwardType) command;

            switch (fastforwardType) {
                case FASTFORWARD:
                    log.warn("Not implemented yet");
                    break;
                case REWIND:
                    log.warn("Not implemented yet");
                    break;
            }

        } else if (command instanceof RefreshType) {
            volumio.getState();
        } else {
            log.error("Command is not handled: {}", command);
        }

    }

    /**
     * Bind default listeners to volumio session.
     * - EVENT_CONNECT - Connection to volumio was established
     * - EVENT_DISCONNECT - Connection was disconnected
     * - PUSH.STATE -
     */
    private void bindDefaultListener() {

        volumio.on(Socket.EVENT_CONNECT, connectListener());
        volumio.on(Socket.EVENT_DISCONNECT, disconnectListener());
        volumio.on(Volumio2Events.PUSH_STATE, pushStateListener());

    }

    /**
     * Read the configuration and connect to volumio device. The Volumio impl. is
     * async so it should not block the process in any way.
     */
    @Override
    public void initialize() {

        String hostname = (String) getThing().getConfiguration().get(CONFIG_PROPERTY_HOSTNAME);
        int port = ((BigDecimal) getThing().getConfiguration().get(CONFIG_PROPERTY_PORT)).intValueExact();
        String protocol = (String) getThing().getConfiguration().get(CONFIG_PROPERTY_PROTOCOL);
        int timeout = ((BigDecimal) getThing().getConfiguration().get(CONFIG_PROPERTY_TIMEOUT)).intValueExact();

        if (hostname == null) {

            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Configuration incomplete, missing hostname");

        } else if (protocol == null) {

            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Configuration incomplete, missing protocol");

        } else {

            log.debug("Trying to connect to Volumio2 on {}://{}:{}", protocol, hostname, port);
            try {
                volumio = new Volumio2Service(protocol, hostname, port, timeout);

                clearChannels();
                bindDefaultListener();
                updateStatus(ThingStatus.OFFLINE);
                volumio.connect();

            } catch (Exception e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            }

        }

    }

    @Override
    public void dispose() {
        if (volumio != null) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    if (volumio.isConnected()) {
                        log.warn("Timeout during disconnect event");
                    } else {
                        volumio.close();
                    }
                    clearChannels();
                }
            }, 30, TimeUnit.SECONDS);

            volumio.disconnect();

        }
    }

    public void playURI(StringType url) {
        log.debug("Play uri sound: {}", url.toFullString());
        this.volumio.playURI(url.toFullString());
    }

    public void playNotificationSoundURI(StringType url) {
        log.debug("Play notification sound: {}", url.toFullString());
    }

    /** Listener **/

    /**
     * As soon as the Connect Listener is executed
     * the ThingStatus is set to ONLINE.
     *
     * @return
     */
    private Emitter.Listener connectListener() {
        return new Emitter.Listener() {

            @Override
            public void call(Object... arg0) {
                updateStatus(ThingStatus.ONLINE);
            }

        };
    }

    /**
     * As soon as the Disconnect Listener is executed
     * the ThingStatus is set to OFFLINE.
     *
     * @return
     */
    private Emitter.Listener disconnectListener() {
        return new Emitter.Listener() {

            @Override
            public void call(Object... arg0) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
            }

        };
    }

    /**
     * On received a pushState Event, the ThingChannels are
     * updated if there is a change and they are linked.
     *
     * @return
     */
    private Emitter.Listener pushStateListener() {
        return new Emitter.Listener() {

            @Override
            public void call(Object... data) {

                try {

                    JSONObject jsonObject = (JSONObject) data[0];
                    log.debug(jsonObject.toString());
                    state.update(jsonObject);

                    if (isLinked(CHANNEL_TITLE) && state.isTitleDirty()) {
                        updateState(CHANNEL_TITLE, state.getTitle());
                    }

                    if (isLinked(CHANNEL_ARTIST) && state.isArtistDirty()) {
                        updateState(CHANNEL_ARTIST, state.getArtist());
                    }

                    if (isLinked(CHANNEL_ALBUM) && state.isAlbumDirty()) {
                        updateState(CHANNEL_ALBUM, state.getAlbum());
                    }

                    if (isLinked(CHANNEL_VOLUME) && state.isVolumeDirty()) {
                        updateState(CHANNEL_VOLUME, state.getVolume());
                    }

                    if (isLinked(CHANNEL_PLAYER) && state.isStateDirty()) {
                        updateState(CHANNEL_PLAYER, state.getState());
                    }

                    if (isLinked(CHANNEL_TRACK_TYPE) && state.isTrackTypeDirty()) {
                        updateState(CHANNEL_TRACK_TYPE, state.getTrackType());
                    }

                    if (isLinked(CHANNEL_PLAY_RANDOM) && state.isRandomDirty()) {
                        updateState(CHANNEL_PLAY_RANDOM, state.getRandom());
                    }

                    if (isLinked(CHANNEL_PLAY_REPEAT) && state.isRepeatDirty()) {
                        updateState(CHANNEL_PLAY_REPEAT, state.getRepeat());
                    }

                    /**
                     * if (isLinked(CHANNEL_COVER_ART) && state.isCoverArtDirty()) {
                     * updateState(CHANNEL_COVER_ART, state.getCoverArt());
                     * }
                     */

                } catch (JSONException e) {
                    log.error("Could not refresh channel", e);

                }
            }
        };
    }

}
