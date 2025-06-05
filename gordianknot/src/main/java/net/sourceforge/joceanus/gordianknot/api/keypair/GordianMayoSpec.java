/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.keypair;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.jcajce.spec.MayoParameterSpec;

/**
 * Mayo KeySpec.
 */
public enum GordianMayoSpec {
    /**
     * Mayo 1.
     */
    MAYO1,

    /**
     * Mayo 2.
     */
    MAYO2,

    /**
     * Mayo 3.
     */
    MAYO3,

    /**
     * Mayo 5.
     */
    MAYO5;

    /**
     * Obtain Mayo Parameters.
     * @return the parameters.
     */
    public MayoParameters getParameters() {
        switch (this) {
            case MAYO1: return MayoParameters.mayo1;
            case MAYO2: return MayoParameters.mayo2;
            case MAYO3: return MayoParameters.mayo3;
            case MAYO5: return MayoParameters.mayo5;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Mayo ParameterSpec.
     * @return the parameters.
     */
    public MayoParameterSpec getParameterSpec() {
        switch (this) {
            case MAYO1: return MayoParameterSpec.mayo1;
            case MAYO2: return MayoParameterSpec.mayo2;
            case MAYO3: return MayoParameterSpec.mayo3;
            case MAYO5: return MayoParameterSpec.mayo5;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain MAYO algorithm Identifier.
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (this) {
            case MAYO1:  return BCObjectIdentifiers.mayo1;
            case MAYO2:  return BCObjectIdentifiers.mayo2;
            case MAYO3:  return BCObjectIdentifiers.mayo3;
            case MAYO5:  return BCObjectIdentifiers.mayo5;
            default: throw new IllegalArgumentException();
        }
    }
}
