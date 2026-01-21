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
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;

import java.io.IOException;
import java.math.BigInteger;

/**
 * CA Status.
 */
public class GordianCAStatus {
    /**
     * Is this a CA.
     */
    private final boolean isCA;

    /**
     * The Path Length.
     */
    private final BigInteger thePathLen;

    /**
     * Constructor.
     *
     * @param pCA is this a CA?
     */
    GordianCAStatus(final boolean pCA) {
        isCA = pCA;
        thePathLen = null;
    }

    /**
     * Constructor.
     *
     * @param pPathLen the path length.
     */
    GordianCAStatus(final BigInteger pPathLen) {
        isCA = true;
        thePathLen = pPathLen;
    }

    /**
     * Constructor.
     *
     * @param pUsage        the keyPair usage
     * @param pSignerStatus the signerStatus.
     */
    GordianCAStatus(final GordianKeyPairUsage pUsage,
                    final GordianCAStatus pSignerStatus) {
        isCA = pUsage.getUsageSet().contains(GordianKeyPairUse.CERTIFICATE);
        if (isCA) {
            final BigInteger mySignerPath = pSignerStatus.getPathLen();
            thePathLen = mySignerPath == null
                    ? BigInteger.ZERO
                    : mySignerPath.add(BigInteger.ONE);
        } else {
            thePathLen = null;
        }
    }

    /**
     * is this a CA?.
     *
     * @return true/false
     */
    boolean isCA() {
        return isCA;
    }

    /**
     * Obtain the pathLen.
     *
     * @return the pathLen
     */
    BigInteger getPathLen() {
        return thePathLen;
    }

    /**
     * Create extensions.
     *
     * @param pGenerator the extensions generator
     * @throws GordianException on error
     */
    void createExtensions(final ExtensionsGenerator pGenerator) throws GordianException {
        /* Protect against exceptions */
        try {
            pGenerator.addExtension(Extension.basicConstraints, isCA, thePathLen == null
                    ? new BasicConstraints(isCA)
                    : new BasicConstraints(thePathLen.intValue()));
        } catch (IOException e) {
            throw new GordianIOException("Failed to create extensions", e);
        }
    }

    /**
     * Determine CAStatus.
     *
     * @param pExtensions the extensions.
     * @return the CAStatus
     */
    static GordianCAStatus determineStatus(final Extensions pExtensions) {
        /* Access details */
        final BasicConstraints myConstraint = BasicConstraints.fromExtensions(pExtensions);

        /* Check for CA */
        if (myConstraint != null && myConstraint.isCA()) {
            return new GordianCAStatus(myConstraint.getPathLenConstraint());
        }

        /* Not CA */
        return new GordianCAStatus(false);
    }
}
