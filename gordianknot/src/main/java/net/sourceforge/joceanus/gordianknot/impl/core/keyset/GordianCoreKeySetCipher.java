/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetCipher;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianKeySetRecipe.GordianKeySetParameters;

/**
 * Core keySetCipher.
 */
public class GordianCoreKeySetCipher
    implements GordianKeySetCipher {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The keySetSpec.
     */
    private final GordianKeySetSpec theSpec;

    /**
     * The Underlying cipher.
     */
    private final GordianMultiCipher theCipher;

    /**
     * The cached header.
     */
    private final byte[] theHeader;

    /**
     * number of bytes in the header cache.
     */
    private int hdrBytes;

    /**
     * Are we in AEAD mode?
     */
    private final boolean aead;

    /**
     * Are we initialised?
     */
    private boolean initialised;

    /**
     * Are we encrypting?
     */
    private boolean encrypting;

    /**
     * Has the header been processed?
     */
    private boolean hdrProcessed;

    /**
     * Constructor.
     * @param pKeySet the keySet.
     * @param pAead are we in AEAD mode
     * @throws GordianException on error
     */
    public GordianCoreKeySetCipher(final GordianBaseKeySet pKeySet,
                                   final boolean pAead) throws GordianException  {
        theFactory = pKeySet.getFactory();
        aead = pAead;
        theSpec = pKeySet.getKeySetSpec();
        theCipher = new GordianMultiCipher(pKeySet);
        theHeader = new byte[GordianKeySetRecipe.HDRLEN];
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianFactory getFactory() {
        return theFactory;
    }

    /**
     * Is the cipher initialised?
     * @return true/false
     */
    protected boolean isInitialised() {
        return initialised;
    }

    /**
     * Is the cipher encrypting?
     * @return true/false
     */
    protected boolean isEncrypting() {
        return encrypting;
    }

    /**
     * Obtain the multi-cipher.
     * @return the cipher
     */
    protected GordianMultiCipher getMultiCipher() {
        return theCipher;
    }

    @Override
    public void initForEncrypt() throws GordianException {
        encrypting = true;
        reset();
    }

    @Override
    public void initForDecrypt() throws GordianException {
        encrypting = false;
        reset();
    }

    /**
     * Reset the cipher.
     * @throws GordianException on error
     */
    protected void reset() throws GordianException {
        /* Set flags */
        hdrBytes = 0;
        initialised = true;
        hdrProcessed = false;
    }

    /**
     * Initialise the ciphers.
     * @param pParams the keySet parameters
     * @throws GordianException on error
     */
    protected void initCiphers(final GordianKeySetParameters pParams) throws GordianException {
        /* Initialise the ciphers */
        theCipher.initCiphers(pParams, encrypting);
    }

    /**
     * check status.
     * @throws GordianException on error
     */
    protected void checkStatus() throws GordianException {
        /* Check we are initialised */
        if (!initialised) {
            throw new GordianLogicException("Cipher is not initialised");
        }
    }

    @Override
    public int getOutputLength(final int pLength) {
        /* Handle encryption */
        if (encrypting) {
            return hdrProcessed ? theCipher.getOutputLength(pLength)
                                : GordianKeySetData.getEncryptionLength(pLength);
        }

        /* Allow for cacheSpace */
        final int cacheSpace = GordianKeySetRecipe.HDRLEN - hdrBytes;
        return pLength < cacheSpace ? 0 : pLength - cacheSpace;
    }

    @Override
    public int update(final byte[] pBytes,
                      final int pOffset,
                      final int pLength,
                      final byte[] pOutput,
                      final int pOutOffset) throws GordianException {
        /* Check status */
        checkStatus();

        /* Make sure that there is no overlap between buffers */
        byte[] myInput = pBytes;
        int myOffset = pOffset;
        if (check4UpdateOverLap(pBytes, pOffset, pLength, pOutput, pOutOffset)) {
            myInput = new byte[pLength];
            myOffset = 0;
            System.arraycopy(pBytes, pOffset, myInput, myOffset, pLength);
        }

        /* process the bytes */
        return encrypting
               ? updateEncryption(myInput, myOffset, pLength, pOutput, pOutOffset)
               : updateDecryption(myInput, myOffset, pLength, pOutput, pOutOffset);
    }

    /**
     * Obtain buffer length (allowing for null).
     * @param pBuffer the buffere
     * @return the length
     */
    private static int bufLength(final byte[] pBuffer) {
        return pBuffer == null ? 0 : pBuffer.length;
    }

    /**
     * Check for buffer overlap in update.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return is there overlap between the two buffers? true/false overlap
     * @throws GordianException on error
     */
    private boolean check4UpdateOverLap(final byte[] pBytes,
                                        final int pOffset,
                                        final int pLength,
                                        final byte[] pOutput,
                                        final int pOutOffset) throws GordianException {
        /* Check that the buffers are sufficient */
        if (bufLength(pBytes) < (pLength + pOffset)) {
            throw new GordianLogicException("Input buffer too short.");
        }
        if (bufLength(pOutput) < (getOutputLength(pLength) + pOutOffset)) {
            throw new GordianLogicException("Output buffer too short.");
        }

        /* Only relevant when the two buffers are the same */
        if (pBytes != pOutput) {
            return false;
        }

        /* Check for overlap */
        return pOutOffset < pOffset + pLength
                && pOffset < pOutOffset + getOutputLength(pLength);
    }

    /**
     * Update for encryption.
     * @param pBytes the input buffer
     * @param pOffset the offset from which to start processing
     * @param pLength the length of data to process
     * @param pOutput the output buffer
     * @param pOutOffset the offset from which to start writing output
     * @return the length of data written out
     * @throws GordianException on error
     */
    protected int updateEncryption(final byte[] pBytes,
                                   final int pOffset,
                                   final int pLength,
                                   final byte[] pOutput,
                                   final int pOutOffset) throws GordianException {
        /* If we have not initialised the ciphers yet */
        if (hdrBytes == 0) {
            /* Generate a new KeySetRecipe */
            final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.newRecipe(theFactory, theSpec, aead);
            final GordianKeySetParameters myParams = myRecipe.getParameters();
            myRecipe.buildHeader(theHeader);
            hdrBytes = GordianKeySetRecipe.HDRLEN;

            /* Initialise the ciphers */
            initCiphers(myParams);
        }

        /* If we have not processed the header yet */
        int bytesWritten = 0;
        if (!hdrProcessed) {
            /* Process the header */
            System.arraycopy(theHeader, 0, pOutput, pOutOffset, hdrBytes);
            hdrProcessed = true;
            bytesWritten = hdrBytes;
        }

        /* Process the bytes */
        final int numBytesWritten = theCipher.update(pBytes, pOffset, pLength, pOutput, pOutOffset + bytesWritten);
        bytesWritten += numBytesWritten;

        /* Return the number of bytes processed */
        return bytesWritten;
    }


    /**
     * Process decryption bytes.
     * @param pBytes the input buffer
     * @param pOffset the offset from which to start processing
     * @param pLength the length of data to process
     * @param pOutput the output buffer
     * @param pOutOffset the offset from which to start writing output
     * @return the length of data written out
     * @throws GordianException on error
     */
    protected int updateDecryption(final byte[] pBytes,
                                   final int pOffset,
                                   final int pLength,
                                   final byte[] pOutput,
                                   final int pOutOffset) throws GordianException {
        /* If we have not yet processed the header*/
        int numRead = 0;
        if (!hdrProcessed) {
            /* Work out how many bytes to copy to cache */
            final int cacheSpace = GordianKeySetRecipe.HDRLEN - hdrBytes;
            numRead = Math.min(cacheSpace, pLength);

            /* Copy to the header */
            System.arraycopy(pBytes, 0, theHeader, hdrBytes, numRead);
            hdrBytes += numRead;

            /* If we have a complete header */
            if (hdrBytes == GordianKeySetRecipe.HDRLEN) {
                /* Process the recipe */
                final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.parseRecipe(theFactory, theSpec, theHeader, aead);
                final GordianKeySetParameters myParams = myRecipe.getParameters();

                /* Initialise the ciphers */
                initCiphers(myParams);
                hdrProcessed = true;
            }
        }

        /* Process the bytes */
        return theCipher.update(pBytes, pOffset + numRead, pLength - numRead, pOutput, pOutOffset);
    }

    @Override
    public int finish(final byte[] pOutput,
                      final int pOutOffset) throws GordianException {
        /* Check that the buffers are sufficient */
        if (bufLength(pOutput) < (getOutputLength(0) + pOutOffset)) {
            throw new GordianLogicException("Output buffer too short.");
        }

        /* finish the cipher */
        return doFinish(pOutput, pOutOffset);
    }

    /**
     * Complete the Cipher operation and return final results.
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the number of bytes transferred to the output buffer
     * @throws GordianException on error
     */
    public int doFinish(final byte[] pOutput,
                        final int pOutOffset) throws GordianException {
        /* Finish the cipher */
        final int myLen = finishCipher(pOutput, pOutOffset);

        /* Reset the cipher */
        reset();

        /* return the number of bytes processed */
        return myLen;
    }

    /**
     * Finish underlying cipher.
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the length of data processed
     * @throws GordianException on error
     */
    protected int finishCipher(final byte[] pOutput,
                               final int pOutOffset) throws GordianException {
        /* Check status */
        checkStatus();

        /* Reject if we have not fully processed the header on decrypt */
        if (!encrypting && !hdrProcessed) {
            throw new GordianDataException("data too short");
        }

        /* Finish the cipher */
        return theCipher.finish(pOutput, pOutOffset);
    }
}
