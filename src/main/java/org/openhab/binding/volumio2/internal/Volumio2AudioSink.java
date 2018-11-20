/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.volumio2.internal;

/**
 * @author Patrick Sernetz - Initial Contribution
 */
public class Volumio2AudioSink {
    // implements AudioSink {
    // }

    // private static final Logger log = LoggerFactory.getLogger(Volumio2AudioSink.class);
    //
    // private static final HashSet<AudioFormat> SUPPORTED_AUDIO_FORMATS = new HashSet<>();
    // private static final HashSet<Class<? extends AudioStream>> SUPPORTED_AUDIO_STREAMS = new HashSet<>();
    //
    // static {
    // SUPPORTED_AUDIO_FORMATS.add(AudioFormat.WAV);
    // SUPPORTED_AUDIO_FORMATS.add(AudioFormat.MP3);
    //
    // SUPPORTED_AUDIO_STREAMS.add(URLAudioStream.class);
    // SUPPORTED_AUDIO_STREAMS.add(FixedLengthAudioStream.class);
    // }
    //
    // private AudioHTTPServer audioHTTPServer;
    // private Volumio2Handler handler;
    // private String callbackUrl;
    //
    // public Volumio2AudioSink(Volumio2Handler handler, AudioHTTPServer audioHTTPServer, String callbackUrl) {
    // this.audioHTTPServer = audioHTTPServer;
    // this.handler = handler;
    // this.callbackUrl = callbackUrl;
    // }
    //
    // @Override
    // public String getId() {
    // return handler.getThing().getUID().toString();
    // }
    //
    // @Override
    // public String getLabel(Locale locale) {
    // return handler.getThing().getLabel();
    // }
    //
    // @Override
    // public void process(AudioStream audioStream) throws UnsupportedAudioFormatException {
    //
    // if (audioStream instanceof URLAudioStream) {
    // URLAudioStream urlAudioStream = (URLAudioStream) audioStream;
    // handler.playURI(new StringType(urlAudioStream.getURL()));
    //
    // try {
    // audioStream.close();
    // } catch (IOException e) {
    //
    // }
    // } else if (audioStream instanceof FixedLengthAudioStream) {
    //
    // if (callbackUrl != null) {
    // String relativeUrl = audioHTTPServer.serve((FixedLengthAudioStream) audioStream, 10).toString();
    // String url = callbackUrl + relativeUrl;
    // AudioFormat format = audioStream.getFormat();
    //
    // if (AudioFormat.WAV.isCompatible(format)) {
    // handler.playNotificationSoundURI(new StringType(url + ".wav"));
    // } else if (AudioFormat.MP3.isCompatible(format)) {
    // handler.playNotificationSoundURI(new StringType(url + ".mp3"));
    // }
    // } else {
    // log.warn("We do nothave any callback url.");
    // }
    //
    // } else {
    // IOUtils.closeQuietly(audioStream);
    // }
    //
    // }
    //
    // public static HashSet<Class<? extends AudioStream>> getSupportedAudioStreams() {
    // return SUPPORTED_AUDIO_STREAMS;
    // }
    //
    // @Override
    // public Set<AudioFormat> getSupportedFormats() {
    // return SUPPORTED_AUDIO_FORMATS;
    // }
    //
    // @Override
    // public PercentType getVolume() throws IOException {
    // return new PercentType(100);
    // }
    //
    // @Override
    // public void setVolume(PercentType volume) throws IOException {
    // // TODO Auto-generated method stub
    // }

}
