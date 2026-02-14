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

package io.github.tonywasher.joceanus.themis.xanalysis.parser.proj;

import java.util.Objects;

/**
 * Comparable version.
 */
public final class ThemisXAnalysisMavenVersion
        implements Comparable<ThemisXAnalysisMavenVersion> {
    /**
     * Version.
     */
    private final String theVersion;

    /**
     * Components.
     */
    private final int[] theComponents;

    /**
     * Modifier.
     */
    private final String theModifier;

    /**
     * Validity.
     */
    private final boolean isValid;

    /**
     * Constructor.
     *
     * @param pVersion the version string
     */
    private ThemisXAnalysisMavenVersion(final String pVersion) {
        /* Save the version */
        theVersion = pVersion;

        /* Split out the modifier */
        String myVersion = pVersion;
        boolean valid = true;
        final String[] myMod = pVersion.split("-");
        if (myMod.length == 2) {
            myVersion = myMod[0];
            theModifier = myMod[1];
        } else {
            theModifier = null;
            valid = myMod.length == 1;
        }

        /* If we are valid */
        if (valid) {
            /* Split out the components */
            final String[] myVers = myVersion.split("\\.");
            theComponents = new int[myVers.length];

            /* Protect against exceptions */
            try {
                for (int i = 0; i < myVers.length; i++) {
                    theComponents[i] = Integer.parseInt(myVers[i]);
                }
            } catch (final NumberFormatException e) {
                valid = false;
            }
        } else {
            theComponents = null;
        }

        /* Set validity flag */
        isValid = valid;
    }

    /**
     * Obtain the version.
     *
     * @return the version
     */
    public String getVersion() {
        return theVersion;
    }

    /**
     * Parse a version string.
     *
     * @param pVersion the version
     * @return the parsed version
     */
    public static ThemisXAnalysisMavenVersion parseVersion(final String pVersion) {
        final ThemisXAnalysisMavenVersion myVersion = new ThemisXAnalysisMavenVersion(pVersion);
        return myVersion.isValid ? myVersion : null;
    }

    @Override
    public String toString() {
        return theVersion;
    }

    @Override
    public int compareTo(final ThemisXAnalysisMavenVersion pThat) {
        /* Note the lengths of the two components */
        final int thisLen = theComponents.length;
        final int thatLen = pThat.theComponents.length;

        /* Loop through the components */
        for (int i = 0; i <= thisLen; i++) {
            /* If we have exhausted all our components */
            if (i == thisLen) {
                /* If the object has further versions, it is later */
                if (thatLen > i) {
                    return -1;
                }

                /* else we are equal on components */
                break;
            }

            /* If we have exhausted all objects versions, it is earlier */
            if (thatLen == i) {
                return 1;
            }

            /* Handle the case where we have a version beyond the other object */
            if (theComponents[i] != pThat.theComponents[i]) {
                return theComponents[i] - pThat.theComponents[i];
            }
        }

        /* Versions are equal, so check the modifier */
        if (theModifier == null) {
            return pThat.theModifier == null ? 0 : -1;
        }
        return pThat.theModifier == null ? 1 : theModifier.compareTo(pThat.theModifier);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check components */
        return pThat instanceof ThemisXAnalysisMavenVersion myVers
                && Objects.equals(theVersion, myVers.getVersion());

    }

    @Override
    public int hashCode() {
        return theVersion.hashCode();
    }
}
