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
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.jcajce.spec.FrodoParameterSpec;

/**
 * FRODO KeySpecs.
 */
public enum GordianFRODOSpec {
    /**
     * AES 640.
     */
    AES640,

    /**
     * SHAKE 640.
     */
    SHAKE640,

    /**
     * AES 976.
     */
    AES976,

    /**
     * SHAKE 976.
     */
    SHAKE976,

    /**
     * AES 1344.
     */
    AES1344,

    /**
     * SHAKE 1344.
     */
    SHAKE1344;

    /**
     * Obtain Frodo Parameters.
     *
     * @return the parameters.
     */
    public FrodoParameters getParameters() {
        switch (this) {
            case AES640:
                return FrodoParameters.frodokem640aes;
            case SHAKE640:
                return FrodoParameters.frodokem640shake;
            case AES976:
                return FrodoParameters.frodokem976aes;
            case SHAKE976:
                return FrodoParameters.frodokem976shake;
            case AES1344:
                return FrodoParameters.frodokem1344aes;
            case SHAKE1344:
                return FrodoParameters.frodokem1344shake;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Frodo ParameterSpec.
     *
     * @return the parameters.
     */
    public FrodoParameterSpec getParameterSpec() {
        switch (this) {
            case AES640:
                return FrodoParameterSpec.frodokem640aes;
            case SHAKE640:
                return FrodoParameterSpec.frodokem640shake;
            case AES976:
                return FrodoParameterSpec.frodokem976aes;
            case SHAKE976:
                return FrodoParameterSpec.frodokem976shake;
            case AES1344:
                return FrodoParameterSpec.frodokem1344aes;
            case SHAKE1344:
                return FrodoParameterSpec.frodokem1344shake;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Frodo algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (this) {
            case AES640:
                return BCObjectIdentifiers.frodokem640aes;
            case SHAKE640:
                return BCObjectIdentifiers.frodokem640shake;
            case AES976:
                return BCObjectIdentifiers.frodokem976aes;
            case SHAKE976:
                return BCObjectIdentifiers.frodokem976shake;
            case AES1344:
                return BCObjectIdentifiers.frodokem1344aes;
            case SHAKE1344:
                return BCObjectIdentifiers.frodokem1344shake;
            default:
                throw new IllegalArgumentException();
        }
    }
}
