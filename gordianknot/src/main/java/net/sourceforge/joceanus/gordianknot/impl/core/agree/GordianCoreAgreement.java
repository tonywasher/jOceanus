/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.agree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreement;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianAgreementResult.GordianDerivationId;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

import java.security.SecureRandom;

/**
 * Key Agreement Specification.
 */
public abstract class GordianCoreAgreement
    implements GordianAgreement {
    /**
     * InitVectorLength.
     */
    protected static final int INITLEN = 32;

    /**
     * Invalid AgreementSpec message.
     */
    protected static final String ERROR_INVSPEC = "Incorrect AgreementSpec";

    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The algorithmId factory.
     */
    private final GordianAgreementAlgId theAlgIds;

    /**
     * The agreementSpec.
     */
    private final GordianAgreementSpec theSpec;

    /**
     * The result calculator.
     */
    private final GordianAgreementResult theResultCalc;

    /**
     * The status.
     */
    private GordianAgreementStatus theStatus;

    /**
     * The resultType.
     */
    private Object theResultType;

    /**
     * The agreed result.
     */
    private Object theResult;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreAgreement(final GordianBaseFactory pFactory,
                                   final GordianAgreementSpec pSpec) {
        theFactory = pFactory;
        theSpec = pSpec;
        theStatus = GordianAgreementStatus.CLEAN;
        theAlgIds = ((GordianCoreAgreementFactory) pFactory.getAsyncFactory().getAgreementFactory()).getAlgorithmIds();
        theResultCalc = new GordianAgreementResult(pFactory);
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianBaseFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianAgreementSpec getAgreementSpec() {
        return theSpec;
    }

    @Override
    public GordianAgreementStatus getStatus() {
        return theStatus;
    }

    /**
     * Set the status.
     * @param pStatus the status
     */
    protected void setStatus(final GordianAgreementStatus pStatus) {
        theStatus = pStatus;
    }

    @Override
    public Object getResultType() {
        return theResultType;
    }

    @Override
    public Object getResult() throws GordianException {
        /* Must be in result available state */
        checkStatus(GordianAgreementStatus.RESULT_AVAILABLE);

        /* Obtain result to return and reset the agreement */
        final Object myResult = theResult;
        reset();

        /* return the result */
        return myResult;
    }

    @Override
    public void setResultType(final Object pResultType) throws GordianException {
        /* Check result Type */
        checkResultType(pResultType);
        theResultType = pResultType;
    }

    @Override
    public void reset() {
        /* Reset the result and status */
        theResult = null;
        setStatus(GordianAgreementStatus.CLEAN);

        /* Reset the client and serverIVs */
        theResultCalc.reset();
    }

    /**
     * Check the resultType is valid.
     * @param pResultType the resultType
     * @throws GordianException on error
     */
    private void checkResultType(final Object pResultType) throws GordianException {
        /* No need to check FactoryType or null */
        if (pResultType instanceof GordianFactoryType
            || pResultType == null) {
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

    /**
     * Obtain the random.
     * @return the random
     */
    protected SecureRandom getRandom() {
        return theFactory.getRandomSource().getRandom();
    }

    /**
     * Check status.
     * @param pStatus the required status
     * @throws GordianException on error
     */
    protected void checkStatus(final GordianAgreementStatus pStatus) throws GordianException {
        /* If we are in the wrong state */
        if (theStatus != pStatus) {
            throw new GordianLogicException("Invalid State: " + theStatus);
        }
    }

    /**
     * Create and return a new clientIV.
     * @return the initVector
     */
    protected byte[] newClientIV() {
        /* Obtain current IV */
        byte[] myClientIV = getClientIV();
        if (myClientIV == null) {
            /* Create a new initVector */
            myClientIV = new byte[INITLEN];
            getRandom().nextBytes(myClientIV);
            storeClientIV(myClientIV);
        }
        return myClientIV;
    }

    /**
     * Store client initVector.
     * @param pInitVector the initVector
     */
    protected void storeClientIV(final byte[] pInitVector) {
        /* Store the initVector */
        theResultCalc.setClientIV(pInitVector);
    }

    /**
     * Obtain the clientIV.
     * @return the clientIV
     */
    protected byte[] getClientIV() {
        return theResultCalc.getClientIV();
    }

    /**
     * Create a new serverIV.
     */
    void newServerIV() {
        /* Obtain current IV */
        if (getServerIV() == null) {
            /* Create a new initVector */
            final byte[] myServerIV = new byte[INITLEN];
            getRandom().nextBytes(myServerIV);
            storeServerIV(myServerIV);
        }
    }

    /**
     * Store server initVector.
     * @param pInitVector the initVector
     */
    protected void storeServerIV(final byte[] pInitVector) {
        /* Store the initVector */
        theResultCalc.setServerIV(pInitVector);
    }

    /**
     * Obtain the serverIV.
     * @return the serverIV
     */
    protected byte[] getServerIV() {
        return theResultCalc.getServerIV();
    }

    /**
     * Store secret.
     * @param pSecret the secret
     * @throws GordianException on error
     */
    protected void storeSecret(final byte[] pSecret) throws GordianException {
        /* Protect against failure */
        try {
            /* Just process the secret */
            processSecret(pSecret);

            /* Clear buffers */
        } finally {
            /* Clear the secret */
             Arrays.fill(pSecret, (byte) 0);
        }
    }

    /**
     * Process the secret.
     * @param pSecret the secret
     * @throws GordianException on error
     */
    protected void processSecret(final byte[] pSecret) throws GordianException {
        /* Calculate result */
        theResult = theResultCalc.processSecret(pSecret, theResultType);

        /* Set status */
        setStatus(GordianAgreementStatus.RESULT_AVAILABLE);
    }

    /**
     * Obtain identifier for result.
     * @return the identifier
     * @throws GordianException on error
     */
    protected AlgorithmIdentifier getIdentifierForResult() throws GordianException {
        /* determine the resultType algId */
        return theAlgIds.getIdentifierForResult(theResultType);
    }

    /**
     * process result algorithmId.
     * @param pResId the result algorithmId.
     * @throws GordianException on error
     */
    public void processResultIdentifier(final AlgorithmIdentifier pResId) throws GordianException {
        /* determine the resultType */
        theResultType = theAlgIds.processResultIdentifier(pResId);
    }

    /**
     * Calculate the derived secret.
     * @param pSecret the secret
     * @param pId the derivation Id
     * @param pResultLen the result length
     * @return the derived secret
     * @throws GordianException on error
     */
    protected byte[] calculateDerivedSecret(final byte[] pSecret,
                                            final GordianDerivationId pId,
                                            final int pResultLen) throws GordianException {
        return theResultCalc.calculateDerivedSecret(pSecret, pId, pResultLen);
    }
}
