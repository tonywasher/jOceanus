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

package net.sourceforge.joceanus.gordianknot.impl.core.xagree;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.KMAC;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

/**
 * Derivation Classes.
 */
public class GordianXCoreAgreementDerivation {
    /**
     * Derivation functional interface.
     */
    private interface GordianXCoreAgreementDerivationMethod {
        /**
         * derive the bytes from the secret into result buffer.
         *
         * @param pSecret the secret
         */
        void deriveBytes(byte[] pSecret);
    }

    /**
     * Empty byteArray.
     */
    private static final byte[] EMPTY = new byte[0];

    /**
     * The builder.
     */
    private final GordianXCoreAgreementBuilder theBuilder;

    /**
     * The state.
     */
    private final GordianXCoreAgreementState theState;

    /**
     * The derivation method.
     */
    private final GordianXCoreAgreementDerivationMethod theDerivation;

    /**
     * The result.
     */
    private byte[] theResult;

    /**
     * Constructor.
     *
     * @param pBuilder the builder
     */
    GordianXCoreAgreementDerivation(final GordianXCoreAgreementBuilder pBuilder) {
        theBuilder = pBuilder;
        theState = theBuilder.getState();
        theDerivation = derivationMethod();
    }

    /**
     * derive the key from the secret.
     *
     * @param pSecret the secret
     */
    void deriveBytes(final byte[] pSecret) throws GordianException {
        /* Protect against failure */
        allocateResult(pSecret);
        try {
            /* Derive the secret and store i */
            theDerivation.deriveBytes(pSecret);
            theBuilder.storeSecret(theResult);

            /* Clear the secret */
        } finally {
            Arrays.fill(theResult, (byte) 0);
        }
    }

    /**
     * determine derivation method.
     *
     * @return the derivation method
     */
    private GordianXCoreAgreementDerivationMethod derivationMethod() {
        switch (theState.getSpec().getKDFType()) {
            case SHA256KDF:
            case SHA512KDF:
                return this::deriveKDF;
            case SHA256CKDF:
            case SHA512CKDF:
                return this::deriveCKDF;
            case SHA256HKDF:
            case SHA512HKDF:
                return this::deriveHKDF;
            case KMAC128:
            case KMAC256:
                return this::deriveKMAC;
            case SHAKE256:
                return this::deriveSHAKE;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * derive bytes via KDF2.
     *
     * @param pSecret the secret
     */
    private void deriveKDF(final byte[] pSecret) {
        final byte[] myAdditional = getAdditional();
        final DerivationFunction myKDF = new KDF2BytesGenerator(getDigest());
        myKDF.init(new KDFParameters(pSecret, myAdditional));
        myKDF.generateBytes(theResult, 0, theResult.length);
    }

    /**
     * derive bytes via CKDF.
     *
     * @param pSecret the secret
     */
    private void deriveCKDF(final byte[] pSecret) {
        final byte[] myAdditional = getAdditional();
        final DerivationFunction myKDF = new ConcatenationKDFGenerator(getDigest());
        myKDF.init(new KDFParameters(pSecret, myAdditional));
        myKDF.generateBytes(theResult, 0, theResult.length);
    }

    /**
     * derive bytes via HKDF.
     *
     * @param pSecret the secret
     */
    private void deriveHKDF(final byte[] pSecret) {
        final byte[] myAdditional = getAdditional();
        final DerivationFunction myKDF = new HKDFBytesGenerator(getDigest());
        myKDF.init(new HKDFParameters(pSecret, null, myAdditional));
        myKDF.generateBytes(theResult, 0, theResult.length);
    }

    /**
     * derive bytes via KMAC.
     *
     * @param pSecret the secret
     */
    private void deriveKMAC(final byte[] pSecret) {
        final byte[] myAdditional = getAdditional();
        final KMAC myKMAC = new KMAC(getLength(), EMPTY);
        myKMAC.init(new KeyParameter(pSecret, 0, pSecret.length));
        myKMAC.update(myAdditional, 0, myAdditional.length);
        myKMAC.doFinal(theResult, 0, theResult.length);
    }

    /**
     * derive bytes via SHAKE.
     *
     * @param pSecret the secret
     */
    private void deriveSHAKE(final byte[] pSecret) {
        final byte[] myAdditional = getAdditional();
        final Xof mySHAKE = new SHAKEDigest(getLength());
        mySHAKE.update(pSecret, 0, pSecret.length);
        mySHAKE.update(myAdditional, 0, myAdditional.length);
        mySHAKE.doFinal(theResult, 0, theResult.length);
    }

    /**
     * Obtain the additionalInfo.
     *
     * @return the additionalData
     */
    private byte[] getAdditional() {
        final byte[] myAdditional = theState.getAdditionalData();
        return myAdditional == null ? EMPTY : myAdditional;
    }

    /**
     * Obtain length for algorithm.
     *
     * @return the length
     */
    private int getLength() {
        switch (theState.getSpec().getKDFType()) {
            case KMAC256:
            case SHAKE256:
                return GordianLength.LEN_256.getLength();
            case KMAC128:
                return GordianLength.LEN_128.getLength();
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain digest for algorithm.
     *
     * @return the digest
     */
    private Digest getDigest() {
        switch (theState.getSpec().getKDFType()) {
            case SHA256KDF:
            case SHA256CKDF:
            case SHA256HKDF:
                return new SHA256Digest();
            case SHA512KDF:
            case SHA512CKDF:
            case SHA512HKDF:
                return new SHA512Digest();
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Allocate the result buffer.
     *
     * @param pSecret the secret
     */
    private void allocateResult(final byte[] pSecret) {
        switch (theState.getSpec().getKeyPairSpec().getKeyPairType()) {
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:
            case DH:
            case XDH:
                theResult = new byte[pSecret.length];
                break;
            default:
                theResult = new byte[GordianLength.LEN_256.getByteLength()];
                break;
        }
    }

    /**
     * NullKeyDerivation.
     */
    static final class GordianXCoreNullKeyDerivation
            implements DerivationFunction {
        /**
         * The key.
         */
        private byte[] theKey;

        @Override
        public int generateBytes(final byte[] pBuffer,
                                 final int pOffset,
                                 final int pLength) {
            System.arraycopy(theKey, 0, pBuffer, pOffset, pLength);
            Arrays.fill(theKey, (byte) 0);
            return pLength;
        }

        @Override
        public void init(final DerivationParameters pParms) {
            final byte[] mySecret = ((KDFParameters) pParms).getSharedSecret();
            theKey = Arrays.copyOf(mySecret, mySecret.length);
        }
    }
}
