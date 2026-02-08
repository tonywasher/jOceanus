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

package io.github.tonywasher.joceanus.gordianknot.impl.core.agree.spec;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianNewAgreementKDF;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianNewAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianNewAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianNewAgreementType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * KeyPair Agreement Specification Builder.
 */
public class GordianCoreAgreementSpecBuilder
        implements GordianNewAgreementSpecBuilder {
    /**
     * The keyPairSpec.
     */
    private GordianKeyPairSpec theKeyPairSpec;

    /**
     * The agreement type.
     */
    private GordianNewAgreementType theAgreementType;

    /**
     * The KDF type.
     */
    private GordianNewAgreementKDF theKDF;

    /**
     * eith Confirm?
     */
    private boolean withConfirm;

    @Override
    public GordianNewAgreementSpecBuilder withKeyPairSpec(final GordianKeyPairSpec pSpec) {
        theKeyPairSpec = pSpec;
        return this;
    }

    @Override
    public GordianNewAgreementSpecBuilder withAgreementType(final GordianNewAgreementType pType) {
        theAgreementType = pType;
        return this;
    }

    @Override
    public GordianNewAgreementSpecBuilder withKDF(final GordianNewAgreementKDF pKDF) {
        theKDF = pKDF;
        return null;
    }

    @Override
    public GordianNewAgreementSpecBuilder withConfirm() {
        withConfirm = true;
        return this;
    }

    @Override
    public GordianNewAgreementSpec build() {
        /* Create spec, reset and return */
        final GordianCoreAgreementSpec mySpec = new GordianCoreAgreementSpec(theKeyPairSpec, theAgreementType, theKDF, withConfirm);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theKeyPairSpec = null;
        theAgreementType = null;
        theKDF = null;
        withConfirm = false;
    }

    /**
     * Obtain a list of all possible agreements for the keyPairSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @return the list
     */
    public static List<GordianNewAgreementSpec> listAllPossibleSpecs(final GordianKeyPairSpec pKeyPairSpec) {
        /* Create list */
        final List<GordianNewAgreementSpec> myAgreements = new ArrayList<>();

        /* Switch on keyPairType */
        switch (pKeyPairSpec.getKeyPairType()) {
            case RSA:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.KEM));
                break;
            case NEWHOPE:
            case CMCE:
            case FRODO:
            case SABER:
            case MLKEM:
            case HQC:
            case BIKE:
            case NTRU:
            case NTRUPRIME:
                myAgreements.add(new GordianCoreAgreementSpec(pKeyPairSpec, GordianNewAgreementType.KEM, GordianNewAgreementKDF.NONE));
                break;
            case EC:
            case SM2:
            case GOST2012:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.UNIFIED, true));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.MQV));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.MQV, true));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.SM2));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.SM2, true));
                break;
            case DH:
            case DSTU4145:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.UNIFIED, true));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.MQV));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.MQV, true));
                break;
            case XDH:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianNewAgreementType.UNIFIED, true));
                break;
            case COMPOSITE:
                /* Loop through the possible keySpecs for the first key */
                final Iterator<GordianKeyPairSpec> myIterator = pKeyPairSpec.keySpecIterator();
                for (GordianNewAgreementSpec mySpec : listAllPossibleSpecs(myIterator.next())) {
                    final GordianNewAgreementSpec myTest = new GordianCoreAgreementSpec(pKeyPairSpec,
                            mySpec.getAgreementType(), mySpec.getKDFType(), mySpec.withConfirm());
                    if (myTest.isValid()) {
                        myAgreements.add(myTest);
                    }
                }
                break;
            default:
                break;
        }

        /* Return the list */
        return myAgreements;
    }

    /**
     * Create list of KDF variants.
     *
     * @param pKeyPairSpec   the keyPairSpec
     * @param pAgreementType the agreementType
     * @return the list
     */
    private static List<GordianNewAgreementSpec> listAllKDFs(final GordianKeyPairSpec pKeyPairSpec,
                                                             final GordianNewAgreementType pAgreementType) {
        return listAllKDFs(pKeyPairSpec, pAgreementType, false);
    }

    /**
     * Create list of KDF variants.
     *
     * @param pKeyPairSpec   the keyPairSpec
     * @param pAgreementType the agreementType
     * @param pConfirm       with key confirmation
     * @return the list
     */
    private static List<GordianNewAgreementSpec> listAllKDFs(final GordianKeyPairSpec pKeyPairSpec,
                                                             final GordianNewAgreementType pAgreementType,
                                                             final boolean pConfirm) {
        /* Create list */
        final List<GordianNewAgreementSpec> myAgreements = new ArrayList<>();

        /* Loop through the KDFs */
        for (final GordianNewAgreementKDF myKDF : GordianNewAgreementKDF.values()) {
            myAgreements.add(new GordianCoreAgreementSpec(pKeyPairSpec, pAgreementType, myKDF, pConfirm));
        }

        /* Return the list */
        return myAgreements;
    }
}
