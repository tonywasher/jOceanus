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
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreAsymFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
    public GordianCoreKeyStoreFactory(final GordianCoreAsymFactory pFactory) {
        theFactory = pFactory.getFactory();
    }

    /**
     * Create a new empty KeyStore.
     * @return the keyStore
     */
    public GordianKeyStore createKeyStore() {
        return new GordianCoreKeyStore(theFactory);
    }

    @Override
    public GordianKeyStore loadKeyStore(final File pFile,
                                        final char[] pPassword) throws OceanusException {
        try {
            return loadKeyStore(new FileInputStream(pFile), pPassword);
        } catch (IOException e) {
            throw new GordianIOException("Failed to load keyStore", e);
        }
    }

    @Override
    public GordianKeyStore loadKeyStore(final InputStream pInputStream,
                                        final char[] pPassword) throws OceanusException {
        /* Access the ZipFile */
        final GordianZipFactory myZipFactory = theFactory.getZipFactory();
        final GordianZipReadFile myZipFile = myZipFactory.openZipFile(pInputStream);

        /* Reject if there is no hash */
        final GordianKeySetFactory myKeySetFactory = theFactory.getKeySetFactory();
        final byte[] myHashBytes = myZipFile.getHashBytes();
        if (myHashBytes == null) {
            throw new GordianDataException("Unsecured keyStore");
        }

        /* Derive the keySetHash */
        final GordianKeySetHash myHash = myKeySetFactory.deriveKeySetHash(myHashBytes, pPassword);
        myZipFile.setKeySetHash(myHash);

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
}