/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot base for AAD Cipher.
 */
public abstract class GordianAADCipher
        extends GordianCipher<GordianSymKeyType> {
    /**
     * The IV length.
     */
    protected static final int AADIVLEN = 12;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     * @param pMode the cipher mode
     */
    protected GordianAADCipher(final GordianFactory pFactory,
                               final GordianSymKeyType pKeyType,
                               final GordianCipherMode pMode) {
        super(pFactory, pKeyType, pMode, GordianPadding.NONE);
    }

    /**
     * Process the passed AAD data.
     * @param pBytes AAD Bytes to update cipher with
     * @throws OceanusException on error
     */
    public void updateAAD(final byte[] pBytes) throws OceanusException {
        updateAAD(pBytes, 0, pBytes.length);
    }

    /**
     * Process the passed AAD data.
     * @param pBytes AAD Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @throws OceanusException on error
     */
    public abstract void updateAAD(final byte[] pBytes,
                                   final int pOffset,
                                   final int pLength) throws OceanusException;
}