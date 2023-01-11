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

import org.bouncycastle.pqc.crypto.crystals.kyber.KyberParameters;
import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;

/**
 * Kyber KeySpec.
 */
public enum GordianKYBERSpec {
    /**
     * Kyber 512.
     */
    KYBER512,

    /**
     * Kyber 768.
     */
    KYBER768,

    /**
     * Kyber 1024.
     */
    KYBER1024,

    /**
     * Kyber 512 AES.
     */
    KYBER512AES,

    /**
     * Kyber 768 AES.
     */
    KYBER768AES,

    /**
     * Kyber 1024 AES.
     */
    KYBER1024AES;

    /**
     * Obtain KYBER Parameters.
     * @return the parameters.
     */
    public KyberParameters getParameters() {
        switch (this) {
            case KYBER512:     return KyberParameters.kyber512;
            case KYBER768:     return KyberParameters.kyber768;
            case KYBER1024:    return KyberParameters.kyber1024;
            case KYBER512AES:  return KyberParameters.kyber512_aes;
            case KYBER768AES:  return KyberParameters.kyber768_aes;
            case KYBER1024AES: return KyberParameters.kyber1024_aes;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Kyber ParameterSpec.
     * @return the parameters.
     */
    public KyberParameterSpec getParameterSpec() {
        switch (this) {
            case KYBER512:     return KyberParameterSpec.kyber512;
            case KYBER768:     return KyberParameterSpec.kyber768;
            case KYBER1024:    return KyberParameterSpec.kyber1024;
            case KYBER512AES:  return KyberParameterSpec.kyber512_aes;
            case KYBER768AES:  return KyberParameterSpec.kyber768_aes;
            case KYBER1024AES: return KyberParameterSpec.kyber1024_aes;
            default: throw new IllegalArgumentException();
        }
    }
}
