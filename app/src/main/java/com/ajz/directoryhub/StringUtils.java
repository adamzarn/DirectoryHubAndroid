package com.ajz.directoryhub;

/**
 * Created by adamzarn on 10/26/17.
 */

public class StringUtils {

    public static Boolean isMissing(String string) {
        if (string != null && !string.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

}
