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
package net.sourceforge.joceanus.jgordianknot.api.asym;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * LMS KeyTypes.
 */
public class GordianLMSKeySpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The key type.
     */
    private final GordianLMSSigType theSigType;

    /**
     * The Ots type.
     */
    private final GordianLMSOtsType theOtsType;

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
     * @param pSigType the sigType
     * @param pOtsType the otsType
     */
    public GordianLMSKeySpec(final GordianLMSSigType pSigType,
                             final GordianLMSOtsType pOtsType) {
        theSigType = pSigType;
        theOtsType = pOtsType;
        isValid = checkValidity();
    }

    /**
     * Obtain the parameters.
     * @return the parameters
     */
    public LMSParameters getParameters() {
        return isValid
               ? new LMSParameters(theSigType.getParameter(), theOtsType.getParameter())
               : null;
    }

    /**
     * Is the keySpec high (height &gt; 15)?
     * @return true/false.
     */
    public boolean isHigh() {
        return isValid && theSigType.isHigh();
    }

    /**
     * Is the keySpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Create McEliece CCA2 keySpec.
     * @param pSigType the sigType
     * @param pOtsType the otsType
     * @return the keySpec
     */
    public static GordianLMSKeySpec keySpec(final GordianLMSSigType pSigType,
                                            final GordianLMSOtsType pOtsType) {
        return new GordianLMSKeySpec(pSigType, pOtsType);
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        return theSigType != null && theOtsType != null;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theSigType.toString() + SEP + theOtsType.toString();
             }  else {
                /* Report invalid spec */
                theName = "InvalidLMSKeySpec: " + theSigType + ":" + theOtsType;
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

        /* Check sigType and otsType */
        return theSigType == myThat.theSigType
                && theOtsType == myThat.theOtsType;
    }

    @Override
    public int hashCode() {
        final int hashCode = theSigType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        return hashCode + theOtsType.hashCode();
    }

    /**
     * Obtain a list of all possible specs.
     * @return the list
     */
    public static List<GordianLMSKeySpec> listPossibleKeySpecs() {
        /* Create the list */
        final List<GordianLMSKeySpec> mySpecs = new ArrayList<>();

        /* Add the specs */
        for (final GordianLMSSigType mySig : GordianLMSSigType.values()) {
            for (final GordianLMSOtsType myOts : GordianLMSOtsType.values()) {
                mySpecs.add(GordianLMSKeySpec.keySpec(mySig, myOts));
            }
        }

        /* Return the list */
        return mySpecs;
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
     * LMS sigTypes.
     */
    public enum GordianLMSSigType {
        /**
         * H5.
         */
        H5(LMSigParameters.lms_sha256_n32_h5),

        /**
         * H10.
         */
        H10(LMSigParameters.lms_sha256_n32_h10),

        /**
         * H15.
         */
        H15(LMSigParameters.lms_sha256_n32_h15),

        /**
         * H20.
         */
        H20(LMSigParameters.lms_sha256_n32_h20),

        /**
         * H25.
         */
        H25(LMSigParameters.lms_sha256_n32_h25);

        /**
         * The Ots parameter.
         */
        private final LMSigParameters theParm;

        /**
         * Constructor.
         * @param pParam the parameter
         */
        GordianLMSSigType(final LMSigParameters pParam) {
            theParm = pParam;
        }

        /**
         * Obtain the parameter.
         * @return the parameter
         */
        public LMSigParameters getParameter() {
            return theParm;
        }

        /**
         * Is the parameter high (height > 15)?
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
     * LMS OTS Types.
     */
    public enum GordianLMSOtsType {
        /**
         * W1.
         */
        W1(LMOtsParameters.sha256_n32_w1),

        /**
         * W2.
         */
        W2(LMOtsParameters.sha256_n32_w2),

        /**
         * W1.
         */
        W4(LMOtsParameters.sha256_n32_w4),

        /**
         * W8.
         */
        W8(LMOtsParameters.sha256_n32_w8);

        /**
         * The Ots parameter.
         */
        private final LMOtsParameters theParm;

        /**
         * Constructor.
         * @param pParam the parameter
         */
        GordianLMSOtsType(final LMOtsParameters pParam) {
            theParm = pParam;
        }

        /**
         * Obtain the parameter.
         * @return the parameter
         */
        public LMOtsParameters getParameter() {
            return theParm;
        }
    }
}
