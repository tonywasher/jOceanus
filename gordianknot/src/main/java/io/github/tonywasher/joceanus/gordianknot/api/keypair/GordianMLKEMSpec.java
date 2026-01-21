/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.api.keypair;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;

/**
 * Kyber KeySpec.
 */
public enum GordianMLKEMSpec {
    /**
     * Kyber 512.
     */
    MLKEM512,

    /**
     * Kyber 768.
     */
    MLKEM768,

    /**
     * Kyber 1024.
     */
    MLKEM1024;

    /**
     * Obtain KYBER Parameters.
     *
     * @return the parameters.
     */
    public MLKEMParameters getParameters() {
        switch (this) {
            case MLKEM512:
                return MLKEMParameters.ml_kem_512;
            case MLKEM768:
                return MLKEMParameters.ml_kem_768;
            case MLKEM1024:
                return MLKEMParameters.ml_kem_1024;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Kyber ParameterSpec.
     *
     * @return the parameters.
     */
    public MLKEMParameterSpec getParameterSpec() {
        switch (this) {
            case MLKEM512:
                return MLKEMParameterSpec.ml_kem_512;
            case MLKEM768:
                return MLKEMParameterSpec.ml_kem_768;
            case MLKEM1024:
                return MLKEMParameterSpec.ml_kem_1024;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain MLKEM algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (this) {
            case MLKEM512:
                return NISTObjectIdentifiers.id_alg_ml_kem_512;
            case MLKEM768:
                return NISTObjectIdentifiers.id_alg_ml_kem_768;
            case MLKEM1024:
                return NISTObjectIdentifiers.id_alg_ml_kem_1024;
            default:
                throw new IllegalArgumentException();
        }
    }
}
