package com.baloise.automagic.mock;

import org.junit.Test;

import static org.junit.Assert.*;

public class MockConfigurationTest {

    @Test
    public void deepMergeMapsEmpty() {
        assertEquals("[:]", ""+MockConfiguration.deepMergeMaps([:],[:]))
    }

    @Test
    public void deepMergeMapsLeftEmpty() {
        assertEquals("[props:[a:A, b:B], sec:[GIT:[u:user, p:pass]]]", "" + MockConfiguration.deepMergeMaps(
                [
                        props: [a: 'A', b: 'B'],
                        sec  : [GIT: [u: 'user', p: 'pass']]
                ],
                [:]
        ))
    }

    @Test
    public void deepMergeMapsLeft() {
        assertEquals("[props:[a:a, b:B, c:c], sec:[GIT:[u:user, p:***], WIN:[u:wsr, p:---]], remaining:test, added:test]", ""+MockConfiguration.deepMergeMaps(
                [
                        props: [a : 'A' , b : 'B'],
                        sec: [GIT : [u : 'user' , p : 'pass']],
                        remaining : 'test'
                ],
                [
                        props: [a : 'a' , c : 'c'],
                        sec: [
                                GIT : [p : '***'],
                                WIN : [u : 'wsr' , p : '---']
                        ],
                        added : 'test'
                ]
        ))
    }
}