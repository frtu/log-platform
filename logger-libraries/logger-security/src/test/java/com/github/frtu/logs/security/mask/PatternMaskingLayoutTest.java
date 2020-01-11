package com.github.frtu.logs.security.mask;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternMaskingLayoutTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatternMaskingLayoutTest.class);

    @Test
    public void doLayout() {
        String unsecuredLog = "{ \"user_id\" : \"1234\", \"password\" : \"pass\", \"ssn\" : \"3310104322\", "
                + "\"favourite_team\" : \"Juventus\", "
                + "\"address\" : "
                + "\"Wiejska 4, Warszawa\", \"additional_info_1\" : \"192.168.1.1\", \"additional_info_2\" : "
                + "\"bianconeri36@gmail.com\" }";
        LOGGER.info(unsecuredLog);
    }
}
