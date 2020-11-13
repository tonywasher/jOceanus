/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreGateway;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianLock;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianKeyStorePairEntry;
import net.sourceforge.joceanus.jgordianknot.impl.core.zip.GordianCoreLock;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * keyStoreGateway implementation.
 */
public class GordianCoreKeyStoreGateway
        implements GordianKeyStoreGateway {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The keyStoreMgr.
     */
    private final GordianCoreKeyStoreManager theKeyStoreMgr;

    /**
     * The keyStore.
     */
    private final GordianCoreKeyStore theKeyStore;

    /**
     * The encryption target certificate.
     */
    private GordianCoreCertificate<?, ?> theTarget;

    /**
     * The keyPairCertifier.
     */
    private GordianKeyStorePair theKeyPairCertifier;

    /**
     * The keyPairSetCertifier.
     */
    private GordianKeyStorePairSet theKeyPairSetCertifier;

    /**
     * The password callback.
     */
    private Function<String, char[]> thePasswordResolver;

    /**
     * The lock callback.
     */
    private GordianLockResolver theLockResolver;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pKeyStoreMgr the keyStoreMgr
     */
    GordianCoreKeyStoreGateway(final GordianCoreFactory pFactory,
                               final GordianCoreKeyStoreManager pKeyStoreMgr) {
        theFactory = pFactory;
        theKeyStoreMgr = pKeyStoreMgr;
        theKeyStore = theKeyStoreMgr.getKeyStore();
    }

    @Override
    public GordianCoreKeyStore getKeyStore() {
        return theKeyStore;
    }

    @Override
    public GordianCoreKeyStoreManager getKeyStoreManager() {
        return theKeyStoreMgr;
    }

    @Override
    public void exportEntry(final String pAlias,
                            final File pFile,
                            final char[] pPassword,
                            final GordianLock pLock) throws OceanusException {
        try (FileOutputStream myStream = new FileOutputStream(pFile)) {
            exportEntry(pAlias, myStream, pPassword, pLock);
        } catch (IOException e) {
            throw new GordianIOException("Failed to write to file", e);
        }
    }

    @Override
    public void exportEntry(final String pAlias,
                            final OutputStream pStream,
                            final char[] pPassword,
                            final GordianLock pLock) throws OceanusException {
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, pPassword);
        final GordianPEMCoder myCoder = new GordianPEMCoder(theKeyStore);
        myCoder.exportKeyStoreEntry(myEntry, pStream, (GordianCoreLock) pLock);
    }

    @Override
    public void setEncryptionTarget(final String pAlias) throws OceanusException {
        final List<GordianKeyPairCertificate> myKeyPairChain = theKeyStore.getKeyPairCertificateChain(pAlias);
        if (myKeyPairChain != null) {
            theTarget = (GordianCoreKeyPairCertificate) myKeyPairChain.get(0);
            return;
        }
        final List<GordianKeyPairSetCertificate> myKeyPairSetChain = theKeyStore.getKeyPairSetCertificateChain(pAlias);
        if (myKeyPairSetChain != null) {
            theTarget = (GordianCoreKeyPairSetCertificate) myKeyPairSetChain.get(0);
            return;
        }
        throw new GordianDataException("Encryption target not found");
    }

    @Override
    public void createCertificateRequest(final String pAlias,
                                         final File pFile,
                                         final char[] pPassword) throws OceanusException {
        try (FileOutputStream myStream = new FileOutputStream(pFile)) {
            createCertificateRequest(pAlias, myStream, pPassword);
        } catch (IOException e) {
            throw new GordianIOException("Failed to create request", e);
        }
    }

    @Override
    public void createCertificateRequest(final String pAlias,
                                         final OutputStream pStream,
                                         final char[] pPassword) throws OceanusException {
        /* Access the requested entry */
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, pPassword);

        /* If it is a keyPair(Set) */
        if (myEntry instanceof GordianKeyStorePairEntry) {
            /* Create the certificate request and write to output stream */
            final GordianCRMBuilder myBuilder = new GordianCRMBuilder(theFactory, theTarget);
            final GordianPEMObject myCertReq = myBuilder.createCertificateRequest((GordianKeyStorePairEntry<?, ?>) myEntry);
            final GordianPEMParser myParser = new GordianPEMParser();
            myParser.writePEMFile(pStream, Collections.singletonList(myCertReq));

            /* else reject request */
        } else {
            throw new GordianDataException("Alias not found");
        }
    }

    @Override
    public void setKeyPairCertifier(final String pAlias,
                                    final char[] pPassword) throws OceanusException {
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, pPassword);
        if (myEntry instanceof GordianKeyStorePair) {
            final GordianKeyStorePair myPair = (GordianKeyStorePair) myEntry;
            final GordianKeyPairCertificate myCert = myPair.getCertificateChain().get(0);
            if (myCert.getUsage().hasUse(GordianKeyPairUse.CERTIFICATE)) {
                theKeyPairCertifier = myPair;
                return;
            }
        }
        throw new GordianDataException("Invalid keyPairCertifier");
    }

    @Override
    public void setKeyPairSetCertifier(final String pAlias,
                                       final char[] pPassword) throws OceanusException {
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, pPassword);
        if (myEntry instanceof GordianKeyStorePairSet) {
            final GordianKeyStorePairSet myPairSet = (GordianKeyStorePairSet) myEntry;
            final GordianKeyPairSetCertificate myCert = myPairSet.getCertificateChain().get(0);
            if (myCert.getUsage().hasUse(GordianKeyPairUse.CERTIFICATE)) {
                theKeyPairSetCertifier = myPairSet;
                return;
            }
        }
        throw new GordianDataException("Invalid keyPairSetCertifier");
    }

    @Override
    public void setPasswordResolver(final Function<String, char[]> pResolver) {
        thePasswordResolver = pResolver;
    }

    @Override
    public void setLockResolver(final GordianLockResolver pResolver) {
        theLockResolver = pResolver;
    }

    @Override
    public void processCertificateRequest(final File pInFile,
                                          final File pOutFile) throws OceanusException {
        try (FileInputStream myInStream = new FileInputStream(pInFile);
             FileOutputStream myOutStream = new FileOutputStream(pOutFile)) {
            processCertificateRequest(myInStream, myOutStream);
        } catch (IOException e) {
            throw new GordianIOException("Failed to process request", e);
        }
    }

    @Override
    public void processCertificateRequest(final InputStream pInStream,
                                          final OutputStream pOutStream) throws OceanusException {
        final GordianPEMParser myParser = new GordianPEMParser();
        final GordianPEMObject myObject = myParser.parsePEMFile(pInStream).get(0);
        switch (myObject.getObjectType()) {
            case KEYPAIRCERTREQ:
                GordianCRMParser myCRMParser = new GordianKeyPairCRMParser(theKeyStoreMgr, theKeyPairCertifier, thePasswordResolver);
                List<GordianPEMObject> myChain = myCRMParser.decodeCertificateRequest(myObject);
                myParser.writePEMFile(pOutStream, myChain);
                break;
            case KEYPAIRSETCERTREQ:
                myCRMParser = new GordianKeyPairSetCRMParser(theKeyStoreMgr, theKeyPairSetCertifier, thePasswordResolver);
                myChain = myCRMParser.decodeCertificateRequest(myObject);
                myParser.writePEMFile(pOutStream, myChain);
                break;
            default:
                throw new GordianDataException("Unexpected object type");
        }
    }

    @Override
    public GordianKeyStoreEntry importEntry(final File pFile,
                                            final char[] pPassword) throws OceanusException {
        try (FileInputStream myStream = new FileInputStream(pFile)) {
            return importEntry(myStream, pPassword);
        } catch (IOException e) {
            throw new GordianIOException("Failed to read from file", e);
        }
    }

    @Override
    public GordianKeyStoreEntry importEntry(final InputStream pStream,
                                            final char[] pPassword) throws OceanusException {
        final GordianPEMCoder myCoder = new GordianPEMCoder(theKeyStore);
        myCoder.setLockResolver(theLockResolver);
        return myCoder.importKeyStoreEntry(pStream, pPassword);
    }

    @Override
    public List<GordianKeyStoreEntry> importCertificates(final File pFile) throws OceanusException {
        try (FileInputStream myStream = new FileInputStream(pFile)) {
            return importCertificates(myStream);
        } catch (IOException e) {
            throw new GordianIOException("Failed to read from file", e);
        }
    }

    @Override
    public List<GordianKeyStoreEntry> importCertificates(final InputStream pStream) throws OceanusException {
        final GordianPEMCoder myCoder = new GordianPEMCoder(theKeyStore);
        return myCoder.importCertificates(pStream);
    }
}
