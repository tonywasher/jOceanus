/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.cipher;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianWrapper;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataConverter;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKey;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * GordianKnot base for WrapCipher.
 * <p>
 * This class uses a variant of RFC5649 to wrap keyData. It has been modified so that it does not
 * require a 128-block cipher.
 */
public class GordianCoreWrapper
        implements GordianWrapper {
    /**
     * Wrap repeat count.
     */
    private static final int WRAP_COUNT = 6;

    /**
     * Integrity value.
     */
    private static final byte INTEGRITY_VALUE1 = (byte) 0xA6;

    /**
     * Integrity value.
     */
    private static final byte INTEGRITY_VALUE2 = (byte) 0x59;

    /**
     * Integrity modulo.
     */
    private static final int INTEGRITY_MODULO = 4;

    /**
     * The Security Factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * Underlying key.
     */
    private final GordianKey<GordianSymKeySpec> theKey;

    /**
     * Underlying cipher.
     */
    private final GordianCoreCipher<GordianSymKeySpec> theCipher;

    /**
     * The block size.
     */
    private final int theBlockLen;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKey the key
     * @param pCipher the underlying cipher
     */
    GordianCoreWrapper(final GordianBaseFactory pFactory,
                       final GordianKey<GordianSymKeySpec> pKey,
                       final GordianCoreCipher<GordianSymKeySpec> pCipher) {
        theFactory = pFactory;
        theKey = pKey;
        theCipher = pCipher;
        theBlockLen = getKeySpec().getBlockLength().getByteLength() >> 1;
    }

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pBlockLen the blockLength
     */
    protected GordianCoreWrapper(final GordianBaseFactory pFactory,
                                 final int pBlockLen) {
        theFactory = pFactory;
        theKey = null;
        theCipher = null;
        theBlockLen = pBlockLen >> 1;
    }

    @Override
    public GordianSymKeySpec getKeySpec() {
        return theCipher.getKeyType();
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianBaseFactory getFactory() {
        return theFactory;
    }

    @Override
    public byte[] secureKey(final GordianKey<?> pKeyToSecure) throws GordianException {
        /* Secure the bytes */
        final byte[] myBytes = secureBytes(((GordianCoreKey<?>) pKeyToSecure).getKeyBytes());

        /* Create the ASN1 */
        final AlgorithmIdentifier myAlgId = theFactory.getIdentifierForSpec(pKeyToSecure.getKeyType());
        final GordianWrappedKeyASN1 myASN1 = new GordianWrappedKeyASN1(myAlgId, myBytes);
        return myASN1.getEncodedBytes();
    }

    @Override
    public <T extends GordianKeySpec> GordianKey<T> deriveKey(final byte[] pSecuredKey,
                                                              final T pKeyType) throws GordianException {
        /* Parse the ASN1 */
        final GordianWrappedKeyASN1 myASN1 = GordianWrappedKeyASN1.getInstance(pSecuredKey);
        final AlgorithmIdentifier myAlgId = myASN1.getKeySpecId();
        final byte[] myWrappedKey = myASN1.getWrappedKey();

        /* Check the algorithmId */
        final GordianKeySpec mySpec = theFactory.getKeySpecForIdentifier(myAlgId);
        if (mySpec == null || !mySpec.equals(pKeyType)) {
            throw new GordianDataException("Incorrect KeySpec");
        }

        /* Unwrap the bytes */
        final byte[] myBytes = deriveBytes(myWrappedKey);

        /* Access the relevant keyGenerator */
        final GordianCoreKeyGenerator<T> myGenerator = pKeyType instanceof GordianMacSpec
                ? (GordianCoreKeyGenerator<T>) theFactory.getMacFactory().getKeyGenerator(pKeyType)
                : (GordianCoreKeyGenerator<T>) theFactory.getCipherFactory().getKeyGenerator(pKeyType);

        /* Generate the key */
        return myGenerator.buildKeyFromBytes(myBytes);
    }

    @Override
    public byte[] securePrivateKey(final GordianKeyPair pKeyPairToSecure) throws GordianException {
        /* Access the KeyPair Generator */
        final GordianKeyPairFactory myFactory = theFactory.getAsyncFactory().getKeyPairFactory();
        final GordianCoreKeyPairGenerator myGenerator = (GordianCoreKeyPairGenerator) myFactory.getKeyPairGenerator(pKeyPairToSecure.getKeyPairSpec());
        final PKCS8EncodedKeySpec myPKCS8Key = myGenerator.getPKCS8Encoding(pKeyPairToSecure);
        return secureBytes(myPKCS8Key.getEncoded());
    }

    @Override
    public GordianKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKeySpec,
                                        final byte[] pSecuredPrivateKey) throws GordianException {
        /* Access the PKCS8Encoding */
        final PKCS8EncodedKeySpec myPrivate = derivePrivateKeySpec(pSecuredPrivateKey);

        /* Determine and check the keyPairSpec */
        final GordianKeyPairFactory myFactory = theFactory.getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(pPublicKeySpec);
        if (!myKeySpec.equals(myFactory.determineKeyPairSpec(myPrivate))) {
            throw new GordianLogicException("Mismatch on keySpecs");
        }

        /* Derive the keyPair */
        final GordianCoreKeyPairGenerator myGenerator = (GordianCoreKeyPairGenerator) myFactory.getKeyPairGenerator(myKeySpec);
        return myGenerator.deriveKeyPair(pPublicKeySpec, myPrivate);
    }

    /**
     * derive private key.
      * @param pSecuredPrivateKey the secured privateKey
     * @return the derived key
     * @throws GordianException on error
     */
    private PKCS8EncodedKeySpec derivePrivateKeySpec(final byte[] pSecuredPrivateKey) throws GordianException {
        /* Derive the keySpec */
        final byte[] myBytes = deriveBytes(pSecuredPrivateKey);
        return new PKCS8EncodedKeySpec(myBytes);
    }

    @Override
    public byte[] secureBytes(final byte[] pBytesToSecure) throws GordianException {
        /* Determine number of blocks */
        final int myDataLen = pBytesToSecure.length;
        int myNumBlocks = myDataLen
                / theBlockLen;

        /* Calculate padding length */
        final int myTrueLen;
        if (myDataLen % theBlockLen == 0) {
            myTrueLen = myDataLen;
        } else {
            myNumBlocks++;
            myTrueLen = myNumBlocks
                    * theBlockLen;
        }

        /* Determine semantics of the initial block */
        final byte[] myByteLen = GordianDataConverter.integerToByteArray(myDataLen);
        int myCheckLen = theBlockLen - Integer.BYTES;
        int myHdrLen = theBlockLen;

        /* Handle 64-bit ciphers */
        if (myCheckLen == 0) {
            myCheckLen = theBlockLen;
            myHdrLen += Integer.BYTES;
            myNumBlocks++;
        }

        /* Add a block for random data */
        myNumBlocks++;
        myHdrLen += theBlockLen;

        /* Allocate buffer for data and encryption */
        final int myBufferLen = theBlockLen << 1;
        final byte[] myData = new byte[myTrueLen
                + myHdrLen];
        final byte[] myBuffer = new byte[myBufferLen];
        final byte[] myResult = new byte[myBufferLen];
        final byte[] myRandom = new byte[theBlockLen];

        /* Build the initial block */
        for (int i = 0; i < myCheckLen; i++) {
            myData[i] = getIntegrityValue(i);
        }
        System.arraycopy(myByteLen, 0, myData, myCheckLen, Integer.BYTES);
        theFactory.getRandomSource().getRandom().nextBytes(myRandom);
        System.arraycopy(myRandom, 0, myData, myCheckLen + Integer.BYTES, theBlockLen);
        System.arraycopy(pBytesToSecure, 0, myData, myHdrLen, myDataLen);

        /* Initialise the cipher */
        initCipherForWrapping();

        /* Loop WRAP_COUNT times */
        int myCount = 1;
        for (int myCycle = 0; myCycle < WRAP_COUNT; myCycle++) {
            /* Loop through the data blocks */
            for (int myBlock = 1, myOffset = theBlockLen; myBlock <= myNumBlocks; myBlock++, myOffset += theBlockLen) {
                /* Build the data to be encrypted */
                System.arraycopy(myData, 0, myBuffer, 0, theBlockLen);
                System.arraycopy(myData, myOffset, myBuffer, theBlockLen, theBlockLen);

                /* Encrypt the byte array */
                iterateCipher(myBuffer, myBufferLen, myResult);

                /* Adjust the result using the count as a mask */
                for (int myMask = myCount++, myIndex = myBufferLen - 1; myMask != 0; myMask >>>= Byte.SIZE, myIndex--) {
                    myResult[myIndex] ^= (byte) myMask;
                }

                /* Restore encrypted data */
                System.arraycopy(myResult, 0, myData, 0, theBlockLen);
                System.arraycopy(myResult, theBlockLen, myData, myOffset, theBlockLen);
            }
        }

        /* Return the wrapped data */
        return myData;
    }

    @Override
    public byte[] deriveBytes(final byte[] pSecuredBytes,
                              final int pOffset) throws GordianException {
        /* Determine number of blocks */
        int myDataLen = pSecuredBytes.length
                - theBlockLen - pOffset;
        final int myNumBlocks = myDataLen
                / theBlockLen;

        /* Data must be a multiple of BlockLength */
        if (myNumBlocks * theBlockLen != myDataLen) {
            throw new GordianDataException("Invalid data length");
        }

        /* Allocate buffers for data and encryption */
        final int myBufferLen = theBlockLen << 1;
        final byte[] myData = new byte[pSecuredBytes.length - pOffset];
        System.arraycopy(pSecuredBytes, pOffset, myData, 0, pSecuredBytes.length - pOffset);
        final byte[] myBuffer = new byte[myBufferLen];
        final byte[] myResult = new byte[myBufferLen];

        /* Initialise the cipher */
        initCipherForUnwrapping();

        /* Loop WRAP_COUNT times */
        int myCount = myNumBlocks * WRAP_COUNT;
        for (int myCycle = WRAP_COUNT; myCycle > 0; myCycle--) {
            /* Loop through the data blocks */
            for (int myBlock = myNumBlocks, myOffset = theBlockLen
                    * myBlock; myBlock >= 1; myBlock--, myOffset -= theBlockLen) {
                /* Build the data to be decrypted */
                System.arraycopy(myData, 0, myBuffer, 0, theBlockLen);
                System.arraycopy(myData, myOffset, myBuffer, theBlockLen, theBlockLen);

                /* Adjust the buffer using the count as a mask */
                for (int myMask = myCount--, myIndex = myBufferLen - 1; myMask != 0; myMask >>>= Byte.SIZE, myIndex--) {
                    myBuffer[myIndex] ^= (byte) myMask;
                }

                /* Decrypt the byte array */
                iterateCipher(myBuffer, myBufferLen, myResult);

                /* Restore decrypted data */
                System.arraycopy(myResult, 0, myData, 0, theBlockLen);
                System.arraycopy(myResult, theBlockLen, myData, myOffset, theBlockLen);
            }
        }

        /* Handle 64-bit ciphers */
        int myCheckLen = theBlockLen - Integer.BYTES;
        if (myCheckLen == 0) {
            myCheckLen = theBlockLen;
            myDataLen -= theBlockLen;
        }

        /* Adjust for random data */
        myDataLen -= theBlockLen;

        /* Check initialisation value */
        int checkFail = 0;
        for (int myInit = 0; myInit < myCheckLen; myInit++) {
            checkFail |= myData[myInit] ^ getIntegrityValue(myInit);
        }

        /* Obtain encoded length */
        final byte[] myByteLen = Arrays.copyOfRange(myData, myCheckLen, myCheckLen + Integer.BYTES);
        final int myEncodedLen = GordianDataConverter.byteArrayToInteger(myByteLen);

        /* Obtain zeroLen and check that it is valid */
        final int myZeroLen = myDataLen - myEncodedLen;
        checkFail |= myZeroLen < 0 ? 1 : 0;
        checkFail |= myZeroLen >= theBlockLen ? 2 : 0;

        /* Check trailing bytes */
        for (int myZeros = myZeroLen, myLoc = myData.length - 1; myZeros > 0 && myLoc > 0; myZeros--, myLoc--) {
            /* Check that byte is zero */
            checkFail |= myData[myLoc];
        }

        /* Return unwrapped data */
        if (checkFail == 0) {
            return Arrays.copyOfRange(myData, myCheckLen + Integer.BYTES + theBlockLen, myData.length
                    - myZeroLen);
        }

        /* Reject if checks fail */
        throw new GordianDataException("Integrity checks failed");
    }

    /**
     * Determine integrity value for position.
     * @param pIndex the index
     * @return the integrity value
     */
    private static byte getIntegrityValue(final int pIndex) {
        return (pIndex + 1) % INTEGRITY_MODULO < 2
               ? INTEGRITY_VALUE1
               : INTEGRITY_VALUE2;
    }

    /**
     * Initialise cipher for wrapping.
     * @throws GordianException on error
     */
    protected void initCipherForWrapping() throws GordianException {
        theCipher.initForEncrypt(GordianCipherParameters.key(theKey));
    }

    /**
     * Initialise cipher for unwrapping.
     * @throws GordianException on error
     */
    protected void initCipherForUnwrapping() throws GordianException {
        theCipher.initForDecrypt(GordianCipherParameters.key(theKey));
    }

    /**
     * Perform Cipher operation.
     * @param pInBuffer the input buffer
     * @param pBufferLen the buffer length
     * @param pResult the results buffer
     * @throws GordianException on erro
     */
    protected void iterateCipher(final byte[] pInBuffer,
                                 final int pBufferLen,
                                 final byte[] pResult) throws GordianException {
        theCipher.finish(pInBuffer, 0, pBufferLen, pResult, 0);
    }

    @Override
    public int getKeyWrapLength(final GordianKey<?> pKey) {
        /* Obtain the id of the keySpec */
        final GordianKeySpec mySpec = pKey.getKeyType();
        final AlgorithmIdentifier myAlgId = theFactory.getIdentifierForSpec(mySpec);

        /* Determine wrapped key length */
        final int myDataLen = getDataWrapLength(mySpec.getKeyLength().getByteLength());

        /* return the calculated length */
        return GordianWrappedKeyASN1.getEncodedLength(myAlgId, myDataLen);
    }

    @Override
    public int getDataWrapLength(final int pDataLength) {
        final GordianLength myBlockLen = getKeySpec().getBlockLength();
        return getKeyWrapLength(pDataLength, myBlockLen)
                + getKeyWrapExpansion(myBlockLen);
    }

    @Override
    public int getPrivateKeyWrapLength(final GordianKeyPair pKeyPair) throws GordianException {
        /* Determine and check the keySpec */
        final GordianKeyPairFactory myFactory = theFactory.getAsyncFactory().getKeyPairFactory();
        final GordianCoreKeyPairGenerator myGenerator = (GordianCoreKeyPairGenerator) myFactory.getKeyPairGenerator(pKeyPair.getKeyPairSpec());
        final PKCS8EncodedKeySpec myPrivateKey = myGenerator.getPKCS8Encoding(pKeyPair);
        return getDataWrapLength(myPrivateKey.getEncoded().length);
    }

    /**
     * Obtain initial wrapLength for a particular dataLength and BlockSize.
     * @param pDataLength the data length
     * @param pBlockLen the number of bits in the blockLen
     * @return the keyWrap expansion
     */
    public static int getKeyWrapLength(final int pDataLength,
                                       final GordianLength pBlockLen) {
        final int myBlockLen = pBlockLen.getByteLength() >> 1;
        final int myNumBlocks = (pDataLength + myBlockLen - 1) / myBlockLen;
        return myNumBlocks * myBlockLen;
    }

    /**
     * Obtain keyWrapExpansion for a blockLen.
     * @param pBlockLen the number of bits in the blockLen
     * @return the keyWrap expansion
     */
    public static int getKeyWrapExpansion(final GordianLength pBlockLen) {
        final int myBlockLen = pBlockLen.getByteLength() >> 1;
        final int myNumBlocks = 1 + (myBlockLen <= Integer.BYTES
                                      ? 2
                                      : 1);
        return myNumBlocks * myBlockLen;
    }
}
