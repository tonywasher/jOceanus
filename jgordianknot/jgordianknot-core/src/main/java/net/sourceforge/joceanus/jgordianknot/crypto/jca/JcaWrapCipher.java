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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianWrapCipher;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher for BouncyCastle Wrap Ciphers.
 */
public class JcaWrapCipher
        extends GordianWrapCipher {
    /**
     * Constructor.
     * @param pFactory the factory
     * @param pCipher the cipher
     */
    protected JcaWrapCipher(final JcaFactory pFactory,
                            final JcaCipher<GordianSymKeyType> pCipher) {
        /* Initialise underlying class */
        super(pFactory, pCipher);
    }

    @Override
    protected JcaFactory getFactory() {
        return (JcaFactory) super.getFactory();
    }

    @Override
    public byte[] wrapKey(final GordianKey<GordianSymKeyType> pKey,
                          final GordianKey<?> pKeyToWrap) throws OceanusException {
        /* Access the keyToWrap */
        JcaKey<?> myKeyToWrap = JcaKey.accessKey(pKeyToWrap);
        return wrapBytes(pKey, myKeyToWrap.getKey().getEncoded());
    }

    @Override
    public <T> JcaKey<T> unwrapKey(final GordianKey<GordianSymKeyType> pKey,
                                   final byte[] pBytes,
                                   final T pKeyType) throws OceanusException {
        /* Unwrap the bytes */
        byte[] myBytes = unwrapBytes(pKey, pBytes);

        /* Generate the key */
        JcaKeyGenerator<T> myGenerator = getFactory().getKeyGenerator(pKeyType);
        return myGenerator.buildKeyFromBytes(myBytes);
    }
}
