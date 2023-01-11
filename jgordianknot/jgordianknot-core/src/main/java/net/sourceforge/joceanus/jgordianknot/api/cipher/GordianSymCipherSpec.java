/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * The SymCipherSpec class.
 */
public class GordianSymCipherSpec
        extends GordianCipherSpec<GordianSymKeySpec> {
    /**
     * The IV length.
     */
    public static final int AADIVLEN = 12;

    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * Cipher Mode.
     */
    private final GordianCipherMode theMode;

    /**
     * Cipher Padding.
     */
    private final GordianPadding thePadding;

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
     * @param pKeySpec the keySpec
     * @param pMode the mode
     * @param pPadding the padding
     */
    public GordianSymCipherSpec(final GordianSymKeySpec pKeySpec,
                                final GordianCipherMode pMode,
                                final GordianPadding pPadding) {
        super(pKeySpec);
        theMode = pMode;
        thePadding = pPadding;
        isValid = checkValidity();
    }

    /**
     * Create an ECB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @param pPadding the padding
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec ecb(final GordianSymKeySpec pKeySpec,
                                           final GordianPadding pPadding) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.ECB, pPadding);
    }

    /**
     * Create a CBC symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @param pPadding the padding
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec cbc(final GordianSymKeySpec pKeySpec,
                                           final GordianPadding pPadding) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.CBC, pPadding);
    }

    /**
     * Create a CFB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec cfb(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.CFB, GordianPadding.NONE);
    }

    /**
     * Create a GCFB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec gcfb(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.GCFB, GordianPadding.NONE);
    }

    /**
     * Create a OFB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec ofb(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.OFB, GordianPadding.NONE);
    }

    /**
     * Create a GOFB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec gofb(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.GOFB, GordianPadding.NONE);
    }

    /**
     * Create a SIC symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec sic(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.SIC, GordianPadding.NONE);
    }

    /**
     * Create a KCTR symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec kctr(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.KCTR, GordianPadding.NONE);
    }

    /**
     * Create a CCM symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec ccm(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.CCM, GordianPadding.NONE);
    }

    /**
     * Create a KCCM symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec kccm(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.KCCM, GordianPadding.NONE);
    }

    /**
     * Create a GCM symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec gcm(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.GCM, GordianPadding.NONE);
    }

    /**
     * Create a KGCM symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec kgcm(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.KGCM, GordianPadding.NONE);
    }

    /**
     * Create an EAX symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec eax(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.EAX, GordianPadding.NONE);
    }

    /**
     * Create an OCB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec ocb(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.OCB, GordianPadding.NONE);
    }

    /**
     * Obtain the cipherMode.
     * @return the mode
     */
    public GordianCipherMode getCipherMode() {
        return theMode;
    }

    /**
     * Obtain the padding.
     * @return the padding
     */
    public GordianPadding getPadding() {
        return thePadding;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public boolean needsIV() {
        return theMode.needsIV();
    }

    /**
     * Obtain the blockLength.
     * @return the blockLength
     */
    public GordianLength getBlockLength() {
        return getKeyType().getBlockLength();
    }

    @Override
    public int getIVLength() {
        if (getCipherMode().isAAD()) {
            return AADIVLEN;
        }
        final int myBlockLen = getKeyType().getBlockLength().getByteLength();
        return GordianCipherMode.G3413CTR.equals(theMode)
               ? myBlockLen >> 1
               : myBlockLen;
    }

    /**
     * Is this an AAD mode?
     * @return true/false
     */
    public boolean isAAD() {
        return theMode != null && theMode.isAAD();
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = super.toString();
                theName += SEP + theMode;
                if (!GordianPadding.NONE.equals(thePadding)) {
                    theName += SEP + thePadding;
                }
            }  else {
                /* Report invalid spec */
                theName = "InvalidSymCipherSpec: " + super.toString() + ":" + theMode + ":" + thePadding;
            }
        }

        /* return the name */
        return theName;
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        final GordianSymKeySpec mySpec = getKeyType();
        return mySpec != null && mySpec.isValid()
                && theMode != null && thePadding != null;
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

        /* Make sure that the object is a SymCipherSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target cipherSpec */
        final GordianSymCipherSpec myThat = (GordianSymCipherSpec) pThat;

        /* Check KeyType */
        if (!getKeyType().equals(myThat.getKeyType())) {
            return false;
        }

        /* Match subfields */
        return theMode == myThat.getCipherMode()
                && thePadding == myThat.getPadding();
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode() << TethysDataConverter.BYTE_SHIFT;
        if (theMode != null) {
            hashCode += theMode.ordinal() + 1;
            hashCode <<= TethysDataConverter.BYTE_SHIFT;
        }
        if (thePadding != null) {
            hashCode += thePadding.ordinal() + 1;
        }
        return hashCode;
    }
}
