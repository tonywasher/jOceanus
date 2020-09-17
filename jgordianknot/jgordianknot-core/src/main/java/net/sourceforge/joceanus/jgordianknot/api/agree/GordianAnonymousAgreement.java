/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
 * Anonymous OneShot Agreement.
 * @param <A> the agreement specification
 * @param <K> the keyPair Type
 */
public interface GordianAnonymousAgreement<A, K>
        extends GordianAgreement<A> {
    /**
     * Create the clientHello message.
     * @param pServer the server keyPair
     * @return the clientHello message
     * @throws OceanusException on error
     */
    byte[] createClientHello(K pServer) throws OceanusException;

    /**
     * Accept the clientHello.
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @throws OceanusException on error
     */
    void acceptClientHello(K pServer,
                           byte[] pClientHello)  throws OceanusException;
}
