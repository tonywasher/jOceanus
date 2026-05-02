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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementKDF;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * KeyPair Agreement Specification Builder.
 */
public final class GordianCoreAgreementSpecBuilder
        implements GordianAgreementSpecBuilder {
    /**
     * The keyPairSpec.
     */
    private GordianKeyPairSpec theKeyPairSpec;

    /**
     * The agreement type.
     */
    private GordianAgreementType theAgreementType;

    /**
     * The KDF type.
     */
    private GordianAgreementKDF theKDF;

    /**
     * eith Confirm?
     */
    private boolean withConfirm;

    /**
     * Private constructor.
     */
    private GordianCoreAgreementSpecBuilder() {
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCoreAgreementSpecBuilder newInstance() {
        return new GordianCoreAgreementSpecBuilder();
    }

    @Override
    public GordianAgreementSpecBuilder withKeyPairSpec(final GordianKeyPairSpec pSpec) {
        theKeyPairSpec = pSpec;
        return this;
    }

    @Override
    public GordianAgreementSpecBuilder withAgreementType(final GordianAgreementType pType) {
        theAgreementType = pType;
        return this;
    }

    @Override
    public GordianAgreementSpecBuilder withKDF(final GordianAgreementKDF pKDF) {
        theKDF = pKDF;
        return this;
    }

    @Override
    public GordianAgreementSpecBuilder withConfirm() {
        withConfirm = true;
        return this;
    }

    @Override
    public GordianAgreementSpec build() {
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
    public static List<GordianAgreementSpec> listAllPossibleSpecs(final GordianKeyPairSpec pKeyPairSpec) {
        /* Create list */
        final List<GordianAgreementSpec> myAgreements = new ArrayList<>();

        /* Switch on keyPairType */
        switch (pKeyPairSpec.getKeyPairType()) {
            case RSA, MLKEM:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.KEM));
                break;
            case NEWHOPE, CMCE, FRODO, SABER, HQC, BIKE, NTRU, NTRUPLUS, NTRUPRIME:
                myAgreements.add(new GordianCoreAgreementSpec(pKeyPairSpec, GordianAgreementType.KEM, GordianAgreementKDF.NONE));
                break;
            case EC, SM2, GOST:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED, true));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.MQV));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.MQV, true));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.SM2));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.SM2, true));
                break;
            case DH, DSTU:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED, true));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.MQV));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.MQV, true));
                break;
            case XDH:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED, true));
                break;
            case COMPOSITE:
                /* Loop through the possible keySpecs for the first key */
                final Iterator<GordianKeyPairSpec> myIterator = ((GordianCoreKeyPairSpec) pKeyPairSpec).keySpecIterator();
                for (GordianAgreementSpec mySpec : listAllPossibleSpecs(myIterator.next())) {
                    final GordianAgreementSpec myTest = new GordianCoreAgreementSpec(pKeyPairSpec,
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
    public static List<GordianAgreementSpec> listAllKDFs(final GordianKeyPairSpec pKeyPairSpec,
                                                         final GordianAgreementType pAgreementType) {
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
    public static List<GordianAgreementSpec> listAllKDFs(final GordianKeyPairSpec pKeyPairSpec,
                                                         final GordianAgreementType pAgreementType,
                                                         final boolean pConfirm) {
        /* Create list */
        final List<GordianAgreementSpec> myAgreements = new ArrayList<>();

        /* Loop through the KDFs */
        for (final GordianAgreementKDF myKDF : GordianAgreementKDF.values()) {
            myAgreements.add(new GordianCoreAgreementSpec(pKeyPairSpec, pAgreementType, myKDF, pConfirm));
        }

        /* Return the list */
        return myAgreements;
    }
}
