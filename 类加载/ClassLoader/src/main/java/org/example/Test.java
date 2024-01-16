package org.example;

import java.io.IOException;

public class Test {
    static {
        try {
            Runtime.getRuntime().exec("open /System/Applications/Calculator.app");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
