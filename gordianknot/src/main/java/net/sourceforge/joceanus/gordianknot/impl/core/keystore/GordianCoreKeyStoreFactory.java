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
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreGateway;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreManager;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * KeyStore Factory implementation.
 */
public class GordianCoreKeyStoreFactory
        implements GordianKeyStoreFactory  {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public GordianCoreKeyStoreFactory(final GordianCoreKeyPairFactory pFactory) {
        theFactory = pFactory.getFactory();
    }

    @Override
    public GordianKeyStore createKeyStore(final GordianPasswordLockSpec pSpec) {
        return new GordianCoreKeyStore(theFactory, pSpec);
    }

    @Override
    public GordianKeyStore loadKeyStore(final File pFile,
                                        final char[] pPassword) throws GordianException {
        try {
            return loadKeyStore(new FileInputStream(pFile), pPassword);
        } catch (IOException e) {
            throw new GordianIOException("Failed to load keyStore", e);
        }
    }

    @Override
    public GordianKeyStore loadKeyStore(final InputStream pInputStream,
                                        final char[] pPassword) throws GordianException {
        /* Access the ZipFile */
        final GordianZipFactory myZipFactory = theFactory.getZipFactory();
        final GordianZipReadFile myZipFile = myZipFactory.openZipFile(pInputStream);

        /* Reject if there is no hash */
        final GordianZipLock myLock = myZipFile.getLock();
        if (myLock == null) {
            throw new GordianDataException("Unsecured keyStore");
        }

        /* Unlock the file */
        myLock.unlock(pPassword);

        /* Locate the entry */
        final GordianZipFileEntry myEntry = myZipFile.getContents().findFileEntry(GordianCoreKeyStore.ZIPENTRY);
        if (myEntry == null) {
            throw new GordianDataException("Invalid keyStore");
        }

        /* Load the XML document */
        final Document myDoc = myZipFile.readXMLDocument(myEntry);
        final GordianKeyStoreDocument myKeyStoreDoc = new GordianKeyStoreDocument(theFactory, myDoc);
        return myKeyStoreDoc.getKeyStore();
    }

    @Override
    public GordianKeyStoreManager createKeyStoreManager(final GordianKeyStore pKeyStore) {
        return new GordianCoreKeyStoreManager(theFactory, (GordianCoreKeyStore) pKeyStore);
    }

    @Override
    public GordianKeyStoreGateway createKeyStoreGateway(final GordianKeyStoreManager pKeyStoreMgr) {
        return new GordianCoreKeyStoreGateway(theFactory, (GordianCoreKeyStoreManager) pKeyStoreMgr);
    }
}
