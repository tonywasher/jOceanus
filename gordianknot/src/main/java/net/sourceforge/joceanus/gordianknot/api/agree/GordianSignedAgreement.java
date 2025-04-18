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
package net.sourceforge.joceanus.gordianknot.api.agree;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;

/**
 * Signed TwoShot Agreement.
 */
public interface GordianSignedAgreement
        extends GordianAgreement {
    /**
     * Create the clientHello message.
     * @return the clientHello message
     * @throws GordianException on error
     */
    byte[] createClientHello() throws GordianException;

    /**
     * Accept the clientHello.
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @return the serverHello message
     * @throws GordianException on error
     */
    byte[] acceptClientHello(GordianKeyPair pServer,
                             byte[] pClientHello)  throws GordianException;

    /**
     * Accept the serverHello.
     * @param pServer the server keyPair
     * @param pServerHello the serverHello message
     * @throws GordianException on error
     */
    void acceptServerHello(GordianKeyPair pServer,
                           byte[] pServerHello) throws GordianException;
}
