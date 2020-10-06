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
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianKeyPairSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * keyPairSet Certificate.
 */
public class GordianCoreKeyPairCertificate
        extends GordianCoreCertificate<GordianSignatureSpec, GordianKeyPair>
        implements GordianKeyPairCertificate {
    /**
     * Create a new self-signed certificate.
     *
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @param pSubject the name of the entity
     * @throws OceanusException on error
     */
    GordianCoreKeyPairCertificate(final GordianCoreFactory pFactory,
                                  final GordianCoreKeyPair pKeyPair,
                                  final X500Name pSubject) throws OceanusException {
        super(pFactory, pKeyPair, pSubject);
    }

    /**
     * Create a new certificate, signed by the relevant authority.
     *
     * @param pFactory the factory
     * @param pSigner  the signing keyPair/certificate
     * @param pKeyPair the keyPair
     * @param pSubject the name of the entity
     * @param pUsage   the key usage
     * @throws OceanusException on error
     */
    GordianCoreKeyPairCertificate(final GordianCoreFactory pFactory,
                                  final GordianKeyStorePair pSigner,
                                  final GordianKeyPair pKeyPair,
                                  final X500Name pSubject,
                                  final GordianKeyPairUsage pUsage) throws OceanusException {
        super(pFactory, pSigner, pKeyPair, pSubject, pUsage);
    }

    /**
     * Parse a certificate.
     *
     * @param pFactory  the factory
     * @param pSequence the DER representation of the certificate
     * @throws OceanusException on error
     */
    public GordianCoreKeyPairCertificate(final GordianCoreFactory pFactory,
                                         final byte[] pSequence) throws OceanusException {
        super(pFactory, pSequence);
    }


    @Override
    protected boolean isPublicOnly(final GordianKeyPair pKeyPair) {
        return pKeyPair.isPublicOnly();
    }

    @Override
    protected GordianKeyPair getPublicOnly(final GordianKeyPair pKeyPair) {
        return ((GordianCoreKeyPair) pKeyPair).getPublicOnly();
    }

    @Override
    GordianSignatureSpec determineSignatureSpecForKeyPair(final GordianKeyPair pKeyPair) {
        return GordianSignatureSpec.defaultForKey(pKeyPair.getKeyPairSpec());
    }

    @Override
    GordianSignatureSpec determineSignatureSpecForAlgId(final AlgorithmIdentifier pAlgId) {
        final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) getFactory().getKeyPairFactory().getSignatureFactory();
        return mySigns.getSpecForIdentifier(pAlgId);
    }

    @Override
    AlgorithmIdentifier determineAlgIdForSignatureSpec(final GordianSignatureSpec pSpec,
                                                       final GordianKeyPair pSigner) {
        final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) getFactory().getKeyPairFactory().getSignatureFactory();
        return mySigns.getIdentifierForSpecAndKeyPair(getSignatureSpec(), pSigner);
    }

    @Override
    boolean checkMatchingPublicKey(final GordianKeyPair pPair) {
        final GordianCoreKeyPair myPair = (GordianCoreKeyPair) pPair;
        return myPair.getPublicKey().equals(((GordianCoreKeyPair) getKeyPair()).getPublicKey());
    }

    @Override
    protected GordianKeyPair parseEncodedKey() throws OceanusException {
        /* Derive the keyPair */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final X509EncodedKeySpec myX509 = getX509KeySpec();
        final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(myX509);
        final GordianCoreKeyPairGenerator myGenerator = (GordianCoreKeyPairGenerator) myFactory.getKeyPairGenerator(myKeySpec);
        return myGenerator.derivePublicOnlyKeyPair(myX509);
    }

    @Override
    protected byte[] getPublicKeyEncoded() throws OceanusException {
        /* Access the keyPair */
        final GordianKeyPair myPair = getKeyPair();

        /* Access the keyPair generator */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myPair.getKeyPairSpec());

        /* Obtain the publicKey Info */
        return myGenerator.getX509Encoding(myPair).getEncoded();
    }

    @Override
    protected GordianDigestSpec getDigestSpec() {
        return getSignatureSpec().getDigestSpec();
    }

    @Override
    protected GordianKeyPairSignature createSigner() throws OceanusException {
        /* Create the signer */
        final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) getFactory().getKeyPairFactory().getSignatureFactory();
        return mySigns.createKeyPairSigner(getSignatureSpec());
    }
}
