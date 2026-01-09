/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.keyset;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;

/**
 * Class to contain the symmetric key ciphers.
 */
final class GordianSymKeyCipherSet {
    /**
     * Key.
     */
    private final GordianKey<GordianSymKeySpec> theKey;

    /**
     * ECB Cipher (padding).
     */
    private final GordianSymCipher thePaddingCipher;

    /**
     * ECB Cipher (noPadding).
     */
    private final GordianSymCipher theStandardCipher;

    /**
     * Stream Cipher.
     */
    private final GordianSymCipher theStreamCipher;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pKey the key
     * @throws GordianException on error
     */
    GordianSymKeyCipherSet(final GordianBaseFactory pFactory,
                           final GordianKey<GordianSymKeySpec> pKey) throws GordianException {
        /* Store parameters */
        theKey = pKey;
        final GordianSymKeySpec myKeySpec = theKey.getKeyType();
        final GordianCipherFactory myFactory = pFactory.getCipherFactory();

        /* Create the standard ciphers */
        thePaddingCipher = myFactory.createSymKeyCipher(GordianSymCipherSpecBuilder.ecb(myKeySpec, GordianPadding.PKCS7));
        theStandardCipher = myFactory.createSymKeyCipher(GordianSymCipherSpecBuilder.ecb(myKeySpec, GordianPadding.NONE));
        theStreamCipher = myFactory.createSymKeyCipher(GordianSymCipherSpecBuilder.sic(myKeySpec));
    }

    /**
     * Obtain the key.
     * @return the Key
     */
    GordianKey<GordianSymKeySpec> getKey() {
        return theKey;
    }

    /**
     * Obtain the Padding cipher.
     * @return the Padding Cipher
     */
    GordianSymCipher getPaddingCipher() {
        return thePaddingCipher;
    }

    /**
     * Obtain the Stream cipher.
     * @return the Stream Cipher
     */
    GordianSymCipher getStreamCipher() {
        return theStreamCipher;
    }

    /**
     * Obtain the Standard cipher.
     * @return the Standard Cipher
     */
    GordianSymCipher getStandardCipher() {
        return theStandardCipher;
    }
}
