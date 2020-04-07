/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import java.io.ByteArrayOutputStream;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetAADCipher;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacParameters;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetRecipe.GordianKeySetParameters;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Core keySetAADCipher.
 */
public class GordianCoreKeySetAADCipher
    extends GordianCoreKeySetCipher
    implements GordianKeySetAADCipher {
    /**
     * The MacSize.
     */
    private static final int MACSIZE = 16;

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
     * @throws OceanusException on error
     */
    public GordianCoreKeySetAADCipher(final GordianCoreKeySet pKeySet) throws OceanusException {
        /* Initialise underlying class */
        super(pKeySet, true);

        /* Create mac and buffers */
        final GordianMacFactory myMacFactory = pKeySet.getFactory().getMacFactory();
        theMac = myMacFactory.createMac(GordianMacSpec.poly1305Mac());
        theAEAD = new ByteArrayOutputStream();
        cachedBytes = new byte[MACSIZE];
    }

    @Override
    public void initForEncrypt(final byte[] pAAD) throws OceanusException {
        initialAEAD = Arrays.clone(pAAD);
        initForEncrypt();
    }

    @Override
    public void initForDecrypt(final byte[] pAAD) throws OceanusException {
        initialAEAD = Arrays.clone(pAAD);
        initForDecrypt();
    }

    @Override
    protected void reset() throws OceanusException {
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
                          final int pLength) throws OceanusException {
        /* Check AAD is allowed */
        checkAEADStatus();

        /* Store the bytes */
        theAEAD.write(pAAD, pOffset, pLength);
    }

    /**
     * check AEAD status.
     *
     * @throws OceanusException on error
     */
    private void checkAEADStatus() throws OceanusException {
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
    protected void checkStatus() throws OceanusException {
        /* Check underlying status */
        super.checkStatus();

        /* aead is now complete */
        aeadComplete = true;
    }

    @Override
    protected void initCiphers(final GordianKeySetParameters pParams) throws OceanusException {
        /* Pass call on */
        super.initCiphers(pParams);

        /* initialise the Mac */
        final GordianKey<GordianMacSpec> myKey = getMultiCipher().derivePoly1305Key(pParams);
        theMac.init(GordianMacParameters.key(myKey));

        /* Update the Mac with the AEAD data */
        final byte[] myAEAD = theAEAD.toByteArray();
        theMac.update(myAEAD);
        aeadLength = myAEAD.length;
        completeAEADMac();
        dataLength = 0;
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
                                   final int pOutOffset) throws OceanusException {
        /* Process the bytes */
        final int myLen = super.updateEncryption(pBytes, pOffset, pLength, pOutput, pOutOffset);

        /* Update the mac */
        theMac.update(pOutput, pOutOffset, myLen);
        dataLength += myLen;

        /* Return the number of bytes processed */
        return myLen;
    }

    @Override
    protected int updateDecryption(final byte[] pBytes,
                                   final int pOffset,
                                   final int pLength,
                                   final byte[] pOutput,
                                   final int pOutOffset) throws OceanusException {
        /* Check that the buffers are sufficient */
        if (pBytes.length < (pLength + pOffset)) {
            throw new GordianLogicException("Input buffer too short.");
        }
        if (pOutput.length < (getOutputLength(pLength) + pOutOffset + cacheBytes - MACSIZE)) {
            throw new GordianLogicException("Output buffer too short.");
        }

        /* Count how much we have processed */
        int processed = 0;

        /* Calculate the number of bytes to process from the cache */
        final int numInputBytes = pLength - MACSIZE;
        int numCacheBytes = Math.max(cacheBytes + numInputBytes, 0);
        numCacheBytes = Math.min(cacheBytes, numCacheBytes);

        /* If we should process bytes from the cache */
        if (numCacheBytes > 0) {
            /* Process any required cachedBytes */
            theMac.update(cachedBytes, 0, numCacheBytes);
            dataLength += numCacheBytes;

            /* Process the cached bytes */
            processed = super.updateDecryption(cachedBytes, 0, numCacheBytes, pOutput, pOutOffset);

            /* Move any remaining cached bytes down in the buffer */
            cacheBytes -= numCacheBytes;
            if (cacheBytes > 0) {
                System.arraycopy(cachedBytes, numCacheBytes, cachedBytes, 0, cacheBytes);
            }
        }

        /* Process any excess bytes from the input buffer */
        if (numInputBytes > 0) {
            /* Process the data */
            theMac.update(pBytes, pOffset, numInputBytes);
            dataLength += numInputBytes;

            /* Process the input */
            processed += super.updateDecryption(pBytes, pOffset, numInputBytes, pOutput, pOutOffset + processed);
        }

        /* Store the remaining input into the cache */
        final int numToCache = Math.min(pLength, MACSIZE);
        System.arraycopy(pBytes, pOffset + pLength - numToCache, cachedBytes, cacheBytes, numToCache);
        cacheBytes += numToCache;

        /* Return the number of bytes processed */
        return processed;
    }

    @Override
    public int finish(final byte[] pOutput,
                      final int pOutOffset) throws OceanusException {
        /* Finish the cipher */
        int myLen = finishCipher(pOutput, pOutOffset);

        /* Update mac if we have output data on encryption */
        if (isEncrypting() && myLen > 0) {
            /* Update the mac */
            theMac.update(pOutput, pOutOffset, myLen);
            dataLength += myLen;
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
     * @throws OceanusException on error
     */
    private int finishEncryptionMac(final byte[] pOutput,
                                    final int pOutOffset) throws OceanusException {
        /* Check that the output buffer is sufficient */
        if (pOutput.length < (MACSIZE + pOutOffset)) {
            throw new GordianLogicException("Output buffer too short.");
        }

        /* Complete the dataMac */
        completeDataMac();

        /* Calculate the Mac */
        final byte[] myMac = new byte[MACSIZE];
        theMac.finish(myMac, 0);

        /* Update and return the mac in the output buffer */
        System.arraycopy(myMac, 0, pOutput, pOutOffset, MACSIZE);
        return MACSIZE;
    }

    /**
     * finish the decryption Mac.
     *
     * @return the length of data written out
     * @throws OceanusException on mac misMatch
     */
    private int finishDecryptionMac() throws OceanusException {
        /* If we do not have sufficient data */
        if (cacheBytes < MACSIZE) {
            throw new GordianDataException("data too short");
        }

        /* Complete the dataMac */
        completeDataMac();

        /* Calculate the Mac */
        final byte[] myMac = new byte[MACSIZE];
        theMac.finish(myMac, 0);

        /* Check that the calculated Mac is identical to that contained in the cache */
        if (!Arrays.constantTimeAreEqual(myMac, cachedBytes)) {
            throw new GordianDataException("mac check failed");
        }

        /* No bytes returned */
        return 0;
    }

    /**
     * Complete AEAD Mac input.
     */
    private void completeAEADMac() {
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
        padToBoundary(dataLength);

        /* Write the lengths */
        final byte[] len = new byte[Long.BYTES << 1];
        Pack.longToLittleEndian(aeadLength, len, 0);
        Pack.longToLittleEndian(dataLength, len, Long.BYTES);
        theMac.update(len, 0, len.length);
    }

    /**
     * Pad to boundary.
     *
     * @param pDataLen the length of the data to pad
     */
    private void padToBoundary(final long pDataLen) {
        /* Pad to boundary */
        final int xtra = (int) pDataLen % MACSIZE;
        if (xtra != 0) {
            final int numPadding = MACSIZE - xtra;
            theMac.update(PADDING, 0, numPadding);
        }
    }
}
