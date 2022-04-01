/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianLock;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Zip Factory.
 */
public class GordianCoreZipFactory
    implements GordianZipFactory {
    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public GordianCoreZipFactory(final GordianFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * The Create ZipFile Error text.
     */
    private static final String ERROR_CREATE = "Failed to create ZipFile";

    @Override
    public GordianLock createPasswordLock(final GordianKeySetHashSpec pKeySetHashSpec,
                                          final char[] pPassword) throws OceanusException {
        return new GordianCoreLock(theFactory, pKeySetHashSpec, pPassword);
    }

    @Override
    public GordianLock createKeyLock(final char[] pPassword) throws OceanusException {
        return new GordianCoreLock(theFactory, pPassword);
    }

    @Override
    public GordianLock createKeyPairLock(final GordianKeyPair pKeyPair,
                                         final GordianKeySetHashSpec pKeySetHashSpec,
                                         final char[] pPassword) throws OceanusException {
        return new GordianCoreLock(theFactory, pKeyPair, pKeySetHashSpec, pPassword);
    }

    @Override
    public GordianZipWriteFile createZipFile(final GordianLock pLock,
                                             final File pFile) throws OceanusException {
        try {
            return createZipFile(pLock, new FileOutputStream(pFile));
        } catch (IOException e) {
            throw new GordianIOException(ERROR_CREATE, e);
        }
    }

    @Override
    public GordianZipWriteFile createZipFile(final GordianLock pLock,
                                             final OutputStream pOutputStream) throws OceanusException {
        return new GordianCoreZipWriteFile((GordianCoreLock) pLock, pOutputStream);
    }

    @Override
    public GordianZipWriteFile createZipFile(final File pFile) throws OceanusException {
        try {
            return createZipFile(new FileOutputStream(pFile));
        } catch (IOException e) {
            throw new GordianIOException(ERROR_CREATE, e);
        }
    }

    @Override
    public GordianZipWriteFile createZipFile(final OutputStream pOutputStream) {
        return new GordianCoreZipWriteFile(pOutputStream);
    }

    @Override
    public GordianZipReadFile openZipFile(final File pFile) throws OceanusException {
        try {
            return openZipFile(new FileInputStream(pFile));
        } catch (IOException e) {
            throw new GordianIOException("Failed to access ZipFile", e);
        }
    }

    @Override
    public GordianZipReadFile openZipFile(final InputStream pInputStream) throws OceanusException {
        return new GordianCoreZipReadFile(theFactory, pInputStream);
    }
}
