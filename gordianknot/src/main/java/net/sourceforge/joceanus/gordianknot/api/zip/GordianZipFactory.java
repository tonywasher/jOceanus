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
package net.sourceforge.joceanus.gordianknot.api.zip;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * GordianKnot Zip Factory API.
 */
public interface GordianZipFactory {
    /**
     * Create a keySetLock.
     * @param pPassword the password
     * @return the zipLock
     * @throws GordianException on error
     */
    default GordianZipLock keySetZipLock(final char[] pPassword) throws GordianException {
        return keySetZipLock(new GordianPasswordLockSpec(), pPassword);
    }

    /**
     * Create a keySetLock.
     * @param pLockSpec the lockSpec
     * @param pPassword the password
     * @return the zipLock
     * @throws GordianException on error
     */
    GordianZipLock keySetZipLock(GordianPasswordLockSpec pLockSpec,
                                 char[] pPassword) throws GordianException;

    /**
     * Create a factoryLock.
     * @param pPassword the password
     * @return the zipLock
     * @throws GordianException on error
     */
    default GordianZipLock factoryZipLock(final char[] pPassword) throws GordianException {
        return factoryZipLock(new GordianPasswordLockSpec(), pPassword);
    }

    /**
     * Create a factoryLock.
     * @param pLockSpec the lockSpec
     * @param pPassword the password
     * @return the zipLock
     * @throws GordianException on error
     */
    GordianZipLock factoryZipLock(GordianPasswordLockSpec pLockSpec,
                                  char[] pPassword) throws GordianException;

    /**
     * Create a keyPairZipLock.
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @return the zipLock
     * @throws GordianException on error
     */
    default GordianZipLock keyPairZipLock(final GordianKeyPair pKeyPair,
                                          final char[] pPassword) throws GordianException {
        return keyPairZipLock(new GordianPasswordLockSpec(), pKeyPair, pPassword);
    }

    /**
     * Create a keyPairZipLock.
     * @param pLockSpec the lockSpec
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @return the zipLock
     * @throws GordianException on error
     */
    GordianZipLock keyPairZipLock(GordianPasswordLockSpec pLockSpec,
                                  GordianKeyPair pKeyPair,
                                  char[] pPassword) throws GordianException;

    /**
     * Create a zipLock.
     * @param pLock the keyPairLock
     * @return the zipLock
     * @throws GordianException on error
     */
    GordianZipLock zipLock(GordianLock<?> pLock) throws GordianException;

    /**
     * Create a secure zipFile.
     * @param pZipLock the zipLock to use
     * @param pFile the file details for the new zip file
     * @return the zipFile
     * @throws GordianException on error
     */
    GordianZipWriteFile createZipFile(GordianZipLock pZipLock,
                                      File pFile) throws GordianException;

    /**
     * Create a secure zipFile.
     * @param pZipLock the zipLock to use
     * @param pOutputStream the output stream to write to
     * @return the zipFile
     * @throws GordianException on error
     */
    GordianZipWriteFile createZipFile(GordianZipLock pZipLock,
                                      OutputStream pOutputStream) throws GordianException;

    /**
     * Create a standard zipFile with no security.
     * @param pFile the file details for the new zip file
     * @return the zipFile
     * @throws GordianException on error
     */
    GordianZipWriteFile createZipFile(File pFile) throws GordianException;

    /**
     * Create a standard zipFile with no security.
     * @param pOutputStream the output stream to write to
     * @return the zipFile
     */
    GordianZipWriteFile createZipFile(OutputStream pOutputStream);

    /**
     * Open an existing zipFile.
     * @param pFile the file to read
     * @return the zipFile
     * @throws GordianException on error
     */
    GordianZipReadFile openZipFile(File pFile) throws GordianException;

    /**
     * Open an existing zipFile.
     * @param pInputStream the input stream to read from
     * @return the zipFile
     * @throws GordianException on error
     */
    GordianZipReadFile openZipFile(InputStream pInputStream) throws GordianException;
}
