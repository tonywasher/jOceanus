/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/GordianBadCredentialsException.java $
 * $Revision: 648 $
 * $Author: Tony $
 * $Date: 2015-11-21 15:20:03 +0000 (Sat, 21 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot interface for signature signer.
 */
public interface GordianSigner
        extends GordianConsumer {
    /**
     * Complete the signature operation and return the signature bytes.
     * @return the signature
     * @throws OceanusException on error
     */
    byte[] sign() throws OceanusException;
}
