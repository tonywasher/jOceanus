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
package net.sourceforge.joceanus.jgordianknot.impl.core.encrypt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianKeyPairEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianKeyPairSetEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianSM2EncryptionSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianCoreKeyPairSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSet encryptor.
 */
public class GordianCoreKeyPairSetEncryptor
    implements GordianKeyPairSetEncryptor {
    /**
     * The keyPairSetSpec.
     */
    private final GordianKeyPairSetSpec theSpec;

    /**
     * The signers.
     */
    private final List<GordianKeyPairEncryptor> theEncryptors;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @throws OceanusException on error
     */
    GordianCoreKeyPairSetEncryptor(final GordianKeyPairFactory pFactory,
                                   final GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException {
        /* Store parameters */
        theSpec = pKeyPairSetSpec;
        theEncryptors = new ArrayList<>();

        /* Create the signers */
        final GordianEncryptorFactory myFactory = pFactory.getEncryptorFactory();
        final Iterator<GordianKeyPairSpec> myIterator = theSpec.iterator();
        while (myIterator.hasNext()) {
            final GordianKeyPairSpec mySpec = myIterator.next();
            final GordianEncryptorSpec myEncSpec = defaultForKey(mySpec);
            theEncryptors.add(myFactory.createKeyPairEncryptor(myEncSpec));
        }
    }

    @Override
    public GordianKeyPairSetSpec getEncryptorSpec() {
        return theSpec;
    }

    @Override
    public void initForEncrypt(final GordianKeyPairSet pKeyPairSet) throws OceanusException {
        /* Check the keyPairSet */
        checkKeySpec(pKeyPairSet);

        /* Initialise the signers */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pKeyPairSet;
        final Iterator<GordianKeyPair> myIterator = mySet.iterator();
        for (GordianKeyPairEncryptor myEncryptor : theEncryptors) {
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
        for (GordianKeyPairEncryptor myEncryptor : theEncryptors) {
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
        for (GordianKeyPairEncryptor myEncryptor : theEncryptors) {
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
        final ListIterator<GordianKeyPairEncryptor> myIterator = theEncryptors.listIterator(theEncryptors.size());
        while (myIterator.hasPrevious()) {
            /* Encrypt using this encryptor */
            final GordianKeyPairEncryptor myEncryptor = myIterator.previous();
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
    public static GordianEncryptorSpec defaultForKey(final GordianKeyPairSpec pKeySpec) {
        final GordianDigestSpec myDigest = GordianDigestSpec.sha2(GordianLength.LEN_512);
        switch (pKeySpec.getKeyPairType()) {
            case RSA:
                return GordianEncryptorSpec.rsa(myDigest);
            case ELGAMAL:
                return GordianEncryptorSpec.elGamal(myDigest);
            case SM2:
                return GordianEncryptorSpec.sm2(GordianSM2EncryptionSpec.c1c2c3(myDigest));
            default:
                return null;
        }
    }
}
