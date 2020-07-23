package net.htlgrieskirchen.aud2.map.generated;

import net.htlgrieskirchen.aud2.map.MyMap;
import net.htlgrieskirchen.aud2.map.generator.AutoTest;

import java.util.Map;
import java.util.TreeMap;

@AutoTest(
        testInterface = Map.class,
        shouldImplementation = TreeMap.class,
        testImplementation = MyMap.class,
        genericArguments = {String.class, String.class}
)
public class MapAutoTest {}
