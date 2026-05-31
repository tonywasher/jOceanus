/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/**
 * EncodedParser interface.
 */
public interface GordianEncodedParser {
    /**
     * The parse error.
     */
    String ERROR_PARSE = "Failed to parse Key";

    /**
     * Obtain KeySpec from PrivateKeyInfo.
     *
     * @param pInfo keySpec
     * @return the keySpec
     * @throws GordianException on error
     */
    GordianKeyPairSpec determineKeyPairSpec(SubjectPublicKeyInfo pInfo) throws GordianException;

    /**
     * Obtain KeySpec from SubjectPublicKeyInfo.
     *
     * @param pInfo keySpec
     * @return the keySpec
     * @throws GordianException on error
     */
    GordianKeyPairSpec determineKeyPairSpec(PrivateKeyInfo pInfo) throws GordianException;
}
