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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypairset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianSM2EncryptionSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSet encryptor.
 */
public class GordianKeyPairSetEncryptor {
    /**
     * The keyPairSetSpec.
     */
    private final GordianKeyPairSetSpec theSpec;

    /**
     * The signers.
     */
    private final List<GordianEncryptor> theEncryptors;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @throws OceanusException on error
     */
    GordianKeyPairSetEncryptor(final GordianAsymFactory pFactory,
                               final GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException {
        /* Store parameters */
        theSpec = pKeyPairSetSpec;
        theEncryptors = new ArrayList<>();

        /* Create the signers */
        final GordianEncryptorFactory myFactory = pFactory.getEncryptorFactory();
        final Iterator<GordianAsymKeySpec> myIterator = theSpec.iterator();
        while (myIterator.hasNext()) {
            final GordianAsymKeySpec mySpec = myIterator.next();
            final GordianEncryptorSpec myEncSpec = defaultForKey(mySpec);
            theEncryptors.add(myFactory.createEncryptor(myEncSpec));
        }
    }

    /**
     * Obtain the keyPairSetSpec.
     * @return the Spec
     */
    public GordianKeyPairSetSpec getSpec() {
        return theSpec;
    }

    /**
     * Initialise for encryption.
     * @param pKeyPairSet the keyPairSet
     * @throws OceanusException on error
     */
    public void initForEncrypt(final GordianKeyPairSet pKeyPairSet) throws OceanusException {
        /* Check the keyPairSet */
        checkKeySpec(pKeyPairSet);

        /* Initialise the signers */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pKeyPairSet;
        final Iterator<GordianKeyPair> myIterator = mySet.iterator();
        for (GordianEncryptor myEncryptor : theEncryptors) {
            final GordianKeyPair myPair = myIterator.next();
            myEncryptor.initForEncrypt(myPair);
        }
    }

    /**
     * Initialise for decryption.
     * @param pKeyPairSet the keyPairSet
     * @throws OceanusException on error
     */
    public void initForDecrypt(final GordianKeyPairSet pKeyPairSet) throws OceanusException {
        /* Check the keyPairSet */
        checkKeySpec(pKeyPairSet);

        /* Initialise the signers */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pKeyPairSet;
        final Iterator<GordianKeyPair> myIterator = mySet.iterator();
        for (GordianEncryptor myEncryptor : theEncryptors) {
            final GordianKeyPair myPair = myIterator.next();
            myEncryptor.initForDecrypt(myPair);
        }
    }

    /**
     * check the keyPairSet.
     * @param pKeyPairSet the keyPairSet
     * @throws OceanusException on error
     */
    private void checkKeySpec(final GordianKeyPairSet pKeyPairSet) throws OceanusException {
        if (!theSpec.equals(pKeyPairSet.getKeyPairSetSpec())) {
            throw new GordianLogicException("Invalid keyPairSet for encryptor");
        }
    }

    /**
     * Encrypt the bytes.
     * @param pBytes the bytes to encrypt
     * @return the encrypted bytes
     * @throws OceanusException on error
     */
    public byte[] encrypt(final byte[] pBytes) throws OceanusException {
        /* Loop through the encryptors */
        byte[] myData = pBytes;
        for (GordianEncryptor myEncryptor : theEncryptors) {
            /* Encrypt using this encryptor */
            myData = myEncryptor.encrypt(myData);
        }

        /* Return the encrypted data */
        return myData;
    }

    /**
     * Decrypt the encrypted bytes.
     * @param pEncrypted the encrypted bytes
     * @return the decrypted bytes
     * @throws OceanusException on error
     */
    public byte[] decrypt(final byte[] pEncrypted) throws OceanusException {
        /* Loop through the encryptors */
        byte[] myData = pEncrypted;
        final ListIterator<GordianEncryptor> myIterator = theEncryptors.listIterator(theEncryptors.size());
        while (myIterator.hasPrevious()) {
            /* Encrypt using this encryptor */
            final GordianEncryptor myEncryptor = myIterator.previous();
            myData = myEncryptor.decrypt(myData);
        }

        /* Return the decrypted data */
        return myData;
    }

    /**
     * Create default encryptorSpec for key.
     * @param pKeySpec the keySpec
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec defaultForKey(final GordianAsymKeySpec pKeySpec) {
        switch (pKeySpec.getKeyType()) {
            case RSA:
                return GordianEncryptorSpec.rsa(GordianDigestSpec.sha3(GordianLength.LEN_512));
            case ELGAMAL:
                return GordianEncryptorSpec.elGamal(GordianDigestSpec.sha3(GordianLength.LEN_512));
            case SM2:
                return GordianEncryptorSpec.sm2(GordianSM2EncryptionSpec.c1c2c3(GordianDigestSpec.sha3(GordianLength.LEN_512)));
            default:
                return null;
        }
    }
}
