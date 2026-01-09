/*******************************************************************************
 * Oceanus: Java Utilities
 * Copyright 2012-2026 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.joceanus.oceanus.base;

/**
 * Oceanus Locale Defaults.
 */
public enum OceanusSystem {
    /**
     * Windows.
     */
    WINDOWS("win"),

    /**
     * Linux.
     */
    LINUX("linux"),

    /**
     * Mac.
     */
    MAC("mac");

    /**
     * The classifier.
     */
    private final String theClassifier;

    /**
     * Constructor.
     * @param pClassifier the classifier
     */
    OceanusSystem(final String pClassifier) {
        theClassifier = pClassifier;
    }

    /**
     * Obtain the classifier.
     * @return the classifier
     */
    public String getClassifier() {
        return theClassifier;
    }

    /**
     * Determine system.
     * @return the system.
     */
    public static OceanusSystem determineSystem() {
        final String myOS = System.getProperty("os.name");
        if (myOS.startsWith("Windows")) {
            return WINDOWS;
        } else if (myOS.startsWith("Mac")) {
            return MAC;
        } else if (myOS.equals("Linux")) {
            return LINUX;
        }
        throw new IllegalStateException();
    }
}
