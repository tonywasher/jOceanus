/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Wrapper for BouncyCastle KeyGenerator.
 * @param <T> the keyType
 */
public final class BouncyKeyGenerator<T>
        extends GordianKeyGenerator<T> {
    /**
     * The Key Generator.
     */
    private final CipherKeyGenerator theGenerator;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     * @param pGenerator the key generator
     */
    protected BouncyKeyGenerator(final BouncyFactory pFactory,
                                 final T pKeyType,
                                 final CipherKeyGenerator pGenerator) {
        /* Initialise underlying class */
        super(pFactory, pKeyType);

        /* Store parameters */
        theGenerator = pGenerator;

        /* Initialise the generator */
        final KeyGenerationParameters myParms = new KeyGenerationParameters(getRandom(), getKeyLength());
        theGenerator.init(myParms);
    }

    @Override
    public BouncyKey<T> generateKey() {
        /* Generate the new keyBytes */
        final byte[] myKeyBytes = theGenerator.generateKey();

        /* Build the new key */
        return buildKeyFromBytes(myKeyBytes);
    }

    @Override
    protected BouncyKey<T> translateKey(final GordianKey<?> pSource) throws OceanusException {
        /* Access key correctly */
        final BouncyKey<?> mySource = BouncyKey.accessKey(pSource);

        /* Build the new key */
        return buildKeyFromBytes(mySource.getKey());
    }

    @Override
    protected BouncyKey<T> buildKeyFromBytes(final byte[] pBytes) {
        /* Build the new key */
        return new BouncyKey<>(getKeyType(), pBytes);
    }

    @Override
    public BouncyKey<T> generateKeyFromSecret(final byte[] pSecret,
                                              final byte[] pInitVector) throws OceanusException {
        return (BouncyKey<T>) super.generateKeyFromSecret(pSecret, pInitVector);
    }
}
