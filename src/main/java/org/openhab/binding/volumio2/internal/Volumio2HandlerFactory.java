/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volumio2.internal;

import static org.openhab.binding.volumio2.Volumio2BindingConstants.THING_TYPE_VOLUMIO2;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.smarthome.core.audio.AudioHTTPServer;
import org.eclipse.smarthome.core.audio.AudioSink;
import org.eclipse.smarthome.core.net.HttpServiceUtil;
import org.eclipse.smarthome.core.net.NetUtil;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.volumio2.handler.Volumio2Handler;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link Volumio2HandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Patrick Sernetz - Initial contribution
 */
public class Volumio2HandlerFactory extends BaseThingHandlerFactory {

    private static final Logger log = LoggerFactory.getLogger(Volumio2HandlerFactory.class);

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_VOLUMIO2);

    private Map<String, ServiceRegistration<AudioSink>> audioSinkRegistrations = new ConcurrentHashMap<>();

    private AudioHTTPServer audioHTTPServer;

    private String callbackUrl;

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        callbackUrl = createCallbackUrl();
        if (thingTypeUID.equals(THING_TYPE_VOLUMIO2)) {

            // Initialize Handler
            Volumio2Handler handler = new Volumio2Handler(thing);

            if (callbackUrl != null) {
                Volumio2AudioSink audioSink = new Volumio2AudioSink(handler, audioHTTPServer, callbackUrl);
                @SuppressWarnings("unchecked")
                ServiceRegistration<AudioSink> reg = (ServiceRegistration<AudioSink>) bundleContext
                        .registerService(AudioSink.class.getName(), audioSink, new Hashtable<String, Object>());
                audioSinkRegistrations.put(thing.getUID().toString(), reg);
            }

            return handler;
        }

        return null;
    }

    @Override
    public void unregisterHandler(Thing thing) {
        super.unregisterHandler(thing);
        ServiceRegistration<AudioSink> reg = audioSinkRegistrations.get(thing.getUID().toString());
        if (reg != null) {
            reg.unregister();
        }
    }

    private String createCallbackUrl() {
        if (callbackUrl != null) {
            return callbackUrl;
        } else {
            final String ipAddress = NetUtil.getLocalIpv4HostAddress();
            if (ipAddress == null) {
                log.warn("No network interface could be found.");
                return null;
            }

            // we do not use SSL as it can cause certificate validation issues.
            final int port = HttpServiceUtil.getHttpServicePort(bundleContext);
            if (port == -1) {
                log.warn("Cannot find port of the http service.");
                return null;
            }

            return "http://" + ipAddress + ":" + port;
        }
    }

    protected void setAudioHTTPServer(AudioHTTPServer audioHTTPServer) {
        this.audioHTTPServer = audioHTTPServer;
    }

    protected void unsetAudioHTTPServer(AudioHTTPServer audioHTTPServer) {
        this.audioHTTPServer = null;
    }
}
