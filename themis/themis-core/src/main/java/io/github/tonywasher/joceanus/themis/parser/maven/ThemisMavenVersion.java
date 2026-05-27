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

import java.util.Arrays;

/**
 * Comparable Maven version.
 */
public class ThemisMavenVersion
        implements Comparable<ThemisMavenVersion> {
    /**
     * Version.
     */
    private final String theVersion;

    /**
     * Components.
     */
    private final Object[] theComponents;

    /**
     * Constructor.
     *
     * @param pVersion    the version string
     * @param pComponents the components
     */
    ThemisMavenVersion(final String pVersion,
                       final Object[] pComponents) {
        theVersion = pVersion;
        theComponents = pComponents;
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
     * Obtain the components.
     *
     * @return the components
     */
    private Object[] getComponents() {
        return theComponents;
    }

    @Override
    public int compareTo(final ThemisMavenVersion pThat) {
        /* Note the lengths of the two componentLists */
        final int thisLen = theComponents.length;
        final Object[] thatComponents = pThat.getComponents();
        final int thatLen = thatComponents.length;
        final int maxLen = Math.max(thisLen, thatLen);

        /* Loop through the components */
        for (int i = 0; i <= maxLen; i++) {
            /* Access the two components */
            final Object myThis = i < thisLen ? theComponents[i] : null;
            final Object myThat = i < thatLen ? thatComponents[i] : null;

            /* Compare the two components */
            final int myCompare = compareComponents(myThis, myThat);
            if (myCompare != 0) {
                return myCompare;
            }
        }

        /* Must be equal */
        return 0;
    }

    /**
     * Compare components.
     *
     * @param pThis this component.
     * @param pThat that component.
     * @return -1, 0, 1 as to order
     */
    private int compareComponents(final Object pThis,
                                  final Object pThat) {
        /* Switch on component type */
        return switch (pThis) {
            case Long myLong -> compareWithLong(myLong, pThat);
            case String myString -> compareWithString(myString, pThat);
            case null -> compareWithNull(pThat);
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Compare components.
     *
     * @param pThis this long.
     * @param pThat that component.
     * @return -1, 0, 1 as to order
     */
    private int compareWithLong(final Long pThis,
                                final Object pThat) {
        /* Switch on component type */
        return switch (pThat) {
            case Long myLong -> pThis.compareTo(myLong);
            case String ignored -> 1;
            case null -> ThemisMavenConstants.ZERO.equals(pThis) ? 0 : 1;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Compare components.
     *
     * @param pThis this long.
     * @param pThat that component.
     * @return -1, 0, 1 as to order
     */
    private int compareWithString(final String pThis,
                                  final Object pThat) {
        /* If we are long */
        return switch (pThat) {
            case Long ignored -> -1;
            case String myString -> {
                final int iThisIndex = markerIndex(pThis);
                final int iThatIndex = markerIndex(myString);
                if (markerSpecial(iThisIndex)) {
                    yield markerSpecial(iThatIndex) ? iThisIndex - iThatIndex : -1;
                }
                yield markerSpecial(iThatIndex) ? 1 : pThis.compareToIgnoreCase(myString);
            }
            case null -> {
                final int iIndex = markerIndex(pThis);
                yield markerSpecialNonSP(iIndex) ? -1 : 1;
            }
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Compare components.
     *
     * @param pThat that component.
     * @return -1, 0, 1 as to order
     */
    private int compareWithNull(final Object pThat) {
        /* Switch on component type */
        return switch (pThat) {
            case Long myLong -> ThemisMavenConstants.ZERO.equals(myLong) ? 0 : -1;
            case String myString -> {
                final int iIndex = markerIndex(myString);
                yield markerSpecialNonSP(iIndex) ? 1 : -1;
            }
            case null -> 0;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Check for special markers.
     *
     * @param pMarker the marker
     * @return the marker index
     */
    private int markerIndex(final String pMarker) {
        return ThemisMavenConstants.NAMES.indexOf(pMarker);
    }

    /**
     * Is the marker index special?
     *
     * @param pIndex the index
     * @return true/false
     */
    private boolean markerSpecial(final int pIndex) {
        return pIndex != -1;
    }

    /**
     * Is the marker index special and nonSP?
     *
     * @param pIndex the index
     * @return true/false
     */
    private boolean markerSpecialNonSP(final int pIndex) {
        return markerSpecial(pIndex) && pIndex != ThemisMavenConstants.SP_INDEX;
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
        return pThat instanceof ThemisMavenVersion myVers
                && Arrays.equals(theComponents, myVers.getComponents());

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(theComponents);
    }
}
