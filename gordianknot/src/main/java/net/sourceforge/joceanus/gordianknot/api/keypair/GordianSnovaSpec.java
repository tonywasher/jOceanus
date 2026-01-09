/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.jcajce.spec.SnovaParameterSpec;

/**
 * Mayo KeySpec.
 */
public enum GordianSnovaSpec {
    /**
     * Snova24A SSK.
     */
    SNOVA24A_SSK,

    /**
     * Snova24A ESK.
     */
    SNOVA24A_ESK,

    /**
     * Snova24A SHAKE SSK.
     */
    SNOVA24A_SHAKE_SSK,

    /**
     * Snova24A SHAKE ESK.
     */
    SNOVA24A_SHAKE_ESK,

    /**
     * Snova24B SSK.
     */
    SNOVA24B_SSK,

    /**
     * SNova24B ESK.
     */
    SNOVA24B_ESK,

    /**
     * SNova24B SHAKESSK.
     */
    SNOVA24B_SHAKE_SSK,

    /**
     * SNova24B SHAKE ESK.
     */
    SNOVA24B_SHAKE_ESK,

    /**
     * SNova25 SSK.
     */
    SNOVA25_SSK,

    /**
     * SNova25 ESK.
     */
    SNOVA25_ESK,

    /**
     * SNova25 SHAKE SSK.
     */
    SNOVA25_SHAKE_SSK,

    /**
     * SNova25 SHAKE ESK.
     */
    SNOVA25_SHAKE_ESK,

    /**
     * SNova29.
     */
    SNOVA29_SSK,

    /**
     * SNova29.
     */
    SNOVA29_ESK,

    /**
     * SNova29 SHAKE SSK.
     */
    SNOVA29_SHAKE_SSK,

    /**
     * SNova29 SHAKE ESK.
     */
    SNOVA29_SHAKE_ESK,

    /**
     * SNova37A SSK.
     */
    SNOVA37A_SSK,

    /**
     * SNova37A ESK.
     */
    SNOVA37A_ESK,

    /**
     * SNova37A SHAKE SSK.
     */
    SNOVA37A_SHAKE_SSK,

    /**
     * SNova37A SHAKE ESK.
     */
    SNOVA37A_SHAKE_ESK,

    /**
     * SNova37B SSK.
     */
    SNOVA37B_SSK,

    /**
     * SNova37B ESK.
     */
    SNOVA37B_ESK,

    /**
     * SNova37B SHAKE SSK.
     */
    SNOVA37B_SHAKE_SSK,

    /**
     * SNova37B SHAKE ESK.
     */
    SNOVA37B_SHAKE_ESK,

    /**
     * SNova49 SSK.
     */
    SNOVA49_SSK,

    /**
     * SNova49 ESK.
     */
    SNOVA49_ESK,

    /**
     * SNova49 SHAKE SSK.
     */
    SNOVA49_SHAKE_SSK,

    /**
     * SNova49 SHAKE ESK.
     */
    SNOVA49_SHAKE_ESK,

    /**
     * SNova56 SSK.
     */
    SNOVA56_SSK,

    /**
     * SNova56 ESK.
     */
    SNOVA56_ESK,

    /**
     * SNova56 SHAKE SSK.
     */
    SNOVA56_SHAKE_SSK,

    /**
     * SNova56 SHAKE ESK.
     */
    SNOVA56_SHAKE_ESK,

    /**
     * SNova60 SSK.
     */
    SNOVA60_SSK,

    /**
     * SNova60 ESK.
     */
    SNOVA60_ESK,

    /**
     * SNova60 SHAKE SSK.
     */
    SNOVA60_SHAKE_SSK,

    /**
     * SNova60 SHAKE ESK.
     */
    SNOVA60_SHAKE_ESK,

    /**
     * SNova66 SSK.
     */
    SNOVA66_SSK,

    /**
     * SNova66 ESK.
     */
    SNOVA66_ESK,

    /**
     * SNova66 SSK.
     */
    SNOVA66_SHAKE_SSK,

    /**
     * SNova66 SHAKE ESK.
     */
    SNOVA66_SHAKE_ESK,

