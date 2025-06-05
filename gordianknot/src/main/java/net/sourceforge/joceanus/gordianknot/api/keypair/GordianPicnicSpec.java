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
import org.bouncycastle.pqc.crypto.picnic.PicnicParameters;
import org.bouncycastle.pqc.jcajce.spec.PicnicParameterSpec;

/**
 * PICNIC KeySpec.
 */
public enum GordianPicnicSpec {
    /**
     * l1fs.
     */
    L1FS,

    /**
     * l1ur.
     */
    L1UR,

    /**
     * l1full.
     */
    L1FULL,

    /**
     * 3l1.
     */
    L13,

    /**
     * l3fs.
     */
    L3FS,

    /**
     * l3ur.
     */
    L3UR,

    /**
     * l3full.
     */
    L3FULL,

    /**
     * 3l3.
     */
    L33,

    /**
     * l5fs.
     */
    L5FS,

    /**
     * l5ur.
     */
    L5UR,

    /**
     * l5full.
     */
    L5FULL,

    /**
     * 3L5.
     */
    L53;

    /**
     * Obtain SABER Parameters.
     * @return the parameters.
     */
    public PicnicParameters getParameters() {
        switch (this) {
            case L1UR:    return PicnicParameters.picnicl1ur;
            case L1FS:    return PicnicParameters.picnicl1fs;
            case L1FULL:  return PicnicParameters.picnicl1full;
            case L13:     return PicnicParameters.picnic3l1;
            case L3UR:    return PicnicParameters.picnicl3ur;
            case L3FS:    return PicnicParameters.picnicl3fs;
            case L3FULL:  return PicnicParameters.picnicl3full;
            case L33:     return PicnicParameters.picnic3l3;
            case L5UR:    return PicnicParameters.picnicl5ur;
            case L5FS:    return PicnicParameters.picnicl5fs;
            case L5FULL:  return PicnicParameters.picnicl5full;
            case L53:     return PicnicParameters.picnic3l5;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain SABER ParameterSpec.
     * @return the parameters.
     */
    public PicnicParameterSpec getParameterSpec() {
        switch (this) {
            case L1UR:   return PicnicParameterSpec.picnicl1ur;
            case L1FS:   return PicnicParameterSpec.picnicl1fs;
            case L1FULL: return PicnicParameterSpec.picnicl1full;
            case L13:    return PicnicParameterSpec.picnic3l1;
            case L3UR:   return PicnicParameterSpec.picnicl3ur;
            case L3FS:   return PicnicParameterSpec.picnicl3fs;
            case L3FULL: return PicnicParameterSpec.picnicl3full;
            case L33:    return PicnicParameterSpec.picnic3l3;
            case L5UR:   return PicnicParameterSpec.picnicl5ur;
            case L5FS:   return PicnicParameterSpec.picnicl5fs;
            case L5FULL: return PicnicParameterSpec.picnicl5full;
            case L53:    return PicnicParameterSpec.picnic3l5;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Picnic algorithm Identifier.
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (this) {
            case L1UR:   return BCObjectIdentifiers.picnicl1ur;
            case L1FS:   return BCObjectIdentifiers.picnicl1fs;
            case L1FULL: return BCObjectIdentifiers.picnicl1full;
            case L13:    return BCObjectIdentifiers.picnic3l1;
            case L3UR:   return BCObjectIdentifiers.picnicl3ur;
            case L3FS:   return BCObjectIdentifiers.picnicl3fs;
            case L3FULL: return BCObjectIdentifiers.picnicl3full;
            case L33:    return BCObjectIdentifiers.picnic3l3;
            case L5UR:   return BCObjectIdentifiers.picnicl5ur;
            case L5FS:   return BCObjectIdentifiers.picnicl5fs;
            case L5FULL: return BCObjectIdentifiers.picnicl5full;
            case L53:    return BCObjectIdentifiers.picnic3l5;
            default: throw new IllegalArgumentException();
        }
    }
}
