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
package net.sourceforge.joceanus.jgordianknot.api.zip;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Zip Factory API.
 */
public interface GordianZipFactory {
    /**
     * Create a passwordLock.
     * @param pPassword the password
     * @return the Lock
     * @throws OceanusException on error
     */
    default GordianLock createPasswordLock(final char[] pPassword) throws OceanusException {
        return createPasswordLock(new GordianKeySetHashSpec(), pPassword);
    }

    /**
     * Create a passwordLock.
     * @param pKeySetHashSpec the KeySetHashSpec
     * @param pPassword the password
     * @return the zipLock
     * @throws OceanusException on error
     */
    GordianLock createPasswordLock(GordianKeySetHashSpec pKeySetHashSpec,
                                   char[] pPassword) throws OceanusException;

    /**
     * Create a keyLock.
     * @param pPassword the password
     * @return the Lock
     * @throws OceanusException on error
     */
    GordianLock createKeyLock(char[] pPassword) throws OceanusException;

    /**
     * Create a keyPairLock.
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @return the Lock
     * @throws OceanusException on error
     */
    default GordianLock createKeyPairLock(final GordianKeyPair pKeyPair,
                                          final char[] pPassword) throws OceanusException {
        return createKeyPairLock(pKeyPair, new GordianKeySetHashSpec(), pPassword);
    }

    /**
     * Create a keyPairLock.
     * @param pKeyPair the keyPair
     * @param pKeySetHashSpec the KeySetHashSpec
     * @param pPassword the password
     * @return the zipLock
     * @throws OceanusException on error
     */
    GordianLock createKeyPairLock(GordianKeyPair pKeyPair,
                                  GordianKeySetHashSpec pKeySetHashSpec,
                                  char[] pPassword) throws OceanusException;

    /**
     * Create a secure zipFile.
     * @param pZipLock the zipLock to use
     * @param pFile the file details for the new zip file
     * @return the zipFile
     * @throws OceanusException on error
     */
    GordianZipWriteFile createZipFile(GordianLock pZipLock,
                                      File pFile) throws OceanusException;

    /**
     * Create a secure zipFile.
     * @param pZipLock the zipLock to use
     * @param pOutputStream the output stream to write to
     * @return the zipFile
     * @throws OceanusException on error
     */
    GordianZipWriteFile createZipFile(GordianLock pZipLock,
                                      OutputStream pOutputStream) throws OceanusException;

    /**
     * Create a standard zipFile with no security.
     * @param pFile the file details for the new zip file
     * @return the zipFile
     * @throws OceanusException on error
     */
    GordianZipWriteFile createZipFile(File pFile) throws OceanusException;

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
     * @throws OceanusException on error
     */
    GordianZipReadFile openZipFile(File pFile) throws OceanusException;

    /**
     * Open an existing zipFile.
     * @param pInputStream the input stream to read from
     * @return the zipFile
     * @throws OceanusException on error
     */
    GordianZipReadFile openZipFile(InputStream pInputStream) throws OceanusException;
}
