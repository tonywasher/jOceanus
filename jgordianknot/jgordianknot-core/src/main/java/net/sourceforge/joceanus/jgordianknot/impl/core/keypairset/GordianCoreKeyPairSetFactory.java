/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypairset;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetAgreement;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSignature;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * CoreKeyPairSet Factory.
 */
public class GordianCoreKeyPairSetFactory
    implements GordianKeyPairSetFactory {
    /**
     * The AsymFactory.
     */
    private final GordianKeyPairFactory theFactory;

    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianKeyPairSetSpec, GordianKeyPairSetGenerator> theCache;

    /**
     * Constructor.
     * @param pFactory the AsymFactory
     */
    public GordianCoreKeyPairSetFactory(final GordianKeyPairFactory pFactory) {
        /* Store parameters */
        theFactory = pFactory;

        /* Create the cache */
        theCache = new EnumMap<>(GordianKeyPairSetSpec.class);
    }

    @Override
    public GordianKeyPairSetGenerator getKeyPairSetGenerator(final GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException {
        /* Look up in the cache */
        GordianKeyPairSetGenerator myGenerator = theCache.get(pKeyPairSetSpec);
        if (myGenerator == null) {
            /* Check valid spec */
            if (!supportedKeyPairSetSpecs().test(pKeyPairSetSpec)) {
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeyPairSetSpec));
            }

            /* Create the new generator */
            myGenerator = new GordianCoreKeyPairSetGenerator(theFactory, pKeyPairSetSpec);

            /* Add to cache */
            theCache.put(pKeyPairSetSpec, myGenerator);
        }
        return myGenerator;
    }

    @Override
    public GordianKeyPairSetSpec determineKeyPairSetSpec(final PKCS8EncodedKeySpec pEncoded) throws OceanusException {
        return GordianKeyPairSetAlgId.determineKeyPairSetSpec(pEncoded);
    }

    @Override
    public GordianKeyPairSetSpec determineKeyPairSetSpec(final X509EncodedKeySpec pEncoded) throws OceanusException {
        return GordianKeyPairSetAlgId.determineKeyPairSetSpec(pEncoded);
    }

    @Override
    public Predicate<GordianKeyPairSetSpec> supportedKeyPairSetSpecs() {
        return Objects::nonNull;
    }

    @Override
    public GordianKeyPairSetAgreement createAgreement(final GordianKeyPairSetAgreementSpec pAgreementSpec) throws OceanusException {
        /* Check valid spec */
        if (pAgreementSpec == null || !pAgreementSpec.isValid()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pAgreementSpec));
        }

        /* Switch on agreementType */
        final GordianCoreFactory myFactory = (GordianCoreFactory) theFactory.getFactory();
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:
                return new GordianCoreKeyPairSetAnonymousAgreement(myFactory, pAgreementSpec);
            case SIGNED:
                return new GordianCoreKeyPairSetSignedAgreement(myFactory, pAgreementSpec);
            case UNIFIED:
                return new GordianCoreKeyPairSetHandshakeAgreement(myFactory, pAgreementSpec);
            default:
                throw new GordianLogicException(GordianCoreFactory.getInvalidText(pAgreementSpec));
        }
    }

    @Override
    public GordianKeyPairSetAgreement createAgreement(final byte[] pClientHello) throws OceanusException {
        /* Parse the client hello message */
        final GordianKeyPairSetAgreeASN1 myASN1 = GordianKeyPairSetAgreeASN1.getInstance(pClientHello);
        final GordianKeyPairSetAgreementSpec mySpec = myASN1.getSpec();
        return createAgreement(mySpec);
    }

    @Override
    public GordianKeyPairSetEncryptor createEncryptor(final GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException {
        /* Check valid spec */
        if (!supportedKeyPairSetSpecs().test(pKeyPairSetSpec)
            || !pKeyPairSetSpec.canEncrypt()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeyPairSetSpec));
        }

        /* Create the new encryptor */
        return new GordianCoreKeyPairSetEncryptor(theFactory, pKeyPairSetSpec);
    }

    @Override
    public GordianKeyPairSetSignature createSigner(final GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException {
        /* Check valid spec */
        if (!supportedKeyPairSetSpecs().test(pKeyPairSetSpec)
            || !pKeyPairSetSpec.canSign()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeyPairSetSpec));
        }

        /* Create the new signer */
        return new GordianKeyPairSetSigner(theFactory, pKeyPairSetSpec);
    }
}
