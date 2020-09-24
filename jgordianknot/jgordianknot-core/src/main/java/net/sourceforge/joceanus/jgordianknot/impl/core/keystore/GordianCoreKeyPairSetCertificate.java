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

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSignature;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianCoreKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianCoreKeyPairSetGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianKeyPairSetAlgId;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * keyPairSet Certificate.
 */
public class GordianCoreKeyPairSetCertificate
        extends GordianCoreCertificate<GordianKeyPairSetSpec, GordianKeyPairSet>
        implements GordianKeyPairSetCertificate {
    /**
     * Create a new self-signed certificate.
     *
     * @param pFactory the factory
     * @param pKeyPairSet the keyPairSet
     * @param pSubject the name of the entity
     * @throws OceanusException on error
     */
    GordianCoreKeyPairSetCertificate(final GordianCoreFactory pFactory,
                                     final GordianCoreKeyPairSet pKeyPairSet,
                                     final X500Name pSubject) throws OceanusException {
        super(pFactory, pKeyPairSet, pSubject);
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
    GordianCoreKeyPairSetCertificate(final GordianCoreFactory pFactory,
                                     final GordianKeyStorePairSet pSigner,
                                     final GordianKeyPairSet pKeyPair,
                                     final X500Name pSubject,
                                     final GordianKeyPairUsage pUsage) throws OceanusException {
        super(pFactory, pSigner, pKeyPair, pSubject, pUsage);
    }

    /**
     * Parse a certificate.
     *
     * @param pFactory    the factory
     * @param pSequence   the DER representation of the certificate
     * @throws OceanusException on error
     */
    public GordianCoreKeyPairSetCertificate(final GordianCoreFactory pFactory,
                                            final byte[] pSequence) throws OceanusException {
        super(pFactory, pSequence);
    }

    @Override
    protected boolean isPublicOnly(final GordianKeyPairSet pKeyPairSet) {
        return pKeyPairSet.isPublicOnly();
    }

    @Override
    protected GordianKeyPairSet getPublicOnly(final GordianKeyPairSet pKeyPairSet) {
        return ((GordianCoreKeyPairSet) pKeyPairSet).getPublicOnly();
    }

    @Override
    GordianKeyPairSetSpec determineSignatureSpecForKeyPair(final GordianKeyPairSet pKeyPairSet) {
        return pKeyPairSet.getKeyPairSetSpec();
    }

    @Override
    GordianKeyPairSetSpec determineSignatureSpecForAlgId(final AlgorithmIdentifier pAlgId) {
        return GordianKeyPairSetAlgId.lookUpKeyPairSetSpec(pAlgId);
    }

    @Override
    AlgorithmIdentifier determineAlgIdForSignatureSpec(final GordianKeyPairSetSpec pSpec,
                                                       final GordianKeyPairSet pSigner) {
        return GordianKeyPairSetAlgId.determineAlgorithmId(pSpec);
    }

    @Override
    protected GordianKeyPairSet parseEncodedKey() throws OceanusException {
        /* Derive the keyPair */
        final GordianKeyPairSetFactory myFactory = getFactory().getKeyPairFactory().getKeyPairSetFactory();
        final X509EncodedKeySpec myX509 = getX509KeySpec();
        final GordianKeyPairSetSpec myKeySpec = myFactory.determineKeyPairSetSpec(myX509);
        final GordianCoreKeyPairSetGenerator myGenerator = (GordianCoreKeyPairSetGenerator) myFactory.getKeyPairSetGenerator(myKeySpec);
        return myGenerator.derivePublicOnlyKeyPairSet(myX509);
    }

    @Override
    boolean checkMatchingPublicKey(final GordianKeyPairSet pSet) {
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pSet;
        final GordianCoreKeyPairSet myPublic = mySet.getPublicOnly();
        return myPublic.equals(getKeyPair());
    }

    @Override
    protected byte[] getPublicKeyEncoded() throws OceanusException {
        /* Access the keyPairSet */
        final GordianKeyPairSet mySet = getKeyPair();

        /* Access the keyPair generator */
        final GordianKeyPairSetFactory myFactory = getFactory().getKeyPairFactory().getKeyPairSetFactory();
        final GordianKeyPairSetGenerator myGenerator = myFactory.getKeyPairSetGenerator(mySet.getKeyPairSetSpec());

        /* Obtain the publicKey Info */
        return myGenerator.getX509Encoding(mySet).getEncoded();
    }

    @Override
    protected GordianDigestSpec getDigestSpec() {
        return GordianDigestSpec.sha2(GordianLength.LEN_512);
    }

    @Override
    protected GordianKeyPairSetSignature createSigner() throws OceanusException {
        /* Create the signer */
        final GordianKeyPairSetFactory mySigns = getFactory().getKeyPairFactory().getKeyPairSetFactory();
        return mySigns.createSigner(getSignatureSpec());
    }
}
