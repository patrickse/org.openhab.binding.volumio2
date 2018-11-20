/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.volumio2.internal.discovery;

import static org.openhab.binding.volumio2.Volumio2BindingConstants.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.jmdns.ServiceInfo;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.io.transport.mdns.discovery.MDNSDiscoveryParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Patrick Sernetz - Initial Contribution
 */
public class Volumio2DiscoveryParticipant implements MDNSDiscoveryParticipant {

    private static final Logger log = LoggerFactory.getLogger(Volumio2DiscoveryParticipant.class);

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return Collections.singleton(THING_TYPE_VOLUMIO2);
    }

    @Override
    public String getServiceType() {
        return DISCOVERY_SERVICE_TYPE;
    }

    @Override
    public DiscoveryResult createResult(ServiceInfo service) {

        String volumioName = service.getPropertyString(DISCOVERY_NAME_PROPERTY);
        String uuid = service.getPropertyString(DISCOVERY_UUID_PROPERTY);
        HashMap<String, Object> properties = new HashMap<String, Object>();
        DiscoveryResult discoveryResult = null;
        ThingUID thingUID = null;

        log.debug("Service Device: {}", service);

        thingUID = getThingUID(service);

        log.debug("Thing UID: {}", thingUID);

        if (thingUID != null) {
            properties.put("hostname", service.getServer());
            properties.put("port", service.getPort());
            properties.put("protocol", "http");
            discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties).withLabel(volumioName)
                    .build();

            log.debug("DiscoveryResult: {}", discoveryResult);
        }

        return discoveryResult;

    }

    @Override
    public ThingUID getThingUID(ServiceInfo service) {
        Collections.list(service.getPropertyNames()).forEach(s -> log.debug("PropertyName: {}", s));

        String volumioName = service.getPropertyString("volumioName");
        if (volumioName == null) {
            return null;
        }

        String uuid = service.getPropertyString("UUID");
        if (uuid == null) {
            return null;
        }

        String uuidAndServername = String.format("%s-%s", uuid, volumioName);
        log.debug("return new ThingUID({}, {});", THING_TYPE_VOLUMIO2, uuidAndServername);
        return new ThingUID(THING_TYPE_VOLUMIO2, uuidAndServername);
    }

}
