package com.serotonin.bacnet4j.test;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DefaultDeviceEventListener;
import com.serotonin.bacnet4j.service.confirmed.SubscribeCOVRequest;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class SimpleSubscriptionClient {
    public static void main(String[] args) throws Exception {
        LocalDevice localDevice = new LocalDevice(1234, "255.255.255.255");
        try {
            localDevice.initialize();
            localDevice.getEventHandler().addListener(new Listener());
            localDevice.sendBroadcast(2068, localDevice.getIAm());
            RemoteDevice d = localDevice.findRemoteDevice(
                    new Address(new byte[] { (byte) 192, (byte) 168, 0, 2 }, 2068), null, 1968);

            ObjectIdentifier oid = new ObjectIdentifier(ObjectType.binaryInput, 0);
            SubscribeCOVRequest req = new SubscribeCOVRequest(new UnsignedInteger(0), oid, new Boolean(true),
                    new UnsignedInteger(0));
            localDevice.send(d, req);

            Thread.sleep(22000);
        }
        finally {
            localDevice.terminate();
        }
    }

    static class Listener extends DefaultDeviceEventListener {
        @Override
        public void covNotificationReceived(UnsignedInteger subscriberProcessIdentifier, RemoteDevice initiatingDevice,
                ObjectIdentifier monitoredObjectIdentifier, UnsignedInteger timeRemaining,
                SequenceOf<PropertyValue> listOfValues) {
            System.out.println("Received COV notification: " + listOfValues);
        }
    }
}