package com.baloise.automagic.properties.internal

import org.junit.Test

import static org.junit.Assert.*

class PropertyStoreImplTest {

    @Test
    void toFileNameHttp() {
        assertEquals('https-github-com-baloise-automagic-git' ,PropertyStoreImpl.toFileName('https://github.com/baloise/automagic.git'))
    }
    @Test
    void toFileNameNull() {
        assertEquals('null' ,PropertyStoreImpl.toFileName(null))
    }
    @Test
    void toFileNameFile() {
        assertEquals('file-C-Users-Public' ,PropertyStoreImpl.toFileName('file:///C:/Users/Public/'))
    }

    @Test
    void toFileNameNoProtocol() {
        assertEquals('C-Users-Public' ,PropertyStoreImpl.toFileName('/C:/Users/Public/'))
    }
    @Test
    void toFileNamePlain() {
        assertEquals('HalloWelt' ,PropertyStoreImpl.toFileName('HalloWelt'))
    }
}