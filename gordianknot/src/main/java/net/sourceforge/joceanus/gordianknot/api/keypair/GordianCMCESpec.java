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
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;
import org.bouncycastle.pqc.jcajce.spec.CMCEParameterSpec;

/**
 * CMCE KeySpecs.
 */
public enum GordianCMCESpec {
    /**
     * Base 3488.
     */
    BASE3488,

    /**
     * Pivot 3488.
     */
    PIVOT3488,

    /**
     * Base 4608.
     */
    BASE4608,

    /**
     * Pivot 4608.
     */
    PIVOT4608,

    /**
     * Base 6688.
     */
    BASE6688,

    /**
     * Pivot 6688.
     */
    PIVOT6688,

    /**
     * Base 6960.
     */
    BASE6960,

    /**
     * Pivot 6960.
     */
    PIVOT6960,

    /**
     * Base 8192.
     */
    BASE8192,

    /**
     * Pivot 8192.
     */
    PIVOT8192;

    /**
     * Obtain CMCE Parameters.
     * @return the parameters.
     */
    public CMCEParameters getParameters() {
        switch (this) {
            case BASE3488:  return CMCEParameters.mceliece348864r3;
            case PIVOT3488: return CMCEParameters.mceliece348864fr3;
            case BASE4608:  return CMCEParameters.mceliece460896r3;
            case PIVOT4608: return CMCEParameters.mceliece460896fr3;
            case BASE6688:  return CMCEParameters.mceliece6688128r3;
            case PIVOT6688: return CMCEParameters.mceliece6688128fr3;
            case BASE6960:  return CMCEParameters.mceliece6960119r3;
            case PIVOT6960: return CMCEParameters.mceliece6960119fr3;
            case BASE8192:  return CMCEParameters.mceliece8192128r3;
            case PIVOT8192: return CMCEParameters.mceliece8192128fr3;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain CMCE ParameterSpec.
     * @return the parameters.
     */
    public CMCEParameterSpec getParameterSpec() {
        switch (this) {
            case BASE3488:  return CMCEParameterSpec.mceliece348864;
            case PIVOT3488: return CMCEParameterSpec.mceliece348864f;
            case BASE4608:  return CMCEParameterSpec.mceliece460896;
            case PIVOT4608: return CMCEParameterSpec.mceliece460896f;
            case BASE6688:  return CMCEParameterSpec.mceliece6688128;
            case PIVOT6688: return CMCEParameterSpec.mceliece6688128f;
            case BASE6960:  return CMCEParameterSpec.mceliece6960119;
            case PIVOT6960: return CMCEParameterSpec.mceliece6960119f;
            case BASE8192:  return CMCEParameterSpec.mceliece8192128;
            case PIVOT8192: return CMCEParameterSpec.mceliece8192128f;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain CMCE algorithm Identifier.
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (this) {
            case BASE3488:      return BCObjectIdentifiers.mceliece348864_r3;
            case PIVOT3488:     return BCObjectIdentifiers.mceliece348864f_r3;
            case BASE4608:      return BCObjectIdentifiers.mceliece460896_r3;
            case PIVOT4608:     return BCObjectIdentifiers.mceliece460896f_r3;
            case BASE6688:      return BCObjectIdentifiers.mceliece6688128_r3;
            case PIVOT6688:     return BCObjectIdentifiers.mceliece6688128f_r3;
            case BASE6960:      return BCObjectIdentifiers.mceliece6960119_r3;
            case PIVOT6960:     return BCObjectIdentifiers.mceliece6960119f_r3;
            case BASE8192:      return BCObjectIdentifiers.mceliece8192128_r3;
            case PIVOT8192:     return BCObjectIdentifiers.mceliece8192128f_r3;
            default: throw new IllegalArgumentException();
        }
    }
}
