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
package net.sourceforge.joceanus.gordianknot.api.agree;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;

/**
 * Handshake Two/ThreeShot Agreement.
 */
public interface GordianHandshakeAgreement
        extends GordianAgreement {
    /**
     * create the clientHello.
     * @param pClient the client keyPair
     * @return the clientHello message
     * @throws GordianException on error
     */
    byte[] createClientHello(GordianKeyPair pClient) throws GordianException;

    /**
     * Accept the clientHello.
     * @param pClient the client keyPair
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @return the serverHello message
     * @throws GordianException on error
     */
    byte[] acceptClientHello(GordianKeyPair pClient,
                             GordianKeyPair pServer,
                             byte[] pClientHello) throws GordianException;

    /**
     * Accept the serverHello.
     * @param pServer the server keyPair
     * @param pServerHello the serverHello message
     * @return the clientConfirm (or null if no confirmation)
     * @throws GordianException on error
     */
    byte[] acceptServerHello(GordianKeyPair pServer,
                             byte[] pServerHello) throws GordianException;

    /**
     * Accept the clientConfirm.
     * @param pClientConfirm the clientConfirm message
     * @throws GordianException on error
     */
    void acceptClientConfirm(byte[] pClientConfirm) throws GordianException;
}
