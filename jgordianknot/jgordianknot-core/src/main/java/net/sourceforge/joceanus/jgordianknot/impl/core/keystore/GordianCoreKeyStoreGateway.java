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
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x500.X500Name;

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
     * The secret MAC key resolver.
     */
    private Function<X500Name, String> theMACSecretResolver;

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
        theParser = new GordianCRMParser(this, theBuilder);
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
     * @param pName the name to resolve for
     * @return the secret
     */
    byte[] getMACSecret(final X500Name pName) {
        final String mySecret = theMACSecretResolver.apply(pName);
        return mySecret == null ? null : TethysDataConverter.stringToByteArray(mySecret);
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
                            final GordianLock pLock) throws OceanusException {
        final char[] myPassword = thePasswordResolver.apply(pAlias);
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, myPassword);
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
    public void setMACSecretResolver(final Function<X500Name, String> pResolver) {
        theMACSecretResolver = pResolver;
    }

    @Override
    public void createCertificateRequest(final String pAlias,
                                         final OutputStream pStream) throws OceanusException {
        /* Access the requested entry */
        final char[] myPassword = thePasswordResolver.apply(pAlias);
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, myPassword);

        /* If it is a keyPair */
        if (myEntry instanceof GordianKeyStorePair) {
            /* Create the certificate request */
            final int myReqId = theNextId.getAndIncrement();
            final GordianKeyStorePair myKeyPair = (GordianKeyStorePair) myEntry;
            final CertReqMsg myCertReq = theBuilder.createCertificateRequest(myKeyPair, myReqId);

            /* Store details in requestMap */
            theRequestMap.put(myReqId, new GordianRequestCache(pAlias, myKeyPair));

            /* Write request to output stream */
            final GordianPEMParser myParser = new GordianPEMParser();
            final GordianPEMObject myPEMObject = GordianPEMCoder.createPEMObject(GordianPEMObjectType.CERTREQ, myCertReq);
            myParser.writePEMFile(pStream, Collections.singletonList(myPEMObject));

            /* else reject request */
        } else {
            throw new GordianDataException("Alias not found");
        }
    }

    @Override
    public void setCertifier(final String pAlias) throws OceanusException {
        final char[] myPassword = thePasswordResolver.apply(pAlias);
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, myPassword);
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
        /* Decode the certificate request */
        final GordianPEMParser myParser = new GordianPEMParser();
        final List<GordianPEMObject> myObjects = myParser.parsePEMFile(pInStream);
        final CertReqMsg myCertReq = GordianPEMCoder.decodeCertRequest(myObjects);

        /* Determine responseId and sign certificate */
        final int myRespId = theNextId.getAndIncrement();
        final List<GordianCertificate> myChain = theParser.processCertificateRequest(myCertReq);

        /* Create the certificate response */
        final int myReqId = myCertReq.getCertReq().getCertReqId().intValueExact();
        final GordianCertResponseASN1 myResponse = GordianCertResponseASN1.createCertResponse(myReqId, myRespId, myChain);

        /* Create PKMACValue if required */
        final X500Name mySubject = myCertReq.getCertReq().getCertTemplate().getSubject();
        final byte[] myMACSecret = getMACSecret(mySubject);
        if (myMACSecret != null) {
            final ASN1Object myMACData = myResponse.getMACData();
            final PKMACValue myMACValue = theBuilder.createPKMACValue(myMACSecret, myMACData);
            myResponse.setMACValue(myMACValue);
        }
        if (GordianCRMParser.requiresEncryption(myCertReq)) {
            myResponse.encryptCertificate(theEncryptor);
        }

        /* Write out the response */
        final GordianPEMObject myPEMObject = GordianPEMCoder.createPEMObject(GordianPEMObjectType.CERTRESP, myResponse);
        myParser.writePEMFile(pOutStream, Collections.singletonList(myPEMObject));
    }

    @Override
    public void processCertificateResponse(final InputStream pInStream) throws OceanusException {
        /* Decode the certificate response */
        final GordianPEMParser myParser = new GordianPEMParser();
        final List<GordianPEMObject> myObjects = myParser.parsePEMFile(pInStream);
        final GordianCertResponseASN1 myResponse = GordianPEMCoder.decodeCertResponse(myObjects);

        /* Access the original keyPair for the request */
        final GordianRequestCache myCache = theRequestMap.get(myResponse.getReqId());
        if (myCache == null) {
            throw new GordianDataException("Unrecognised request Id");
        }
        theRequestMap.remove(myResponse.getReqId());

        /* Process the certificate response */
        theParser.processCertificateResponse(myResponse, myCache.getKeyPair());
        final GordianCertificate[] myChain = myResponse.getCertificateChain(theEncryptor);

        /* Update the keyStore with the new certificate chain */
        final List<GordianCertificate> myList = List.of(myChain);
        theKeyStore.updateCertificateChain(myCache.getAlias(), myList);
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
