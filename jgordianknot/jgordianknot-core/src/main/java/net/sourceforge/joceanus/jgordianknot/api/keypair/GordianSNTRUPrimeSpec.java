/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.keypair;

import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.pqc.jcajce.spec.SNTRUPrimeParameterSpec;

/**
 * SNTRUPRIME KeySpec.
 */
public enum GordianSNTRUPrimeSpec {
    /**
     * PR653.
     */
    PR653,

    /**
     * PR761.
     */
    PR761,

    /**
     * PR857.
     */
    PR857,

    /**
     * PR953.
     */
    PR953,

    /**
     * PR1013.
     */
    PR1013,

    /**
     * PR1277.
     */
    PR1277;

    /**
     * Obtain Parameters.
     * @return the parameters.
     */
    public SNTRUPrimeParameters getParameters() {
        switch (this) {
            case PR653:   return SNTRUPrimeParameters.sntrup653;
            case PR761:   return SNTRUPrimeParameters.sntrup761;
            case PR857:   return SNTRUPrimeParameters.sntrup857;
            case PR953:   return SNTRUPrimeParameters.sntrup953;
            case PR1013:  return SNTRUPrimeParameters.sntrup1013;
            case PR1277:  return SNTRUPrimeParameters.sntrup1277;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain SNTRU ParameterSpec.
     * @return the parameters.
     */
    public SNTRUPrimeParameterSpec getParameterSpec() {
        switch (this) {
            case PR653:   return SNTRUPrimeParameterSpec.sntrup653;
            case PR761:   return SNTRUPrimeParameterSpec.sntrup761;
            case PR857:   return SNTRUPrimeParameterSpec.sntrup857;
            case PR953:   return SNTRUPrimeParameterSpec.sntrup953;
            case PR1013:  return SNTRUPrimeParameterSpec.sntrup1013;
            case PR1277:  return SNTRUPrimeParameterSpec.sntrup1277;
            default: throw new IllegalArgumentException();
        }
    }
}
