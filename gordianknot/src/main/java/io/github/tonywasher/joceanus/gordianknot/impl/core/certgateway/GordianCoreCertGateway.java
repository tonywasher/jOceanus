/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.core.certgateway;

import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertUse;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayConfirm;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayRequest;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayResponse;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayStatus.GordianCertStatus;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreGateway.GordianLockResolver;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianDataConverter;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStore;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreManager;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x500.X500Name;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * keyStoreGateway implementation.
 */
public class GordianCoreCertGateway
        implements GordianBaseCertGateway {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

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
    private final GordianCertGatewayEncryptor theEncryptor;

    /**
     * The builder.
     */
    private final GordianCertGatewayBuilder theBuilder;

    /**
     * The parser.
     */
    private final GordianCertGatewayParser theParser;

    /**
     * The next messageId.
     */
    private final AtomicInteger theNextId;

    /**
     * The requestMap.
     */
    private final Map<Integer, GordianRequestCache> theRequestMap;

    /**
     * The responseMap.
     */
    private final Map<Integer, GordianCoreCertificate> theResponseMap;

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
     *
     * @param pFactory     the factory
     * @param pKeyStoreMgr the keyStoreMgr
     */
    GordianCoreCertGateway(final GordianBaseFactory pFactory,
                           final GordianCoreKeyStoreManager pKeyStoreMgr) {
        /* Store parameters */
        theFactory = pFactory;
        theKeyStoreMgr = pKeyStoreMgr;
        theKeyStore = theKeyStoreMgr.getKeyStore();

        /* Create underlying classes */
        theEncryptor = new GordianCertGatewayEncryptor(theFactory);
        theBuilder = new GordianCertGatewayBuilder(this);
        theParser = new GordianCertGatewayParser(this, theBuilder);
        theNextId = new AtomicInteger(1);
        theRequestMap = new HashMap<>();
        theResponseMap = new HashMap<>();
    }

    @Override
    public GordianBaseFactory getFactory() {
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

    @Override
    public GordianCertGatewayEncryptor getEncryptor() {
        return theEncryptor;
    }

    @Override
    public GordianKeyStorePair getSigner() {
        return theKeyPairCertifier;
    }

    @Override
    public byte[] getMACSecret(final X500Name pName) {
        final String mySecret = theMACSecretResolver.apply(pName);
        return mySecret == null ? null : GordianDataConverter.stringToByteArray(mySecret);
    }

    @Override
    public Function<String, char[]> getPasswordResolver() {
        return thePasswordResolver;
    }

    @Override
    public void setMACSecretResolver(final Function<X500Name, String> pResolver) {
        theMACSecretResolver = pResolver;
    }

    @Override
    public GordianCertGatewayRequest createCertificateRequest(final String pAlias) throws GordianException {
        /* Access the requested entry */
        final char[] myPassword = thePasswordResolver.apply(pAlias);
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, myPassword);

        /* If it is a keyPair */
        if (myEntry instanceof GordianKeyStorePair myKeyPair) {
            /* Create the certificate request */
            final int myReqId = theNextId.getAndIncrement();
            final CertReqMsg myCertReq = theBuilder.createCertificateRequest(myKeyPair, myReqId);
            final GordianCertGatewayRequestASN1 myRequest = new GordianCertGatewayRequestASN1(myCertReq);

            /* Store details in requestMap */
            theRequestMap.put(myReqId, new GordianRequestCache(pAlias, myKeyPair));

            /* Return the request */
            return myRequest;

            /* else reject request */
        } else {
            throw new GordianDataException("Alias not found");
        }
    }

    @Override
    public void setCertifier(final String pAlias) throws GordianException {
        final char[] myPassword = thePasswordResolver.apply(pAlias);
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, myPassword);
        if (myEntry instanceof GordianKeyStorePair myPair) {
            final GordianCertificate myCert = myPair.getCertificateChain().getFirst();
            if (myCert.getUsage().hasUse(GordianCertUse.CERTIFICATE)) {
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
    public GordianCertGatewayResponse processCertificateRequest(final GordianCertGatewayRequest pRequest) throws GordianException {
        /* Extract the certificate request */
        final CertReqMsg myCertReq = pRequest.getCertificateRequest();

        /* Determine responseId and sign certificate */
        final int myRespId = theNextId.getAndIncrement();
        final List<GordianCertificate> myChain = theParser.processCertificateRequest(myCertReq);

        /* Create the certificate response */
        final int myReqId = myCertReq.getCertReq().getCertReqId().intValueExact();
        final GordianCertGatewayStatusASN1 myStatus = GordianCertGatewayStatusASN1.createCertStatus(GordianCertStatus.ACCEPTED);
        final GordianCertGatewayResponseASN1 myResponse
                = GordianCertGatewayResponseASN1.createCertResponse(myReqId, myRespId, myStatus, myChain);

        /* Create PKMACValue if required */
        final X500Name mySubject = myCertReq.getCertReq().getCertTemplate().getSubject();
        final byte[] myMACSecret = getMACSecret(mySubject);
        if (myMACSecret != null) {
            final ASN1Object myMACData = myResponse.getMACData();
            final PKMACValue myMACValue = theBuilder.createPKMACValue(myMACSecret, myMACData);
            myResponse.setMACValue(myMACValue);
        }

        /* Access the new certificate */
        final GordianCoreCertificate myCert = (GordianCoreCertificate) myChain.getFirst();

        /* If the certificate requires encryption */
        if (GordianCertGatewayParser.requiresEncryption(myCertReq)) {
            /* Encrypt the certificate */
            myResponse.encryptCertificate(theEncryptor);

            /* Store in the response cache */
            theResponseMap.put(myRespId, myCert);

            /* else store the certificate */
        } else {
            theKeyStore.setCertificate(getCertificateAlias(myRespId), myCert);
        }

        /* Return the response */
        return myResponse;
    }

    @Override
    public GordianCertGatewayConfirm processCertificateResponse(final GordianCertGatewayResponse pResponse) throws GordianException {
        /* Decode the certificate response */
        final GordianCertGatewayResponseASN1 myResponse = (GordianCertGatewayResponseASN1) pResponse;

        /* Access the original keyPair for the request */
        final GordianRequestCache myCache = theRequestMap.get(myResponse.getRequestId());
        if (myCache == null) {
            throw new GordianDataException("Unrecognised request Id");
        }
        theRequestMap.remove(myResponse.getRequestId());

        /* Process the certificate response */
        theParser.processCertificateResponse(myResponse, myCache.keyPair());
        final GordianCertificate[] myChain = myResponse.getCertificateChain(theEncryptor);

        /* Update the keyStore with the new certificate chain */
        final List<GordianCertificate> myList = List.of(myChain);
        theKeyStore.updateCertificateChain(myCache.alias(), myList);

        /* calculate the Digest value */
        final byte[] myDigest = theBuilder.calculateAckValue((GordianCoreCertificate) myChain[0]);
        final GordianCertGatewayStatusASN1 myStatus = GordianCertGatewayStatusASN1.createCertStatus(GordianCertStatus.ACCEPTED);
        final GordianCertGatewayConfirmASN1 myConfirm = new GordianCertGatewayConfirmASN1(myResponse.getResponseId(), myStatus, myDigest);

        /* Return the response id */
        return myConfirm;
    }

    @Override
    public void processCertificateConfirm(final GordianCertGatewayConfirm pConfirm) throws GordianException {
        /* Decode the certificate confirm */
        final GordianCertGatewayConfirmASN1 myConfirm = (GordianCertGatewayConfirmASN1) pConfirm;
        final Integer myRespId = myConfirm.getResponseId();

        /* Access the original certificate for the ack */
        final GordianCoreCertificate myCert = theResponseMap.get(myRespId);
        if (myCert == null) {
            throw new GordianDataException("Unrecognised response Id");
        }
        theResponseMap.remove(myRespId);

        /* Check the Digest value */
        final byte[] myDigest = theBuilder.calculateAckValue(myCert);
        if (!Arrays.equals(myDigest, myConfirm.getDigestValue())) {
            throw new GordianDataException("Invalid Digest");
        }

        /* Store the certificate */
        theKeyStore.setCertificate(getCertificateAlias(myRespId), myCert);
    }

    /**
     * Get certificate alias.
     *
     * @param pRespId the response id
     * @return the alias
     */
    public String getCertificateAlias(final Integer pRespId) {
        return "AllocatedCertificate_" + pRespId;
    }

    /**
     * RequestMapCache.
     *
     * @param alias   the alias
     * @param keyPair the keyPair
     */
    record GordianRequestCache(String alias,
                               GordianKeyStorePair keyPair) {
    }
}
