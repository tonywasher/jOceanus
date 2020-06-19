/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.random;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
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
     * The SubSpec.
     */
    private final Object theSubSpec;

    /**
     * Is the secureRandom predicationResistant?
     */
    private final boolean isPredictionResistant;

    /**
     * The Validity.
     */
    private final boolean isValid;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pRandomType the randomType
     * @param pSubSpec the subSpec
     * @param pResistant is the secureRandom predicationResistant?
     */
    public GordianRandomSpec(final GordianRandomType pRandomType,
                             final Object pSubSpec,
                             final boolean pResistant) {
        theRandomType = pRandomType;
        theSubSpec = pSubSpec;
        isPredictionResistant = pResistant;
        isValid = checkValidity();
    }

    /**
     * Create hashSpec.
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec hash(final GordianDigestSpec pDigest) {
        return new GordianRandomSpec(GordianRandomType.HASH, pDigest, false);
    }

    /**
     * Create prediction resistant hashSpec.
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec hashResist(final GordianDigestSpec pDigest) {
        return new GordianRandomSpec(GordianRandomType.HASH, pDigest, true);
    }

    /**
     * Create hMacSpec.
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec hMac(final GordianDigestSpec pDigest) {
        return new GordianRandomSpec(GordianRandomType.HMAC, pDigest, false);
    }

    /**
     * Create prediction resistant hMacSpec.
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec hMacResist(final GordianDigestSpec pDigest) {
        return new GordianRandomSpec(GordianRandomType.HMAC, pDigest, true);
    }

    /**
     * Create ctrSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec ctr(final GordianSymKeySpec pSymKeySpec) {
        return new GordianRandomSpec(GordianRandomType.CTR, pSymKeySpec, false);
    }

    /**
     * Create prediction resistant ctrSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec ctrResist(final GordianSymKeySpec pSymKeySpec) {
        return new GordianRandomSpec(GordianRandomType.CTR, pSymKeySpec, true);
    }

    /**
     * Create x931Spec.
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec x931(final GordianSymKeySpec pSymKeySpec) {
        return new GordianRandomSpec(GordianRandomType.X931, pSymKeySpec, false);
    }

    /**
     * Create prediction resistant x931Spec.
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec x931Resist(final GordianSymKeySpec pSymKeySpec) {
        return new GordianRandomSpec(GordianRandomType.X931, pSymKeySpec, true);
    }

    /**
     * Obtain the randomType.
     * @return the randomType.
     */
    public GordianRandomType getRandomType() {
        return theRandomType;
    }

    /**
     * Obtain the subSpec.
     * @return the subSpec.
     */
    private Object getSubSpec() {
        return theSubSpec;
    }

    /**
     * Is the macSpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Obtain the digestSpec.
     * @return the digestSpec.
     */
    public GordianDigestSpec getDigestSpec() {
        return theSubSpec instanceof GordianDigestSpec
               ? (GordianDigestSpec) theSubSpec
               : null;
    }

    /**
     * Obtain the symKeySpec.
     * @return the symKeySpec.
     */
    public GordianSymKeySpec getSymKeySpec() {
        return theSubSpec instanceof GordianSymKeySpec
               ? (GordianSymKeySpec) theSubSpec
               : null;
    }

    /**
     * Obtain the predication resistance.
     * @return the resistance.
     */
    public boolean isPredictionResistant() {
        return isPredictionResistant;
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        if (theRandomType == null) {
            return false;
        }
        switch (theRandomType) {
            case HMAC:
            case HASH:
                return theSubSpec instanceof GordianDigestSpec
                        && ((GordianDigestSpec) theSubSpec).isValid()
                        && ((GordianDigestSpec) theSubSpec).getDigestType().supportsLargeData();
            case CTR:
            case X931:
                return theSubSpec instanceof GordianSymKeySpec
                        && ((GordianSymKeySpec) theSubSpec).isValid();
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the randomSpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theRandomType.toString();
                theName += SEP + theSubSpec;
                if (isPredictionResistant) {
                    theName += SEP + "resistant";
                }
            }  else {
                /* Report invalid spec */
                theName = "InvalidRandomSpec: " + theRandomType + ":" + theSubSpec + ":" + isPredictionResistant;
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

        /* Check KeyType and prediction */
        if (theRandomType != myThat.getRandomType()) {
            return false;
        }
        if (isPredictionResistant != myThat.isPredictionResistant()) {
            return false;
        }

        /* Match subSpecs */
        return Objects.equals(theSubSpec, myThat.getSubSpec());
    }

    @Override
    public int hashCode() {
        int hashCode = theRandomType.ordinal() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theSubSpec != null
                    ? theSubSpec.hashCode()
                    : 0;
        return hashCode + (isPredictionResistant ? 1 : 0);
    }

    /**
     * List all possible randomSpecs.
     * @return the list
     */
    public static List<GordianRandomSpec> listAll() {
        /* Create the array list */
        final List<GordianRandomSpec> myList = new ArrayList<>();

        /* For each digestSpec */
        for (final GordianDigestSpec mySpec : GordianDigestSpec.listAll()) {
            /* Add a hash random */
            myList.add(GordianRandomSpec.hash(mySpec));
            myList.add(GordianRandomSpec.hashResist(mySpec));

            /* Add an hMac random */
            myList.add(GordianRandomSpec.hMac(mySpec));
            myList.add(GordianRandomSpec.hMacResist(mySpec));
        }

        /* For each KeyLength */
        final Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* For each symKeySpec */
            for (final GordianSymKeySpec mySpec : GordianSymKeySpec.listAll(myKeyLen)) {
                /* Add a CTR random */
                myList.add(GordianRandomSpec.ctr(mySpec));
                myList.add(GordianRandomSpec.ctrResist(mySpec));

                /* Add an X931 random */
                myList.add(GordianRandomSpec.x931(mySpec));
                myList.add(GordianRandomSpec.x931Resist(mySpec));
            }
        }

        /* Return the list */
        return myList;
    }
}
