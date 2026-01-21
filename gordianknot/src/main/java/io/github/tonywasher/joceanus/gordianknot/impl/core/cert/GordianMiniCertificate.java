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
package io.github.tonywasher.joceanus.gordianknot.impl.core.cert;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificateId;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/**
 * Mini Certificate implementation.
 */
public class GordianMiniCertificate
        implements GordianCertificate {
    /**
     * Subject of certificate.
     */
    private final GordianCertificateId theSubject;

    /**
     * keyPair of certificate.
     */
    private final GordianKeyPair theKeyPair;

    /**
     * Subject of certificate.
     */
    private final GordianKeyPairUsage theUsage;

    /**
     * The encoded representation.
     */
    private final byte[] theEncoded;

    /**
     * The ASN1 representation.
     */
    private final GordianMiniCertificateASN1 theASN1;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSubject the subject of the certificate
     * @param pKeyPair the keyPair.
     * @param pUsage   the usage
     * @throws GordianException on error
     */
    public GordianMiniCertificate(final GordianFactory pFactory,
                                  final X500Name pSubject,
                                  final GordianKeyPair pKeyPair,
                                  final GordianKeyPairUsage pUsage) throws GordianException {
        /* Store parameters */
        theKeyPair = pKeyPair;
        theUsage = pUsage;

        /* Access the keyPairFactory */
        final GordianKeyPairFactory myFactory = pFactory.getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeyPair.getKeyPairSpec());
        final X509EncodedKeySpec myX509Spec = myGenerator.getX509Encoding(pKeyPair);
        theSubject = new GordianCoreCertificateId(pSubject, null);

        /* Create the encoded */
        theASN1 = new GordianMiniCertificateASN1(pSubject, myX509Spec, pUsage);
        theEncoded = theASN1.getEncodedBytes();
    }

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pEncoded the encoded bytes
     * @throws GordianException on error
     */
    public GordianMiniCertificate(final GordianFactory pFactory,
                                  final byte[] pEncoded) throws GordianException {
        /* Parse the bytes */
        this(pFactory, GordianMiniCertificateASN1.getInstance(pEncoded));
    }

    /**
     * Constructor.
     *
     * @param pFactory the factory.
     * @param pASN1    the ASN1
     * @throws GordianException on error
     */
    public GordianMiniCertificate(final GordianFactory pFactory,
                                  final GordianMiniCertificateASN1 pASN1) throws GordianException {
        /* Parse the bytes */
        theASN1 = pASN1;
        theEncoded = theASN1.getEncodedBytes();

        /* Derive the keyPair */
        final GordianKeyPairFactory myFactory = pFactory.getAsyncFactory().getKeyPairFactory();
        final X509EncodedKeySpec myX509Spec = theASN1.getPublicKey();
        final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(myX509Spec);
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myKeySpec);
        theKeyPair = myGenerator.derivePublicOnlyKeyPair(myX509Spec);

        /* Store subject and usage */
        theSubject = new GordianCoreCertificateId(theASN1.getSubject(), null);
        theUsage = theASN1.getUsage();
    }

    @Override
    public GordianCertificateId getSubject() {
        return theSubject;
    }

    @Override
    public GordianCertificateId getIssuer() {
        return null;
    }

    @Override
    public GordianKeyPair getKeyPair() {
        return theKeyPair;
    }

    @Override
    public byte[] getEncoded() {
        return theEncoded.clone();
    }

    @Override
    public boolean isValidOnDate(final Date pDate) {
        return true;
    }

    @Override
    public boolean isSelfSigned() {
        return false;
    }

    @Override
    public GordianKeyPairUsage getUsage() {
        return theUsage;
    }

    @Override
    public boolean validateCertificate(final GordianCertificate pSigner) throws GordianException {
        return true;
    }

    /**
     * Obtain the ASN1.
     *
     * @return the ASN1
     */
    public GordianMiniCertificateASN1 getASN1() {
        return theASN1;
    }
}
