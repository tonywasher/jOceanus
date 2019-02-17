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
package net.sourceforge.joceanus.jgordianknot.api.keyset;

import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot KeySet API.
 */
public interface GordianKeySetFactory {
    /**
     * create KeySet.
     * @return the new keySet
     */
    GordianKeySet createKeySet();

    /**
     * Generate a keySetHash for the given password.
     * @param pPassword the password
     * @return the Password hash
     * @throws OceanusException on error
     */
    GordianKeySetHash generateKeySetHash(char[] pPassword) throws OceanusException;

    /**
     * Derive a keySetHash for the given hash and password.
     * @param pHashBytes the hash bytes
     * @param pPassword the password
     * @return the Password hash
     * @throws OceanusException on error
     * @throws GordianBadCredentialsException if password does not match
     */
    GordianKeySetHash deriveKeySetHash(byte[] pHashBytes,
                                       char[] pPassword) throws OceanusException;

    /**
     * Obtain the obfuscater.
     * @return the obfuscater
     */
    GordianKnuthObfuscater getObfuscater();

    /**
     * Obtain predicate for supported KeyHash digests.
     * @return the predicate
     */
    Predicate<GordianDigestType> supportedKeySetDigestTypes();

    /**
     * Obtain predicate for keySet SymKeyTypes.
     * @return the predicate
     */
    Predicate<GordianSymKeyType> supportedKeySetSymKeyTypes();

    /**
     * Obtain predicate for supported keySet symKeySpecs.
     * @return the predicate
     */
    Predicate<GordianSymKeySpec> supportedKeySetSymKeySpecs();
}
