/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.kems.ECIESKeyEncapsulation;
import org.bouncycastle.crypto.kems.RSAKeyEncapsulation;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * BouncyCastle Key Encapsulation.
 */
public abstract class BouncyKeyEncapsulation {
    /**
     * InitVectorLength.
     */
    private static final int INITLEN = 16;

    /**
     * CipherLength.
     */
    private static final int CIPHERLEN = 256;

    /**
     * Private Constructor.
     */
    private BouncyKeyEncapsulation() {
    }

    /**
     * ClientRSA Encapsulation.
     */
    public static class BouncyRSAKEMSender
            extends GordianKEMSender {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPublicKey the target publicKey
         * @throws OceanusException on error
         */
        protected BouncyRSAKEMSender(final BouncyFactory pFactory,
                                     final BouncyRSAPublicKey pPublicKey) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Create Key Encapsulation */
            BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDefaultDigest());
            RSAKeyEncapsulation myKEMS = new RSAKeyEncapsulation(myKDF, getRandom());

            /* Initialise the generator */
            myKEMS.init(pPublicKey.getPublicKey());

            /* Create initVector */
            byte[] myInitVector = new byte[INITLEN];
            getRandom().nextBytes(myInitVector);

            /* Create cipherText */
            int myLen = CIPHERLEN;
            byte[] myCipherText = new byte[myLen + INITLEN];
            KeyParameter myParms = (KeyParameter) myKEMS.encrypt(myCipherText, INITLEN, myKDF.getKeyLen());
            System.arraycopy(myInitVector, 0, myCipherText, 0, INITLEN);

            /* Store secret and cipherText */
            storeSecret(myParms.getKey(), myInitVector);
            storeCipherText(myCipherText);
        }
    }

    /**
     * ServerRSA Encapsulation.
     */
    public static class BouncyRSAKEMReceiver
            extends GordianKeyEncapsulation {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPrivateKey the target privateKey
         * @param pCipherText the cipherText
         * @throws OceanusException on error
         */
        protected BouncyRSAKEMReceiver(final BouncyFactory pFactory,
                                       final BouncyRSAPrivateKey pPrivateKey,
                                       final byte[] pCipherText) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Create Key Encapsulation */
            BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDefaultDigest());
            RSAKeyEncapsulation myKEMS = new RSAKeyEncapsulation(myKDF, null);

            /* Initialise the generator */
            myKEMS.init(pPrivateKey.getPrivateKey());

            /* Obtain initVector */
            byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pCipherText, 0, myInitVector, 0, INITLEN);

            /* Parse cipherText */
            KeyParameter myParms = (KeyParameter) myKEMS.decrypt(pCipherText, INITLEN, pCipherText.length - INITLEN, myKDF.getKeyLen());

            /* Store secret */
            storeSecret(myParms.getKey(), myInitVector);
        }
    }

    /**
     * ClientECIES Encapsulation.
     */
    public static class BouncyECIESSender
            extends GordianKEMSender {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPublicKey the target publicKey
         * @throws OceanusException on error
         */
        protected BouncyECIESSender(final BouncyFactory pFactory,
                                    final BouncyECPublicKey pPublicKey) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Create Key Encapsulation */
            BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDefaultDigest());
            ECIESKeyEncapsulation myKEMS = new ECIESKeyEncapsulation(myKDF, getRandom());

            /* Initialise the generator */
            myKEMS.init(pPublicKey.getPublicKey());

            /* Create initVector */
            byte[] myInitVector = new byte[INITLEN];
            getRandom().nextBytes(myInitVector);

            /* Create cipherText */
            int myLen = CIPHERLEN;
            byte[] myCipherText = new byte[myLen + INITLEN];
            KeyParameter myParms = (KeyParameter) myKEMS.encrypt(myCipherText, INITLEN, myKDF.getKeyLen());
            System.arraycopy(myInitVector, 0, myCipherText, 0, INITLEN);

            /* Store secret and cipherText */
            storeSecret(myParms.getKey(), myInitVector);
            storeCipherText(myCipherText);
        }
    }

    /**
     * ServerECIES Encapsulation.
     */
    public static class BouncyECIESReceiver
            extends GordianKeyEncapsulation {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPrivateKey the target privateKey
         * @param pCipherText the cipherText
         * @throws OceanusException on error
         */
        protected BouncyECIESReceiver(final BouncyFactory pFactory,
                                      final BouncyECPrivateKey pPrivateKey,
                                      final byte[] pCipherText) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Create Key Encapsulation */
            BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDefaultDigest());
            ECIESKeyEncapsulation myKEMS = new ECIESKeyEncapsulation(myKDF, null);

            /* Initialise the generator */
            myKEMS.init(pPrivateKey.getPrivateKey());

            /* Obtain initVector */
            byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pCipherText, 0, myInitVector, 0, INITLEN);

            /* Parse cipherText */
            KeyParameter myParms = (KeyParameter) myKEMS.decrypt(pCipherText, INITLEN, pCipherText.length - INITLEN, myKDF.getKeyLen());

            /* Store secret */
            storeSecret(myParms.getKey(), myInitVector);
        }
    }

    /**
     * KeyDerivation.
     */
    private static final class BouncyKeyDerivation
            implements DerivationFunction {
        /**
         * Digest.
         */
        private final GordianDigest theDigest;

        /**
         * Constructor.
         * @param pDigest the security digest
         */
        private BouncyKeyDerivation(final GordianDigest pDigest) {
            theDigest = pDigest;
        }

        /**
         * Obtain the key length.
         * @return the keyLen
         */
        private int getKeyLen() {
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
