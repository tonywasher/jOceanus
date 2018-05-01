/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * SecureRandom Specification.
 */
public class GordianRandomSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The RandomType.
     */
    private final GordianRandomType theRandomType;

    /**
     * The DigestSpec.
     */
    private final GordianDigestSpec theDigestSpec;

    /**
     * The SymKeySpec.
     */
    private final GordianSymKeySpec theSymKeySpec;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pRandomType the randomType
     * @param pDigestSpec the digestSpec
     */
    protected GordianRandomSpec(final GordianRandomType pRandomType,
                                final GordianDigestSpec pDigestSpec) {
        theRandomType = pRandomType;
        theDigestSpec = pDigestSpec;
        theSymKeySpec = null;
    }

    /**
     * Constructor.
     * @param pRandomType the randomType
     * @param pSymKeySpec the symKeySpec
     */
    protected GordianRandomSpec(final GordianRandomType pRandomType,
                                final GordianSymKeySpec pSymKeySpec) {
        theRandomType = pRandomType;
        theDigestSpec = null;
        theSymKeySpec = pSymKeySpec;
    }

    /**
     * Create hashSpec.
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec hash(final GordianDigestSpec pDigest) {
        return new GordianRandomSpec(GordianRandomType.HASH, pDigest);
    }

    /**
     * Create hMacSpec.
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec hMac(final GordianDigestSpec pDigest) {
        return new GordianRandomSpec(GordianRandomType.HMAC, pDigest);
    }

    /**
     * Create x931Spec.
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec x931(final GordianSymKeySpec pSymKeySpec) {
        return new GordianRandomSpec(GordianRandomType.X931, pSymKeySpec);
    }

    /**
     * Obtain the randomType.
     * @return the randomType.
     */
    public GordianRandomType getRandomType() {
        return theRandomType;
    }

    /**
     * Obtain the digestSpec.
     * @return the digestSpec.
     */
    public GordianDigestSpec getDigestSpec() {
        return theDigestSpec;
    }

    /**
     * Obtain the symKeySpec.
     * @return the symKeySpec.
     */
    public GordianSymKeySpec getSymKeySpec() {
        return theSymKeySpec;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theRandomType.toString();
            if (theDigestSpec != null) {
                theName += SEP + theDigestSpec.toString();
            } else if (theSymKeySpec != null) {
                theName += SEP + theSymKeySpec.toString();
            }
        }

        /* return the name */
        return theName;
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

        /* Make sure that the object is a RandomSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the targetSpec */
        final GordianRandomSpec myThat = (GordianRandomSpec) pThat;

        /* Check KeyType */
        if (theRandomType != myThat.getRandomType()) {
            return false;
        }

        /* Match subfields */
        final boolean isDigestEqual = theDigestSpec != null
                                                            ? theDigestSpec.equals(myThat.getDigestSpec())
                                                            : myThat.getDigestSpec() == null;
        final boolean isSymKeyEqual = theSymKeySpec != null
                                                            ? theSymKeySpec.equals(myThat.getSymKeySpec())
                                                            : myThat.getSymKeySpec() == null;
        return isDigestEqual && isSymKeyEqual;
    }

    @Override
    public int hashCode() {
        int hashCode = theRandomType.ordinal() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theDigestSpec != null
                                          ? theDigestSpec.hashCode()
                                          : theSymKeySpec.hashCode();
        return hashCode;
    }
}
