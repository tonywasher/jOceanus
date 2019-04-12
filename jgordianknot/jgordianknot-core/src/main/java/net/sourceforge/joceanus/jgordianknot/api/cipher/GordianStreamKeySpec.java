/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * GordianKnot StreamKeySpec.
 */
public class GordianStreamKeySpec
    implements GordianKeySpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The StreamKey Type.
     */
    private final GordianStreamKeyType theStreamKeyType;

    /**
     * The Key Length.
     */
    private final GordianLength theKeyLength;

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
     * @param pStreamKeyType the streamKeyType
     * @param pKeyLength the keyLength
     */
    public GordianStreamKeySpec(final GordianStreamKeyType pStreamKeyType,
                                final GordianLength pKeyLength) {
        /* Store parameters */
        theStreamKeyType = pStreamKeyType;
        theKeyLength = pKeyLength;
        isValid = checkValidity();
    }

    /**
     * Create hcKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec hc(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.HC, pKeyLength);
    }

    /**
     * Create chachaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec chacha(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.CHACHA, pKeyLength);
    }

    /**
     * Create hcKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec salsa(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SALSA20, pKeyLength);
    }

    /**
     * Create xsalsaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec xsalsa(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.XSALSA20, pKeyLength);
    }

    /**
     * Create xsalsaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec xchacha(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.XCHACHA20, pKeyLength);
    }

    /**
     * Create isaacKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec isaac(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.ISAAC, pKeyLength);
    }

    /**
     * Create rc4KeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec rc4(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.RC4, pKeyLength);
    }

    /**
     * Create vmpcKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec vmpc(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.VMPC, pKeyLength);
    }

    /**
     * Create grainKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec grain(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.GRAIN, pKeyLength);
    }

    /**
     * Create rabbitKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec rabbit(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.RABBIT, pKeyLength);
    }

    /**
     * Create sosemaukKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec sosemanuk(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SOSEMANUK, pKeyLength);
    }

    /**
     * Create snow3GKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec snow3G(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SNOW3G, pKeyLength);
    }

    /**
     * Create zucKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec zuc(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.ZUC, pKeyLength);
    }

    /**
     * Obtain streamKey Type.
     * @return the streamKeyType
     */
    public GordianStreamKeyType getStreamKeyType() {
        return theStreamKeyType;
    }

    @Override
    public GordianLength getKeyLength() {
        return theKeyLength;
    }

    /**
     * Is the keySpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Does this cipher need an IV?
     * @return true/false
     */
    public boolean needsIV() {
        return theStreamKeyType.getIVLength(theKeyLength) > 0;
    }

    /**
     * Obtain the IV length for this cipher.
     * @return the IV Length
     */
    public int getIVLength() {
        return theStreamKeyType.getIVLength(theKeyLength);
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Everything must be non-null */
        if (theStreamKeyType == null
                || theKeyLength == null) {
            return false;
        }

        /* Check keyLength validity */
        return theStreamKeyType.validForKeyLength(theKeyLength);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theStreamKeyType.toString();
                theName += SEP + theKeyLength;
            }  else {
                /* Report invalid spec */
                theName = "InvalidStreamKeySpec: " + theStreamKeyType + ":" + theKeyLength;
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

        /* Make sure that the object is a StreamKeySpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target StreamKeySpec */
        final GordianStreamKeySpec myThat = (GordianStreamKeySpec) pThat;

        /* Check subFields */
        return theStreamKeyType == myThat.getStreamKeyType()
                && theKeyLength == myThat.getKeyLength();
    }

    @Override
    public int hashCode() {
        int hashCode = theStreamKeyType.ordinal() + 1 << TethysDataConverter.BYTE_SHIFT;
        hashCode += theKeyLength.ordinal() + 1;
        return hashCode;
    }

    /**
     * List all possible streamKeySpecs for the keyLength.
     * @param pKeyLen the keyLength
     * @return the list
     */
    public static List<GordianStreamKeySpec> listAll(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianStreamKeySpec> myList = new ArrayList<>();

        /* Check that the keyLength is supported */
        if (!GordianKeyLengths.isSupportedLength(pKeyLen)) {
            return myList;
        }

        /* For each streamKey type */
        for (final GordianStreamKeyType myType : GordianStreamKeyType.values()) {
            /* Add spec if valid for keyLength */
            if (myType.validForKeyLength(pKeyLen)) {
                myList.add(new GordianStreamKeySpec(myType, pKeyLen));
            }
        }

        /* Return the list */
        return myList;
    }
}
