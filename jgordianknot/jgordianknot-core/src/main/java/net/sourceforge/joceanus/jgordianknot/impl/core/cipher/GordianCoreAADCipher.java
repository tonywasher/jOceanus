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
package net.sourceforge.joceanus.jgordianknot.impl.core.cipher;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianAADCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;

/**
 * GordianKnot base for AAD Cipher.
 */
public abstract class GordianCoreAADCipher
        extends GordianCoreCipher<GordianSymKeySpec>
        implements GordianAADCipher {
    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pCipherSpec the cipherSpec
     */
    protected GordianCoreAADCipher(final GordianCoreFactory pFactory,
                                   final GordianSymCipherSpec pCipherSpec) {
        super(pFactory, pCipherSpec);
    }

    @Override
    public GordianSymCipherSpec getCipherSpec() {
        return (GordianSymCipherSpec) super.getCipherSpec();
    }
}
