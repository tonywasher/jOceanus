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

package io.github.tonywasher.joceanus.gordianknot.impl.bc.encrypt;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.digest.BouncyDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt.GordianCoreEncryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpec;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.params.ParametersWithRandom;

import javax.crypto.spec.PSource;
import java.util.Arrays;

/**
 * RSA Encryptor.
 */
public abstract class BouncyCoreEncryptor
        extends GordianCoreEncryptor {
    /**
     * The underlying encryptor.
     */
    private final AsymmetricBlockCipher theEncryptor;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the encryptorSpec
     * @param pEngine  the underlying engine
     * @throws GordianException on error
     */
    BouncyCoreEncryptor(final GordianBaseFactory pFactory,
                        final GordianCoreEncryptorSpec pSpec,
                        final AsymmetricBlockCipher pEngine) throws GordianException {
        /* Initialise underlying cipher */
        super(pFactory, pSpec);
        final BouncyDigest myDigest = (BouncyDigest) pFactory.getDigestFactory().createDigest(pSpec.getDigestSpec());
        theEncryptor = new OAEPEncoding(pEngine, myDigest.getDigest(), PSource.PSpecified.DEFAULT.getValue());
    }

    @Override
    protected BouncyPublicKey<?> getPublicKey() {
        return (BouncyPublicKey<?>) super.getPublicKey();
    }

    @Override
    protected BouncyPrivateKey<?> getPrivateKey() {
        return (BouncyPrivateKey<?>) super.getPrivateKey();
    }

    @Override
    public void initForEncrypt(final GordianKeyPair pKeyPair) throws GordianException {
        /* Initialise underlying cipher */
        BouncyKeyPair.checkKeyPair(pKeyPair);
        super.initForEncrypt(pKeyPair);

        /* Initialise for encryption */
        final ParametersWithRandom myParms = new ParametersWithRandom(getPublicKey().getPublicKey(), getRandom());
        theEncryptor.init(true, myParms);
    }

    @Override
    public void initForDecrypt(final GordianKeyPair pKeyPair) throws GordianException {
        /* Initialise underlying cipher */
        BouncyKeyPair.checkKeyPair(pKeyPair);
        super.initForDecrypt(pKeyPair);

        /* Initialise for decryption */
        theEncryptor.init(false, getPrivateKey().getPrivateKey());
    }

    @Override
    public byte[] encrypt(final byte[] pBytes) throws GordianException {
        /* Check that we are in encryption mode */
        checkMode(GordianEncryptMode.ENCRYPT);

        /* Encrypt the message */
        return processData(pBytes);
    }

    @Override
    public byte[] decrypt(final byte[] pBytes) throws GordianException {
        /* Check that we are in decryption mode */
        checkMode(GordianEncryptMode.DECRYPT);

        /* Decrypt the message */
        return processData(pBytes);
    }

    /**
     * Process a data buffer.
     *
     * @param pData the buffer to process
     * @return the processed buffer
     * @throws GordianException on error
     */
    private byte[] processData(final byte[] pData) throws GordianException {
        try {
            /* Create the output buffer */
            int myInLen = pData.length;
            final byte[] myOutput = new byte[getProcessedLength(myInLen)];

            /* Access input block length */
            final int myInBlockLength = theEncryptor.getInputBlockSize();

            /* Loop encrypting the blocks */
            int myInOff = 0;
            int myOutOff = 0;
            while (myInLen > 0) {
                /* Process the data */
                final int myLen = Math.min(myInLen, myInBlockLength);
                final byte[] myBlock = theEncryptor.processBlock(pData, myInOff, myLen);

                /* Copy to the output buffer */
                final int myOutLen = myBlock.length;
                System.arraycopy(myBlock, 0, myOutput, myOutOff, myOutLen);
                myOutOff += myOutLen;

                /* Move to next block */
                myInOff += myInBlockLength;
                myInLen -= myInBlockLength;
            }

            /* Return full buffer if possible */
            if (myOutOff == myOutput.length) {
                return myOutput;
            }

            /* Cut down buffer */
            final byte[] myReturn = Arrays.copyOf(myOutput, myOutOff);
            Arrays.fill(myOutput, (byte) 0);
            return myReturn;

        } catch (InvalidCipherTextException e) {
            throw new GordianCryptoException("Failed to process data", e);
        }
    }

    /**
     * Obtain the length of the buffer required for the processed output.
     *
     * @param pLength the length of input data
     * @return the number of bytes.
     */
    private int getProcessedLength(final int pLength) {
        return theEncryptor.getOutputBlockSize() * getNumBlocks(pLength, theEncryptor.getInputBlockSize());
    }

    /**
     * Obtain the number of blocks required for the length in terms of blocks.
     *
     * @param pLength      the length of data
     * @param pBlockLength the blockLength
     * @return the number of blocks.
     */
    private static int getNumBlocks(final int pLength, final int pBlockLength) {
        return (pLength + pBlockLength - 1) / pBlockLength;
    }
}
