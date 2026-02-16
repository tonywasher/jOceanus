/*
 * GordianKnot: Security Suite
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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec;
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * LMS KeyTypes.
 */
public class GordianCoreLMSSpec
        implements GordianNewLMSSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * Invalid length error.
     */
    private static final String INVALID_LENGTH = "Invalid Length: ";

    /**
     * The hash.
     */
    private final GordianNewLMSHash theHash;

    /**
     * The width.
     */
    private final GordianNewLMSWidth theWidth;

    /**
     * The height.
     */
    private final GordianNewLMSHeight theHeight;

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
     *
     * @param pHashType the hashType
     * @param pHeight   the height
     * @param pWidth    the width
     * @param pLength   the length
     */
    public GordianCoreLMSSpec(final GordianNewLMSHash pHashType,
                              final GordianNewLMSHeight pHeight,
                              final GordianNewLMSWidth pWidth,
                              final GordianLength pLength) {
        /* Store parameters */
        theHash = pHashType;
        theWidth = pWidth;
        theHeight = pHeight;
        theLength = pLength;

        /* Check validity */
        isValid = checkValidity();

        /* Calculate parameters */
        final LMSigParameters mySig = isValid ? getSigParameter() : null;
        final LMOtsParameters myOts = isValid ? getOtsParameter() : null;
        theParams = isValid ? new LMSParameters(mySig, myOts) : null;
    }

    @Override
    public GordianNewLMSHash getHash() {
        return theHash;
    }

    @Override
    public GordianNewLMSHeight getHeight() {
        return theHeight;
    }

    @Override
    public GordianNewLMSWidth getWidth() {
        return theWidth;
    }

    @Override
    public GordianLength getLength() {
        return theLength;
    }

    /**
     * Obtain the parameters.
     *
     * @return the parameters
     */
    public LMSParameters getParameters() {
        return theParams;
    }

    /**
     * Is the keySpec high (height &ge; 15)?
     *
     * @return true/false.
     */
    public boolean isHigh() {
        return isValid && isHigh(theHeight);
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    protected boolean checkValidity() {
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
            } else {
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

        /* Check fields */
        return pThat instanceof GordianCoreLMSSpec myThat
                && theHash == myThat.theHash
                && theLength == myThat.theLength
                && theWidth == myThat.theWidth
                && theHeight == myThat.theHeight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(theHash, theHeight, theWidth, theLength);
    }

    /**
     * Is the parameter high (height &ge; 15)?
     *
     * @param pHeight the height
     * @return true/false.
     */
    private boolean isHigh(final GordianNewLMSHeight pHeight) {
        switch (pHeight) {
            case H15:
            case H20:
            case H25:
                return true;
            default:
                return false;
        }
    }

    /**
     * Obtain the sigParameter.
     *
     * @return the parameter
     */
    private LMSigParameters getSigParameter() {
        switch (theHeight) {
            case H5:
                return getH5Parameter();
            case H10:
                return getH10Parameter();
            case H15:
                return getH15Parameter();
            case H20:
                return getH20Parameter();
            case H25:
                return getH25Parameter();
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Obtain the H5 sigParameter.
     *
     * @return the parameter
     */
    private LMSigParameters getH5Parameter() {
        switch (theLength) {
            case LEN_192:
                return theHash == GordianNewLMSHash.SHA256 ? LMSigParameters.lms_sha256_n24_h5 : LMSigParameters.lms_shake256_n24_h5;
            case LEN_256:
                return theHash == GordianNewLMSHash.SHA256 ? LMSigParameters.lms_sha256_n32_h5 : LMSigParameters.lms_shake256_n32_h5;
            default:
                throw new IllegalArgumentException(INVALID_LENGTH + theLength);
        }
    }

    /**
     * Obtain the H10 sigParameter.
     *
     * @return the parameter
     */
    private LMSigParameters getH10Parameter() {
        switch (theLength) {
            case LEN_192:
                return theHash == GordianNewLMSHash.SHA256 ? LMSigParameters.lms_sha256_n24_h10 : LMSigParameters.lms_shake256_n24_h10;
            case LEN_256:
                return theHash == GordianNewLMSHash.SHA256 ? LMSigParameters.lms_sha256_n32_h10 : LMSigParameters.lms_shake256_n32_h10;
            default:
                throw new IllegalArgumentException(INVALID_LENGTH + theLength);
        }
    }

    /**
     * Obtain the H15 sigParameter.
     *
     * @return the parameter
     */
    private LMSigParameters getH15Parameter() {
        switch (theLength) {
            case LEN_192:
                return theHash == GordianNewLMSHash.SHA256 ? LMSigParameters.lms_sha256_n24_h15 : LMSigParameters.lms_shake256_n24_h15;
            case LEN_256:
                return theHash == GordianNewLMSHash.SHA256 ? LMSigParameters.lms_sha256_n32_h15 : LMSigParameters.lms_shake256_n32_h15;
            default:
                throw new IllegalArgumentException(INVALID_LENGTH + theLength);
        }
    }

    /**
     * Obtain the H20 sigParameter.
     *
     * @return the parameter
     */
    private LMSigParameters getH20Parameter() {
        switch (theLength) {
            case LEN_192:
                return theHash == GordianNewLMSHash.SHA256 ? LMSigParameters.lms_sha256_n24_h20 : LMSigParameters.lms_shake256_n24_h20;
            case LEN_256:
                return theHash == GordianNewLMSHash.SHA256 ? LMSigParameters.lms_sha256_n32_h20 : LMSigParameters.lms_shake256_n32_h20;
            default:
                throw new IllegalArgumentException(INVALID_LENGTH + theLength);
        }
    }

    /**
     * Obtain the H25 sigParameter.
     *
     * @return the parameter
     */
    private LMSigParameters getH25Parameter() {
        switch (theLength) {
            case LEN_192:
                return theHash == GordianNewLMSHash.SHA256 ? LMSigParameters.lms_sha256_n24_h25 : LMSigParameters.lms_shake256_n24_h25;
            case LEN_256:
                return theHash == GordianNewLMSHash.SHA256 ? LMSigParameters.lms_sha256_n32_h25 : LMSigParameters.lms_shake256_n32_h25;
            default:
                throw new IllegalArgumentException(INVALID_LENGTH + theLength);
        }
    }

    /**
     * Obtain the otsParameter.
     *
     * @return the parameter
     */
    private LMOtsParameters getOtsParameter() {
        switch (theWidth) {
            case W1:
                return getW1Parameter();
            case W2:
                return getW2Parameter();
            case W4:
                return getW4Parameter();
            case W8:
                return getW8Parameter();
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Obtain the W1 otsParameter.
     *
     * @return the parameter
     */
    private LMOtsParameters getW1Parameter() {
        switch (theLength) {
            case LEN_192:
                return theHash == GordianNewLMSHash.SHA256 ? LMOtsParameters.sha256_n24_w1 : LMOtsParameters.shake256_n24_w1;
            case LEN_256:
                return theHash == GordianNewLMSHash.SHA256 ? LMOtsParameters.sha256_n32_w1 : LMOtsParameters.shake256_n32_w1;
            default:
                throw new IllegalArgumentException(INVALID_LENGTH + theLength);
        }
    }

    /**
     * Obtain the W2 otsParameter.
     *
     * @return the parameter
     */
    private LMOtsParameters getW2Parameter() {
        switch (theLength) {
            case LEN_192:
                return theHash == GordianNewLMSHash.SHA256 ? LMOtsParameters.sha256_n24_w2 : LMOtsParameters.shake256_n24_w2;
            case LEN_256:
                return theHash == GordianNewLMSHash.SHA256 ? LMOtsParameters.sha256_n32_w2 : LMOtsParameters.shake256_n32_w2;
            default:
                throw new IllegalArgumentException(INVALID_LENGTH + theLength);
        }
    }

    /**
     * Obtain the W4 otsParameter.
     *
     * @return the parameter
     */
    private LMOtsParameters getW4Parameter() {
        switch (theLength) {
            case LEN_192:
                return theHash == GordianNewLMSHash.SHA256 ? LMOtsParameters.sha256_n24_w4 : LMOtsParameters.shake256_n24_w4;
            case LEN_256:
                return theHash == GordianNewLMSHash.SHA256 ? LMOtsParameters.sha256_n32_w4 : LMOtsParameters.shake256_n32_w4;
            default:
                throw new IllegalArgumentException(INVALID_LENGTH + theLength);
        }
    }

    /**
     * Obtain the W8 otsParameter.
     *
     * @return the parameter
     */
    private LMOtsParameters getW8Parameter() {
        switch (theLength) {
            case LEN_192:
                return theHash == GordianNewLMSHash.SHA256 ? LMOtsParameters.sha256_n24_w8 : LMOtsParameters.shake256_n24_w8;
            case LEN_256:
                return theHash == GordianNewLMSHash.SHA256 ? LMOtsParameters.sha256_n32_w8 : LMOtsParameters.shake256_n32_w8;
            default:
                throw new IllegalArgumentException(INVALID_LENGTH + theLength);
        }
    }

    /**
     * HSS keySpec.
     */
    public static class GordianCoreHSSSpec
            extends GordianCoreLMSSpec
            implements GordianNewHSSSpec {
        /**
         * Max depth for HSS key.
         */
        public static final int MAX_DEPTH = 8;

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
         *
         * @param pBase  the Base LMS
         * @param pDepth the depth
         */
        public GordianCoreHSSSpec(final GordianNewLMSSpec pBase,
                                  final int pDepth) {
            super(pBase.getHash(), pBase.getHeight(), pBase.getWidth(), pBase.getLength());
            theDepth = pDepth;
            isValid = checkValidity();
        }

        @Override
        public int getTreeDepth() {
            return theDepth;
        }

        @Override
        public boolean isValid() {
            return isValid;
        }

        /**
         * Check spec validity.
         *
         * @return valid true/false
         */
        protected boolean checkValidity() {
            return super.checkValidity()
                    && (theDepth < 1 || theDepth > MAX_DEPTH);
        }

        @Override
        public String toString() {
            /* If we have not yet loaded the name */
            if (theName == null) {
                /* If the keySpec is valid */
                if (isValid) {
                    /* Load the name */
                    theName = "HSS-" + theDepth + "-" + super.toString();

                } else {
                    /* Report invalid spec */
                    theName = "InvalidHSSKeySpec: " + theDepth + ":" + super.toString();
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

            /* Access the target hssSpec */
            return pThat instanceof GordianCoreHSSSpec myThat
                    && super.equals(pThat)
                    && theDepth == myThat.getTreeDepth();
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), theDepth);
        }
    }

    /**
     * Obtain a list of all possible LMS specs.
     *
     * @return the list
     */
    public static List<GordianNewLMSSpec> listAllPossibleSpecs() {
        /* Create the list */
        final List<GordianNewLMSSpec> mySpecs = new ArrayList<>();

        /* Add the specs */
        for (final GordianNewLMSSpec mySpec : listPossibleLMSSpecs()) {
            for (int i = 1; i < GordianHSSKeySpec.MAX_DEPTH; i++) {
                mySpecs.add(new GordianCoreHSSSpec(mySpec, i));
            }
        }

        /* Return the list */
        return mySpecs;
    }

    /**
     * Obtain a list of all possible LMS specs.
     *
     * @return the list
     */
    public static List<GordianNewLMSSpec> listPossibleLMSSpecs() {
        /* Create the list */
        final List<GordianNewLMSSpec> mySpecs = new ArrayList<>();

        /* Add the specs */
        for (final GordianNewLMSHeight myHeight : GordianNewLMSHeight.values()) {
            for (final GordianNewLMSWidth myWidth : GordianNewLMSWidth.values()) {
                mySpecs.add(new GordianCoreLMSSpec(GordianNewLMSHash.SHA256, myHeight, myWidth, GordianLength.LEN_256));
                mySpecs.add(new GordianCoreLMSSpec(GordianNewLMSHash.SHA256, myHeight, myWidth, GordianLength.LEN_192));
                mySpecs.add(new GordianCoreLMSSpec(GordianNewLMSHash.SHAKE256, myHeight, myWidth, GordianLength.LEN_256));
                mySpecs.add(new GordianCoreLMSSpec(GordianNewLMSHash.SHAKE256, myHeight, myWidth, GordianLength.LEN_192));
            }
        }

        /* Return the list */
        return mySpecs;
    }

    /**
     * Match keySpec against LMSParameters.
     *
     * @param pSigParams the sigParameters
     * @param pOtsParams the otsParameters
     * @return the matching keySpec
     */
    public static GordianNewLMSSpec determineSpec(final LMSigParameters pSigParams,
                                                  final LMOtsParameters pOtsParams) {
        final List<GordianNewLMSSpec> mySpecs = listPossibleLMSSpecs();
        for (GordianNewLMSSpec mySpec : mySpecs) {
            final GordianCoreLMSSpec myCoreSpec = (GordianCoreLMSSpec) mySpec;
            if (pSigParams.equals(myCoreSpec.getParameters().getLMSigParam())
                    && pOtsParams.equals(myCoreSpec.getParameters().getLMOTSParam())) {
                return mySpec;
            }
        }
        throw new IllegalArgumentException("Unsupported LMSSpec");
    }
}
