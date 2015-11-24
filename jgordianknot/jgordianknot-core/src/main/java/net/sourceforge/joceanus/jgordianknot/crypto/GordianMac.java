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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/CipherSetRecipe.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;

import net.sourceforge.joceanus.jgordianknot.GordianLogicException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot interface for Message Authentication Codes.
 */
public abstract class GordianMac {
    /**
     * MacSpec.
     */
    private final GordianMacSpec theMacSpec;

    /**
     * The Security Factory.
     */
    private final GordianFactory theFactory;

    /**
     * The Random Generator.
     */
    private final SecureRandom theRandom;

    /**
     * The KeyGenerator.
     */
    private GordianKeyGenerator<GordianMacSpec> theGenerator;

    /**
     * Key.
     */
    private GordianKey<GordianMacSpec> theKey;

    /**
     * InitialisationVector.
     */
    private byte[] theInitVector;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pMacSpec the macSpec
     */
    protected GordianMac(final GordianFactory pFactory,
                         final GordianMacSpec pMacSpec) {
        theMacSpec = pMacSpec;
        theFactory = pFactory;
        theRandom = pFactory.getRandom();
    }

    /**
     * Obtain MacSpec.
     * @return the MacSpec
     */
    public GordianMacSpec getMacSpec() {
        return theMacSpec;
    }

    /**
     * Obtain the key.
     * @return the key
     */
    public GordianKey<GordianMacSpec> getKey() {
        return theKey;
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Store key.
     * @param pKey the key
     */
    protected void setKey(final GordianKey<GordianMacSpec> pKey) {
        theKey = pKey;
    }

    /**
     * Store initVector.
     * @param pInitVector the initVector
     */
    protected void setInitVector(final byte[] pInitVector) {
        theInitVector = pInitVector;
    }

    /**
     * Obtain the MAC size.
     * @return the MAC size
     */
    public abstract int getMacSize();

    /**
     * Check that the key matches the keyType.
     * @param pKey the passed key.
     * @throws OceanusException on error
     */
    protected void checkValidKey(final GordianKey<GordianMacSpec> pKey) throws OceanusException {
        if (!theMacSpec.equals(pKey.getKeyType())) {
            throw new GordianLogicException("MisMatch on macSpec");
        }
    }

    /**
     * Initialise the MAC with KeyBytes and random IV (if needed).
     * @param pKeyBytes the keyBytes
     * @throws OceanusException on error
     */
    public void initMac(final byte[] pKeyBytes) throws OceanusException {
        /* Create generator if needed */
        if (theGenerator == null) {
            theGenerator = theFactory.getKeyGenerator(theMacSpec);
        }

        /* Create the key and initialise */
        GordianKey<GordianMacSpec> myKey = theGenerator.buildKeyFromBytes(pKeyBytes);
        initMac(myKey);
    }

    /**
     * Initialise the MAC with Key and random IV (if needed).
     * @param pKey the key
     * @throws OceanusException on error
     */
    public void initMac(final GordianKey<GordianMacSpec> pKey) throws OceanusException {
        /* Determine the required length of IV */
        int myLen = getMacSpec().getMacType().getIVLen();
        byte[] myIV = null;

        /* If we need an IV */
        if (myLen > 0) {
            /* Create a random IV */
            myIV = new byte[myLen];
            theRandom.nextBytes(myIV);
        }

        /* initialise with this IV */
        initMac(pKey, myIV);
    }

    /**
     * Initialise with key.
     * @param pKey the key to initialise with
     * @param pIV the initialisation vector (or null)
     * @throws OceanusException on error
     */
    public abstract void initMac(final GordianKey<GordianMacSpec> pKey,
                                 final byte[] pIV) throws OceanusException;

    /**
     * Update the MAC with a portion of a byte array.
     * @param pBytes the bytes to update with.
     * @param pOffset the offset of the data within the byte array
     * @param pLength the length of the data to use
     */
    public abstract void update(final byte[] pBytes,
                                final int pOffset,
                                final int pLength);

    /**
     * Update the MAC with a single byte.
     * @param pByte the byte to update with.
     */
    public abstract void update(final byte pByte);

    /**
     * Update the MAC with a byte array.
     * @param pBytes the bytes to update with.
     */
    public abstract void update(final byte[] pBytes);

    /**
     * Reset the MAC.
     */
    public abstract void reset();

    /**
     * Calculate the MAC.
     * @return the MAC
     */
    public abstract byte[] finish();

    /**
     * Calculate the MAC, and return it in the buffer provided.
     * @param pBuffer the buffer to return the digest in.
     * @param pOffset the offset in the buffer to store the digest.
     * @return the number of bytes placed into buffer
     * @throws OceanusException on error
     */
    public abstract int finish(final byte[] pBuffer,
                               final int pOffset) throws OceanusException;

    /**
     * Update the MAC, calculate and reset it.
     * @param pBytes the bytes to update with.
     * @return the MAC
     */
    public byte[] finish(final byte[] pBytes) {
        update(pBytes);
        return finish();
    }
}
