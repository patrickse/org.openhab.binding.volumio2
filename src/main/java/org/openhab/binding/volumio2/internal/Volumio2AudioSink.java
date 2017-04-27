package org.openhab.binding.volumio2.internal;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.smarthome.core.audio.AudioFormat;
import org.eclipse.smarthome.core.audio.AudioHTTPServer;
import org.eclipse.smarthome.core.audio.AudioSink;
import org.eclipse.smarthome.core.audio.AudioStream;
import org.eclipse.smarthome.core.audio.URLAudioStream;
import org.eclipse.smarthome.core.audio.UnsupportedAudioFormatException;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.openhab.binding.volumio2.handler.Volumio2Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Volumio2AudioSink implements AudioSink {

    private static final Logger log = LoggerFactory.getLogger(Volumio2AudioSink.class);

    private static HashSet<AudioFormat> supportedFormats = new HashSet<>();

    static {
        supportedFormats.add(AudioFormat.WAV);
        supportedFormats.add(AudioFormat.MP3);
    }

    private AudioHTTPServer audioHTTPServer;
    private Volumio2Handler handler;
    private String callbackUrl;

    public Volumio2AudioSink(Volumio2Handler handler, AudioHTTPServer audioHTTPServer, String callbackUrl) {
        this.audioHTTPServer = audioHTTPServer;
        this.handler = handler;
        this.callbackUrl = callbackUrl;
    }

    @Override
    public String getId() {
        return handler.getThing().getUID().toString();
    }

    @Override
    public String getLabel(Locale locale) {
        return handler.getThing().getLabel();
    }

    @Override
    public void process(AudioStream audioStream) throws UnsupportedAudioFormatException {

        String url = null;

        if (audioStream instanceof URLAudioStream) {
            URLAudioStream urlAudioStream = (URLAudioStream) audioStream;
            url = urlAudioStream.getURL();
        } else {
            if (callbackUrl != null) {
                String relativeUrl = audioHTTPServer.serve(audioStream);
                url = callbackUrl + relativeUrl;
            } else {
                log.warn("We do nothave any callback url.");
            }
        }

        handler.getVolumio().playURI(url);

    }

    @Override
    public Set<AudioFormat> getSupportedFormats() {
        return supportedFormats;
    }

    @Override
    public PercentType getVolume() throws IOException {
        return new PercentType(100);
    }

    @Override
    public void setVolume(PercentType volume) throws IOException {
        // TODO Auto-generated method stub
    }

}
