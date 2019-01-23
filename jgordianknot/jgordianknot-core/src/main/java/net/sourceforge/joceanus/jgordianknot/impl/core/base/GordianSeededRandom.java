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
package net.sourceforge.joceanus.jgordianknot.impl.core.base;

import java.security.SecureRandom;

/**
 * SecureRandom interface.
 */
public interface GordianSeededRandom {
    /**
     * Generate seed bytes.
     * @param pLength the length of the seed bytes needed
     * @return the seed bytes
     */
    byte[] generateSeed(int pLength);

    /**
     * Set the seed.
     * @param pSeed the seed
     */
    void setSeed(byte[] pSeed);

    /**
     * ReSeed.
     * @param pXtraBytes the optional extra bytes
     */
    void reseed(byte[] pXtraBytes);

    /**
     * Obtain the secureRandom.
     * @return the secure random
     */
    SecureRandom getRandom();
}
