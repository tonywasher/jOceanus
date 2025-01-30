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
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;

/**
 * NTRU KeySpec.
 */
public enum GordianNTRUSpec {
    /**
     * HPS 509 2048.
     */
    HPS509,

    /**
     * HPS 677 2048.
     */
    HPS677,

    /**
     * HPS 821 4096.
     */
    HPS821,

    /**
     * HPS 1229 4096.
     */
    HPS1229,

    /**
     * HRSS 701.
     */
    HRSS701,

    /**
     * HRSS 1373.
     */
    HRSS1373;

    /**
     * Obtain NTRU Parameters.
     * @return the parameters.
     */
    public NTRUParameters getParameters() {
        switch (this) {
            case HPS509:  return NTRUParameters.ntruhps2048509;
            case HPS677:  return NTRUParameters.ntruhps2048677;
            case HPS821:  return NTRUParameters.ntruhps4096821;
            case HPS1229:  return NTRUParameters.ntruhps40961229;
            case HRSS701: return NTRUParameters.ntruhrss701;
            case HRSS1373: return NTRUParameters.ntruhrss1373;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain NTRU ParameterSpec.
     * @return the parameters.
     */
    public NTRUParameterSpec getParameterSpec() {
        switch (this) {
            case HPS509:  return NTRUParameterSpec.ntruhps2048509;
            case HPS677:  return NTRUParameterSpec.ntruhps2048677;
            case HPS821:  return NTRUParameterSpec.ntruhps4096821;
            case HPS1229:  return NTRUParameterSpec.ntruhps40961229;
            case HRSS701: return NTRUParameterSpec.ntruhrss701;
            case HRSS1373: return NTRUParameterSpec.ntruhrss1373;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain NTRU algorithm Identifier.
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (this) {
            case HPS509:     return BCObjectIdentifiers.ntruhps2048509;
            case HPS677:     return BCObjectIdentifiers.ntruhps2048677;
            case HPS821:     return BCObjectIdentifiers.ntruhps4096821;
            case HPS1229:    return BCObjectIdentifiers.ntruhps40961229;
            case HRSS701:    return BCObjectIdentifiers.ntruhrss701;
            case HRSS1373:   return BCObjectIdentifiers.ntruhrss1373;
            default: throw new IllegalArgumentException();
        }
    }
}
