/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.pqc.jcajce.spec.SABERParameterSpec;

/**
 * SABER KeySpec.
 */
public enum GordianSABERSpec {
    /**
     * Light 128.
     */
    LIGHT128,

    /**
     * Base 128.
     */
    BASE128,

    /**
     * Fire 128.
     */
    FIRE128,

    /**
     * Light 192.
     */
    LIGHT192,

    /**
     * Base 192.
     */
    BASE192,

    /**
     * Fire 192.
     */
    FIRE192,

    /**
     * Light 256.
     */
    LIGHT256,

    /**
     * Base 256.
     */
    BASE256,

    /**
     * Fire 256.
     */
    FIRE256;

    /**
     * Obtain SABER Parameters.
     * @return the parameters.
     */
    public SABERParameters getParameters() {
        switch (this) {
            case LIGHT128: return SABERParameters.lightsaberkem128r3;
            case BASE128:  return SABERParameters.saberkem128r3;
            case FIRE128:  return SABERParameters.firesaberkem128r3;
            case LIGHT192: return SABERParameters.lightsaberkem192r3;
            case BASE192:  return SABERParameters.saberkem192r3;
            case FIRE192:  return SABERParameters.firesaberkem192r3;
            case LIGHT256: return SABERParameters.lightsaberkem256r3;
            case BASE256:  return SABERParameters.saberkem256r3;
            case FIRE256:  return SABERParameters.firesaberkem256r3;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain SABER ParameterSpec.
     * @return the parameters.
     */
    public SABERParameterSpec getParameterSpec() {
        switch (this) {
            case LIGHT128: return SABERParameterSpec.lightsaberkem128r3;
            case BASE128:  return SABERParameterSpec.saberkem128r3;
            case FIRE128:  return SABERParameterSpec.firesaberkem128r3;
            case LIGHT192: return SABERParameterSpec.lightsaberkem192r3;
            case BASE192:  return SABERParameterSpec.saberkem192r3;
            case FIRE192:  return SABERParameterSpec.firesaberkem192r3;
            case LIGHT256: return SABERParameterSpec.lightsaberkem256r3;
            case BASE256:  return SABERParameterSpec.saberkem256r3;
            case FIRE256:  return SABERParameterSpec.firesaberkem256r3;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Saber algorithm Identifier.
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (this) {
            case LIGHT128:   return BCObjectIdentifiers.lightsaberkem128r3;
            case BASE128:    return BCObjectIdentifiers.saberkem128r3;
            case FIRE128:    return BCObjectIdentifiers.firesaberkem128r3;
            case LIGHT192:   return BCObjectIdentifiers.lightsaberkem192r3;
            case BASE192:    return BCObjectIdentifiers.saberkem192r3;
            case FIRE192:    return BCObjectIdentifiers.firesaberkem192r3;
            case LIGHT256:   return BCObjectIdentifiers.lightsaberkem256r3;
            case BASE256:    return BCObjectIdentifiers.saberkem256r3;
            case FIRE256:    return BCObjectIdentifiers.firesaberkem256r3;
            default: throw new IllegalArgumentException();
        }
    }
}
