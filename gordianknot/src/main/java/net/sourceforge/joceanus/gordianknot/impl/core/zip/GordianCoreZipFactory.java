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
package net.sourceforge.joceanus.gordianknot.impl.core.zip;

import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryLock;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianLockFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeyPairLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    public GordianZipLock keySetZipLock(final GordianPasswordLockSpec pLockSpec,
                                        final char[] pPassword) throws OceanusException {
        final GordianLockFactory myLockFactory = theFactory.getLockFactory();
        final GordianKeySetLock myLock = myLockFactory.newKeySetLock(pLockSpec, pPassword);
        return zipLock(myLock);
    }

    @Override
    public GordianZipLock factoryZipLock(final GordianPasswordLockSpec pLockSpec,
                                         final char[] pPassword) throws OceanusException {
        final GordianLockFactory myLockFactory = theFactory.getLockFactory();
        final GordianFactoryLock myLock = myLockFactory.newFactoryLock(pLockSpec, GordianFactoryType.BC, pPassword);
        return zipLock(myLock);
    }

    @Override
    public GordianZipLock keyPairZipLock(final GordianPasswordLockSpec pLockSpec,
                                         final GordianKeyPair pKeyPair,
                                         final char[] pPassword) throws OceanusException {
        final GordianLockFactory myLockFactory = theFactory.getLockFactory();
        final GordianKeyPairLock myLock = myLockFactory.newKeyPairLock(pLockSpec, pKeyPair, pPassword);
        return zipLock(myLock);
    }

    @Override
    public GordianZipLock zipLock(final GordianLock<?> pLock) throws OceanusException {
        if (pLock instanceof GordianKeySetLock) {
            return new GordianCoreZipLock(theFactory, (GordianKeySetLock) pLock);
        }
        if (pLock instanceof GordianFactoryLock) {
            return new GordianCoreZipLock(theFactory, (GordianFactoryLock) pLock);
        }
        if (pLock instanceof GordianKeyPairLock) {
            return new GordianCoreZipLock(theFactory, (GordianKeyPairLock) pLock);
        }
        throw new GordianLogicException("Invalid Lock type");
    }

    @Override
    public GordianZipWriteFile createZipFile(final GordianZipLock pLock,
                                             final File pFile) throws OceanusException {
        try {
            return createZipFile(pLock, new FileOutputStream(pFile));
        } catch (IOException e) {
            throw new GordianIOException(ERROR_CREATE, e);
        }
    }

    @Override
    public GordianZipWriteFile createZipFile(final GordianZipLock pLock,
                                             final OutputStream pOutputStream) throws OceanusException {
        return new GordianCoreZipWriteFile((GordianCoreZipLock) pLock, pOutputStream);
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
