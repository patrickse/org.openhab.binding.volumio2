---
layout: documentation
---

{% include base.html %}

# Volumio2 Binding

[![Build Status](https://travis-ci.org/patrickse/org.openhab.binding.volumio2.svg?branch=master)](https://travis-ci.org/patrickse/org.openhab.binding.volumio2)

This binding integrates the [Volumio - Audiophile Music Player Project](https://volumio.org). The binding
is inspired by the [Sonos-Binding](https://github.com/eclipse/smarthome/tree/master/extensions/binding/org.eclipse.smarthome.binding.sonos) from the [Eclipse Smarthome Project](http://www.eclipse.org/smarthome/)

## Supported Things

Any device running Volumio2 is supported. Volumio2 can be used on Raspberry Pi, x86/x64, ... for more information have a look at the [Volumio homepage](https://volumio.org/get-started/).

## Discovery

Volumio2 devices are discovered through MDNS in the local network. All devices will be put in the Inbox.

## Binding Configuration

The binding has the following configuration options, which can be set for "binding:volumio2":

| Parameter   | Name         | Description  | Required |
|-------------|--------------|--------------|------------ |
| callbackUrl | Callback URL | URL to use for playing notification sounds, e.g. http://192.168.0.2:8080 | no |

## Thing Configuration

The Volumio2 Player Thing requires the hostname (or ip adress) as a configuration value in order for the binding to know how to access it.

```
Thing volumio2:player:livingRoom "Volumio@WLivingRoom"  [ hostname="192.168.64.5" ]
Thing volumio2:player:kitchen    "Volumio@Kitchen"      [ hostname="192.168.64.6" ]
```

## Channels

The devices support the following channels:

| Channel Type ID | Item Type              | Description  | Thing types      |              |   |
|-----------------|------------------------|--------------|----------------- |------------- |---|
| title           | String                 |              |                  |              |   |
| artist          | String                 |              |                  |              |   |
| album           | String                 |              |                  |              |   |
| volume          | Number                 |              |                  |              |   |
| player          | Player                 |              |                  |              |   |
| trackType       | String                 |              |                  |              |   |
| playRadioStream | String                 |              |                  |              |   |
| playPlaylist    | String                 |              |                  |              |   |
| clearQueue      | Switch                 |              |                  |              |   |
| random          | Switch                 |              |                  |              |   |
| repeat          | Switch                 |              |                  |              |   |



## Audio Support

Audio Support is still experimental in this binding.

## Full Example
