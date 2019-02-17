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
package net.sourceforge.joceanus.jgordianknot.impl.core.zip;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Zip Factory.
 */
public class GordianCoreZipFactory
    implements GordianZipFactory {
    @Override
    public GordianZipWriteFile createZipFile(final GordianKeySetHash pHash,
                                             final File pFile) throws OceanusException {
        return new GordianCoreZipWriteFile(pHash, pFile);
    }

    @Override
    public GordianZipWriteFile createZipFile(final File pFile) throws OceanusException {
        return new GordianCoreZipWriteFile(pFile);
    }

    @Override
    public GordianZipReadFile openZipFile(final File pFile) throws OceanusException {
        return new GordianCoreZipReadFile(pFile);
    }
}

