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

import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2Parameters;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * McEliece KeyTypes.
 */
public final class GordianMcElieceKeySpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The key type.
     */
    private final GordianMcElieceKeyType theKeyType;

    /**
     * The digest type.
     */
    private final GordianMcElieceDigestType theDigestType;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pKeyType the keyType
     * @param pDigestType the digestType
     */
    private GordianMcElieceKeySpec(final GordianMcElieceKeyType pKeyType,
                                   final GordianMcElieceDigestType pDigestType) {
        theKeyType = pKeyType;
        theDigestType = pDigestType;
    }

    /**
     * Is this key CCA2?
     * @return true/false
     */
    public boolean isCCA2() {
        return theKeyType.isCCA2();
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public GordianMcElieceKeyType getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the digestType.
     * @return the digestType
     */
    public GordianMcElieceDigestType getDigestType() {
        return theDigestType;
    }

    /**
     * Create McElieceSpec.
     * @return the keySpec
     */
    public static GordianMcElieceKeySpec standard() {
        return new GordianMcElieceKeySpec(GordianMcElieceKeyType.STANDARD, null);
    }

    /**
     * Create McEliece CCA2 keySpec.
     * @param pDigestType the digestType
     * @return the keySpec
     */
    public static GordianMcElieceKeySpec cca2(final GordianMcElieceDigestType pDigestType) {
        return new GordianMcElieceKeySpec(GordianMcElieceKeyType.CCA2, pDigestType);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theKeyType.toString();
            if (theDigestType != null) {
                theName += SEP + theDigestType.toString();
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

        /* Make sure that the object is a mEliecesSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target mcElieceSpec */
        final GordianMcElieceKeySpec myThat = (GordianMcElieceKeySpec) pThat;

        /* Check KeyType and digestType */
        return theKeyType == myThat.getKeyType()
               && theDigestType == myThat.getDigestType();
    }

    @Override
    public int hashCode() {
        final int hashCode = theKeyType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        return theDigestType == null
                                     ? hashCode
                                     : hashCode + theDigestType.hashCode();
    }

    /**
     * Check valid encryption type.
     * @param pKeySpec the McElieceKeySpec
     * @param pEncryptorType the encryptorType
     * @return true/false
     */
    static boolean checkValidEncryptionType(final GordianMcElieceKeySpec pKeySpec,
                                            final GordianMcElieceEncryptionType pEncryptorType) {
        switch (pEncryptorType) {
            case STANDARD:
                return GordianMcElieceKeyType.STANDARD.equals(pKeySpec.getKeyType());
            case FUJISAKI:
            case KOBARAIMAI:
            case POINTCHEVAL:
                return !GordianMcElieceKeyType.STANDARD.equals(pKeySpec.getKeyType());
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain a list of all possible specs.
     * @return the list
     */
    public static List<GordianMcElieceKeySpec> listPossibleKeySpecs() {
        /* Create the list */
        final List<GordianMcElieceKeySpec> mySpecs = new ArrayList<>();

        /* Add the specs */
        mySpecs.add(GordianMcElieceKeySpec.standard());
        for (final GordianMcElieceDigestType myType : GordianMcElieceDigestType.values()) {
            mySpecs.add(GordianMcElieceKeySpec.cca2(myType));
        }

        /* Return the list */
        return mySpecs;
    }

    /**
     * McEliece keyTypes.
     */
    public enum GordianMcElieceKeyType {
        /**
         * Standard.
         */
        STANDARD,

        /**
         * CCA2.
         */
        CCA2;

        /**
         * Is this key CCA2?
         * @return true/false
         */
        public boolean isCCA2() {
            return this == CCA2;
        }
    }

    /**
     * McEliece digestTypes.
     */
    public enum GordianMcElieceDigestType {
        /**
         * SHA256.
         */
        SHA256(McElieceCCA2KeyGenParameterSpec.SHA256),

        /**
         * SHA1.
         */
        SHA1(McElieceCCA2KeyGenParameterSpec.SHA1),

        /**
         * SHA224.
         */
        SHA224(McElieceCCA2KeyGenParameterSpec.SHA224),

        /**
         * SHA384.
         */
        SHA384(McElieceCCA2KeyGenParameterSpec.SHA384),

        /**
         * SHA512.
         */
        SHA512(McElieceCCA2KeyGenParameterSpec.SHA512);

        /**
         * The McEliece parameter.
         */
        private final String theParm;

        /**
         * Constructor.
         * @param pParam the parameter
         */
        GordianMcElieceDigestType(final String pParam) {
            theParm = pParam;
        }

        /**
         * Obtain the parameter.
         * @return the parameter
         */
        public String getParameter() {
            return theParm;
        }

        /**
         * Obtain the required value for M.
         * @return M
         */
        public int getM() {
            switch(this) {
                case SHA512:
                    return McElieceCCA2Parameters.DEFAULT_M + 4;
                case SHA384:
                    return McElieceCCA2Parameters.DEFAULT_M + 1;
                default:
                    return McElieceCCA2Parameters.DEFAULT_M;
            }
        }
    }

    /**
     * McEliece encryptionType.
     */
    public enum GordianMcElieceEncryptionType {
        /**
         * Standard.
         */
        STANDARD("Standard"),

        /**
         * KobaraImai.
         */
        KOBARAIMAI("KobaraImai"),

        /**
         * Fujisaki.
         */
        FUJISAKI("Fujisaki"),

        /**
         * Pointcheval.
         */
        POINTCHEVAL("Pointcheval");

        /**
         * The name.
         */
        private final String theName;

        /**
         * Constructor.
         * @param pName the name of the encryption
         */
        GordianMcElieceEncryptionType(final String pName) {
            theName = pName;
        }

        @Override
        public String toString() {
            return theName;
        }
    }
}
