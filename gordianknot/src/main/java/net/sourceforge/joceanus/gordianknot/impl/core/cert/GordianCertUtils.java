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

package net.sourceforge.joceanus.gordianknot.impl.core.cert;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.KeyUsage;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Certificate utilities.
 */
public final class GordianCertUtils {
    /**
     * Private constructor.
     */
    GordianCertUtils() {
    }

    /**
     * Create New Serial#.
     *
     * @return the new serial#
     */
    static BigInteger newSerialNo() {
        final long myNow = System.currentTimeMillis();
        return BigInteger.valueOf(myNow);
    }

    /**
     * Determine usage.
     *
     * @param pExtensions the extensions.
     * @return the usage
     */
    public static GordianKeyPairUsage determineUsage(final Extensions pExtensions) {
        /* Access details */
        final KeyUsage myUsage = KeyUsage.fromExtensions(pExtensions);
        final BasicConstraints myConstraint = BasicConstraints.fromExtensions(pExtensions);
        final GordianKeyPairUsage myResult = new GordianKeyPairUsage();

        /* Check for CERTIFICATE */
        final boolean isCA = myConstraint != null && myConstraint.isCA();
        if (isCA && checkUsage(myUsage, KeyUsage.keyCertSign)) {
            myResult.addUse(GordianKeyPairUse.CERTIFICATE);
        }

        /* Check for signer. */
        if (checkUsage(myUsage, KeyUsage.digitalSignature)) {
            myResult.addUse(GordianKeyPairUse.SIGNATURE);
        }

        /* Check for nonRepudiation. */
        if (checkUsage(myUsage, KeyUsage.nonRepudiation)) {
            myResult.addUse(GordianKeyPairUse.NONREPUDIATION);
        }

        /* Check for keyAgreement. */
        if (checkUsage(myUsage, KeyUsage.keyAgreement)) {
            myResult.addUse(GordianKeyPairUse.AGREEMENT);
        }

        /* Check for keyEncryption. */
        if (checkUsage(myUsage, KeyUsage.keyEncipherment)) {
            myResult.addUse(GordianKeyPairUse.KEYENCRYPT);
        }

        /* Check for dataEncryption. */
        if (checkUsage(myUsage, KeyUsage.dataEncipherment)) {
            myResult.addUse(GordianKeyPairUse.DATAENCRYPT);
        }

        /* Check for encipherOnly. */
        if (checkUsage(myUsage, KeyUsage.encipherOnly)) {
            myResult.addUse(GordianKeyPairUse.ENCRYPTONLY);
        }

        /* Check for decipherOnly. */
        if (checkUsage(myUsage, KeyUsage.decipherOnly)) {
            myResult.addUse(GordianKeyPairUse.DECRYPTONLY);
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Check for usage.
     *
     * @param pUsage    the usage control
     * @param pRequired the required usage
     * @return true/false
     */
    private static boolean checkUsage(final KeyUsage pUsage,
                                      final int pRequired) {
        return pUsage == null || pUsage.hasUsages(pRequired);
    }

    /**
     * Create extensions for tbsCertificate.
     *
     * @param pCAStatus the CA status
     * @param pUsage    the keyPair usage
     * @return the extensions
     * @throws GordianException on error
     */
    static Extensions createExtensions(final GordianCAStatus pCAStatus,
                                       final GordianKeyPairUsage pUsage) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Create extensions for the certificate */
            final ExtensionsGenerator myGenerator = new ExtensionsGenerator();
            myGenerator.addExtension(Extension.keyUsage, true, pUsage.getKeyUsage());
            pCAStatus.createExtensions(myGenerator);
            return myGenerator.generate();

        } catch (IOException e) {
            throw new GordianIOException("Failed to create extensions", e);
        }
    }

    /**
     * Create extensions.
     *
     * @param pUsage the usage
     * @return the extensions
     * @throws GordianException on error
     */
    static Extensions createExtensions(final GordianKeyPairUsage pUsage) throws GordianException {
        /* Protect against exceptions */
        try {
            final ExtensionsGenerator myGenerator = new ExtensionsGenerator();
            myGenerator.addExtension(Extension.keyUsage, true, pUsage.getKeyUsage());
            return myGenerator.generate();
        } catch (IOException e) {
            throw new GordianIOException("Failed to create extensions", e);
        }
    }
}
