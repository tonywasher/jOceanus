/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.cipher;

import java.security.SecureRandom;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianKeyedCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPBESpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Core Cipher implementation.
 * @param <T> the keyType
 */
public abstract class GordianCoreCipher<T extends GordianKeySpec>
    implements GordianKeyedCipher<T> {
    /**
     * KeyType.
     */
    private final T theKeyType;

    /**
     * CipherSpec.
     */
    private final GordianCipherSpec<T> theCipherSpec;

    /**
     * The Security Factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * KeyLength.
     */
    private final GordianLength theKeyLength;

    /**
     * The Random Generator.
     */
    private final GordianRandomSource theRandom;

    /**
     * Parameters.
     */
    private GordianCoreCipherParameters<T> theParameters;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pCipherSpec the cipherSpec
     */
    protected GordianCoreCipher(final GordianCoreFactory pFactory,
                                final GordianCipherSpec<T> pCipherSpec) {
        theCipherSpec = pCipherSpec;
        theKeyType = theCipherSpec.getKeyType();
        theRandom = pFactory.getRandomSource();
        theFactory = pFactory;
        theKeyLength = pCipherSpec.getKeyType().getKeyLength();
        theParameters = new GordianCoreCipherParameters<>(theFactory, theCipherSpec);
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public T getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the cipherSpec.
     * @return the mode
     */
    public GordianCipherSpec<T> getCipherSpec() {
        return theCipherSpec;
    }

    /**
     * Obtain random generator.
     * @return the generator
     */
    protected SecureRandom getRandom() {
        return theRandom.getRandom();
    }

    @Override
    public GordianLength getKeyLength() {
        return theKeyLength;
    }

    /**
     * Obtain the blockSize.
     * @return true/false
     */
    public abstract int getBlockSize();

    @Override
    public GordianKey<T> getKey() {
        return theParameters.getKey();
    }

    @Override
    public byte[] getInitVector() {
        return theParameters.getInitVector();
    }

    @Override
    public byte[] getInitialAEAD() {
        return theParameters.getInitialAEAD();
    }

    @Override
    public byte[] getPBESalt() {
        return theParameters.getPBESalt();
    }

    @Override
    public GordianPBESpec getPBESpec() {
        return theParameters.getPBESpec();
    }

    /**
     * Init with bytes as key.
     * @param pKeyBytes the bytes to use
     * @throws OceanusException on error
     */
    public void initKeyBytes(final byte[] pKeyBytes) throws OceanusException {
        /* Create the key and initialise */
        final GordianKey<T> myKey = theParameters.buildKeyFromBytes(pKeyBytes);
        init(true, GordianCipherParameters.key(myKey));
    }

    /**
     * Process cipherParameters.
     * @param pParams the cipher parameters
     * @throws OceanusException on error
     */
    protected void processParameters(final GordianCipherParameters pParams) throws OceanusException {
        /* Process the parameters */
        theParameters.processParameters(pParams);
        checkValidKey(getKey());
    }

    /**
     * Check that the key matches the keyType.
     * @param pKey the passed key.
     * @throws OceanusException on error
     */
    protected void checkValidKey(final GordianKey<T> pKey) throws OceanusException {
        if (!theKeyType.equals(pKey.getKeyType())) {
            throw new GordianLogicException("MisMatch on keyType");
        }
    }
}
