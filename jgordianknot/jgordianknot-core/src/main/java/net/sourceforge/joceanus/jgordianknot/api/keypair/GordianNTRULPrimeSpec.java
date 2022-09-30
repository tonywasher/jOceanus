/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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

import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.jcajce.spec.NTRULPRimeParameterSpec;

/**
 * NTRULPRIME KeySpec.
 */
public enum GordianNTRULPrimeSpec {
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
    public NTRULPRimeParameters getParameters() {
        switch (this) {
            case PR653:   return NTRULPRimeParameters.ntrulpr653;
            case PR761:   return NTRULPRimeParameters.ntrulpr761;
            case PR857:   return NTRULPRimeParameters.ntrulpr857;
            case PR953:   return NTRULPRimeParameters.ntrulpr953;
            case PR1013:  return NTRULPRimeParameters.ntrulpr1013;
            case PR1277:  return NTRULPRimeParameters.ntrulpr1277;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain ParameterSpec.
     * @return the parameters.
     */
    public NTRULPRimeParameterSpec getParameterSpec() {
        switch (this) {
            case PR653:   return NTRULPRimeParameterSpec.ntrulpr653;
            case PR761:   return NTRULPRimeParameterSpec.ntrulpr761;
            case PR857:   return NTRULPRimeParameterSpec.ntrulpr857;
            case PR953:   return NTRULPRimeParameterSpec.ntrulpr953;
            case PR1013:  return NTRULPRimeParameterSpec.ntrulpr1013;
            case PR1277:  return NTRULPRimeParameterSpec.ntrulpr1277;
            default: throw new IllegalArgumentException();
        }
    }
}
