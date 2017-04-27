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

public class Volumio2DiscoveryParticipant implements MDNSDiscoveryParticipant {

    private static final Logger log = LoggerFactory.getLogger(Volumio2DiscoveryParticipant.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return Collections.singleton(THING_TYPE_VOLUMIO2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceType() {
        return DISCOVERY_SERVICE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DiscoveryResult createResult(ServiceInfo service) {

        String volumioName = service.getPropertyString(DISCOVERY_NAME_PROPERTY);
        String uuid = service.getPropertyString(DISCOVERY_UUID_PROPERTY);
        HashMap<String, Object> properties = new HashMap<String, Object>();
        DiscoveryResult discoveryResult = null;
        ThingUID thingUID = null;

        if (uuid != null) {
            thingUID = getThingUID(service);
        }

        if (thingUID != null) {
            properties.put("hostname", service.getServer());
            discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties).withLabel(volumioName)
                    .build();
        }

        return discoveryResult;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThingUID getThingUID(ServiceInfo service) {
        log.debug("return new ThingUID({}, {});", THING_TYPE_VOLUMIO2, service.getPropertyString("UUID"));
        return new ThingUID(THING_TYPE_VOLUMIO2, service.getPropertyString("UUID"));
    }

}
