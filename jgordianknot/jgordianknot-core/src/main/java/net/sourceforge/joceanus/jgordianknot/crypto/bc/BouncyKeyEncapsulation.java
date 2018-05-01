/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.params.KDFParameters;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * BouncyCastle Key Encapsulation.
 */
public final class BouncyKeyEncapsulation {
    /**
     * Private Constructor.
     */
    private BouncyKeyEncapsulation() {
    }

    /**
     * Hash secret.
     * @param pSecret the sharedSecret
     * @param pDigest the digest
     * @return the hashed secret
     */
    static byte[] hashSecret(final byte[] pSecret,
                             final GordianDigest pDigest) {
        pDigest.update(pSecret);
        return pDigest.finish();
    }

    /**
     * KeyDerivation.
     */
    protected static final class BouncyKeyDerivation
            implements DerivationFunction {
        /**
         * Digest.
         */
        private final GordianDigest theDigest;

        /**
         * Constructor.
         * @param pDigest the security digest
         */
        BouncyKeyDerivation(final GordianDigest pDigest) {
            theDigest = pDigest;
        }

        /**
         * Obtain the key length.
         * @return the keyLen
         */
        int getKeyLen() {
            return theDigest.getDigestSize();
        }

        @Override
        public int generateBytes(final byte[] pBuffer,
                                 final int pOffset,
                                 final int pLength) {
            /* Protect against exceptions */
            try {
                return theDigest.finish(pBuffer, pOffset);
            } catch (OceanusException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public void init(final DerivationParameters pParms) {
            theDigest.update(((KDFParameters) pParms).getSharedSecret());
        }
    }
}
