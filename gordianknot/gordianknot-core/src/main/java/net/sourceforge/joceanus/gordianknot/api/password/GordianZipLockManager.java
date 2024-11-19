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
package net.sourceforge.joceanus.gordianknot.api.password;

import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * ZipLock Manager.
 */
public interface GordianZipLockManager {
    /**
     * Resolve the zipLock.
     * @param pZipLock the hash bytes to resolve
     * @param pSource the description of the secured resource
     * @throws OceanusException on error
     */
    void resolveZipLock(GordianZipLock pZipLock,
                        String pSource) throws OceanusException;
}
