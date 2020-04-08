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
package net.sourceforge.joceanus.jgordianknot.impl.core.mac;

import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacParameters;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot interface for Message Authentication Codes.
 */
public abstract class GordianCoreMac
        implements GordianMac {
    /**
     * MacSpec.
     */
    private final GordianMacSpec theMacSpec;

    /**
     * Parameters.
     */
    private GordianCoreMacParameters theParameters;

    /**
     * Constructor.
     *
     * @param pFactory the Security Factory
     * @param pMacSpec the macSpec
     */
    protected GordianCoreMac(final GordianCoreFactory pFactory,
                             final GordianMacSpec pMacSpec) {
        theMacSpec = pMacSpec;
        theParameters = new GordianCoreMacParameters(pFactory, theMacSpec);
    }

    @Override
    public GordianMacSpec getMacSpec() {
        return theMacSpec;
    }

    @Override
    public GordianKey<GordianMacSpec> getKey() {
        return theParameters.getKey();
    }

    @Override
    public byte[] getInitVector() {
        return theParameters.getInitVector();
    }

    /**
     * Check that the key matches the keyType.
     *
     * @param pKey the passed key.
     * @throws OceanusException on error
     */
    private void checkValidKey(final GordianKey<GordianMacSpec> pKey) throws OceanusException {
        if (!theMacSpec.equals(pKey.getKeyType())) {
            throw new GordianLogicException("MisMatch on macSpec");
        }
    }

    /**
     * Init with bytes as key.
     * @param pKeyBytes the bytes to use
     * @throws OceanusException on error
     */
    public void initKeyBytes(final byte[] pKeyBytes) throws OceanusException {
        /* Create the key and initialise */
        final GordianKey<GordianMacSpec> myKey = theParameters.buildKeyFromBytes(pKeyBytes);
        init(GordianMacParameters.key(myKey));
    }

    /**
     * Process macParameters.
     * @param pParams the mac parameters
     * @throws OceanusException on error
     */
    protected void processParameters(final GordianMacParameters pParams) throws OceanusException {
        /* Process the parameters */
        theParameters.processParameters(pParams);
        checkValidKey(getKey());
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        /* Check that the buffers are sufficient */
        final int myInBufLen = pBytes == null ? 0 : pBytes.length;
        if (myInBufLen < (pLength + pOffset)) {
            throw new IllegalArgumentException("Input buffer too short.");
        }

        /* Process the bytes */
        doUpdate(pBytes, pOffset, pLength);
    }

    /**
     * Update the mac with a portion of a byte array.
     * @param pBytes the bytes to update with.
     * @param pOffset the offset of the data within the byte array
     * @param pLength the length of the data to use
     */
    public abstract void doUpdate(byte[] pBytes,
                                  int pOffset,
                                  int pLength);
    @Override
    public int finish(final byte[] pBuffer,
                      final int pOffset) throws OceanusException {
        /* Check that the buffers are sufficient */
        if (pBuffer.length < (getMacSize() + pOffset)) {
            throw new IllegalArgumentException("Output buffer too short.");
        }

        /* Finish the digest */
        return doFinish(pBuffer, pOffset);
    }

    /**
     * Calculate the Mac, and return it in the buffer provided.
     * @param pBuffer the buffer to return the digest in.
     * @param pOffset the offset in the buffer to store the digest.
     * @return the number of bytes placed into buffer
     * @throws OceanusException on error
     */
    public abstract int doFinish(byte[] pBuffer,
                                 int pOffset) throws OceanusException;
}
