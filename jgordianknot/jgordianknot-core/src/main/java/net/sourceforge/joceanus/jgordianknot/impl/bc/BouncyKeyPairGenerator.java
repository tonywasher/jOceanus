/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.bc;


import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;

/**
 * BouncyCastle KeyPair generator.
 */
public abstract class BouncyKeyPairGenerator
        extends GordianCoreKeyPairGenerator {
    /**
     * Parsing error.
     */
    static final String ERROR_PARSE = "Failed to parse encoding";

    /**
     * Prime certainty.
     */
    static final int PRIME_CERTAINTY = 128;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     */
    protected BouncyKeyPairGenerator(final BouncyFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
        super(pFactory, pKeySpec);
    }
}
