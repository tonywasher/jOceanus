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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Wrapper for JCA BouncyCastle KeyGenerator.
 * @param <T> the keyType
 */
public final class JcaKeyGenerator<T>
        extends GordianKeyGenerator<T> {
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
    protected JcaKeyGenerator(final JcaFactory pFactory,
                              final T pKeyType,
                              final KeyGenerator pGenerator) {
        /* Initialise underlying class */
        super(pFactory, pKeyType);

        /* Store parameters */
        theGenerator = pGenerator;

        /* Initialise the generator */
        theGenerator.init(getKeyLength(), getRandom());
    }

    @Override
    public JcaKey<T> generateKey() {
        /* Generate the new key */
        final SecretKey myKey = theGenerator.generateKey();

        /* Build the new key */
        return new JcaKey<>(getKeyType(), myKey);
    }

    @Override
    protected JcaKey<T> translateKey(final GordianKey<?> pSource) throws OceanusException {
        /* Access key correctly */
        final JcaKey<?> mySource = JcaKey.accessKey(pSource);

        /* Build the new key */
        return buildKeyFromBytes(mySource.getKey().getEncoded());
    }

    @Override
    protected JcaKey<T> buildKeyFromBytes(final byte[] pBytes) {
        /* Build the new key */
        final SecretKey myKey = new SecretKeySpec(pBytes, theGenerator.getAlgorithm());
        return new JcaKey<>(getKeyType(), myKey);
    }

    @Override
    public JcaKey<T> generateKeyFromSecret(final byte[] pSecret,
                                           final byte[] pInitVector) throws OceanusException {
        return (JcaKey<T>) super.generateKeyFromSecret(pSecret, pInitVector);
    }
}
