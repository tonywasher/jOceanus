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
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreWrapper;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetRecipe.GordianKeySetParameters;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * A full set of symmetric keys, subject to the relevant predicate.
 */
public final class GordianCoreKeySet
    implements GordianKeySet {
    /**
     * Initialisation Vector size.
     */
    private static final GordianLength BLOCKLEN = GordianLength.LEN_128;

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The keySetSpec.
     */
    private final GordianKeySetSpec theSpec;

    /**
     * Map of KeySpec to symKey.
     */
    private final Map<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> theSymKeyMap;

    /**
     * The underlying Cipher.
     */
    private final GordianMultiCipher theCipher;

    /**
     * is the keySet in AEAD mode?
     */
    private boolean isAEAD;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec the keySetSpec
     */
    GordianCoreKeySet(final GordianCoreFactory pFactory,
                      final GordianKeySetSpec pSpec) {
        /* Store parameters */
        theFactory = pFactory;
        theSpec = pSpec;

        /* Create maps */
        theSymKeyMap = new HashMap<>();

        /* Create the cipher */
        theCipher = new GordianMultiCipher(this);
    }

    /**
     * Constructor.
     *
     * @param pSource the source
     */
    private GordianCoreKeySet(final GordianCoreKeySet pSource) {
        /* Copy factory */
        theFactory = pSource.getFactory();
        theSpec = pSource.getKeySetSpec();

        /* Copy the symKeyMap */
        theSymKeyMap = new HashMap<>(pSource.getSymKeyMap());

        /* Create the cipher */
        theCipher = new GordianMultiCipher(this);

        /* Copy AEAD mode */
        isAEAD = pSource.isAEAD();
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    public GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianKeySetSpec getKeySetSpec() {
        return theSpec;
    }

    /**
     * Obtain the symKeySet.
     *
     * @return the keySet
     */
    Map<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> getSymKeyMap() {
        return theSymKeyMap;
    }

    @Override
    public GordianCoreKeySet cloneIt() {
        return new GordianCoreKeySet(this);
    }

    @Override
    public boolean isAEAD() {
        return isAEAD;
    }

    @Override
    public void setAEAD(final boolean pAEAD) {
        isAEAD = pAEAD;
    }

    @Override
    public int getEncryptionLength(final int pDataLength) {
        return getEncryptionLength(pDataLength, isAEAD);
    }

    /**
     * Encryption length.
     *
     * @param pDataLength the length of data to be encrypted
     * @param pAEAD true/false is AEAD in use?
     * @return the length of encrypted data
     */
    public static int getEncryptionLength(final int pDataLength,
                                          final boolean pAEAD) {
        final int iBlocks = 1 + (pDataLength / BLOCKLEN.getByteLength());
        return iBlocks * BLOCKLEN.getByteLength()
                + getEncryptionOverhead(pAEAD);
    }

    /**
     * Encryption overhead.
     * @param pAEAD true/false is AEAD in use?
     * @return the encryption overhead
     */
    private static int getEncryptionOverhead(final boolean pAEAD) {
        return GordianKeySetRecipe.SALTLEN
                + GordianKeySetRecipe.RECIPELEN
                + (pAEAD ? GordianKeySetRecipe.MACLEN : 0);
    }

    @Override
    public int getKeyWrapLength(final GordianLength pKeyLen) {
        return getDataWrapLength(pKeyLen.getByteLength());
    }

    @Override
    public int getDataWrapLength(final int pDataLength) {
        return getDataWrapLength(pDataLength, theSpec.getCipherSteps());
    }

    /**
     * Obtain wrapped size of a byte array of the given length.
     * @param pDataLength the length of the byte array
     * @param pNumSteps the numbert of cipher steps
     * @return the wrapped length
     */
    public static int getDataWrapLength(final int pDataLength,
                                        final int pNumSteps) {
        return GordianCoreWrapper.getKeyWrapLength(pDataLength, BLOCKLEN)
                + getEncryptionOverhead(false)
                + (pNumSteps - 1)
                * GordianCoreWrapper.getKeyWrapExpansion(BLOCKLEN);
    }

    /**
     * Obtain the keySet wrap length.
     * @param pKeyLen the keyLength.
     * @param pNumSteps the numbert of cipher steps
     * @return the wrapped length
     */
    public static int getKeySetWrapLength(final GordianLength pKeyLen,
                                          final int pNumSteps) {
        /* Count the number of KeySetSymTypes for 256 bit keys */
        int myCount = 0;
        for (GordianSymKeyType myType : GordianSymKeyType.values()) {
            if (GordianCoreCipherFactory.validStdBlockSymKeyTypeForKeyLength(myType, pKeyLen)) {
                myCount++;
            }
        }

        /* Determine the length of the encoded keySet prior to wrapping */
        final int myWrapLength = getDataWrapLength(pKeyLen.getByteLength(), pNumSteps);
        final int myEncodedLength = GordianKeySetASN1.getEncodedLength(myWrapLength, myCount);

        /* Determine the wrapped length of the data */
        return getDataWrapLength(myEncodedLength, pNumSteps);
    }

    @Override
    public int getPrivateKeyWrapLength(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Determine and check the keySpec */
        final GordianAsymFactory myAsym = theFactory.getAsymmetricFactory();
        final GordianCoreKeyPairGenerator myGenerator = (GordianCoreKeyPairGenerator) myAsym.getKeyPairGenerator(pKeyPair.getKeySpec());
        final PKCS8EncodedKeySpec myPrivateKey = myGenerator.getPKCS8Encoding(pKeyPair);
        return getDataWrapLength(myPrivateKey.getEncoded().length);
    }

    @Override
    public int getKeySetWrapLength() throws OceanusException {
        /* Obtain the count of valid symKeyTypes */
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        final Predicate<GordianSymKeyType> myPredicate = myFactory.supportedKeySetSymKeyTypes(theSpec.getKeyLength());
        final int myCount = (int) Arrays.stream(GordianSymKeyType.values()).filter(myPredicate).count();

        /* Determine the size of the encoded ASN1 keySet */
        final int mySize = GordianKeySetASN1.getEncodedLength(getKeyWrapLength(theSpec.getKeyLength()), myCount);
        return getDataWrapLength(mySize);
    }

    @Override
    public byte[] encryptBytes(final byte[] pBytes) throws OceanusException {
        /* Generate set of keys and initialisation vector */
        final GordianKeySetRecipe myRecipe =  GordianKeySetRecipe.newRecipe(theFactory, theSpec, isAEAD);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Encrypt the bytes */
        theCipher.initCiphers(myParams, true);
        final byte[] myBytes = theCipher.finish(pBytes);

        /* Calculate the Mac if we are in AEAD mode */
        if (isAEAD) {
            theCipher.calculateMac(myParams);
        }

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(myBytes);
    }

    @Override
    public byte[] decryptBytes(final byte[] pBytes) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.parseRecipe(theFactory, theSpec, pBytes, isAEAD);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Decrypt the bytes and return them */
        theCipher.initCiphers(myParams, false);
        final byte[] myResult = theCipher.finish(myBytes);

        /* Validate the Mac if we are in AEAD mode */
        if (isAEAD) {
            theCipher.validateMac(myParams);
        }

        /* Return the result */
        return myResult;
    }

    @Override
    public byte[] secureKey(final GordianKey<?> pKeyToSecure) throws OceanusException {
        /* Generate set of keys */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.newRecipe(theFactory, theSpec, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* secure the key */
        final byte[] myBytes = theCipher.secureKey(myParams, pKeyToSecure);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(myBytes);
    }

    @Override
    public <T extends GordianKeySpec> GordianKey<T> deriveKey(final byte[] pSecuredKey,
                                                              final T pKeyType) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.parseRecipe(theFactory, theSpec, pSecuredKey, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Unwrap the key and return it */
        return theCipher.deriveKey(myParams, myBytes, pKeyType);
    }

    @Override
    public byte[] secureBytes(final byte[] pBytesToSecure) throws OceanusException {
        /* Generate set of keys */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.newRecipe(theFactory, theSpec, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* secure the key */
        final byte[] myBytes = theCipher.secureBytes(myParams, pBytesToSecure);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(myBytes);
    }

    @Override
    public byte[] deriveBytes(final byte[] pSecuredBytes) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.parseRecipe(theFactory, theSpec, pSecuredBytes, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Unwrap the bytes and return them */
        return theCipher.deriveBytes(myParams, myBytes);
   }

    @Override
    public byte[] securePrivateKey(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Generate set of keys */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.newRecipe(theFactory, theSpec, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Wrap the key */
        final byte[] myBytes = theCipher.securePrivateKey(myParams, pKeyPair);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(myBytes);
    }

    @Override
    public GordianKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKeySpec,
                                        final byte[] pSecuredPrivateKey) throws OceanusException {
        /* Access the PKCS8Encoding */
        final PKCS8EncodedKeySpec myPrivate = derivePrivateKeySpec(pSecuredPrivateKey);

        /* Determine and check the keySpec */
        final GordianAsymFactory myAsym = theFactory.getAsymmetricFactory();
        final GordianAsymKeySpec myKeySpec = myAsym.determineKeySpec(pPublicKeySpec);
        if (!myKeySpec.equals(myAsym.determineKeySpec(myPrivate))) {
            throw new GordianLogicException("Mismatch on keySpecs");
        }

        /* Derive the keyPair */
        final GordianCoreKeyPairGenerator myGenerator = (GordianCoreKeyPairGenerator) myAsym.getKeyPairGenerator(myKeySpec);
        return myGenerator.deriveKeyPair(pPublicKeySpec, myPrivate);
    }

    /**
     * derive privateKeySpec.
     * @param pSecuredPrivateKey the secured private keySpec
     * @return the private keySpec
     * @throws OceanusException on error
     */
    private PKCS8EncodedKeySpec derivePrivateKeySpec(final byte[] pSecuredPrivateKey) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.parseRecipe(theFactory, theSpec, pSecuredPrivateKey, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Unwrap the key and return it */
        return theCipher.derivePrivateKeySpec(myParams, myBytes);
    }

    @Override
    public byte[] secureKeySet(final GordianKeySet pKeySetToSecure) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Generate set of keys */
            final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.newRecipe(theFactory, theSpec, false);
            final GordianKeySetParameters myParams = myRecipe.getParameters();

            /* Encode the keySet */
            final GordianKeySetASN1 myEncoded = new GordianKeySetASN1((GordianCoreKeySet) pKeySetToSecure, this);
            final byte[] myBytesToSecure = myEncoded.toASN1Primitive().getEncoded();

            /* secure the key */
            final byte[] myBytes = theCipher.secureBytes(myParams, myBytesToSecure);

            /* Package and return the encrypted bytes */
            return myRecipe.buildExternal(myBytes);

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to secure KeySet", e);
        }
    }

    @Override
    public GordianCoreKeySet deriveKeySet(final byte[] pSecuredKeySet) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.parseRecipe(theFactory, theSpec, pSecuredKeySet, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Unwrap the bytes resolve them */
        final byte[] mySecuredBytes = theCipher.deriveBytes(myParams, myBytes);
        final GordianKeySetASN1 myEncoded = GordianKeySetASN1.getInstance(mySecuredBytes);

        /* Build the keySet and return it */
        return myEncoded.buildKeySet((GordianCoreKeySetFactory) theFactory.getKeySetFactory(), this);
    }

    /**
     * Declare symmetricKey.
     * @param pKey the key
     * @throws OceanusException on error
     */
    void declareSymKey(final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        declareKey(pKey, myFactory.supportedKeySetSymKeySpecs(theSpec.getKeyLength()), theSymKeyMap);
    }

    /**
     * Declare Key.
     * @param <T> the keyType
     * @param pKey the key
     * @param pPredicate the predicate
     * @param pKeyMap the keyMap
     * @throws OceanusException on error
     */
    private static <T extends GordianKeySpec> void declareKey(final GordianKey<T> pKey,
                                                              final Predicate<T> pPredicate,
                                                              final Map<T, GordianKey<T>> pKeyMap) throws OceanusException {
        /* Access keyType */
        final T myKeyType = pKey.getKeyType();

        /* Check that the key is supported */
        if (!pPredicate.test(myKeyType)) {
            throw new GordianDataException("invalid keyType");
        }

        /* Look for existing key of this type */
        final GordianKey<T> myExisting = pKeyMap.get(myKeyType);
        if (myExisting != null) {
            /* Must be same as existing key */
            if (!myExisting.equals(pKey)) {
                throw new GordianDataException("keyType already declared");
            }

            /* else new key */
        } else {
            /* Store into map */
            pKeyMap.put(myKeyType, pKey);
        }
    }

    /**
     * Build key set from random.
     * @throws OceanusException on error
     */
    void buildFromRandom() throws OceanusException {
        /* Loop through the symmetricKeys values */
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        final GordianLength myKeyLen = theSpec.getKeyLength();
        final Predicate<GordianSymKeyType> mySymPredicate = myFactory.supportedKeySetSymKeyTypes(myKeyLen);
        for (final GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* If this is supported for a keySet */
            if (mySymPredicate.test(myType)) {
                /* Generate the key and add to map */
                final GordianSymKeySpec mySpec = new GordianSymKeySpec(myType, myKeyLen);
                final GordianCipherFactory myCipherFactory = theFactory.getCipherFactory();
                final GordianKeyGenerator<GordianSymKeySpec> myGenerator = myCipherFactory.getKeyGenerator(mySpec);
                theSymKeyMap.put(mySpec, myGenerator.generateKey());
            }
        }
    }

    /**
     * Build key set from secret.
     * @param pSecret the secret.
     * @param pInitVector the initialisation vector.
     * @throws OceanusException on error
     */
    public void buildFromSecret(final byte[] pSecret,
                                final byte[] pInitVector) throws OceanusException {
        /* Loop through the symmetricKeys values */
        final GordianKeySetFactory myKeySetFactory = theFactory.getKeySetFactory();
        final GordianLength myKeyLen = theSpec.getKeyLength();
        final Predicate<GordianSymKeyType> mySymPredicate = myKeySetFactory.supportedKeySetSymKeyTypes(myKeyLen);
        for (final GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* If this is supported for a keySet */
            if (mySymPredicate.test(myType)) {
                /* Generate the key and add to map */
                final GordianSymKeySpec mySpec = new GordianSymKeySpec(myType, myKeyLen);
                theSymKeyMap.put(mySpec, generateKey(mySpec, pSecret, pInitVector));
            }
        }
    }

    /**
     * Generate key for a Key Type from the secret and initVector.
     * @param <T> the class of key
     * @param pKeyType the keyType
     * @param pSecret the derived Secret
     * @param pInitVector the initialisation vector.
     * @return the generated key
     * @throws OceanusException on error
     */
    private <T extends GordianKeySpec> GordianKey<T> generateKey(final T pKeyType,
                                                                 final byte[] pSecret,
                                                                 final byte[] pInitVector) throws OceanusException {
        /* Generate a new Secret Key from the secret */
        final GordianCipherFactory myFactory = theFactory.getCipherFactory();
        final GordianCoreKeyGenerator<T> myGenerator = (GordianCoreKeyGenerator<T>) myFactory.getKeyGenerator(pKeyType);
        return myGenerator.generateKeyFromSecret(pSecret, pInitVector);
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
        if (!(pThat instanceof GordianCoreKeySet)) {
            return false;
        }

        /* Access the target field */
        final GordianCoreKeySet myThat = (GordianCoreKeySet) pThat;

        /* Check differences */
        return theFactory.equals(myThat.getFactory())
                && theSpec.equals(myThat.getKeySetSpec())
                && theSymKeyMap.equals(myThat.getSymKeyMap());
    }

    @Override
    public int hashCode() {
        return GordianParameters.HASH_PRIME * theFactory.hashCode()
                + theSpec.hashCode()
                + theSymKeyMap.hashCode();
    }
}
