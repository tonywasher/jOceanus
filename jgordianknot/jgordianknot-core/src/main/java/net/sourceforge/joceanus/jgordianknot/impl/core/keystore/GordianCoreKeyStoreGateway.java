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
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreGateway;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianLock;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianPEMObject.GordianPEMObjectType;
import net.sourceforge.joceanus.jgordianknot.impl.core.zip.GordianCoreLock;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

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
     * The encryptor.
     */
    private final GordianCRMEncryptor theEncryptor;

    /**
     * The builder.
     */
    private final GordianCRMBuilder theBuilder;

    /**
     * The parser.
     */
    private final GordianCRMParser theParser;

    /**
     * The next messageId.
     */
    private final AtomicInteger theNextId;

    /**
     * The requestMap.
     */
    private final Map<Integer, GordianRequestCache> theRequestMap;

    /**
     * The encryption target certificate.
     */
    private GordianCoreCertificate theTarget;

    /**
     * The secret MAC key value.
     */
    private byte[] theMACSecret;

    /**
     * The keyPairCertifier.
     */
    private GordianKeyStorePair theKeyPairCertifier;

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
        /* Store parameters */
        theFactory = pFactory;
        theKeyStoreMgr = pKeyStoreMgr;
        theKeyStore = theKeyStoreMgr.getKeyStore();

        /* Create underlying classes */
        theEncryptor = new GordianCRMEncryptor(theFactory);
        theBuilder = new GordianCRMBuilder(this);
        theParser = new GordianCRMParser(this);
        theNextId = new AtomicInteger(1);
        theRequestMap = new HashMap<>();
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianCoreKeyStore getKeyStore() {
        return theKeyStore;
    }

    @Override
    public GordianCoreKeyStoreManager getKeyStoreManager() {
        return theKeyStoreMgr;
    }

    /**
     * Obtain the encryptor.
     * @return the encryptor
     */
    GordianCRMEncryptor getEncryptor() {
        return theEncryptor;
    }

    /**
     * Obtain the signer.
     * @return the signer
     */
    GordianKeyStorePair getSigner() {
        return theKeyPairCertifier;
    }

    /**
     * Obtain the MACSecret.
     * @return the secret
     */
    byte[] getMACSecret() {
        return theMACSecret;
    }

    /**
     * Obtain the EncryptionTarget.
     * @return the target
     */
    GordianCoreCertificate getTarget() {
        return theTarget;
    }

    /**
     * Obtain the PasswordResolver.
     * @return the passwordResolver
     */
    Function<String, char[]> getPasswordResolver() {
        return thePasswordResolver;
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
        final List<GordianCertificate> myKeyPairChain = theKeyStore.getCertificateChain(pAlias);
        if (myKeyPairChain != null) {
            theTarget = (GordianCoreCertificate) myKeyPairChain.get(0);
            return;
        }
        throw new GordianDataException("Encryption target not found");
    }

    @Override
    public void setMACSecret(final String pMACSecret) {
        theMACSecret = TethysDataConverter.stringToByteArray(pMACSecret);
    }

    @Override
    public void createCertificateRequest(final String pAlias,
                                         final OutputStream pStream,
                                         final char[] pPassword) throws OceanusException {
        /* Access the requested entry */
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, pPassword);

        /* If it is a keyPair */
        if (myEntry instanceof GordianKeyStorePair) {
            /* Create the certificate request */
            final int myReqId = theNextId.getAndIncrement();
            final GordianKeyStorePair myKeyPair = (GordianKeyStorePair) myEntry;
            final GordianPEMObject myCertReq = theBuilder.createCertificateRequest(myKeyPair, myReqId);

            /* Store details in requestMap */
            theRequestMap.put(myReqId, new GordianRequestCache(pAlias, myKeyPair));

            /* Write request to output stream */
            final GordianPEMParser myParser = new GordianPEMParser();
            myParser.writePEMFile(pStream, Collections.singletonList(myCertReq));

            /* else reject request */
        } else {
            throw new GordianDataException("Alias not found");
        }
    }

    @Override
    public void setCertifier(final String pAlias,
                             final char[] pPassword) throws OceanusException {
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, pPassword);
        if (myEntry instanceof GordianKeyStorePair) {
            final GordianKeyStorePair myPair = (GordianKeyStorePair) myEntry;
            final GordianCertificate myCert = myPair.getCertificateChain().get(0);
            if (myCert.getUsage().hasUse(GordianKeyPairUse.CERTIFICATE)) {
                theKeyPairCertifier = myPair;
                return;
            }
        }
        throw new GordianDataException("Invalid keyPairCertifier");
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
    public void processCertificateRequest(final InputStream pInStream,
                                          final OutputStream pOutStream) throws OceanusException {
        final GordianPEMParser myParser = new GordianPEMParser();
        final GordianPEMObject myObject = myParser.parsePEMFile(pInStream).get(0);
        if (myObject.getObjectType() == GordianPEMObjectType.CERTREQ) {
            final int myRespId = theNextId.getAndIncrement();
            final GordianPEMObject myResponse = theParser.decodeCertificateRequest(myObject, myRespId);
            myParser.writePEMFile(pOutStream, Collections.singletonList(myResponse));
        } else {
            throw new GordianDataException("Unexpected object type");
        }
    }

    @Override
    public List<GordianCertificate> processCertificateResponse(final InputStream pInStream) throws OceanusException {
        final GordianPEMParser myParser = new GordianPEMParser();
        final GordianPEMObject myObject = myParser.parsePEMFile(pInStream).get(0);
        if (myObject.getObjectType() == GordianPEMObjectType.CERTRESP) {
            final GordianCertResponseASN1 myResponse = theParser.decodeCertificateResponse(myObject, theRequestMap);
            final GordianCertificate[] myChain = myResponse.getCertificateChain(theEncryptor);
            return List.of(myChain);
        } else {
            throw new GordianDataException("Unexpected object type");
        }
    }

    @Override
    public GordianKeyStoreEntry importEntry(final InputStream pStream) throws OceanusException {
        final GordianPEMCoder myCoder = new GordianPEMCoder(theKeyStore);
        myCoder.setLockResolver(theLockResolver);
        return myCoder.importKeyStoreEntry(pStream);
    }

    @Override
    public List<GordianKeyStoreEntry> importCertificates(final InputStream pStream) throws OceanusException {
        final GordianPEMCoder myCoder = new GordianPEMCoder(theKeyStore);
        return myCoder.importCertificates(pStream);
    }

    /**
     * RequestMapCache.
     */
    static final class GordianRequestCache {
         /**
         * The alias.
         */
        private final String theAlias;

        /**
         * The KeyPair.
         */
        private final GordianKeyStorePair theKeyPair;

        /**
         * Constructor.
         * @param pAlias the alias
         * @param pKeyPair the keyPair
         */
        GordianRequestCache(final String pAlias,
                            final GordianKeyStorePair pKeyPair) {
            theAlias = pAlias;
            theKeyPair = pKeyPair;
        }

        /**
         * Obtain the alias.
         * @return the alias
         */
        String getAlias() {
            return theAlias;
        }

        /**
         * Obtain the keyPair.
         * @return the keyPair
         */
        GordianKeyStorePair getKeyPair() {
            return theKeyPair;
        }
    }
}
