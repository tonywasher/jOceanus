/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.tonywasher.joceanus.themis.parser.maven;

import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;

import java.util.ArrayList;
import java.util.List;

/**
 * Maven version parser.
 */
public class ThemisMavenVersionParser {
    /**
     * The components.
     */
    private final List<Object> theComponents;

    /**
     * The version.
     */
    private String theVersion;

    /**
     * The version length.
     */
    private int theVersLen;

    /**
     * The current version index.
     */
    private int theIndex;

    /**
     * Constructor.
     */
    public ThemisMavenVersionParser() {
        theComponents = new ArrayList<>();
    }

    /**
     * Parse the version.
     *
     * @param pVersion the version
     * @return the parsed version
     */
    public ThemisMavenVersion parseVersion(final String pVersion) {
        /* Initialise state */
        theComponents.clear();
        theVersion = pVersion;
        theVersLen = pVersion.length();
        theIndex = 0;

        /* While we have more data */
        while (theIndex < theVersLen) {
            /* Parse the components */
            final char myChar = theVersion.charAt(theIndex);
            if (Character.isDigit(myChar)) {
                theComponents.add(parseLong());
            } else if (isSeparator(myChar)) {
                theIndex++;
            } else {
                theComponents.add(parseString());
            }
        }

        /* Prune the components */
        pruneComponents();

        /* Return the parsed version */
        return new ThemisMavenVersion(theVersion, theComponents.toArray());
    }

    /**
     * Parse a long component.
     *
     * @return the component
     */
    private Long parseLong() {
        final StringBuilder myBuffer = new StringBuilder();
        while (theIndex < theVersLen) {
            /* Access character and break loop if non-numeric */
            final char myChar = theVersion.charAt(theIndex);
            if (!Character.isDigit(myChar)) {
                break;
            }
            myBuffer.append(myChar);
            theIndex++;
        }
        return Long.parseLong(myBuffer.toString());
    }

    /**
     * Parse a string component.
     *
     * @return the component
     */
    private String parseString() {
        final StringBuilder myBuffer = new StringBuilder();
        while (theIndex < theVersLen) {
            /* Access character and break loop if numeric or separator */
            final char myChar = theVersion.charAt(theIndex);
            if (Character.isDigit(myChar)
                    || isSeparator(myChar)) {
                break;
            }
            myBuffer.append(myChar);
            theIndex++;
        }
        return normalise(myBuffer.toString());
    }

    /**
     * Is the character a separator.
     *
     * @param pChar the character
     * @return true/false
     */
    private boolean isSeparator(final char pChar) {
        return switch (pChar) {
            case ThemisChar.PERIOD, ThemisChar.HYPHEN -> true;
            default -> false;
        };
    }

    /**
     * Normalise the string component.
     *
     * @param pValue the component
     * @return the normalised component
     */
    private String normalise(final String pValue) {
        return switch (pValue.toLowerCase()) {
            case "alpha" -> ThemisMavenConstants.ALPHA;
            case "beta" -> ThemisMavenConstants.BETA;
            case "milestone" -> ThemisMavenConstants.MILESTONE;
            case "final" -> ThemisMavenConstants.GA;
            case "cr" -> ThemisMavenConstants.RC;
            default -> pValue;
        };
    }

    /**
     * Prune components.
     */
    private void pruneComponents() {
        /* If we have components */
        if (!theComponents.isEmpty()) {
            /* Access last component */
            final Object myLast = theComponents.getLast();

            /* If the component is empty */
            if (myLast.equals(ThemisMavenConstants.ZERO)
                    || ThemisMavenConstants.GA.equalsIgnoreCase(myLast.toString())) {
                /* Remove and reprune */
                theComponents.removeLast();
                pruneComponents();
            }
        }
    }
}
