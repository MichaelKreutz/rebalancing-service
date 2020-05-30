package de.kreutz.michael.rebalanceservice.csvimport;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {

    String findPathOfResource(final String resourceName) {
        return TestUtils.class.getResource(resourceName).getPath();
    }
}
