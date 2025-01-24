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
package net.sourceforge.joceanus.gordianknot.impl.jca;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKeyGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;

/**
 * Wrapper for JCA BouncyCastle KeyGenerator.
 * @param <T> the keyType
 */
public final class JcaKeyGenerator<T extends GordianKeySpec>
        extends GordianCoreKeyGenerator<T> {
    /**
     * The Key Generator.
     */
    private final KeyGenerator theGenerator;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     * @param pGenerator the key generator
     */
    JcaKeyGenerator(final JcaFactory pFactory,
                    final T pKeyType,
                    final KeyGenerator pGenerator) {
        /* Initialise underlying class */
        super(pFactory, pKeyType);

        /* Store parameters */
        theGenerator = pGenerator;

        /* Initialise the generator */
        theGenerator.init(pKeyType.getKeyLength().getLength(), getRandom());
    }

    @Override
    public JcaKey<T> generateKey() {
        /* Generate the new key */
        final SecretKey myKey = theGenerator.generateKey();

        /* Build the new key */
        return new JcaKey<>(getKeyType(), myKey);
    }

    @Override
    public JcaKey<T> buildKeyFromBytes(final byte[] pBytes) {
        /* Build the new key */
        final SecretKey myKey = new SecretKeySpec(pBytes, theGenerator.getAlgorithm());
        return new JcaKey<>(getKeyType(), myKey);
    }

    @Override
    public JcaKey<T> generateKeyFromSecret(final byte[] pSecret,
                                            final Random pSeededRandom) throws GordianException {
        return (JcaKey<T>) super.generateKeyFromSecret(pSecret, pSeededRandom);
    }

    @Override
    public void initMacKeyBytes(final GordianMac pMac,
                                final byte[] pKeyBytes) throws GordianException {
        pMac.initKeyBytes(pKeyBytes);
    }
}
