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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKeyGenerator;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

import java.util.Random;

/**
 * Wrapper for BouncyCastle KeyGenerator.
 * @param <T> the keyType
 */
public final class BouncyKeyGenerator<T extends GordianKeySpec>
        extends GordianCoreKeyGenerator<T> {
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
    BouncyKeyGenerator(final BouncyFactory pFactory,
                       final T pKeyType,
                       final CipherKeyGenerator pGenerator) {
        /* Initialise underlying class */
        super(pFactory, pKeyType);

        /* Store parameters */
        theGenerator = pGenerator;

        /* Initialise the generator */
        final KeyGenerationParameters myParms = new KeyGenerationParameters(getRandom(), pKeyType.getKeyLength().getLength());
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
    public BouncyKey<T> buildKeyFromBytes(final byte[] pBytes) {
        /* Build the new key */
        return new BouncyKey<>(getKeyType(), pBytes);
    }

    @Override
    public BouncyKey<T> generateKeyFromSecret(final byte[] pSecret,
                                               final Random pSeededRandom) throws GordianException {
        return (BouncyKey<T>) super.generateKeyFromSecret(pSecret, pSeededRandom);
    }

    @Override
    public void initMacKeyBytes(final GordianMac pMac,
                                final byte[] pKeyBytes) throws GordianException {
        pMac.initKeyBytes(pKeyBytes);
    }
}
