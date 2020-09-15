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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetGenerator;
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
    private final GordianAsymFactory theFactory;

    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianKeyPairSetSpec, GordianKeyPairSetGenerator> theCache;

    /**
     * Constructor.
     * @param pFactory the AsymFactory
     */
    public GordianCoreKeyPairSetFactory(final GordianAsymFactory pFactory) {
        /* Store parameters */
        theFactory = pFactory;

        /* Create the cache */
        theCache = new HashMap<>();
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

    /**
     * create keyPairSetAgreement.
     * @param pAgreementSpec the keyPairSetSpec
     * @return the encryptor
     * @throws OceanusException on error
     */
    public Object getKeyPairSetAgreement(final GordianAgreementSpec pAgreementSpec) throws OceanusException {
        /* Switch on agreementType */
        final GordianCoreFactory myFactory = (GordianCoreFactory) theFactory.getFactory();
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:
                return new GordianKeyPairSetAnonymousAgreement(myFactory, pAgreementSpec);
            case SIGNED:
                return new GordianKeyPairSetSignedAgreement(myFactory, pAgreementSpec);
            case UNIFIED:
                return new GordianKeyPairSetHandshakeAgreement(myFactory, pAgreementSpec);
            default:
                throw new GordianLogicException(GordianCoreFactory.getInvalidText(pAgreementSpec));
        }
    }

    /**
     * create keyPairSetEncryptor.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @return the encryptor
     * @throws OceanusException on error
     */
    public GordianKeyPairSetEncryptor getKeyPairSetEncryptor(final GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException {
        /* Check valid spec */
        if (!supportedKeyPairSetSpecs().test(pKeyPairSetSpec)
            || !pKeyPairSetSpec.canEncrypt()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeyPairSetSpec));
        }

        /* Create the new encryptor */
        return new GordianKeyPairSetEncryptor(theFactory, pKeyPairSetSpec);
    }

    /**
     * create keyPairSetSigner.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @return the encryptor
     * @throws OceanusException on error
     */
    public GordianKeyPairSetSigner getKeyPairSetSigner(final GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException {
        /* Check valid spec */
        if (!supportedKeyPairSetSpecs().test(pKeyPairSetSpec)
            || !pKeyPairSetSpec.canSign()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeyPairSetSpec));
        }

        /* Create the new signer */
        return new GordianKeyPairSetSigner(theFactory, pKeyPairSetSpec);
    }
}
