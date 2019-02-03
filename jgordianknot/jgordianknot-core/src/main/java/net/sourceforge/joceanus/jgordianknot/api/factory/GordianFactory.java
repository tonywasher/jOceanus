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
package net.sourceforge.joceanus.jgordianknot.api.factory;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;

/**
 * Factory API.
 */
public interface GordianFactory {
    /**
     * Obtain factory type.
     * @return the factory type
     */
    GordianFactoryType getFactoryType();

    /**
     * Obtain the Digest Factory.
     * @return the digest factory
     */
    GordianDigestFactory getDigestFactory();

    /**
     * Obtain the Cipher Factory.
     * @return the cipher factory
     */
    GordianCipherFactory getCipherFactory();

    /**
     * Obtain the Mac Factory.
     * @return the Mac factory
     */
    GordianMacFactory getMacFactory();

    /**
     * Obtain the keySet Factory.
     * @return the keySet factory
     */
    GordianKeySetFactory getKeySetFactory();

    /**
     * Obtain the random Factory.
     * @return the random factory
     */
    GordianRandomFactory getRandomFactory();

    /**
     * Obtain the Zip Factory.
     * @return the zip factory
     */
    GordianZipFactory getZipFactory();

    /**
     * Obtain the asymmetric Factory.
     * @return the asym factory
     */
    GordianAsymFactory getAsymmetricFactory();

    /**
     * ReSeed the random number generator.
     */
    void reSeedRandom();
}
