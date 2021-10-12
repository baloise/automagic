package com.baloise.automagic.properties
/*
 * The default PropertyStoreService stores properties in PropertyStoreService.yaml on the 'automagic' branch in git
 */
interface PropertyStoreService  extends PropertyService{
    String put(String key, String value)
}