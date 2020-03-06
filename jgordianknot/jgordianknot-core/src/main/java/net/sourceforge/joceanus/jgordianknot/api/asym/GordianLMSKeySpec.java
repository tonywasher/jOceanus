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
import java.util.Arrays;
import java.util.List;

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
         * The array of keySpecs.
         */
        private final GordianLMSKeySpec[] theKeySpecs;

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
         * @param pKeySpecs the keySpecs
         */
        GordianHSSKeySpec(final GordianLMSKeySpec... pKeySpecs) {
            /* Create the list of keySpecs */
            theKeySpecs = Arrays.copyOf(pKeySpecs, pKeySpecs.length);
            isValid = checkValidity();
        }

        /**
         * Obtain the parameters.
         * @return the parameters.
         */
        public GordianLMSKeySpec[] getParameters() {
            /* If we are valid */
            return isValid
                   ? Arrays.copyOf(theKeySpecs, theKeySpecs.length)
                   : null;
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
            /* Must have at least two elements */
            if (theKeySpecs.length <= 1) {
                return false;
            }

            /* Check each keySpec */
            for (GordianLMSKeySpec myKeySpec : theKeySpecs) {
                if (myKeySpec == null || !myKeySpec.isValid) {
                    return false;
                }
            }

            /* valid */
            return true;
        }

        @Override
        public String toString() {
            /* If we have not yet loaded the name */
            if (theName == null) {
                /* If the keySpec is valid */
                if (isValid) {
                    /* Load the name */
                    final StringBuilder myBuilder = new StringBuilder("(");
                    boolean mySkip = true;
                    for (GordianLMSKeySpec myKeySpec : theKeySpecs) {
                        if (mySkip) {
                            mySkip = false;
                        } else {
                            myBuilder.append(",");
                        }
                        myBuilder.append(myKeySpec.toString());
                    }
                    myBuilder.append(")");
                    theName = myBuilder.toString();

                }  else {
                    /* Report invalid spec */
                    theName = "InvalidHSSKeySpec ";
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

            /* Check lists are identical */
            return Arrays.equals(theKeySpecs, myThat.theKeySpecs);
        }

        @Override
        public int hashCode() {
            return theKeySpecs.hashCode();
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
