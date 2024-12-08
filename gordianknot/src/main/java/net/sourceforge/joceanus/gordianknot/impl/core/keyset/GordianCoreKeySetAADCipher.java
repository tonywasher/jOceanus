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
package net.sourceforge.joceanus.gordianknot.impl.core.keyset;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetAADCipher;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacParameters;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpecBuilder;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianKeySetRecipe.GordianKeySetParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

import java.io.ByteArrayOutputStream;

/**
 * Core keySetAADCipher.
 */
public class GordianCoreKeySetAADCipher
    extends GordianCoreKeySetCipher
    implements GordianKeySetAADCipher {
    /**
     * The MacSize.
     */
    static final int MACSIZE = 16;

    /**
     * The Zero padding.
     */
    private static final byte[] PADDING = new byte[MACSIZE - 1];

    /**
     * The Mac.
     */
    private final GordianMac theMac;

    /**
     * The AEAD Data.
     */
    private final ByteArrayOutputStream theAEAD;

    /**
     * The Initial AEAD Data.
     */
    private byte[] initialAEAD;

    /**
     * Have we completed AEAD?
     */
    private boolean aeadComplete;

    /**
     * The aeadLength.
     */
    private long aeadLength;

    /**
     * The dataLength.
     */
    private long dataLength;

    /**
     * The encryptedLength.
     */
    private long encryptedLength;

    /**
     * The Digest.
     */
    private GordianDigest theDigest;

    /**
     * The SymKeyType.
     */
    private GordianSymKeyType theSymKeyType;

    /**
     * The cachedBytes.
     */
    private final byte[] cachedBytes;

    /**
     * number of bytes in the cache.
     */
    private int cacheBytes;

    /**
     * Constructor.
     *
     * @param pKeySet the keySet.
     * @throws GordianException on error
     */
    public GordianCoreKeySetAADCipher(final GordianCoreKeySet pKeySet) throws GordianException {
        /* Initialise underlying class */
        super(pKeySet, true);

        /* Create mac and buffers */
        final GordianMacFactory myMacFactory = pKeySet.getFactory().getMacFactory();
        theMac = myMacFactory.createMac(GordianMacSpecBuilder.poly1305Mac());
        theAEAD = new ByteArrayOutputStream();
        cachedBytes = new byte[MACSIZE];
    }

    @Override
    public void initForEncrypt(final byte[] pAAD) throws GordianException {
        initialAEAD = Arrays.clone(pAAD);
        super.initForEncrypt();
    }

    @Override
    public void initForDecrypt(final byte[] pAAD) throws GordianException {
        initialAEAD = Arrays.clone(pAAD);
        super.initForDecrypt();
    }

    @Override
    public void initForEncrypt() throws GordianException {
        initForEncrypt(null);
    }

    @Override
    public void initForDecrypt() throws GordianException {
        initForDecrypt(null);
    }

    @Override
    protected void reset() throws GordianException {
        /* Process underlying reset */
        super.reset();

        /* reset the AAD */
        theAEAD.reset();
        aeadComplete = false;
        cacheBytes = 0;

        /* initialise with any initialAEAD */
        if (initialAEAD != null) {
            theAEAD.write(initialAEAD, 0, initialAEAD.length);
        }
    }

    @Override
    public void updateAAD(final byte[] pAAD,
                          final int pOffset,
                          final int pLength) throws GordianException {
        /* Check AAD is allowed */
        checkAEADStatus();

        /* Store the bytes */
        theAEAD.write(pAAD, pOffset, pLength);
    }

    /**
     * check AEAD status.
     *
     * @throws GordianException on error
     */
    private void checkAEADStatus() throws GordianException {
        /* Check we are initialised */
        if (!isInitialised()) {
            throw new GordianLogicException("Cipher is not initialised");
        }

        /* Check AAD is allowed */
        if (aeadComplete) {
            throw new GordianLogicException("AEAD data cannot be processed after ordinary data");
        }
    }

    @Override
    protected void checkStatus() throws GordianException {
        /* Check underlying status */
        super.checkStatus();

        /* aead is now complete */
        aeadComplete = true;
    }

    @Override
    protected void initCiphers(final GordianKeySetParameters pParams) throws GordianException {
        /* Pass call on */
        super.initCiphers(pParams);

        /* Create the digest */
        final GordianDigestFactory myDigests = getFactory().getDigestFactory();
        final GordianDigestSpec myDigestSpec = new GordianDigestSpec(pParams.getDigestType(), GordianLength.LEN_512);
        theDigest = myDigests.createDigest(myDigestSpec);

        /* initialise the Mac */
        final GordianKey<GordianMacSpec> myKey = getMultiCipher().derivePoly1305Key(pParams);
        theMac.init(GordianMacParameters.key(myKey));

        /* Stash the symKeyType */
        theSymKeyType = pParams.getPoly1305SymKeyType();

        /* Update the Mac with the AEAD data */
        final byte[] myAEAD = theAEAD.toByteArray();
        theMac.update(myAEAD);
        aeadLength = myAEAD.length;
        completeAEADMac();
        dataLength = 0;
        encryptedLength = 0;
    }

    @Override
    public int getOutputLength(final int pLength) {
        /* Handle encrypting calculation */
        if (isEncrypting()) {
            return super.getOutputLength(pLength) + MACSIZE;
        }

        /* Allow for cacheSpace */
        final int cacheSpace = MACSIZE - cacheBytes;
        final int len = super.getOutputLength(pLength);
        return len < cacheSpace
               ? 0
               : len - cacheSpace;
    }

    @Override
    protected int updateEncryption(final byte[] pBytes,
                                   final int pOffset,
                                   final int pLength,
                                   final byte[] pOutput,
                                   final int pOutOffset) throws GordianException {
        /* Process the bytes */
        final int myLen = super.updateEncryption(pBytes, pOffset, pLength, pOutput, pOutOffset);

        /* Process data into mac and  digest */
        theMac.update(pOutput, pOutOffset, myLen);
        encryptedLength += myLen;
        theDigest.update(pBytes, pOffset, pLength);
        dataLength += pLength;

        /* Return the number of bytes processed */
        return myLen;
    }

    @Override
    protected int updateDecryption(final byte[] pBytes,
                                   final int pOffset,
                                   final int pLength,
                                   final byte[] pOutput,
                                   final int pOutOffset) throws GordianException {
        /* Count how much we have processed */
        int processed = 0;

        /* Calculate the number of bytes to process from the cache */
        final int numInputBytes = pLength - MACSIZE;
        int numCacheBytes = Math.max(cacheBytes + numInputBytes, 0);
        numCacheBytes = Math.min(cacheBytes, numCacheBytes);

        /* If we should process bytes from the cache */
        if (numCacheBytes > 0) {
            /* Process the cached bytes */
            processed = super.updateDecryption(cachedBytes, 0, numCacheBytes, pOutput, pOutOffset);

            /* Process data into mac and  digest */
            theMac.update(cachedBytes, 0, numCacheBytes);
            encryptedLength += numCacheBytes;
            theDigest.update(pOutput, pOutOffset, processed);
            dataLength += processed;

            /* Move any remaining cached bytes down in the buffer */
            cacheBytes -= numCacheBytes;
            if (cacheBytes > 0) {
                System.arraycopy(cachedBytes, numCacheBytes, cachedBytes, 0, cacheBytes);
            }
        }

        /* Process any excess bytes from the input buffer */
        if (numInputBytes > 0) {
            /* Process the input */
            final int numProcessed = super.updateDecryption(pBytes, pOffset, numInputBytes, pOutput, pOutOffset + processed);

            /* Process data into mac and  digest */
            theMac.update(pBytes, pOffset, numInputBytes);
            encryptedLength += numInputBytes;
            theDigest.update(pOutput, pOutOffset + processed, numProcessed);
            dataLength += numProcessed;
            processed += numProcessed;
        }

        /* Store the remaining input into the cache */
        final int numToCache = Math.min(pLength, MACSIZE);
        System.arraycopy(pBytes, pOffset + pLength - numToCache, cachedBytes, cacheBytes, numToCache);
        cacheBytes += numToCache;

        /* Return the number of bytes processed */
        return processed;
    }

    @Override
    public int doFinish(final byte[] pOutput,
                        final int pOutOffset) throws GordianException {
        /* Finish the cipher */
        int myLen = finishCipher(pOutput, pOutOffset);

        /* Update mac if we have output data on encryption */
        if (myLen > 0) {
            /* Update Mac/digest as appropriate */
            if (isEncrypting()) {
                /* Update the mac */
                theMac.update(pOutput, pOutOffset, myLen);
                encryptedLength += myLen;
            } else {
                /* Update the digest */
                theDigest.update(pOutput, pOutOffset, myLen);
                dataLength += myLen;
            }
        }

        /* finish the mac */
        myLen += isEncrypting()
                 ? finishEncryptionMac(pOutput, pOutOffset + myLen)
                 : finishDecryptionMac();

        /* Reset the cipher */
        reset();

        /* return the number of bytes processed */
        return myLen;
    }

    /**
     * finish the encryption Mac.
     *
     * @param pOutput    the output buffer
     * @param pOutOffset the offset from which to start writing output
     * @return the length of data written out
     * @throws GordianException on error
     */
    private int finishEncryptionMac(final byte[] pOutput,
                                    final int pOutOffset) throws GordianException {
        /* Complete the dataMac */
        completeDataMac();

        /* Calculate the Mac */
        final byte[] myMac = new byte[MACSIZE];
        theMac.finish(myMac, 0);

        /* Encrypt the Mac */
        final byte[] myResult = getMultiCipher().encryptMac(theSymKeyType, myMac);

        /* return the encrypted mac in the output buffer */
        System.arraycopy(myResult, 0, pOutput, pOutOffset, MACSIZE);
        return MACSIZE;
    }

    /**
     * finish the decryption Mac.
     *
     * @return the length of data written out
     * @throws GordianException on mac misMatch
     */
    private int finishDecryptionMac() throws GordianException {
        /* If we do not have sufficient data */
        if (cacheBytes < MACSIZE) {
            throw new GordianDataException("data too short");
        }

        /* Complete the dataMac */
        completeDataMac();

        /* Calculate the Mac */
        final byte[] myMac = new byte[MACSIZE];
        theMac.finish(myMac, 0);

        /* Encrypt the Mac */
        final byte[] myResult = getMultiCipher().encryptMac(theSymKeyType, myMac);

        /* Check that the encrypted Mac is identical to that contained in the cache */
        if (!Arrays.constantTimeAreEqual(myResult, cachedBytes)) {
            throw new GordianDataException("mac check failed");
        }

        /* No bytes returned */
        return 0;
    }

    /**
     * Complete AEAD Mac input.
     */
    private void completeAEADMac()  {
        /* Pad to boundary */
        padToBoundary(aeadLength);

        /* Set flag */
        aeadComplete = true;
    }

    /**
     * Complete Mac data input.
     */
    private void completeDataMac() {
        /* Pad to boundary */
        padToBoundary(encryptedLength);

        /* Write the lengths */
        final byte[] len = new byte[Long.BYTES << 1];
        Pack.longToLittleEndian(aeadLength, len, 0);
        Pack.longToLittleEndian(dataLength, len, Long.BYTES);
        theMac.update(len, 0, len.length);

        /* Calculate the digest and update the mac */
        final byte[] myDigest = theDigest.finish();
        theMac.update(myDigest);
    }

    /**
     * Pad to boundary.
     *
     * @param pDataLen the length of the data to pad
     */
    private void padToBoundary(final long pDataLen) {
        /* Pad to boundary */
        final int xtra = (int) pDataLen & (MACSIZE - 1);
        if (xtra != 0) {
            final int numPadding = MACSIZE - xtra;
            theMac.update(PADDING, 0, numPadding);
        }
    }
}
