package com.myorg;

import software.amazon.awscdk.core.App;

import java.util.Arrays;

public class AwsCdk13512App {
    public static void main(final String[] args) {
        App app = new App();

        new AwsCdk13512Stack(app, "AwsCdk13512Stack");

        app.synth();
    }
}
