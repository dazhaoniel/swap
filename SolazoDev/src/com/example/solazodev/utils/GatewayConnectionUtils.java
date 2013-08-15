package com.example.solazodev.utils;

/**
 * Created by Daniel Zhao on 8/11/13.
 */
public class GatewayConnectionUtils {

    private static final String guessURL = "http://danielatwork.com/solazo/scripts/guess.php";

    private static final String submitURL = "http://solazo.org/scripts/store_data.php";

    public static String getGuessURL() {
        return guessURL;
    }

    public static String getSubmitURL() {
        return submitURL;
    }
}
