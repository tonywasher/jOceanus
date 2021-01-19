/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Handshake Two/ThreeShot Agreement.
 * @param <A> the agreement specification
 * @param <K> the keyPair Type
 */
public interface GordianHandshakeAgreement<A, K>
        extends GordianAgreement<A> {
    /**
     * create the clientHello.
     * @param pClient the client keyPair
     * @return the clientHello message
     * @throws OceanusException on error
     */
    byte[] createClientHello(K pClient) throws OceanusException;

    /**
     * Accept the clientHello.
     * @param pClient the client keyPair
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @return the serverHello message
     * @throws OceanusException on error
     */
    byte[] acceptClientHello(K pClient,
                             K pServer,
                             byte[] pClientHello) throws OceanusException;

    /**
     * Accept the serverHello.
     * @param pServer the server keyPair
     * @param pServerHello the serverHello message
     * @return the clientConfirm (or null if no confirmation)
     * @throws OceanusException on error
     */
    byte[] acceptServerHello(K pServer,
                             byte[] pServerHello) throws OceanusException;

    /**
     * Accept the clientConfirm.
     * @param pClientConfirm the clientConfirm message
     * @throws OceanusException on error
     */
    void acceptClientConfirm(byte[] pClientConfirm) throws OceanusException;
}