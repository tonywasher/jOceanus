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
 * Encapsulation Agreement.
 */
public interface GordianEncapsulationAgreement
        extends GordianAgreement {
    /**
     * Initiate the agreement.
     * @param pTarget the target keyPair
     * @return the message
     * @throws OceanusException on error
     */
    byte[] initiateAgreement(GordianKeyPair pTarget) throws OceanusException;


    /**
     * Accept the agreement.
     * @param pTarget the target keyPair
     * @param pMessage the incoming message
     * @throws OceanusException on error
     */
    void acceptAgreement(GordianKeyPair pTarget,
                         byte[] pMessage)  throws OceanusException;
}