    /**
     * SNova75 SSK.
     */
    SNOVA75_SSK,

    /**
     * SNova75 ESK.
     */
    SNOVA75_ESK,

    /**
     * SNova75 SHAKE SSK.
     */
    SNOVA75_SHAKE_SSK,

    /**
     * SNova75 SHAKE ESK.
     */
    SNOVA75_SHAKE_ESK;

    /**
     * Obtain Mayo Parameters.
     * @return the parameters.
     */
    public SnovaParameters getParameters() {
        switch (this) {
            case SNOVA24A_SSK:       return SnovaParameters.SNOVA_24_5_4_SSK;
            case SNOVA24A_ESK:       return SnovaParameters.SNOVA_24_5_4_ESK;
            case SNOVA24A_SHAKE_SSK: return SnovaParameters.SNOVA_24_5_4_SHAKE_SSK;
            case SNOVA24A_SHAKE_ESK: return SnovaParameters.SNOVA_24_5_4_SHAKE_ESK;
            case SNOVA24B_SSK:       return SnovaParameters.SNOVA_24_5_5_SSK;
            case SNOVA24B_ESK:       return SnovaParameters.SNOVA_24_5_5_ESK;
            case SNOVA24B_SHAKE_SSK: return SnovaParameters.SNOVA_24_5_5_SHAKE_SSK;
            case SNOVA24B_SHAKE_ESK: return SnovaParameters.SNOVA_24_5_5_SHAKE_ESK;
            case SNOVA25_SSK:        return SnovaParameters.SNOVA_25_8_3_SSK;
            case SNOVA25_ESK:        return SnovaParameters.SNOVA_25_8_3_ESK;
            case SNOVA25_SHAKE_SSK:  return SnovaParameters.SNOVA_25_8_3_SHAKE_SSK;
            case SNOVA25_SHAKE_ESK:  return SnovaParameters.SNOVA_25_8_3_SHAKE_ESK;
            case SNOVA29_SSK:        return SnovaParameters.SNOVA_29_6_5_SSK;
            case SNOVA29_ESK:        return SnovaParameters.SNOVA_29_6_5_ESK;
            case SNOVA29_SHAKE_SSK:  return SnovaParameters.SNOVA_29_6_5_SHAKE_SSK;
            case SNOVA29_SHAKE_ESK:  return SnovaParameters.SNOVA_29_6_5_SHAKE_ESK;
            case SNOVA37A_SSK:       return SnovaParameters.SNOVA_37_8_4_SSK;
            case SNOVA37A_ESK:       return SnovaParameters.SNOVA_37_8_4_ESK;
            case SNOVA37A_SHAKE_SSK: return SnovaParameters.SNOVA_37_8_4_SHAKE_SSK;
            case SNOVA37A_SHAKE_ESK: return SnovaParameters.SNOVA_37_8_4_SHAKE_ESK;
            case SNOVA37B_SSK:       return SnovaParameters.SNOVA_37_17_2_SSK;
            case SNOVA37B_ESK:       return SnovaParameters.SNOVA_37_17_2_ESK;
            case SNOVA37B_SHAKE_SSK: return SnovaParameters.SNOVA_37_17_2_SHAKE_SSK;
            case SNOVA37B_SHAKE_ESK: return SnovaParameters.SNOVA_37_17_2_SHAKE_ESK;
            case SNOVA49_SSK:        return SnovaParameters.SNOVA_49_11_3_SSK;
            case SNOVA49_ESK:        return SnovaParameters.SNOVA_49_11_3_ESK;
            case SNOVA49_SHAKE_SSK:  return SnovaParameters.SNOVA_49_11_3_SHAKE_SSK;
            case SNOVA49_SHAKE_ESK:  return SnovaParameters.SNOVA_49_11_3_SHAKE_ESK;
            case SNOVA56_SSK:        return SnovaParameters.SNOVA_56_25_2_SSK;
            case SNOVA56_ESK:        return SnovaParameters.SNOVA_56_25_2_ESK;
            case SNOVA56_SHAKE_SSK:  return SnovaParameters.SNOVA_56_25_2_SHAKE_SSK;
            case SNOVA56_SHAKE_ESK:  return SnovaParameters.SNOVA_56_25_2_SHAKE_ESK;
            case SNOVA60_SSK:        return SnovaParameters.SNOVA_60_10_4_SSK;
            case SNOVA60_ESK:        return SnovaParameters.SNOVA_60_10_4_ESK;
            case SNOVA60_SHAKE_SSK:  return SnovaParameters.SNOVA_60_10_4_SHAKE_SSK;
            case SNOVA60_SHAKE_ESK:  return SnovaParameters.SNOVA_60_10_4_SHAKE_ESK;
            case SNOVA66_SSK:        return SnovaParameters.SNOVA_66_15_3_SSK;
            case SNOVA66_ESK:        return SnovaParameters.SNOVA_66_15_3_ESK;
            case SNOVA66_SHAKE_SSK:  return SnovaParameters.SNOVA_66_15_3_SHAKE_SSK;
            case SNOVA66_SHAKE_ESK:  return SnovaParameters.SNOVA_66_15_3_SHAKE_ESK;
            case SNOVA75_SSK:        return SnovaParameters.SNOVA_75_33_2_SSK;
            case SNOVA75_ESK:        return SnovaParameters.SNOVA_75_33_2_ESK;
            case SNOVA75_SHAKE_SSK:  return SnovaParameters.SNOVA_75_33_2_SHAKE_SSK;
            case SNOVA75_SHAKE_ESK:  return SnovaParameters.SNOVA_75_33_2_SHAKE_ESK;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Mayo ParameterSpec.
     * @return the parameters.
     */
    public SnovaParameterSpec getParameterSpec() {
        switch (this) {
            case SNOVA24A_SSK:       return SnovaParameterSpec.SNOVA_24_5_4_SSK;
            case SNOVA24A_ESK:       return SnovaParameterSpec.SNOVA_24_5_4_ESK;
            case SNOVA24A_SHAKE_SSK: return SnovaParameterSpec.SNOVA_24_5_4_SHAKE_SSK;
            case SNOVA24A_SHAKE_ESK: return SnovaParameterSpec.SNOVA_24_5_4_SHAKE_ESK;
            case SNOVA24B_SSK:       return SnovaParameterSpec.SNOVA_24_5_5_SSK;
            case SNOVA24B_ESK:       return SnovaParameterSpec.SNOVA_24_5_5_ESK;
            case SNOVA24B_SHAKE_SSK: return SnovaParameterSpec.SNOVA_24_5_5_SHAKE_SSK;
            case SNOVA24B_SHAKE_ESK: return SnovaParameterSpec.SNOVA_24_5_5_SHAKE_ESK;
            case SNOVA25_SSK:        return SnovaParameterSpec.SNOVA_25_8_3_SSK;
            case SNOVA25_ESK:        return SnovaParameterSpec.SNOVA_25_8_3_ESK;
            case SNOVA25_SHAKE_SSK:  return SnovaParameterSpec.SNOVA_25_8_3_SHAKE_SSK;
            case SNOVA25_SHAKE_ESK:  return SnovaParameterSpec.SNOVA_25_8_3_SHAKE_ESK;
            case SNOVA29_SSK:        return SnovaParameterSpec.SNOVA_29_6_5_SSK;
            case SNOVA29_ESK:        return SnovaParameterSpec.SNOVA_29_6_5_ESK;
            case SNOVA29_SHAKE_SSK:  return SnovaParameterSpec.SNOVA_29_6_5_SHAKE_SSK;
            case SNOVA29_SHAKE_ESK:  return SnovaParameterSpec.SNOVA_29_6_5_SHAKE_ESK;
            case SNOVA37A_SSK:       return SnovaParameterSpec.SNOVA_37_8_4_SSK;
            case SNOVA37A_ESK:       return SnovaParameterSpec.SNOVA_37_8_4_ESK;
            case SNOVA37A_SHAKE_SSK: return SnovaParameterSpec.SNOVA_37_8_4_SHAKE_SSK;
            case SNOVA37A_SHAKE_ESK: return SnovaParameterSpec.SNOVA_37_8_4_SHAKE_ESK;
            case SNOVA37B_SSK:       return SnovaParameterSpec.SNOVA_37_17_2_SSK;
            case SNOVA37B_ESK:       return SnovaParameterSpec.SNOVA_37_17_2_ESK;
            case SNOVA37B_SHAKE_SSK: return SnovaParameterSpec.SNOVA_37_17_2_SHAKE_SSK;
            case SNOVA37B_SHAKE_ESK: return SnovaParameterSpec.SNOVA_37_17_2_SHAKE_ESK;
            case SNOVA49_SSK:        return SnovaParameterSpec.SNOVA_49_11_3_SSK;
            case SNOVA49_ESK:        return SnovaParameterSpec.SNOVA_49_11_3_ESK;
            case SNOVA49_SHAKE_SSK:  return SnovaParameterSpec.SNOVA_49_11_3_SHAKE_SSK;
            case SNOVA49_SHAKE_ESK:  return SnovaParameterSpec.SNOVA_49_11_3_SHAKE_ESK;
            case SNOVA56_SSK:        return SnovaParameterSpec.SNOVA_56_25_2_SSK;
            case SNOVA56_ESK:        return SnovaParameterSpec.SNOVA_56_25_2_ESK;
            case SNOVA56_SHAKE_SSK:  return SnovaParameterSpec.SNOVA_56_25_2_SHAKE_SSK;
            case SNOVA56_SHAKE_ESK:  return SnovaParameterSpec.SNOVA_56_25_2_SHAKE_ESK;
            case SNOVA60_SSK:        return SnovaParameterSpec.SNOVA_60_10_4_SSK;
            case SNOVA60_ESK:        return SnovaParameterSpec.SNOVA_60_10_4_ESK;
            case SNOVA60_SHAKE_SSK:  return SnovaParameterSpec.SNOVA_60_10_4_SHAKE_SSK;
            case SNOVA60_SHAKE_ESK:  return SnovaParameterSpec.SNOVA_60_10_4_SHAKE_ESK;
            case SNOVA66_SSK:        return SnovaParameterSpec.SNOVA_66_15_3_SSK;
            case SNOVA66_ESK:        return SnovaParameterSpec.SNOVA_66_15_3_ESK;
            case SNOVA66_SHAKE_SSK:  return SnovaParameterSpec.SNOVA_66_15_3_SHAKE_SSK;
            case SNOVA66_SHAKE_ESK:  return SnovaParameterSpec.SNOVA_66_15_3_SHAKE_ESK;
            case SNOVA75_SSK:        return SnovaParameterSpec.SNOVA_75_33_2_SSK;
            case SNOVA75_ESK:        return SnovaParameterSpec.SNOVA_75_33_2_ESK;
            case SNOVA75_SHAKE_SSK:  return SnovaParameterSpec.SNOVA_75_33_2_SHAKE_SSK;
            case SNOVA75_SHAKE_ESK:  return SnovaParameterSpec.SNOVA_75_33_2_SHAKE_ESK;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain MAYO algorithm Identifier.
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (this) {
            case SNOVA24A_SSK:       return BCObjectIdentifiers.snova_24_5_4_ssk;
            case SNOVA24A_ESK:       return BCObjectIdentifiers.snova_24_5_4_esk;
            case SNOVA24A_SHAKE_SSK: return BCObjectIdentifiers.snova_24_5_4_shake_ssk;
            case SNOVA24A_SHAKE_ESK: return BCObjectIdentifiers.snova_24_5_4_shake_esk;
            case SNOVA24B_SSK:       return BCObjectIdentifiers.snova_24_5_5_ssk;
            case SNOVA24B_ESK:       return BCObjectIdentifiers.snova_24_5_5_esk;
            case SNOVA24B_SHAKE_SSK: return BCObjectIdentifiers.snova_24_5_5_shake_ssk;
            case SNOVA24B_SHAKE_ESK: return BCObjectIdentifiers.snova_24_5_5_shake_esk;
            case SNOVA25_SSK:        return BCObjectIdentifiers.snova_25_8_3_ssk;
            case SNOVA25_ESK:        return BCObjectIdentifiers.snova_25_8_3_esk;
            case SNOVA25_SHAKE_SSK:  return BCObjectIdentifiers.snova_25_8_3_shake_ssk;
            case SNOVA25_SHAKE_ESK:  return BCObjectIdentifiers.snova_25_8_3_shake_esk;
            case SNOVA29_SSK:        return BCObjectIdentifiers.snova_29_6_5_ssk;
            case SNOVA29_ESK:        return BCObjectIdentifiers.snova_29_6_5_esk;
            case SNOVA29_SHAKE_SSK:  return BCObjectIdentifiers.snova_29_6_5_shake_ssk;
            case SNOVA29_SHAKE_ESK:  return BCObjectIdentifiers.snova_29_6_5_shake_esk;
            case SNOVA37A_SSK:       return BCObjectIdentifiers.snova_37_8_4_ssk;
            case SNOVA37A_ESK:       return BCObjectIdentifiers.snova_37_8_4_esk;
            case SNOVA37A_SHAKE_SSK: return BCObjectIdentifiers.snova_37_8_4_shake_ssk;
            case SNOVA37A_SHAKE_ESK: return BCObjectIdentifiers.snova_37_8_4_shake_esk;
            case SNOVA37B_SSK:       return BCObjectIdentifiers.snova_37_17_2_ssk;
            case SNOVA37B_ESK:       return BCObjectIdentifiers.snova_37_17_2_esk;
            case SNOVA37B_SHAKE_SSK: return BCObjectIdentifiers.snova_37_17_2_shake_ssk;
            case SNOVA37B_SHAKE_ESK: return BCObjectIdentifiers.snova_37_17_2_shake_esk;
            case SNOVA49_SSK:        return BCObjectIdentifiers.snova_49_11_3_ssk;
            case SNOVA49_ESK:        return BCObjectIdentifiers.snova_49_11_3_esk;
            case SNOVA49_SHAKE_SSK:  return BCObjectIdentifiers.snova_49_11_3_shake_ssk;
            case SNOVA49_SHAKE_ESK:  return BCObjectIdentifiers.snova_49_11_3_shake_esk;
            case SNOVA56_SSK:        return BCObjectIdentifiers.snova_56_25_2_ssk;
            case SNOVA56_ESK:        return BCObjectIdentifiers.snova_56_25_2_esk;
            case SNOVA56_SHAKE_SSK:  return BCObjectIdentifiers.snova_56_25_2_shake_ssk;
            case SNOVA56_SHAKE_ESK:  return BCObjectIdentifiers.snova_56_25_2_shake_esk;
            case SNOVA60_SSK:        return BCObjectIdentifiers.snova_60_10_4_ssk;
            case SNOVA60_ESK:        return BCObjectIdentifiers.snova_60_10_4_esk;
            case SNOVA60_SHAKE_SSK:  return BCObjectIdentifiers.snova_60_10_4_shake_ssk;
            case SNOVA60_SHAKE_ESK:  return BCObjectIdentifiers.snova_60_10_4_shake_esk;
            case SNOVA66_SSK:        return BCObjectIdentifiers.snova_66_15_3_ssk;
            case SNOVA66_ESK:        return BCObjectIdentifiers.snova_66_15_3_esk;
            case SNOVA66_SHAKE_SSK:  return BCObjectIdentifiers.snova_66_15_3_shake_ssk;
            case SNOVA66_SHAKE_ESK:  return BCObjectIdentifiers.snova_66_15_3_shake_esk;
            case SNOVA75_SSK:        return BCObjectIdentifiers.snova_75_33_2_ssk;
            case SNOVA75_ESK:        return BCObjectIdentifiers.snova_75_33_2_esk;
            case SNOVA75_SHAKE_SSK:  return BCObjectIdentifiers.snova_75_33_2_shake_ssk;
            case SNOVA75_SHAKE_ESK:  return BCObjectIdentifiers.snova_75_33_2_shake_esk;
            default: throw new IllegalArgumentException();
        }
    }
}
