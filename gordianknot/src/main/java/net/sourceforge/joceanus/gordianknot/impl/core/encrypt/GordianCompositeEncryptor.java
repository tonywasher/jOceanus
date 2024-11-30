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
package net.sourceforge.joceanus.gordianknot.impl.core.encrypt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptor;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCompositeKeyPair;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * Composite encryptor.
 */
public class GordianCompositeEncryptor
        implements GordianEncryptor {
    /**
     * The factory.
     */
    private final GordianEncryptorFactory theFactory;

    /**
     * The keyPairSpec.
     */
    private final GordianEncryptorSpec theSpec;

    /**
     * The encryptors.
     */
    private final List<GordianEncryptor> theEncryptors;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the encryptorSpec
     * @throws OceanusException on error
     */
    public GordianCompositeEncryptor(final GordianFactory pFactory,
                                     final GordianEncryptorSpec pSpec) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory.getKeyPairFactory().getEncryptorFactory();
        theSpec = pSpec;
        theEncryptors = new ArrayList<>();

        /* Create the signers */
        final Iterator<GordianEncryptorSpec> myIterator = theSpec.encryptorSpecIterator();
        while (myIterator.hasNext()) {
            final GordianEncryptorSpec mySpec = myIterator.next();
            theEncryptors.add(theFactory.createEncryptor(mySpec));
        }
    }

    @Override
    public GordianEncryptorSpec getEncryptorSpec() {
        return theSpec;
    }

    @Override
    public void initForEncrypt(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check the keyPair */
        checkKeySpec(pKeyPair);

        /* Initialise the encryptors */
        final GordianCompositeKeyPair myCompositePair = (GordianCompositeKeyPair) pKeyPair;
        final Iterator<GordianKeyPair> myIterator = myCompositePair.iterator();
        for (GordianEncryptor myEncryptor : theEncryptors) {
            final GordianKeyPair myPair = myIterator.next();
            myEncryptor.initForEncrypt(myPair);
        }
    }

    @Override
    public void initForDecrypt(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check the keyPair */
        checkKeySpec(pKeyPair);

        /* Initialise the signers */
        final GordianCompositeKeyPair myCompositePair = (GordianCompositeKeyPair) pKeyPair;
        final Iterator<GordianKeyPair> myIterator = myCompositePair.iterator();
        for (GordianEncryptor myEncryptor : theEncryptors) {
            final GordianKeyPair myPair = myIterator.next();
            myEncryptor.initForDecrypt(myPair);
        }
    }

    /**
     * check the keyPair.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    private void checkKeySpec(final GordianKeyPair pKeyPair) throws OceanusException {
        if (!theFactory.validEncryptorSpecForKeyPairSpec(pKeyPair.getKeyPairSpec(), theSpec)) {
            throw new GordianLogicException("Invalid keyPair for encryptor");
        }
    }

    @Override
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

    @Override
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
}
