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
package net.sourceforge.joceanus.gordianknot.impl.core.keystore;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreManager;
import org.bouncycastle.asn1.x500.X500Name;

import java.util.List;

/**
 * keyStoreManager base.
 */
public interface GordianBaseKeyStoreManager
    extends GordianKeyStoreManager {
    /**
     * Sign keyPair.
     * @param pKeyPair the keyPair
     * @param pSubject the name of the entity
     * @param pUsage   the key usage
     * @param pSigner the signer
     * @return the certificate chain
     * @throws GordianException on error
     */
    List<GordianCertificate> signKeyPair(GordianKeyPair pKeyPair,
                                         X500Name pSubject,
                                         GordianKeyPairUsage pUsage,
                                         GordianKeyStorePair pSigner) throws GordianException;
}
