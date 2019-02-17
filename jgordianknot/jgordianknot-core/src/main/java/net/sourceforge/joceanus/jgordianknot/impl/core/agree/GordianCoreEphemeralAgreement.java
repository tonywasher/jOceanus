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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import java.security.spec.X509EncodedKeySpec;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Ephemeral Agreement.
 */
public abstract class GordianCoreEphemeralAgreement
        extends GordianCoreAgreement
        implements GordianEphemeralAgreement {
    /**
     * The owning KeyPair.
     */
    private GordianKeyPair theOwner;

    /**
     * The ephemeral KeyPair.
     */
    private GordianKeyPair theEphemeral;

    /**
     * The partner ephemeral KeyPair.
     */
    private GordianKeyPair thePartnerEphemeral;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreEphemeralAgreement(final GordianCoreFactory pFactory,
                                            final GordianAgreementSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Obtain the Ephemeral keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getOwnerKeyPair() {
        return theOwner;
    }

    /**
     * Obtain the Ephemeral keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getEphemeralKeyPair() {
        return theEphemeral;
    }

    /**
     * Obtain the partner Ephemeral keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getPartnerEphemeralKeyPair() {
        return thePartnerEphemeral;
    }

    @Override
    public byte[] initiateAgreement(final GordianKeyPair pInitiator) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pInitiator);

        /* Store the keyPair */
        theOwner = pInitiator;

        /* Create ephemeral key */
        final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theOwner.getKeySpec());
        theEphemeral = myGenerator.generateKeyPair();
        final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(theEphemeral);
        final byte[] myKeyBytes = myKeySpec.getEncoded();

        /* Create buffer for message */
        final int myLen = myKeyBytes.length;
        final byte[] myMessage = new byte[myLen + INITLEN];

        /* Create the message */
        System.arraycopy(newInitVector(), 0, myMessage, 0, INITLEN);
        System.arraycopy(myKeyBytes, 0, myMessage, INITLEN, myLen);
        return myMessage;
    }

    /**
     * Parse the incoming message.
     * @param pResponder the responding keyPair
     * @param pMessage the incoming message
     * @return the ephemeral keySpec
     * @throws OceanusException on error
     */
    protected byte[] parseMessage(final GordianKeyPair pResponder,
                                  final byte[] pMessage) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pResponder);

        /* Store the keyPair */
        theOwner = pResponder;

        /* Obtain initVector */
        final byte[] myInitVector = new byte[INITLEN];
        System.arraycopy(pMessage, 0, myInitVector, 0, INITLEN);
        storeInitVector(myInitVector);

        /* Obtain keySpec */
        final int myBaseLen = pMessage.length - INITLEN;
        final byte[] myBase = new byte[myBaseLen];
        System.arraycopy(pMessage, INITLEN, myBase, 0, myBaseLen);
        final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myBase);

        /* Create ephemeral key */
        final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theOwner.getKeySpec());
        theEphemeral = myGenerator.generateKeyPair();

        /* Derive partner ephemeral key */
        thePartnerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);

        /* Return the ephemeral keySpec */
        return myGenerator.getX509Encoding(theEphemeral).getEncoded();
    }

    /**
     * Parse the ephemeral keySpec.
     * @param pKeySpec the target ephemeral keySpec
     * @throws OceanusException on error
     */
    protected void parseEphemeral(final byte[] pKeySpec) throws OceanusException {
        /* Obtain keySpec */
        final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(pKeySpec);

        /* Derive partner ephemeral key */
        final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theOwner.getKeySpec());
        thePartnerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
    }
}
