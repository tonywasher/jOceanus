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

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherSpec;
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
    implements GordianCipher<T> {
    /**
     * KeyType.
     */
    private final T theKeyType;

    /**
     * CipherSpec.
     */
    private final GordianCipherSpec<T> theCipherSpec;

    /**
     * Restricted.
     */
    private final boolean isRestricted;

    /**
     * The Random Generator.
     */
    private final GordianRandomSource theRandom;

    /**
     * Key.
     */
    private GordianKey<T> theKey;

    /**
     * InitialisationVector.
     */
    private byte[] theInitVector;

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
        isRestricted = pFactory.isRestricted();
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

    /**
     * Is the cipher restricted?
     * @return true/false
     */
    public boolean isRestricted() {
        return isRestricted;
    }

    /**
     * Obtain the key.
     * @return the key
     */
    public GordianKey<T> getKey() {
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
    protected void setKey(final GordianKey<T> pKey) {
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
