/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.sign;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianConsumer;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot base for signature.
 */
public interface GordianSignature
    extends GordianConsumer {
    /**
     * Obtain the signatureSpec.
     * @return the Spec
     */
    GordianSignatureSpec getSignatureSpec();

    /**
     * Initialise for signature.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    void initForSigning(GordianKeyPair pKeyPair) throws OceanusException;

    /**
     * Initialise for verify.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    void initForVerify(GordianKeyPair pKeyPair) throws OceanusException;

    /**
     * Complete the signature operation and return the signature bytes.
     * @return the signature
     * @throws OceanusException on error
     */
    byte[] sign() throws OceanusException;

    /**
     * Verify the signature against the supplied signature bytes.
     * @param pSignature the supplied signature
     * @return the signature
     * @throws OceanusException on error
     */
    boolean verify(byte[] pSignature) throws OceanusException;
}
