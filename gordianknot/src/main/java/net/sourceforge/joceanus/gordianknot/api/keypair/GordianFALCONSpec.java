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
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.jcajce.spec.FalconParameterSpec;

/**
 * FALCON KeySpec.
 */
public enum GordianFALCONSpec {
    /**
     * Falcon 512.
     */
    FALCON512,

    /**
     * Falcon 1024.
     */
    FALCON1024;

    /**
     * Obtain FALCON Parameters.
     * @return the parameters.
     */
    public FalconParameters getParameters() {
        switch (this) {
            case FALCON512:  return FalconParameters.falcon_512;
            case FALCON1024: return FalconParameters.falcon_1024;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Falcon ParameterSpec.
     * @return the parameters.
     */
    public FalconParameterSpec getParameterSpec() {
        switch (this) {
            case FALCON512:  return FalconParameterSpec.falcon_512;
            case FALCON1024: return FalconParameterSpec.falcon_1024;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Falcon algorithm Identifier.
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (this) {
            case FALCON512:   return BCObjectIdentifiers.falcon_512;
            case FALCON1024:  return BCObjectIdentifiers.falcon_1024;
            default: throw new IllegalArgumentException();
        }
    }
}
