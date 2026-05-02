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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPrimeSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.base.GordianSpecConstants;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.pqc.jcajce.spec.NTRULPRimeParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SNTRUPrimeParameterSpec;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * NTRUPRIME KeySpec.
 */
public class GordianCoreNTRUPrimeSpec
        implements GordianNTRUPrimeSpec {
    /**
     * The type.
     */
    private final GordianNTRUPrimeType theType;

    /**
     * The params.
     */
    private final GordianCoreNTRUPrimeParams theParams;

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
     *
     * @param pType   the Type
     * @param pParams the params
     */
    GordianCoreNTRUPrimeSpec(final GordianNTRUPrimeType pType,
                             final GordianNTRUPrimeParams pParams) {
        /* Store parameters */
        theType = pType;
        theParams = GordianCoreNTRUPrimeParams.mapCoreParams(pParams);

        /* Check validity */
        isValid = checkValidity();
    }

    @Override
    public GordianNTRUPrimeType getType() {
        return theType;
    }

    @Override
    public GordianNTRUPrimeParams getParams() {
        return theParams.getParams();
    }

    /**
     * Obtain the params.
     *
     * @return the params
     */
    public GordianCoreNTRUPrimeParams getCoreParams() {
        return theParams;
    }

    /**
     * Is the keySpec valid?
     *
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Check spec validity.
     *
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
                theName = theType.toString() + "Prime" + GordianSpecConstants.SEP + theParams.toString();
            } else {
                /* Report invalid spec */
                theName = "InvalidLMSKeySpec: " + theType + GordianSpecConstants.SEP + theParams;
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

        /* Check values */
        return pThat instanceof GordianCoreNTRUPrimeSpec myThat
                && theType == myThat.getType()
                && theParams == myThat.getCoreParams();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theType, theParams);
    }

    /**
     * Obtain a list of all possible specs.
     *
     * @return the list
     */
    public static List<GordianNTRUPrimeSpec> listAllPossibleSpecs() {
        /* Create the list */
        final List<GordianNTRUPrimeSpec> mySpecs = new ArrayList<>();

        /* Add the specs */
        for (final GordianNTRUPrimeType myType : GordianNTRUPrimeType.values()) {
            for (final GordianNTRUPrimeParams myParams : GordianNTRUPrimeParams.values()) {
                mySpecs.add(new GordianCoreNTRUPrimeSpec(myType, myParams));
            }
        }

        /* Return the list */
        return mySpecs;
    }

    /**
     * NTRUPRIME Parameters.
     */
    public static final class GordianCoreNTRUPrimeParams {
        /**
         * The paramsMap.
         */
        private static final Map<GordianNTRUPrimeParams, GordianCoreNTRUPrimeParams> PARMMAP = newParamsMap();

        /**
         * The parmArray.
         */
        private static final GordianCoreNTRUPrimeParams[] VALUES = PARMMAP.values().toArray(new GordianCoreNTRUPrimeParams[0]);

        /**
         * The Params.
         */
        private final GordianNTRUPrimeParams theParams;

        /**
         * Constructor.
         *
         * @param pParams the params
         */
        private GordianCoreNTRUPrimeParams(final GordianNTRUPrimeParams pParams) {
            theParams = pParams;
        }

        /**
         * Obtain the spec.
         *
         * @return the spec
         */
        public GordianNTRUPrimeParams getParams() {
            return theParams;
        }

        /**
         * Obtain NTRUL Parameters.
         *
         * @return the parameters.
         */
        public NTRULPRimeParameters getNTRULParameters() {
            return switch (theParams) {
                case PR653 -> NTRULPRimeParameters.ntrulpr653;
                case PR761 -> NTRULPRimeParameters.ntrulpr761;
                case PR857 -> NTRULPRimeParameters.ntrulpr857;
                case PR953 -> NTRULPRimeParameters.ntrulpr953;
                case PR1013 -> NTRULPRimeParameters.ntrulpr1013;
                case PR1277 -> NTRULPRimeParameters.ntrulpr1277;
                default -> throw new IllegalArgumentException();
            };
        }

        /**
         * Obtain NTRUL ParameterSpec.
         *
         * @return the parameters.
         */
        public NTRULPRimeParameterSpec getNTRULParameterSpec() {
            return switch (theParams) {
                case PR653 -> NTRULPRimeParameterSpec.ntrulpr653;
                case PR761 -> NTRULPRimeParameterSpec.ntrulpr761;
                case PR857 -> NTRULPRimeParameterSpec.ntrulpr857;
                case PR953 -> NTRULPRimeParameterSpec.ntrulpr953;
                case PR1013 -> NTRULPRimeParameterSpec.ntrulpr1013;
                case PR1277 -> NTRULPRimeParameterSpec.ntrulpr1277;
                default -> throw new IllegalArgumentException();
            };
        }

        /**
         * Obtain NTRUL algorithm Identifier.
         *
         * @return the identifier.
         */
        public ASN1ObjectIdentifier getNTRULIdentifier() {
            return switch (theParams) {
                case PR653 -> BCObjectIdentifiers.ntrulpr653;
                case PR761 -> BCObjectIdentifiers.ntrulpr761;
                case PR857 -> BCObjectIdentifiers.ntrulpr857;
                case PR953 -> BCObjectIdentifiers.ntrulpr953;
                case PR1013 -> BCObjectIdentifiers.ntrulpr1013;
                case PR1277 -> BCObjectIdentifiers.ntrulpr1277;
                default -> throw new IllegalArgumentException();
            };
        }

        /**
         * Obtain NTRUL Parameters.
         *
         * @return the parameters.
         */
        public SNTRUPrimeParameters getSNTRUParameters() {
            return switch (theParams) {
                case PR653 -> SNTRUPrimeParameters.sntrup653;
                case PR761 -> SNTRUPrimeParameters.sntrup761;
                case PR857 -> SNTRUPrimeParameters.sntrup857;
                case PR953 -> SNTRUPrimeParameters.sntrup953;
                case PR1013 -> SNTRUPrimeParameters.sntrup1013;
                case PR1277 -> SNTRUPrimeParameters.sntrup1277;
                default -> throw new IllegalArgumentException();
            };
        }

        /**
         * Obtain NTRUL ParameterSpec.
         *
         * @return the parameters.
         */
        public SNTRUPrimeParameterSpec getSNTRUParameterSpec() {
            return switch (theParams) {
                case PR653 -> SNTRUPrimeParameterSpec.sntrup653;
                case PR761 -> SNTRUPrimeParameterSpec.sntrup761;
                case PR857 -> SNTRUPrimeParameterSpec.sntrup857;
                case PR953 -> SNTRUPrimeParameterSpec.sntrup953;
                case PR1013 -> SNTRUPrimeParameterSpec.sntrup1013;
                case PR1277 -> SNTRUPrimeParameterSpec.sntrup1277;
                default -> throw new IllegalArgumentException();
            };
        }

        /**
         * Obtain SNTRU algorithm Identifier.
         *
         * @return the identifier.
         */
        public ASN1ObjectIdentifier getSNTRUIdentifier() {
            return switch (theParams) {
                case PR653 -> BCObjectIdentifiers.sntrup653;
                case PR761 -> BCObjectIdentifiers.sntrup761;
                case PR857 -> BCObjectIdentifiers.sntrup857;
                case PR953 -> BCObjectIdentifiers.sntrup953;
                case PR1013 -> BCObjectIdentifiers.sntrup1013;
                case PR1277 -> BCObjectIdentifiers.sntrup1277;
                default -> throw new IllegalArgumentException();
            };
        }

        @Override
        public String toString() {
            return theParams.toString();
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

            /* Check subFields */
            return pThat instanceof GordianCoreNTRUPrimeParams myThat
                    && theParams == myThat.getParams();
        }

        @Override
        public int hashCode() {
            return theParams.hashCode();
        }

        /**
         * Obtain the core params.
         *
         * @param pParams the base params
         * @return the core params
         */
        public static GordianCoreNTRUPrimeParams mapCoreParams(final Object pParams) {
            return pParams instanceof GordianNTRUPrimeParams myParams ? PARMMAP.get(myParams) : null;
        }

        /**
         * Build the type map.
         *
         * @return the type map
         */
        private static Map<GordianNTRUPrimeParams, GordianCoreNTRUPrimeParams> newParamsMap() {
            final Map<GordianNTRUPrimeParams, GordianCoreNTRUPrimeParams> myMap = new EnumMap<>(GordianNTRUPrimeParams.class);
            for (GordianNTRUPrimeParams mySpec : GordianNTRUPrimeParams.values()) {
                myMap.put(mySpec, new GordianCoreNTRUPrimeParams(mySpec));
            }
            return myMap;
        }

        /**
         * Obtain the values.
         *
         * @return the values
         */
        public static GordianCoreNTRUPrimeParams[] values() {
            return VALUES;
        }
    }
}
