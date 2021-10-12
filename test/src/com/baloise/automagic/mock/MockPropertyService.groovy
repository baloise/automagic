package com.baloise.automagic.mock

import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.properties.PropertyService
import org.yaml.snakeyaml.Yaml

class MockPropertyService implements PropertyService {

    Map<String, String> properties

    @Override
    String get(String key) {
        return properties['AUTOMAGIC_'+key]
    }

}
