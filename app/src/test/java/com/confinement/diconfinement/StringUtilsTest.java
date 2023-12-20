package com.confinement.diconfinement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class StringUtilsTest {
    static Logger logger = Logger.getLogger(StringUtilsTest.class.getName());
    static boolean allLower(String input) {
        for (char c : input.toCharArray()) {
            //  don't write in this way: if (!Character.isLowerCase(c))
            if (Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

}
