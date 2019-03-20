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
package net.sourceforge.joceanus.jgordianknot.junit.regression;

import java.security.spec.KeySpec;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianGenerator;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreDocument;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyStore Tests.
 */
public class KeyStoreTest {
    /**
     * Default password.
     */
    private static final char[] DEF_PASSWORD = "SimplePassword".toCharArray();

    /**
     * A factory.
     */
    private static GordianFactory FACTORY;

    /**
     * Create the factory.
     * @throws OceanusException on error
     */
    @BeforeAll
    public static void createFactory() throws OceanusException {
        /* Create the factory */
        FACTORY = GordianGenerator.createFactory(new GordianParameters(GordianFactoryType.BC));

    }

    /**
     * Simple creation test.
     * @throws OceanusException on error
     */
    @Test
    public void runSimpleTest() throws OceanusException {
        /* Access keyStoreFactory and create a keyStore */
        final GordianKeyStoreFactory myFactory = FACTORY.getAsymmetricFactory().getKeyStoreFactory();
        final GordianKeyStore myStore = myFactory.createKeyStore();

        /* Create a root certificate */
        final GordianAsymKeySpec mySpec = GordianAsymKeySpec.ec(GordianDSAElliptic.SECT571K1);
        final X500Name myRootName = buildX500Name("Root Certificate");
        final GordianKeyStorePair myRoot = myStore.createRootKeyPair(mySpec, myRootName, "RootCert", DEF_PASSWORD);

        /* Create an intermediate */
        final X500Name myInterName = buildX500Name("Intermediate Certificate");
        GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        final GordianKeyStorePair myIntermediate = myStore.createKeyPair(mySpec, myInterName, myUsage, myRoot, "InterCert", DEF_PASSWORD);

        /* Create a signature keyPair */
        final X500Name mySignName = buildX500Name("Signing Certificate");
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePair mySigner = myStore.createKeyPair(mySpec, mySignName, myUsage, myIntermediate, "SigningCert", DEF_PASSWORD);

        /* Create an agreement keyPair */
        final X500Name myAgreeName = buildX500Name("Agreement Certificate");
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        final GordianKeyStorePair myAgree = myStore.createKeyPair(mySpec, myAgreeName, myUsage, myIntermediate, "AgreementCert", DEF_PASSWORD);

        /* Create a signature keyPair */
        final X500Name myEncryptName = buildX500Name("Encryption Certificate");
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.DATAENCRYPT);
        final GordianKeyStorePair myEncrypt = myStore.createKeyPair(mySpec, myEncryptName, myUsage, myIntermediate, "EncryptCert", DEF_PASSWORD);

        /* Create a keySetHash */
        final GordianKeySetFactory mySetFactory = FACTORY.getKeySetFactory();
        final GordianKeySetHash myHash = mySetFactory.generateKeySetHash(DEF_PASSWORD);
        myStore.setKeySetHash("HashDef", myHash);
        final GordianKeyStoreHash myHashRec = myStore.getKeySetHash("HashDef", DEF_PASSWORD);
        Assertions.assertEquals(myHash, myHashRec.getKeySetHash());

        /* Record the keySet */
        final GordianKeySet myKeySet = mySetFactory.generateKeySet();
        myStore.setKeySet("KeySet", myKeySet, DEF_PASSWORD);
        final GordianKeyStoreSet mySetRec = myStore.getKeySet("KeySet", DEF_PASSWORD);
        Assertions.assertEquals(myKeySet, mySetRec.getKeySet());

        /* Create a symKey */
        final GordianCipherFactory myCipherFactory = FACTORY.getCipherFactory();
        final GordianKeyGenerator<GordianSymKeySpec> mySymGenerator = myCipherFactory.getKeyGenerator(GordianSymKeySpec.aes());
        final GordianKey<GordianSymKeySpec> mySymKey = mySymGenerator.generateKey();
        myStore.setKey("symKey", mySymKey, DEF_PASSWORD);
        final GordianKeyStoreKey<?> mySymKeyRec = myStore.getKey("symKey", DEF_PASSWORD);
        Assertions.assertEquals(mySymKey, mySymKeyRec.getKey());

        /* Create a streamKey */
        final GordianKeyGenerator<GordianStreamKeyType> myStreamGenerator = myCipherFactory.getKeyGenerator(GordianStreamKeyType.HC);
        final GordianKey<GordianStreamKeyType> myStreamKey = myStreamGenerator.generateKey();
        myStore.setKey("streamKey", myStreamKey, DEF_PASSWORD);
        final GordianKeyStoreKey<?> myStreamKeyRec = myStore.getKey("streamKey", DEF_PASSWORD);
        Assertions.assertEquals(myStreamKey, myStreamKeyRec.getKey());

        /* Create a macKey */
        final GordianMacFactory myMacFactory = FACTORY.getMacFactory();
        final GordianKeyGenerator<GordianMacSpec> myMacGenerator = myMacFactory.getKeyGenerator(GordianMacSpec.vmpcMac());
        final GordianKey<GordianMacSpec> myMacKey = myMacGenerator.generateKey();
        myStore.setKey("macKey", myMacKey, DEF_PASSWORD);
        final GordianKeyStoreKey<?> myMacKeyRec = myStore.getKey("macKey", DEF_PASSWORD);
        Assertions.assertEquals(myMacKey, myMacKeyRec.getKey());

        /* Create keyStore documents */
        final GordianKeyStoreDocument myDoc1 = new GordianKeyStoreDocument(myStore);
        final GordianKeyStoreDocument myDoc2 = new GordianKeyStoreDocument(FACTORY, myDoc1.getDocument());
        Assertions.assertEquals(myStore, myDoc2.getKeyStore());

        /* delete the entries */
        myStore.deleteEntry("RootCert");
        myStore.deleteEntry("InterCert");
        myStore.deleteEntry("SigningCert");
        myStore.deleteEntry("AgreementCert");
        myStore.deleteEntry("EncryptCert");
        myStore.deleteEntry("HashDef");
        myStore.deleteEntry("KeySet");
        myStore.deleteEntry("symKey");
        myStore.deleteEntry("streamKey");
        myStore.deleteEntry("macKey");

        int mySize = myStore.size();
    }

    /**
     * Build X500Name.
     * @param pName the CN value.
     */
    @Test
    public X500Name buildX500Name(final String pName) throws OceanusException {
        /* Build the name */
        X500NameBuilder myBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        myBuilder.addRDN(BCStyle.CN, pName);
        myBuilder.addRDN(BCStyle.O, "jOceanus development");
        myBuilder.addRDN(BCStyle.L, "Romsey");
        myBuilder.addRDN(BCStyle.ST, "HANTS");
        myBuilder.addRDN(BCStyle.C, "UK");
        return myBuilder.build();
    }
}
