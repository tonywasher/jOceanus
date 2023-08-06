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
package net.sourceforge.joceanus.jgordianknot.api.keypair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;

/**
 * LMS KeyTypes.
 */
public class GordianLMSKeySpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * Invalid length error,
     */
    private static final String INVALID_LENGTH = "Invalid Length: ";

    /**
     * The hash.
     */
    private final GordianLMSHash theHash;

    /**
     * The width.
     */
    private final GordianLMSWidth theWidth;

    /**
     * The height.
     */
    private final GordianLMSHeight theHeight;

    /**
     * The length.
     */
    private final GordianLength theLength;

    /**
     * The Parameters.
     */
    private final LMSParameters theParams;

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
     * @param pHashType the hashType
     * @param pHeight the height
     * @param pWidth the width
     * @param pLength the length
     */
    public GordianLMSKeySpec(final GordianLMSHash pHashType,
                             final GordianLMSHeight pHeight,
                             final GordianLMSWidth pWidth,
                             final GordianLength pLength) {
        /* Store parameters */
        theHash = pHashType;
        theWidth = pWidth;
        theHeight = pHeight;
        theLength = pLength;

        /* Check validity */
        isValid = checkValidity();

        /* Calculate parameters */
        final LMSigParameters mySig = isValid ? theHeight.getSigParameter(theHash, theLength) : null;
        final LMOtsParameters myOts = isValid ? theWidth.getOtsParameter(theHash, theLength) : null;
        theParams = isValid ? new LMSParameters(mySig, myOts) : null;
    }

    /**
     * Obtain the hash.
     * @return the hash
     */
    public GordianLMSHash getHash() {
        return theHash;
    }

    /**
     * Obtain the width.
     * @return the width
     */
    public GordianLMSHeight getHeight() {
        return theHeight;
    }

    /**
     * Obtain the width.
     * @return the width
     */
    public GordianLMSWidth getWidth() {
        return theWidth;
    }

    /**
     * Obtain the legth.
     * @return the width
     */
    public GordianLength getLength() {
        return theLength;
    }

    /**
     * Obtain the parameters.
     * @return the parameters
     */
    public LMSParameters getParameters() {
        return theParams;
    }

    /**
     * Is the keySpec high (height &ge; 15)?
     * @return true/false.
     */
    public boolean isHigh() {
        return isValid && theHeight.isHigh();
    }

    /**
     * Is the keySpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        if (theWidth == null || theHeight == null || theHash == null || theLength == null) {
            return false;
        }
        switch (theLength) {
            case LEN_192:
            case LEN_256:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theHash.toString() + SEP + theWidth.toString() + SEP + theHeight.toString() + SEP + theLength.toString();
             }  else {
                /* Report invalid spec */
                theName = "InvalidLMSKeySpec: " + theHash + SEP + theWidth + SEP + theHeight + SEP + theLength;
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

        /* Make sure that the object is lmsSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target lmsSpec */
        final GordianLMSKeySpec myThat = (GordianLMSKeySpec) pThat;

        /* Check hash and length */
        if (theHash != myThat.theHash
            || theLength != myThat.theLength) {
            return false;
        }

        /* Check height and width */
        return theWidth == myThat.theWidth
                && theHeight == myThat.theHeight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(theHash, theHeight, theWidth, theLength);
    }

    /**
     * Obtain a list of all possible specs.
     * @return the list
     */
    public static List<GordianLMSKeySpec> listPossibleKeySpecs() {
        /* Create the list */
        final List<GordianLMSKeySpec> mySpecs = new ArrayList<>();

        /* Add the specs */
        for (final GordianLMSHeight myHeight : GordianLMSHeight.values()) {
            for (final GordianLMSWidth myWidth : GordianLMSWidth.values()) {
                mySpecs.add(new GordianLMSKeySpec(GordianLMSHash.SHA256, myHeight, myWidth, GordianLength.LEN_256));
                mySpecs.add(new GordianLMSKeySpec(GordianLMSHash.SHA256, myHeight, myWidth, GordianLength.LEN_192));
                mySpecs.add(new GordianLMSKeySpec(GordianLMSHash.SHAKE256, myHeight, myWidth, GordianLength.LEN_256));
                mySpecs.add(new GordianLMSKeySpec(GordianLMSHash.SHAKE256, myHeight, myWidth, GordianLength.LEN_192));
            }
        }

        /* Return the list */
        return mySpecs;
    }

    /**
     * Match keySpec against LMSParameters.
     * @param pSigParams the sigParameters
     * @param pOtsParams the otsParameters
     * @return the matching keySpec
     */
    public static GordianLMSKeySpec determineKeySpec(final LMSigParameters pSigParams,
                                                     final LMOtsParameters pOtsParams) {
        final List<GordianLMSKeySpec> mySpecs = listPossibleKeySpecs();
        for (GordianLMSKeySpec mySpec : mySpecs) {
            if (pSigParams.equals(mySpec.getParameters().getLMSigParam())
                && pOtsParams.equals(mySpec.getParameters().getLMOTSParam())) {
                return mySpec;
            }
        }
        throw new IllegalArgumentException("Unsupported LMSSpec");
    }

    /**
     * HSS keySpec.
     */
    public static class GordianHSSKeySpec {
        /**
         * Max depth for HSS key.
         */
        public static final int MAX_DEPTH = 8;

        /**
         * The top level keySpec.
         */
        private final GordianLMSKeySpec theKeySpec;

        /**
         * The tree depth.
         */
        private final int theDepth;

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
         * @param pKeySpec the keySpecs
         * @param pDepth the tree depth
         */
        GordianHSSKeySpec(final GordianLMSKeySpec pKeySpec,
                          final int pDepth) {
            /* Create the list of keySpecs */
            theKeySpec = pKeySpec;
            theDepth = pDepth;
            isValid = checkValidity();
        }

        /**
         * Obtain the parameters.
         * @return the parameters.
         */
        public GordianLMSKeySpec getKeySpec() {
            /* If we are valid */
            return isValid
                   ? theKeySpec
                   : null;
        }

        /**
         * Obtain the treeDepth.
         * @return the treeDepth.
         */
        public int getTreeDepth() {
            /* If we are valid */
            return isValid
                   ? theDepth
                   : -1;
        }

        /**
         * Is the keySpec valid?
         * @return true/false.
         */
        public boolean isValid() {
            return isValid;
        }

        /**
         * Check spec validity.
         * @return valid true/false
         */
        private boolean checkValidity() {
            /* Depth must be at least 2 and  no more that MAX */
            if (theDepth <= 1 || theDepth > MAX_DEPTH) {
                return false;
            }

            /* Check keySpec */
            return theKeySpec != null && theKeySpec.isValid;
        }

        @Override
        public String toString() {
            /* If we have not yet loaded the name */
            if (theName == null) {
                /* If the keySpec is valid */
                if (isValid) {
                    /* Load the name */
                    theName = "HSS-" + theDepth + "-" + theKeySpec;

                }  else {
                    /* Report invalid spec */
                    theName = "InvalidHSSKeySpec: " + theDepth + ":" + theKeySpec;
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

            /* Make sure that the object is hssSpec */
            if (pThat.getClass() != this.getClass()) {
                return false;
            }

            /* Access the target hssSpec */
            final GordianHSSKeySpec myThat = (GordianHSSKeySpec) pThat;

            /* Check depth and keySpec are identical */
            return theDepth == myThat.theDepth
                    && Objects.equals(theKeySpec, myThat.theKeySpec);
        }

        @Override
        public int hashCode() {
            return theKeySpec.hashCode() + theDepth;
        }
    }

    /**
     * LMS hash.
     */
    public enum GordianLMSHash {
        /**
         * Sha256.
         */
        SHA256,

        /**
         * Shake256.
         */
        SHAKE256;
    }

    /**
     * LMS height.
     */
    public enum GordianLMSHeight {
        /**
         * H5.
         */
        H5,

        /**
         * H10.
         */
        H10,

        /**
         * H15.
         */
        H15,

        /**
         * H20.
         */
        H20,

        /**
         * H25.
         */
        H25;

        /**
         * Obtain the sigParameter.
         * @return the parameter
         */
        private LMSigParameters getSigParameter(final GordianLMSHash pHash,
                                                final GordianLength pLength) {
            switch (this) {
                case H5:    return getH5Parameter(pHash, pLength);
                case H10:   return getH10Parameter(pHash, pLength);
                case H15:   return getH15Parameter(pHash, pLength);
                case H20:   return getH20Parameter(pHash, pLength);
                case H25:   return getH25Parameter(pHash, pLength);
                default:    throw new IllegalStateException();
            }
        }

        /**
         * Obtain the H5 sigParameter.
         * @return the parameter
         */
        private LMSigParameters getH5Parameter(final GordianLMSHash pHash,
                                               final GordianLength pLength) {
            switch (pLength) {
                case LEN_192:
                    return pHash == GordianLMSHash.SHA256 ? LMSigParameters.lms_sha256_n24_h5 : LMSigParameters.lms_shake256_n24_h5;
                case LEN_256:
                    return pHash == GordianLMSHash.SHA256 ? LMSigParameters.lms_sha256_n32_h5 : LMSigParameters.lms_shake256_n32_h5;
                default:
                    throw new IllegalArgumentException(INVALID_LENGTH + pLength);
            }
        }

        /**
         * Obtain the H10 sigParameter.
         * @return the parameter
         */
        private LMSigParameters getH10Parameter(final GordianLMSHash pHash,
                                                final GordianLength pLength) {
            switch (pLength) {
                case LEN_192:
                    return pHash == GordianLMSHash.SHA256 ? LMSigParameters.lms_sha256_n24_h10 : LMSigParameters.lms_shake256_n24_h10;
                case LEN_256:
                    return pHash == GordianLMSHash.SHA256 ? LMSigParameters.lms_sha256_n32_h10 : LMSigParameters.lms_shake256_n32_h10;
                default:
                    throw new IllegalArgumentException(INVALID_LENGTH + pLength);
            }
        }

        /**
         * Obtain the H15 sigParameter.
         * @return the parameter
         */
        private LMSigParameters getH15Parameter(final GordianLMSHash pHash,
                                                final GordianLength pLength) {
            switch (pLength) {
                case LEN_192:
                    return pHash == GordianLMSHash.SHA256 ? LMSigParameters.lms_sha256_n24_h15 : LMSigParameters.lms_shake256_n24_h15;
                case LEN_256:
                    return pHash == GordianLMSHash.SHA256 ? LMSigParameters.lms_sha256_n32_h15 : LMSigParameters.lms_shake256_n32_h15;
                default:
                    throw new IllegalArgumentException(INVALID_LENGTH + pLength);
            }
        }

        /**
         * Obtain the H20 sigParameter.
         * @return the parameter
         */
        private LMSigParameters getH20Parameter(final GordianLMSHash pHash,
                                                final GordianLength pLength) {
            switch (pLength) {
                case LEN_192:
                    return pHash == GordianLMSHash.SHA256 ? LMSigParameters.lms_sha256_n24_h20 : LMSigParameters.lms_shake256_n24_h20;
                case LEN_256:
                    return pHash == GordianLMSHash.SHA256 ? LMSigParameters.lms_sha256_n32_h20 : LMSigParameters.lms_shake256_n32_h20;
                default:
                    throw new IllegalArgumentException(INVALID_LENGTH + pLength);
            }
        }

        /**
         * Obtain the H25 sigParameter.
         * @return the parameter
         */
        private LMSigParameters getH25Parameter(final GordianLMSHash pHash,
                                                final GordianLength pLength) {
            switch (pLength) {
                case LEN_192:
                    return pHash == GordianLMSHash.SHA256 ? LMSigParameters.lms_sha256_n24_h25 : LMSigParameters.lms_shake256_n24_h25;
                case LEN_256:
                    return pHash == GordianLMSHash.SHA256 ? LMSigParameters.lms_sha256_n32_h25 : LMSigParameters.lms_shake256_n32_h25;
                default:
                    throw new IllegalArgumentException(INVALID_LENGTH + pLength);
            }
        }

        /**
         * Is the parameter high (height &ge; 15)?
         * @return true/false.
         */
        public boolean isHigh() {
            switch (this) {
                case H15:
                case H20:
                case H25:
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * LMS Width.
     */
    public enum GordianLMSWidth {
        /**
         * W1.
         */
        W1,

        /**
         * W2.
         */
        W2,

        /**
         * W4.
         */
        W4,

        /**
         * W8.
         */
        W8;

        /**
         * Obtain the sigParameter.
         * @return the parameter
         */
        private LMOtsParameters getOtsParameter(final GordianLMSHash pHash,
                                                final GordianLength pLength) {
            switch (this) {
                case W1:   return getW1Parameter(pHash, pLength);
                case W2:   return getW2Parameter(pHash, pLength);
                case W4:   return getW4Parameter(pHash, pLength);
                case W8:   return getW8Parameter(pHash, pLength);
                default:   throw new IllegalStateException();
            }
        }

        /**
         * Obtain the W1 otsParameter.
         * @return the parameter
         */
        private LMOtsParameters getW1Parameter(final GordianLMSHash pHash,
                                               final GordianLength pLength) {
            switch (pLength) {
                case LEN_192:
                    return pHash == GordianLMSHash.SHA256 ? LMOtsParameters.sha256_n24_w1 : LMOtsParameters.shake256_n24_w1;
                case LEN_256:
                    return pHash == GordianLMSHash.SHA256 ? LMOtsParameters.sha256_n32_w1 : LMOtsParameters.shake256_n32_w1;
                default:
                    throw new IllegalArgumentException(INVALID_LENGTH + pLength);
            }
        }

        /**
         * Obtain the W2 otsParameter.
         * @return the parameter
         */
        private LMOtsParameters getW2Parameter(final GordianLMSHash pHash,
                                               final GordianLength pLength) {
            switch (pLength) {
                case LEN_192:
                    return pHash == GordianLMSHash.SHA256 ? LMOtsParameters.sha256_n24_w2 : LMOtsParameters.shake256_n24_w2;
                case LEN_256:
                    return pHash == GordianLMSHash.SHA256 ? LMOtsParameters.sha256_n32_w2 : LMOtsParameters.shake256_n32_w2;
                default:
                    throw new IllegalArgumentException(INVALID_LENGTH + pLength);
            }
        }

        /**
         * Obtain the W4 otsParameter.
         * @return the parameter
         */
        private LMOtsParameters getW4Parameter(final GordianLMSHash pHash,
                                               final GordianLength pLength) {
            switch (pLength) {
                case LEN_192:
                    return pHash == GordianLMSHash.SHA256 ? LMOtsParameters.sha256_n24_w4 : LMOtsParameters.shake256_n24_w4;
                case LEN_256:
                    return pHash == GordianLMSHash.SHA256 ? LMOtsParameters.sha256_n32_w4 : LMOtsParameters.shake256_n32_w4;
                default:
                    throw new IllegalArgumentException(INVALID_LENGTH + pLength);
            }
        }

        /**
         * Obtain the W8 otsParameter.
         * @return the parameter
         */
        private LMOtsParameters getW8Parameter(final GordianLMSHash pHash,
                                               final GordianLength pLength) {
            switch (pLength) {
                case LEN_192:
                    return pHash == GordianLMSHash.SHA256 ? LMOtsParameters.sha256_n24_w8 : LMOtsParameters.shake256_n24_w8;
                case LEN_256:
                    return pHash == GordianLMSHash.SHA256 ? LMOtsParameters.sha256_n32_w8 : LMOtsParameters.shake256_n32_w8;
                default:
                    throw new IllegalArgumentException(INVALID_LENGTH + pLength);
            }
        }
    }
}
