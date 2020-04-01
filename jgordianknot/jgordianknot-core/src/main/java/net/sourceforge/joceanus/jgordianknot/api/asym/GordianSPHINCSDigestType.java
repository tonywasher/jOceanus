/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.asym;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;

/**
 * SPHINCS digestTypes.
 */
public enum GordianSPHINCSDigestType {
    /**
     * sha2.
     */
    SHA2,

    /**
     * sha3.
     */
    SHA3;

    /**
     * Obtain the required digestSpec.
     * @return the digestSpec
     */
    public GordianDigestSpec getDigestSpec() {
        switch (this) {
            case SHA2:
                return GordianDigestSpec.sha2(GordianLength.LEN_512);
            case SHA3:
                return GordianDigestSpec.sha3(GordianLength.LEN_512);
            default:
                throw new IllegalStateException();
        }
    }
}
