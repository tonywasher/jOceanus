/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.password;

import java.util.Arrays;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianEdwardsElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianKeyPairLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordLockSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1.GordianMessageType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * KeyPair Lock implementation.
 */
public class GordianKeyPairLockImpl
        implements GordianKeyPairLock {
    /**
     * The keySet.
     */
    private final GordianCoreKeySet theKeySet;

    /**
     * The lockBytes.
     */
    private final byte[] theLockBytes;

    /**
     * Locking constructor.
     * @param pLockingFactory the locking factory
     * @param pLockSpec the passwordLockSpec
     * @param pKeyPair the locking keyPair
     * @param pPassword the password
     * @throws OceanusException on error
     */
    public GordianKeyPairLockImpl(final GordianCoreFactory pLockingFactory,
                                  final GordianPasswordLockSpec pLockSpec,
                                  final GordianKeyPair pKeyPair,
                                  final char[] pPassword) throws OceanusException {
        /* Protect from exceptions */
        byte[] myPassword = null;
        try {
            /* Create the agreement and derive the factory */
            final GordianKeyPairFactory myKeyPairFactory = pLockingFactory.getKeyPairFactory();
            final GordianAgreementFactory myAgreeFactory = myKeyPairFactory.getAgreementFactory();
            final GordianAgreementSpec mySpec = getAgreementSpec(pKeyPair.getKeyPairSpec());
            final GordianAnonymousAgreement myAgreement = (GordianAnonymousAgreement) myAgreeFactory.createAgreement(mySpec);
            myAgreement.setResultType(GordianFactoryType.BC);
            final byte[] myClientHello = myAgreement.createClientHello(pKeyPair);
            final GordianAgreementMessageASN1 myHelloASN = GordianAgreementMessageASN1.getInstance(myClientHello);
            myHelloASN.checkMessageType(GordianMessageType.CLIENTHELLO);
            final GordianCoreFactory myFactory = (GordianCoreFactory) myAgreement.getResult();

            /* Create a recipe */
            final GordianPasswordLockRecipe myRecipe = new GordianPasswordLockRecipe(pLockingFactory, pLockSpec);

            /* Generate the keySet */
            myPassword = TethysDataConverter.charsToByteArray(pPassword);
            theKeySet = myRecipe.processPassword(myFactory, myPassword);

            /* Create lockBytes */
            final byte[] myLockBytes = myRecipe.buildLockBytes(myPassword.length, null);
            theLockBytes = new GordianKeyPairLockASN1(myHelloASN, myLockBytes).getLockBytes();

        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    /**
     * UnLocking constructor.
     * @param pLockingFactory the locking factory
     * @param pLockBytes the lockBytes
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @throws OceanusException on error
     */
    public GordianKeyPairLockImpl(final GordianCoreFactory pLockingFactory,
                                  final byte[] pLockBytes,
                                  final GordianKeyPair pKeyPair,
                                  final char[] pPassword) throws OceanusException {
         /* Protect from exceptions */
        byte[] myPassword = null;
        try {
            /* Store the Lock */
            theLockBytes = pLockBytes;

            /* Resolve the agreement */
            final GordianKeyPairFactory myKeyPairFactory = pLockingFactory.getKeyPairFactory();
            final GordianAgreementFactory myAgreeFactory = myKeyPairFactory.getAgreementFactory();
            final byte[] myClientHello = GordianKeyPairLockASN1.getInstance(pLockBytes).getAgreement().getEncodedBytes();
            final GordianAnonymousAgreement myAgreement = (GordianAnonymousAgreement) myAgreeFactory.createAgreement(myClientHello);
            myAgreement.acceptClientHello(pKeyPair, myClientHello);
            final GordianCoreFactory myFactory = (GordianCoreFactory) myAgreement.getResult();

            /* Resolve the recipe */
            myPassword = TethysDataConverter.charsToByteArray(pPassword);
            final GordianPasswordLockRecipe myRecipe = new GordianPasswordLockRecipe(pLockingFactory, myPassword.length, pLockBytes);

            /* Process the password, creating keySet */
            theKeySet = myRecipe.processPassword(myFactory, myPassword);

        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    @Override
    public GordianKeySet getLockedObject() {
        return theKeySet;
    }

    @Override
    public byte[] getLockBytes() {
        return theLockBytes;
    }

    /**
     * Obtain AgreementSpec for asymKeySpec.
     * @param pKeySpec the keySpec
     * @return the agreementSpec
     * @throws OceanusException on error
     */
    private static GordianAgreementSpec getAgreementSpec(final GordianKeyPairSpec pKeySpec) throws OceanusException {
        /* Determine KDF type */
        final GordianKDFType myKDFType = GordianEdwardsElliptic.CURVE25519.equals(pKeySpec.getSubKeyType())
                ? GordianKDFType.SHA256KDF
                : GordianKDFType.SHA512KDF;

        /* Determine AgreementType - either ANON or KEM */
        if (GordianAgreementType.ANON.isSupported(pKeySpec.getKeyPairType())) {
            return new GordianAgreementSpec(pKeySpec, GordianAgreementType.ANON, myKDFType);
        }
        if (GordianAgreementType.KEM.isSupported(pKeySpec.getKeyPairType())) {
            return new GordianAgreementSpec(pKeySpec, GordianAgreementType.KEM, GordianKDFType.NONE);
        }
        throw new GordianLogicException("Invalid KeyPair type");
    }

    /**
     * Obtain the byte length of the encoded sequence.
     * @return the byte length
     */
    public static int getEncodedLength() {
        return GordianPasswordLockASN1.getEncodedLength(GordianCoreKeySet.getEncryptionLength(GordianParameters.SECRET_LEN.getByteLength() << 1));
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof GordianKeyPairLockImpl)) {
            return false;
        }

        /* Access the target field */
        final GordianKeyPairLockImpl myThat = (GordianKeyPairLockImpl) pThat;

        /* Check differences */
        return theKeySet.equals(myThat.getLockedObject())
                && Arrays.equals(theLockBytes, myThat.getLockBytes());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(theKeySet)
                + Arrays.hashCode(theLockBytes);
    }
}
