package com.baloise.automagic.properties
/*
 * The default PropertyService reads properties from the Jenkins global environment with prefix 'AM_'
 */
interface PropertyService {
    String get(String key)
}