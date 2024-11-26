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
package net.sourceforge.joceanus.gordianknot.impl.jca;

import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.oceanus.OceanusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Jca Digest Factory.
 */
public class JcaDigestFactory
        extends GordianCoreDigestFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaDigestFactory(final GordianCoreFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    public JcaFactory getFactory() {
        return (JcaFactory) super.getFactory();
    }

    @Override
    public JcaDigest createDigest(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Check validity of DigestSpec */
        checkDigestSpec(pDigestSpec);

        /* Create digest */
        final MessageDigest myJavaDigest = getJavaDigest(pDigestSpec);
        return new JcaDigest(pDigestSpec, myJavaDigest);
    }

    /**
     * Create the BouncyCastle digest via JCA.
     * @param pDigestSpec the digestSpec
     * @return the digest
     * @throws OceanusException on error
     */
    private static MessageDigest getJavaDigest(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a digest for the algorithm */
            return MessageDigest.getInstance(JcaDigest.getFullAlgorithm(pDigestSpec), JcaFactory.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Digest", e);
        }
    }

    @Override
    public boolean validDigestType(final GordianDigestType pDigestType) {
        /* Perform standard checks */
        if (!super.validDigestType(pDigestType)) {
            return false;
        }

        /* Disable non-JCE digests */
        switch (pDigestType) {
            case JH:
            case GROESTL:
            case CUBEHASH:
            case KANGAROO:
            case ASCON:
            case ISAP:
            case PHOTONBEETLE:
            case SPARKLE:
            case XOODYAK:
                return false;
            default:
                return true;
        }
    }

    @Override
    public boolean validDigestSpec(final GordianDigestSpec pDigestSpec) {
        /* Perform standard checks */
        if (!super.validDigestSpec(pDigestSpec)) {
            return false;
        }

        /* OK */
        return true;
    }
}