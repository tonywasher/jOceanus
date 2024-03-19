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
package net.sourceforge.joceanus.jgordianknot.api.password;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * FactoryLock Manager.
 */
public interface GordianFactoryLockManager {
    /**
     * Create a new factoryLock.
     * @param pFactory the factory to lock
     * @param pSource the description of the secured resource
     * @return the factoryLock
     * @throws OceanusException on error
     */
    GordianFactoryLock newFactoryLock(GordianFactory pFactory,
                                      String pSource) throws OceanusException;

    /**
     * Resolve the factoryLock bytes.
     * @param pLockBytes the lock bytes to resolve
     * @param pSource the description of the secured resource
     * @return the factoryLock
     * @throws OceanusException on error
     */
    GordianFactoryLock resolveFactoryLock(byte[] pLockBytes,
                                          String pSource) throws OceanusException;

    /**
     * obtain new locked factory (same password).
     * @param pReference the reference to clone password from
     * @return the similar factoryLock
     * @throws OceanusException on error
     */
    GordianFactoryLock similarFactoryLock(Object pReference) throws OceanusException;
}
