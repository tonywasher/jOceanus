/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.agree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementKDF;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementParams;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseData;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySetFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * Key Agreement Parameters Implementation.
 */
public class GordianCoreAgreementParams
        implements GordianAgreementParams {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * Is this a client or server parameters.
     */
    private final boolean isClient;

    /**
     * The Id.
     */
    private Long theId;

    /**
     * The Spec.
     */
    private final GordianAgreementSpec theSpec;

    /**
     * The ResultType.
     */
    private final Object theResultType;

    /**
     * The ClientCertificate.
     */
    private GordianCertificate theClient;

    /**
     * The ServerCertificate.
     */
    private GordianCertificate theServer;

    /**
     * The SignerCertificate.
     */
    private GordianCertificate theSigner;

    /**
     * The SignatureSpec.
     */
    private GordianSignatureSpec theSignSpec;

    /**
     * The Additional data.
     */
    private byte[] theAdditional;

    /**
     * Constructor.
     *
     * @param pSupplier   the supplier
     * @param pSpec       the agreement Spec
     * @param pResultType the resultType
     * @throws GordianException on error
     */
    GordianCoreAgreementParams(final GordianCoreAgreementSupplier pSupplier,
                               final GordianAgreementSpec pSpec,
                               final Object pResultType) throws GordianException {
        isClient = true;
        theFactory = pSupplier.getFactory();
        theSpec = pSpec;
        checkResultType(pResultType);
        theResultType = pResultType;
    }

    /**
     * Constructor.
     *
     * @param pBuilder the builder
     */
    GordianCoreAgreementParams(final GordianCoreAgreementBuilder pBuilder) {
        /* Copy all fields */
        final GordianCoreAgreementSupplier mySupplier = pBuilder.getSupplier();
        final GordianCoreAgreementState myState = pBuilder.getState();
        isClient = false;
        theId = mySupplier.getNextId();
        theFactory = pBuilder.getSupplier().getFactory();
        theSpec = myState.getSpec();
        theResultType = myState.getResultType();
        theClient = myState.getClient().getCertificate();
        theServer = myState.getServer().getCertificate();
        theSigner = myState.getSignerCertificate();
        theSignSpec = myState.getSignSpec();
    }

    /**
     * Constructor.
     *
     * @param pSource the source parameters to copy
     */
    GordianCoreAgreementParams(final GordianCoreAgreementParams pSource) {
        /* Copy all fields */
        isClient = pSource.isClient();
        theId = pSource.getId();
        theFactory = pSource.theFactory;
        theSpec = pSource.getAgreementSpec();
        theResultType = pSource.getResultType();
        theClient = pSource.getClientCertificate();
        theServer = pSource.getServerCertificate();
        theSigner = pSource.getSignerCertificate();
        theSignSpec = pSource.getSignatureSpec();
        theAdditional = pSource.theAdditional;
    }

    /**
     * is this a client parameters.
     *
     * @return true/false
     */
    boolean isClient() {
        return isClient;
    }

    /**
     * Obtain the id.
     *
     * @return the id
     */
    Long getId() {
        return theId;
    }

    @Override
    public GordianAgreementSpec getAgreementSpec() {
        return theSpec;
    }

    @Override
    public Object getResultType() {
        return theResultType;
    }

    /**
     * Check the resultType is valid.
     *
     * @param pResultType the resultType
     * @throws GordianException on error
     */
    private void checkResultType(final Object pResultType) throws GordianException {
        /* No need to check FactoryType */
        if (pResultType instanceof GordianFactoryType) {
            return;
        }

        /* Validate a keySetSpec */
        if (pResultType instanceof GordianKeySetSpec mySpec) {
            /* Check Spec */
            final GordianCoreKeySetFactory myKeySetFactory = (GordianCoreKeySetFactory) theFactory.getKeySetFactory();
            myKeySetFactory.checkKeySetSpec(mySpec);
            return;
        }

        /* Validate a symCipherSpec */
        if (pResultType instanceof GordianSymCipherSpec mySpec) {
            /* Check Spec */
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            myCipherFactory.checkSymCipherSpec(mySpec);
            return;
        }

        /* Validate a streamCipherSpec */
        if (pResultType instanceof GordianStreamCipherSpec mySpec) {
            /* Check Spec */
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            myCipherFactory.checkStreamCipherSpec(mySpec);
            return;
        }

        /* Validate a byte array */
        if (pResultType instanceof Integer myInt) {
            if (myInt <= 0) {
                throw new GordianLogicException("Invalid length for byteArray");
            }
            return;
        }

        /* Invalid resultType */
        throw new GordianLogicException("Invalid resultType");
    }

    @Override
    public GordianCertificate getClientCertificate() {
        return theClient;
    }

    @Override
    public GordianCertificate getServerCertificate() {
        return theServer;
    }

    @Override
    public GordianCertificate getSignerCertificate() {
        return theSigner;
    }

    @Override
    public GordianSignatureSpec getSignatureSpec() {
        return theSignSpec;
    }

    @Override
    public byte[] getAdditionalData() {
        return theAdditional == null ? null : theAdditional.clone();
    }

    @Override
    public GordianAgreementParams setClientCertificate(final GordianCertificate pClient) throws GordianException {
        /* Not allowed for server parameters */
        if (!isClient) {
            throw new GordianDataException("Client Certificate cannot be changed for server");
        }

        /* If we have a client certificate */
        final GordianAgreementSpec mySpec = getAgreementSpec();
        final GordianAgreementType myType = mySpec.getAgreementType();
        if (pClient != null) {
            /* Check that the keySpec matches the agreement and that we have a private key */
            final GordianKeyPair myKeyPair = pClient.getKeyPair();
            if (myType.isSigned() || myType.isAnonymous()) {
                throw new GordianDataException("Client Certificate not supported for agreement");
            }
            if (!Objects.equals(mySpec.getKeyPairSpec(), myKeyPair.getKeyPairSpec())) {
                throw new GordianDataException("Client Certificate not valid for agreement");
            }
            if (!pClient.getUsage().hasUse(GordianKeyPairUse.AGREEMENT)) {
                throw new GordianDataException("Client Certificate must be capable of keyAgreement");
            }
            if (myKeyPair.isPublicOnly()) {
                throw new GordianDataException("Client Certificate must supply privateKey");
            }
        } else if (!myType.isSigned() && !myType.isAnonymous()) {
            throw new GordianDataException("Null Client Certificate not allowed");
        }

        /* Create new updated parameters */
        final GordianCoreAgreementParams myParams = new GordianCoreAgreementParams(this);
        myParams.theClient = pClient;
        return myParams;
    }

    @Override
    public GordianAgreementParams setServerCertificate(final GordianCertificate pServer) throws GordianException {
        /* If we have a server certificate */
        final GordianAgreementSpec mySpec = getAgreementSpec();
        if (pServer != null) {
            /* Check that the keySpec matches the agreement */
            final GordianKeyPair myKeyPair = pServer.getKeyPair();
            if (mySpec.getAgreementType().isSigned()) {
                throw new GordianDataException("Server Certificate not supported for agreement");
            }
            if (!Objects.equals(mySpec.getKeyPairSpec(), myKeyPair.getKeyPairSpec())) {
                throw new GordianDataException("Server Certificate not valid for agreement");
            }
            if (!pServer.getUsage().hasUse(GordianKeyPairUse.AGREEMENT)) {
                throw new GordianDataException("Server Certificate must be capable of keyAgreement");
            }

            /* If we are a server */
            if (!isClient) {
                /* Perform additional checks */
                if (myKeyPair.isPublicOnly()) {
                    throw new GordianDataException("Server Certificate must supply privateKey");
                }

                /* Check that we match the existing server certificate */
                if (!Arrays.equals(theServer.getEncoded(), pServer.getEncoded())) {
                    throw new GordianDataException("Server Certificate must match requested certificate");
                }
            }

        } else if (!mySpec.getAgreementType().isSigned()) {
            throw new GordianDataException("Null Server Certificate not allowed");
        }

        /* Create new updated parameters */
        final GordianCoreAgreementParams myParams = new GordianCoreAgreementParams(this);
        myParams.theServer = pServer;
        return myParams;
    }

    @Override
    public GordianAgreementParams setSigner(final GordianCertificate pSigner) throws GordianException {
        final GordianSignatureFactory mySignFactory = theFactory.getAsyncFactory().getSignatureFactory();
        final GordianSignatureSpec mySignSpec = pSigner == null ? null : mySignFactory.defaultForKeyPair(pSigner.getKeyPair().getKeyPairSpec());
        return setSigner(pSigner, mySignSpec);
    }

    @Override
    public GordianAgreementParams setSigner(final GordianCertificate pSigner,
                                            final GordianSignatureSpec pSignSpec) throws GordianException {
        /* Not allowed for client parameters */
        if (isClient) {
            throw new GordianDataException("Signer Certificate cannot be set for client");
        }

        /* If we have a signer certificate */
        final GordianAgreementSpec mySpec = getAgreementSpec();
        if (pSigner != null) {
            /* Check that we require a signer */
            if (!mySpec.getAgreementType().isSigned()) {
                throw new GordianDataException("Signer Certificate not allowed");
            }

            /* Check that certificate can sign data */
            if (!pSigner.getUsage().hasUse(GordianKeyPairUse.SIGNATURE)) {
                throw new GordianDataException("Certificate must be capable of signing data");
            }

            /* Check that signSpec is valid for keyPair */
            final GordianSignatureFactory mySignFactory = theFactory.getAsyncFactory().getSignatureFactory();
            if (!mySignFactory.validSignatureSpecForKeyPair(pSigner.getKeyPair(), pSignSpec)) {
                throw new GordianDataException(GordianBaseData.getInvalidText(pSignSpec));
            }

        } else if (mySpec.getAgreementType().isSigned()) {
            throw new GordianDataException("Null Signer Certificate not allowed");
        }

        /* Create new updated parameters */
        final GordianCoreAgreementParams myParams = new GordianCoreAgreementParams(this);
        myParams.theSigner = pSigner;
        myParams.theSignSpec = pSignSpec;
        return myParams;
    }

    @Override
    public GordianAgreementParams setAdditionalData(final byte[] pData) throws GordianException {
        /* Only allowed if KDFType is not NONE */
        if (pData != null
                && GordianAgreementKDF.NONE.equals(theSpec.getKDFType())) {
            throw new GordianDataException("Additional Data not allowed for KDFType NONE");
        }

        /* Create new updated parameters */
        final GordianCoreAgreementParams myParams = new GordianCoreAgreementParams(this);
        myParams.theAdditional = pData == null ? null : pData.clone();
        return myParams;
    }
}
