package com.confinement.diconfinement;

import static org.junit.Assert.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class DisplayUtilsTest {

    @Test
    public void trimTrailingWhitespace() {
        String trimTest = "Voiture ";
        assertFalse(StringUtils.containsWhitespace(DisplayUtils.trimTrailingWhitespace(trimTest)));
    }
}