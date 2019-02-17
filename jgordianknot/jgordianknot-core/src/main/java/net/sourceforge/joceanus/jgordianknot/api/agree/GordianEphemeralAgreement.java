/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.agree;

import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Ephemeral Agreement.
 */
public interface GordianEphemeralAgreement
        extends GordianAgreement {
    /**
     * Initiate the agreement.
     * @param pInitiator the initiating keyPair
     * @return the composite message
     * @throws OceanusException on error
     */
    byte[] initiateAgreement(GordianKeyPair pInitiator) throws OceanusException;

    /**
     * Parse the incoming message.
     * @param pSource the source keyPair
     * @param pResponder the responding keyPair
     * @param pMessage the incoming message
     * @return the ephemeral keySpec
     * @throws OceanusException on error
     */
    byte[] acceptAgreement(GordianKeyPair pSource,
                           GordianKeyPair pResponder,
                           byte[] pMessage) throws OceanusException;

    /**
     * Confirm the agreement.
     * @param pResponder the responding keyPair
     * @param pKeySpec the target ephemeral keyPair
     * @throws OceanusException on error
     */
    void confirmAgreement(GordianKeyPair pResponder,
                          byte[] pKeySpec) throws OceanusException;
}
