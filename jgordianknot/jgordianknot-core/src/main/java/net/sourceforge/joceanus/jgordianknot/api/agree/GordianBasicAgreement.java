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
 * Basic Agreement.
 */
public interface GordianBasicAgreement
        extends GordianAgreement {
    /**
     * Create the clientHello message.
     * @param pClient the client keyPair
     * @param pServer the server keyPair
     * @return the clientHello message
     * @throws OceanusException on error
     */
    byte[] createClientHello(GordianKeyPair pClient,
                             GordianKeyPair pServer) throws OceanusException;

    /**
     * Accept the clientHello.
     * @param pClient the client keyPair
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @throws OceanusException on error
     */
    void acceptClientHello(GordianKeyPair pClient,
                           GordianKeyPair pServer,
                           byte[] pClientHello)  throws OceanusException;
}
