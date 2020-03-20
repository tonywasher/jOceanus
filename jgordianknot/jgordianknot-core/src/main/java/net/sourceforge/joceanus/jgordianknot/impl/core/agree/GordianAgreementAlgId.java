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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;

/**
 * Mappings from EncodedId to AgreementSpec.
 */
public class GordianAgreementAlgId {
    /**
     * AgreementOID branch.
     */
    private static final ASN1ObjectIdentifier AGREEOID = GordianASN1Util.ASYMOID.branch("2");

    /**
     * Map of DigestSpec to Identifier.
     */
    private final Map<GordianAgreementSpec, AlgorithmIdentifier> theSpecMap;

    /**
     * Map of Identifier to AgreementSpec.
     */
    private final Map<AlgorithmIdentifier, GordianAgreementSpec> theIdentifierMap;

    /**
     * The factory.
     */
    private final GordianAgreementFactory theFactory;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianAgreementAlgId(final GordianCoreFactory pFactory) {
        /* Create the maps */
        theSpecMap = new HashMap<>();
        theIdentifierMap = new HashMap<>();

        /* Access the agreementFactory  */
        theFactory = pFactory.getAsymmetricFactory().getAgreementFactory();

        /* Loop through the possible AsymKeys */
        for (GordianAsymKeyType myKeyType : GordianAsymKeyType.values()) {
            /* Add any non-standard agreementSpecs */
            addAgreements(myKeyType);
        }
    }

    /**
     * Obtain Identifier for AgreementSpec.
     *
     * @param pSpec the agreementSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianAgreementSpec pSpec) {
        return theSpecMap.get(pSpec);
    }

    /**
     * Obtain AgreementSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the agreementSpec (or null if not found)
     */
    public GordianAgreementSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return theIdentifierMap.get(pIdentifier);
    }

    /**
     * Create Identifiers for all valid AgreementTypes.
     * @param pKeyType the keyType
     */
    private void addAgreements(final GordianAsymKeyType pKeyType) {
        for (GordianAgreementSpec mySpec : theFactory.listAllSupportedAgreements(pKeyType)) {
            ensureAgreement(mySpec);
        }
    }

    /**
     * Add agreementSpec to map if supported and not already present.
     * @param pSpec the agreementSpec
     */
    private void ensureAgreement(final GordianAgreementSpec pSpec) {
        /* If the encryptor is not already known */
        if (!theSpecMap.containsKey(pSpec)) {
            addAgreement(pSpec);
        }
    }

    /**
     * Create Identifier for an agreementSpec.
     *
     * @param pSpec the agreementSpec
     */
    private void addAgreement(final GordianAgreementSpec pSpec) {
        /* Create a branch for mac based on the AgreementType */
        final GordianAsymKeyType myKeyType = pSpec.getAsymKeyType();
        ASN1ObjectIdentifier myId = AGREEOID.branch(Integer.toString(myKeyType.ordinal() + 1));

        /* Add branch for agreementType */
        final GordianAgreementType myType = pSpec.getAgreementType();
        myId = myId.branch(Integer.toString(myType.ordinal() + 1));

        /* Add branch for kdfType */
        final GordianKDFType myKDFType = pSpec.getKDFType();
        myId = myId.branch(Integer.toString(myKDFType.ordinal() + 1));

        /* Add the spec to the maps */
        addToMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }

    /**
     * Add agreement to maps.
     * @param pSpec the agreementSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianAgreementSpec pSpec,
                           final AlgorithmIdentifier pIdentifier) {
        theSpecMap.put(pSpec, pIdentifier);
        theIdentifierMap.put(pIdentifier, pSpec);
    }
}
