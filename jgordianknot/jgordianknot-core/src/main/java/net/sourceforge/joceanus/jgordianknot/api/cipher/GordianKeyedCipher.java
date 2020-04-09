/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Keyed Cipher.
 * @param <T> the keyType
 */
public interface GordianKeyedCipher<T extends GordianKeySpec>
    extends GordianCipher {
    /**
     * Obtain the keyType.
     * @return the keyType
     */
    T getKeyType();

    /**
     * Obtain the cipherSpec.
     * @return the spec
     */
    GordianCipherSpec<T> getCipherSpec();

    /**
     * Obtain the initVector.
     * @return the initVector
     */
    byte[] getInitVector();

    /**
     * Obtain the initialAEAD.
     * @return the initialAEAD
     */
    byte[] getInitialAEAD();

    /**
     * Obtain the pbeSalt.
     * @return the pbeSalt
     */
    byte[] getPBESalt();

    /**
     * Obtain the pbeSpec.
     * @return the pbeSpec
     */
    GordianPBESpec getPBESpec();

    /**
     * Initialise the cipher for encryption.
     * @param pParams the parameters
     * @throws OceanusException on error
     */
    void initForEncrypt(GordianCipherParameters pParams) throws OceanusException;

    /**
     * Initialise the cipher for decryption.
     * @param pParams the parameters
     * @throws OceanusException on error
     */
    void initForDecrypt(GordianCipherParameters pParams) throws OceanusException;
}
