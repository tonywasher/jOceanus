/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.keypair;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.pqc.jcajce.spec.NTRULPRimeParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SNTRUPrimeParameterSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * NTRUPRIME KeySpec.
 */
public class GordianNTRUPrimeSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The type.
     */
    private final GordianNTRUPrimeType theType;

    /**
     * The params.
     */
    private final GordianNTRUPrimeParams theParams;

    /**
     * is the spec valid?.
     */
    private final boolean isValid;

    /**
     * The name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pType the Type
     * @param pParams the params
     */
    public GordianNTRUPrimeSpec(final GordianNTRUPrimeType pType,
                                final GordianNTRUPrimeParams pParams) {
        /* Store parameters */
        theType = pType;
        theParams = pParams;

        /* Check validity */
        isValid = checkValidity();
    }

    /**
     * Obtain the type.
     * @return the type
     */
    public GordianNTRUPrimeType getType() {
        return theType;
    }

    /**
     * Obtain the params.
     * @return the params
     */
    public GordianNTRUPrimeParams getParams() {
        return theParams;
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
        return theType != null && theParams != null;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theType.toString() + "Prime" + SEP + theParams.toString();
            }  else {
                /* Report invalid spec */
                theName = "InvalidLMSKeySpec: " + theType + SEP + theParams;
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
        final GordianNTRUPrimeSpec myThat = (GordianNTRUPrimeSpec) pThat;

        /* Check values */
        return theType == myThat.theType
                && theParams == myThat.theParams;
    }

    @Override
    public int hashCode() {
        return Objects.hash(theType, theParams);
    }

    /**
     * Obtain a list of all possible specs.
     * @return the list
     */
    public static List<GordianNTRUPrimeSpec> listPossibleKeySpecs() {
        /* Create the list */
        final List<GordianNTRUPrimeSpec> mySpecs = new ArrayList<>();

        /* Add the specs */
        for (final GordianNTRUPrimeType myType : GordianNTRUPrimeType.values()) {
            for (final GordianNTRUPrimeParams myParams : GordianNTRUPrimeParams.values()) {
                mySpecs.add(new GordianNTRUPrimeSpec(myType, myParams));
            }
        }

        /* Return the list */
        return mySpecs;
    }

    /**
     * NTRUPRIME Type.
     */
    public enum GordianNTRUPrimeType {
        /**
         * NTRULPrime.
         */
        NTRUL,

        /**
         * SNTRUPrime.
         */
        SNTRU;
    }

    /**
     * NTRUPRIME Parameters.
     */
    public enum GordianNTRUPrimeParams {
        /**
         * PR653.
         */
        PR653,

        /**
         * PR761.
         */
        PR761,

        /**
         * PR857.
         */
        PR857,

        /**
         * PR953.
         */
        PR953,

        /**
         * PR1013.
         */
        PR1013,

        /**
         * PR1277.
         */
        PR1277;

        /**
         * Obtain NTRUL Parameters.
         * @return the parameters.
         */
        public NTRULPRimeParameters getNTRULParameters() {
            switch (this) {
                case PR653:   return NTRULPRimeParameters.ntrulpr653;
                case PR761:   return NTRULPRimeParameters.ntrulpr761;
                case PR857:   return NTRULPRimeParameters.ntrulpr857;
                case PR953:   return NTRULPRimeParameters.ntrulpr953;
                case PR1013:  return NTRULPRimeParameters.ntrulpr1013;
                case PR1277:  return NTRULPRimeParameters.ntrulpr1277;
                default: throw new IllegalArgumentException();
            }
        }

        /**
         * Obtain NTRUL ParameterSpec.
         * @return the parameters.
         */
        public NTRULPRimeParameterSpec getNTRULParameterSpec() {
            switch (this) {
                case PR653:   return NTRULPRimeParameterSpec.ntrulpr653;
                case PR761:   return NTRULPRimeParameterSpec.ntrulpr761;
                case PR857:   return NTRULPRimeParameterSpec.ntrulpr857;
                case PR953:   return NTRULPRimeParameterSpec.ntrulpr953;
                case PR1013:  return NTRULPRimeParameterSpec.ntrulpr1013;
                case PR1277:  return NTRULPRimeParameterSpec.ntrulpr1277;
                default: throw new IllegalArgumentException();
            }
        }

        /**
         * Obtain NTRUL algorithm Identifier.
         * @return the identifier.
         */
        public ASN1ObjectIdentifier getNTRULIdentifier() {
            switch (this) {
                case PR653:   return BCObjectIdentifiers.ntrulpr653;
                case PR761:   return BCObjectIdentifiers.ntrulpr761;
                case PR857:   return BCObjectIdentifiers.ntrulpr857;
                case PR953:   return BCObjectIdentifiers.ntrulpr953;
                case PR1013:  return BCObjectIdentifiers.ntrulpr1013;
                case PR1277:  return BCObjectIdentifiers.ntrulpr1277;
                default: throw new IllegalArgumentException();
            }
        }

        /**
         * Obtain NTRUL Parameters.
         * @return the parameters.
         */
        public SNTRUPrimeParameters getSNTRUParameters() {
            switch (this) {
                case PR653:   return SNTRUPrimeParameters.sntrup653;
                case PR761:   return SNTRUPrimeParameters.sntrup761;
                case PR857:   return SNTRUPrimeParameters.sntrup857;
                case PR953:   return SNTRUPrimeParameters.sntrup953;
                case PR1013:  return SNTRUPrimeParameters.sntrup1013;
                case PR1277:  return SNTRUPrimeParameters.sntrup1277;
                default: throw new IllegalArgumentException();
            }
        }

        /**
         * Obtain NTRUL ParameterSpec.
         * @return the parameters.
         */
        public SNTRUPrimeParameterSpec getSNTRUParameterSpec() {
            switch (this) {
                case PR653:   return SNTRUPrimeParameterSpec.sntrup653;
                case PR761:   return SNTRUPrimeParameterSpec.sntrup761;
                case PR857:   return SNTRUPrimeParameterSpec.sntrup857;
                case PR953:   return SNTRUPrimeParameterSpec.sntrup953;
                case PR1013:  return SNTRUPrimeParameterSpec.sntrup1013;
                case PR1277:  return SNTRUPrimeParameterSpec.sntrup1277;
                default: throw new IllegalArgumentException();
            }
        }

        /**
         * Obtain SNTRU algorithm Identifier.
         * @return the identifier.
         */
        public ASN1ObjectIdentifier getSNTRUIdentifier() {
            switch (this) {
                case PR653:   return BCObjectIdentifiers.sntrup653;
                case PR761:   return BCObjectIdentifiers.sntrup761;
                case PR857:   return BCObjectIdentifiers.sntrup857;
                case PR953:   return BCObjectIdentifiers.sntrup953;
                case PR1013:  return BCObjectIdentifiers.sntrup1013;
                case PR1277:  return BCObjectIdentifiers.sntrup1277;
                default: throw new IllegalArgumentException();
            }
        }
    }
}
