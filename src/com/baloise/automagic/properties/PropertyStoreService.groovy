package com.baloise.automagic.properties
/*
 * The default PropertyStoreService stores properties in PropertyStoreService.yaml on the 'automagic' branch in git
 */
interface PropertyStoreService  extends PropertyService{
    PropertyStoreService put(String key, String value)
    PropertyStoreService put(Map<String,String> key2value)
}