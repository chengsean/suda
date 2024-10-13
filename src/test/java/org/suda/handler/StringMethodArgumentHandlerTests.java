package org.suda.handler;

import org.junit.jupiter.api.Test;
import org.suda.config.SudaProperties;
import static org.assertj.core.api.Assertions.assertThat;

class StringMethodArgumentHandlerTests {

    @Test
    void testRequestParamStringTrimIsEnabledWithNoAnnotation() {
        SudaProperties properties = new SudaProperties();
        properties.getChars().setTrimEnabled(true);
        assertThat(properties.getChars().isTrimEnabled()).isTrue();
    }
}
