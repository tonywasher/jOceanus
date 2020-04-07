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

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetCipher;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetRecipe.GordianKeySetParameters;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Core keySetCipher.
 */
public class GordianCoreKeySetCipher
    implements GordianKeySetCipher {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The keySetSpec.
     */
    private final GordianKeySetSpec theSpec;

    /**
     * The Underlying cipher.
     */
    private final GordianMultiCipher theCipher;

    /**
     * The cachedBytes.
     */
    private final byte[] cachedBytes;

    /**
     * number of bytes in the cache.
     */
    private int cacheBytes;

    /**
     * Are we in AEAD mode?
     */
    private boolean aead;

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
     * @throws OceanusException on error
     */
    public GordianCoreKeySetCipher(final GordianCoreKeySet pKeySet,
                                   final boolean pAead) throws OceanusException  {
        theFactory = pKeySet.getFactory();
        aead = pAead;
        theSpec = pKeySet.getKeySetSpec();
        theCipher = new GordianMultiCipher(pKeySet);
        cachedBytes = new byte[GordianKeySetRecipe.HDRLEN];
    }

    @Override
    public void initForEncrypt() throws OceanusException {
        encrypting = true;
        reset();
    }

    @Override
    public void initForDecrypt() throws OceanusException {
        encrypting = false;
        reset();
    }

    /**
     * Reset the cipher.
     * @throws OceanusException on error
     */
    public void reset() throws OceanusException {
        /* If we are encrypting */
        if (encrypting) {
            /* Generate a new KeySetRecipe */
            final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.newRecipe(theFactory, theSpec, aead);
            final GordianKeySetParameters myParams = myRecipe.getParameters();
            myRecipe.buildHeader(cachedBytes);
            cacheBytes = GordianKeySetRecipe.HDRLEN;

            /* Initialise the ciphers */
            theCipher.initCiphers(myParams, encrypting);
        } else {
            cacheBytes = 0;
        }

        /* Set flags */
        initialised = true;
        hdrProcessed = false;
    }

    /**
     * check status.
     * @throws OceanusException on error
     */
    private void checkStatus() throws OceanusException {
        /* Check we are initialised */
        if (!initialised) {
            throw new GordianLogicException("Cipher is not initialised");
        }
    }

    @Override
    public int getOutputLength(final int len) {
        if (encrypting) {
            return theCipher.getOutputLength(len) + cacheBytes;
        }

        /* Allow for cacheSpace */
        final int cacheSpace = GordianKeySetRecipe.HDRLEN - cacheBytes;
        return len < cacheSpace ? 0 : len - cacheSpace;
    }

    @Override
    public int update(final byte[] pBytes,
                      final int pOffset,
                      final int pLength,
                      final byte[] pOutput,
                      final int pOutOffset) throws OceanusException {
        /* Check status */
        checkStatus();

        /* process the bytes */
        return encrypting
               ? updateEncryption(pBytes, pOffset, pLength, pOutput, pOutOffset)
               : updateDecryption(pBytes, pOffset, pLength, pOutput, pOutOffset);
    }

    /**
     * Update for encryption.
     * @param pBytes the input buffer
     * @param pOffset the offset from which to start processing
     * @param pLength the length of data to process
     * @param pOutput the output buffer
     * @param pOutOffset the offset from which to start writing output
     * @return the length of data written out
     * @throws OceanusException on error
     */
    private int updateEncryption(final byte[] pBytes,
                                 final int pOffset,
                                 final int pLength,
                                 final byte[] pOutput,
                                 final int pOutOffset) throws OceanusException {
        /* Check that the buffers are sufficient */
        if (pBytes.length < (pLength + pOffset)) {
            throw new GordianLogicException("Input buffer too short.");
        }
        if (pOutput.length < (getOutputLength(pLength) + pOutOffset)) {
            throw new GordianLogicException("Output buffer too short.");
        }

        /* Process any header bytes */
        int bytesWritten = 0;
        if (!hdrProcessed) {
            System.arraycopy(cachedBytes, 0, pOutput, pOutOffset, cacheBytes);
            hdrProcessed = true;
            bytesWritten = cacheBytes;
            cacheBytes = 0;
        }

        /* Process the bytes */
        bytesWritten += theCipher.update(pBytes, pOffset, pLength, pOutput, pOutOffset + bytesWritten);

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
     * @throws OceanusException on error
     */
    private int updateDecryption(final byte[] pBytes,
                                 final int pOffset,
                                 final int pLength,
                                 final byte[] pOutput,
                                 final int pOutOffset) throws OceanusException {
        /* Check that the buffers are sufficient */
        if (pBytes.length < (pLength + pOffset)) {
            throw new GordianLogicException("Input buffer too short.");
        }
        if (pOutput.length < (getOutputLength(pLength) + pOutOffset)) {
            throw new GordianLogicException("Output buffer too short.");
        }

        /* If we have not yet processed the header*/
        int numRead = 0;
        if (!hdrProcessed) {
            /* Note how many bytes to copy to cache */
            final int cacheSpace = GordianKeySetRecipe.HDRLEN - cacheBytes;
            numRead = Math.min(cacheSpace, pLength);
            System.arraycopy(pBytes, 0, cachedBytes, cacheBytes, numRead);
            cacheBytes += numRead;

            /* If we have a complete header */
            if (cacheBytes == GordianKeySetRecipe.HDRLEN) {
                /* Process the recipe */
                final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.parseRecipe(theFactory, theSpec, cachedBytes, aead);
                final GordianKeySetParameters myParams = myRecipe.getParameters();

                /* Initialise the ciphers */
                theCipher.initCiphers(myParams, encrypting);
                hdrProcessed = true;
            }
        }

        /* Process the bytes */
        return theCipher.update(pBytes, pOffset + numRead, pLength - numRead, pOutput, pOutOffset);
    }

    @Override
    public int finish(final byte[] pOutput,
                      final int pOutOffset) throws OceanusException {
        /* Check status */
        checkStatus();

        /* Finish the cipher */
        final int myLen = theCipher.finish(pOutput, pOutOffset);

        /* Reset the cipher */
        reset();

        /* return the number of bytes processed */
        return myLen;
    }
}
