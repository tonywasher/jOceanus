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
package net.sourceforge.joceanus.gordianknot.impl.core.lock;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianEdwardsElliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeyPairLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreement;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementFactory;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementParams;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataConverter;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianKeySetData;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementMessageASN1;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

import java.util.Arrays;
import java.util.Objects;

/**
 * KeyPair Lock implementation.
 */
public class GordianKeyPairLockImpl
        implements GordianKeyPairLock {
    /**
     * Server X500Name.
     */
    static final X500Name SERVER = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Server").build();

    /**
     * The keySet.
     */
    private final GordianCoreKeySet theKeySet;

    /**
     * The lockASN1.
     */
    private final GordianKeyPairLockASN1 theLockASN1;

    /**
     * The lockBytes.
     */
    private final byte[] theLockBytes;

    /**
     * The keyPair.
     */
    private final GordianKeyPair theKeyPair;

    /**
     * Locking constructor.
     *
     * @param pLockingFactory the locking factory
     * @param pLockSpec       the passwordLockSpec
     * @param pKeyPair        the locking keyPair
     * @param pPassword       the password
     * @throws GordianException on error
     */
    public GordianKeyPairLockImpl(final GordianBaseFactory pLockingFactory,
                                  final GordianPasswordLockSpec pLockSpec,
                                  final GordianKeyPair pKeyPair,
                                  final char[] pPassword) throws GordianException {
        /* Protect from exceptions */
        byte[] myPassword = null;
        try {
            /* Create the agreement and derive the factory */
            final GordianAsyncFactory myAsyncFactory = pLockingFactory.getAsyncFactory();
            final GordianXAgreementFactory myAgreeFactory = myAsyncFactory.getXAgreementFactory();
            final GordianCertificate myCert = myAgreeFactory.newMiniCertificate(SERVER, pKeyPair,
                    new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
            final GordianAgreementSpec mySpec = getAgreementSpec(pKeyPair.getKeyPairSpec());
            final GordianXAgreementParams myParams = myAgreeFactory.newAgreementParams(mySpec, GordianFactoryType.BC)
                    .setServerCertificate(myCert);
            final GordianXAgreement myAgreement = myAgreeFactory.createAgreement(myParams);
            final byte[] myClientHello = myAgreement.nextMessage();
            final GordianBaseFactory myFactory = (GordianBaseFactory) myAgreement.getFactoryResult();

            /* Create a recipe */
            final GordianPasswordLockRecipe myRecipe = new GordianPasswordLockRecipe(pLockingFactory, pLockSpec);

            /* Generate the keySet */
            myPassword = GordianDataConverter.charsToByteArray(pPassword);
            theKeySet = myRecipe.processPassword(myFactory, myPassword);

            /* Create lockBytes */
            final GordianPasswordLockASN1 myLock = myRecipe.buildLockASN1(myPassword.length, null);
            theLockASN1 = new GordianKeyPairLockASN1(GordianXCoreAgreementMessageASN1.getInstance(myClientHello), myLock);
            theLockBytes = theLockASN1.getEncodedBytes();
            theKeyPair = pKeyPair;

        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    /**
     * UnLocking constructor.
     *
     * @param pLockingFactory the locking factory
     * @param pLockBytes      the lockBytes
     * @param pKeyPair        the keyPair
     * @param pPassword       the password
     * @throws GordianException on error
     */
    public GordianKeyPairLockImpl(final GordianBaseFactory pLockingFactory,
                                  final byte[] pLockBytes,
                                  final GordianKeyPair pKeyPair,
                                  final char[] pPassword) throws GordianException {
        this(pLockingFactory, GordianKeyPairLockASN1.getInstance(pLockBytes), pLockBytes, pKeyPair, pPassword);
    }

    /**
     * UnLocking constructor.
     *
     * @param pLockingFactory the locking factory
     * @param pLockASN1       the lockASN1
     * @param pKeyPair        the keyPair
     * @param pPassword       the password
     * @throws GordianException on error
     */
    public GordianKeyPairLockImpl(final GordianBaseFactory pLockingFactory,
                                  final GordianKeyPairLockASN1 pLockASN1,
                                  final GordianKeyPair pKeyPair,
                                  final char[] pPassword) throws GordianException {
        this(pLockingFactory, pLockASN1, pLockASN1.getEncodedBytes(), pKeyPair, pPassword);
    }

    /**
     * UnLocking constructor.
     *
     * @param pLockingFactory the locking factory
     * @param pLockASN1       the lockASN1
     * @param pLockBytes      the lockBytes
     * @param pKeyPair        the keyPair
     * @param pPassword       the password
     * @throws GordianException on error
     */
    public GordianKeyPairLockImpl(final GordianBaseFactory pLockingFactory,
                                  final GordianKeyPairLockASN1 pLockASN1,
                                  final byte[] pLockBytes,
                                  final GordianKeyPair pKeyPair,
                                  final char[] pPassword) throws GordianException {
        /* Protect from exceptions */
        byte[] myPassword = null;
        try {
            /* Store the Lock */
            theLockBytes = pLockBytes;
            theLockASN1 = pLockASN1;
            theKeyPair = pKeyPair;

            /* Resolve the agreement */
            final GordianAsyncFactory myAsyncFactory = pLockingFactory.getAsyncFactory();
            final GordianXAgreementFactory myAgreeFactory = myAsyncFactory.getXAgreementFactory();
            final byte[] myClientHello = theLockASN1.getAgreement().getEncodedBytes();
            final GordianXAgreement myAgreement = myAgreeFactory.parseAgreementMessage(myClientHello);
            final GordianCertificate myCert = myAgreeFactory.newMiniCertificate(SERVER, pKeyPair,
                    new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
            final GordianXAgreementParams myParams = myAgreement.getAgreementParams().setServerCertificate(myCert);
            myAgreement.updateParams(myParams);
            final GordianBaseFactory myFactory = (GordianBaseFactory) myAgreement.getFactoryResult();

            /* Resolve the recipe */
            myPassword = GordianDataConverter.charsToByteArray(pPassword);
            final GordianPasswordLockRecipe myRecipe = new GordianPasswordLockRecipe(pLockingFactory, myPassword.length, theLockASN1.getPasswordLock());

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
    public GordianKeyPairLockASN1 getLockASN1() {
        return theLockASN1;
    }

    @Override
    public byte[] getLockBytes() {
        return theLockBytes;
    }

    @Override
    public GordianKeyPair getKeyPair() {
        return theKeyPair;
    }

    /**
     * Obtain AgreementSpec for asymKeySpec.
     *
     * @param pKeySpec the keySpec
     * @return the agreementSpec
     * @throws GordianException on error
     */
    private static GordianAgreementSpec getAgreementSpec(final GordianKeyPairSpec pKeySpec) throws GordianException {
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
     *
     * @return the byte length
     */
    public static int getEncodedLength() {
        return GordianPasswordLockASN1.getEncodedLength(GordianKeySetData.getEncryptionLength(GordianParameters.SEED_LEN.getByteLength()));
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
