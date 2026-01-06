/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.keyset;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetAADCipher;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetCipher;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianPersonalisation.GordianPersonalId;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianValidator;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreWrapper;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianKeySetRecipe.GordianKeySetParameters;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;

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
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec the keySetSpec
     * @throws GordianException on error
     */
    GordianCoreKeySet(final GordianCoreFactory pFactory,
                      final GordianKeySetSpec pSpec) throws GordianException {
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
     * @throws GordianException on error
     */
    private GordianCoreKeySet(final GordianCoreKeySet pSource) throws GordianException {
        /* Copy factory */
        theFactory = pSource.getFactory();
        theSpec = pSource.getKeySetSpec();

        /* Copy the symKeyMap */
        theSymKeyMap = new HashMap<>(pSource.getSymKeyMap());

        /* Create the cipher */
        theCipher = new GordianMultiCipher(this);
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
    public GordianCoreKeySet cloneIt() throws GordianException  {
        return new GordianCoreKeySet(this);
    }

    /**
     * Obtain the encryption length for a length of data.
     * @param pDataLength the dataLength
     * @return the enctyption length
     */
    public static int getEncryptionLength(final int pDataLength) {
        final int iBlocks = 1 + (pDataLength / BLOCKLEN.getByteLength());
        return iBlocks * BLOCKLEN.getByteLength()
                + getEncryptionOverhead();
    }

    /**
     * Obtain the encryption length for a length of data.
     * @param pDataLength the dataLength
     * @return the encryption length
     */
    public static int getAADEncryptionLength(final int pDataLength) {
        return getEncryptionLength(pDataLength) + GordianCoreKeySetAADCipher.MACSIZE;
    }

    /**
     * Encryption overhead.
     * @return the encryption overhead
     */
    private static int getEncryptionOverhead() {
        return GordianKeySetRecipe.HDRLEN;
    }

    @Override
    public int getKeyWrapLength(final GordianLength pKeyLen) {
        return getDataWrapLength(pKeyLen.getByteLength());
    }

    /**
     * Obtain the wrapped length for a length of data.
     * @param pDataLength the dataLength
     * @return the wrapped length
     */
    public static int getDataWrapLength(final int pDataLength) {
        return GordianCoreWrapper.getKeyWrapLength(pDataLength, BLOCKLEN)
                + getEncryptionOverhead()
                + GordianCoreWrapper.getKeyWrapExpansion(BLOCKLEN);
    }

    /**
     * Obtain the keySet wrap length.
     * @param pKeyLen the keyLength.
     * @return the wrapped length
     */
    public static int getKeySetWrapLength(final GordianLength pKeyLen) {
        /* Count the number of KeySetSymTypes for 256 bit keys */
        int myCount = 0;
        for (GordianSymKeyType myType : GordianSymKeyType.values()) {
            if (GordianValidator.validStdBlockSymKeyTypeForKeyLength(myType, pKeyLen)) {
                myCount++;
            }
        }

        /* Determine the length of the encoded keySet prior to wrapping */
        final int mySize = GordianKeySetASN1.getEncodedLength(pKeyLen.getByteLength(), myCount);
        return getDataWrapLength(mySize);
    }

    @Override
    public int getPrivateKeyWrapLength(final GordianKeyPair pKeyPair) throws GordianException {
        /* Determine and check the keySpec */
        final GordianKeyPairFactory myFactory = theFactory.getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeyPair.getKeyPairSpec());
        final PKCS8EncodedKeySpec myPrivateKey = myGenerator.getPKCS8Encoding(pKeyPair);
        return getDataWrapLength(myPrivateKey.getEncoded().length);
    }

    @Override
    public int getKeySetWrapLength() {
        /* Obtain the count of valid symKeyTypes */
        final GordianValidator myValidator = theFactory.getValidator();
        final Predicate<GordianSymKeyType> myPredicate = myValidator.supportedKeySetSymKeyTypes(theSpec.getKeyLength());
        final int myCount = (int) Arrays.stream(GordianSymKeyType.values()).filter(myPredicate).count();

        /* Determine the size of the encoded ASN1 keySet */
        final int mySize = GordianKeySetASN1.getEncodedLength(theSpec.getKeyLength().getByteLength(), myCount);
        return getDataWrapLength(mySize);
    }

    @Override
    public GordianKeySetCipher createCipher() throws GordianException {
        return new GordianCoreKeySetCipher(this, false);
    }

    @Override
    public GordianKeySetAADCipher createAADCipher() throws GordianException {
        return new GordianCoreKeySetAADCipher(this);
    }

    @Override
    public byte[] encryptBytes(final byte[] pBytes) throws GordianException {
        /* Generate set of keys and initialisation vector */
        final GordianKeySetRecipe myRecipe =  GordianKeySetRecipe.newRecipe(theFactory, theSpec, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Initialise the ciphers */
        theCipher.initCiphers(myParams, true);
        final int myOutLen = GordianKeySetRecipe.HDRLEN + theCipher.getOutputLength(pBytes.length);
        final byte[] myOutput = new byte[myOutLen];

        /* build the output */
        myRecipe.buildHeader(myOutput);
        theCipher.finish(pBytes, 0, pBytes.length, myOutput, GordianKeySetRecipe.HDRLEN);
        return myOutput;
    }

    @Override
    public byte[] decryptBytes(final byte[] pBytes) throws GordianException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.parseRecipe(theFactory, theSpec, pBytes, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Initialise the ciphers */
        theCipher.initCiphers(myParams, false);
        final int myOutLen = theCipher.getOutputLength(pBytes.length - GordianKeySetRecipe.HDRLEN);
        final byte[] myOutput = new byte[myOutLen];

        /* Build output */
        final int myLen = theCipher.finish(pBytes, GordianKeySetRecipe.HDRLEN,
                pBytes.length - GordianKeySetRecipe.HDRLEN, myOutput, 0);
        return myLen == myOutLen
                ? myOutput
                : Arrays.copyOf(myOutput, myLen);
    }

    @Override
    public byte[] encryptAADBytes(final byte[] pBytes,
                                  final byte[] pAAD) throws GordianException {
        /* Creat cipher and initialise to encrypt */
        final GordianKeySetAADCipher myCipher = createAADCipher();
        myCipher.initForEncrypt(pAAD);

        /* Build the output buffer */
        final int myOutLen = myCipher.getOutputLength(pBytes.length);
        final byte[] myOutput = new byte[myOutLen];

        /* build the and return the output */
        final int myLen = theCipher.finish(pBytes, 0, pBytes.length, myOutput, 0);
        return myLen == myOutLen
               ? myOutput
               : Arrays.copyOf(myOutput, myLen);
    }

    @Override
    public byte[] decryptAADBytes(final byte[] pBytes,
                                  final byte[] pAAD) throws GordianException {
        /* Creat cipher and initialise to encrypt */
        final GordianKeySetAADCipher myCipher = createAADCipher();
        myCipher.initForDecrypt(pAAD);

        /* Build the output buffer */
        final int myOutLen = myCipher.getOutputLength(pBytes.length);
        final byte[] myOutput = new byte[myOutLen];

        /* build the and return the output */
        final int myLen = theCipher.finish(pBytes, 0, pBytes.length, myOutput, 0);
        return myLen == myOutLen
               ? myOutput
               : Arrays.copyOf(myOutput, myLen);
    }

    @Override
    public byte[] secureKey(final GordianKey<?> pKeyToSecure) throws GordianException {
        /* Generate set of keys */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.newRecipe(theFactory, theSpec, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* secure the key */
        final byte[] myBytes = theCipher.secureKey(myParams, pKeyToSecure);

        /* Package and return the encrypted bytes */
        return buildExternal(myRecipe, myBytes);
    }

    @Override
    public <T extends GordianKeySpec> GordianKey<T> deriveKey(final byte[] pSecuredKey,
                                                              final T pKeyType) throws GordianException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.parseRecipe(theFactory, theSpec, pSecuredKey, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Unwrap the key and return it */
        return theCipher.deriveKey(myParams, pSecuredKey, GordianKeySetRecipe.HDRLEN, pKeyType);
    }

    @Override
    public byte[] secureBytes(final byte[] pBytesToSecure) throws GordianException {
        /* Generate set of keys */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.newRecipe(theFactory, theSpec, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* secure the key */
        final byte[] myBytes = theCipher.secureBytes(myParams, pBytesToSecure);

        /* Package and return the encrypted bytes */
        return buildExternal(myRecipe, myBytes);
    }

    @Override
    public byte[] deriveBytes(final byte[] pSecuredBytes) throws GordianException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.parseRecipe(theFactory, theSpec, pSecuredBytes, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Unwrap the bytes and return them */
        return theCipher.deriveBytes(myParams, pSecuredBytes, GordianKeySetRecipe.HDRLEN);
   }

    @Override
    public byte[] securePrivateKey(final GordianKeyPair pKeyPair) throws GordianException {
        /* Generate set of keys */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.newRecipe(theFactory, theSpec, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Wrap the key */
        final byte[] myBytes = theCipher.securePrivateKey(myParams, pKeyPair);

        /* Package and return the encrypted bytes */
        return buildExternal(myRecipe, myBytes);
    }

    @Override
    public GordianKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKeySpec,
                                        final byte[] pSecuredPrivateKey) throws GordianException {
        /* Access the PKCS8Encoding */
        final PKCS8EncodedKeySpec myPrivate = derivePrivateKeySpec(pSecuredPrivateKey);

        /* Determine and check the keySpec */
        final GordianKeyPairFactory myFactory = theFactory.getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(pPublicKeySpec);
        if (!myKeySpec.equals(myFactory.determineKeyPairSpec(myPrivate))) {
            throw new GordianLogicException("Mismatch on keySpecs");
        }

        /* Derive the keyPair */
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myKeySpec);
        return myGenerator.deriveKeyPair(pPublicKeySpec, myPrivate);
    }

    /**
     * derive privateKeySpec.
     * @param pSecuredPrivateKey the secured private keySpec
     * @return the private keySpec
     * @throws GordianException on error
     */
    private PKCS8EncodedKeySpec derivePrivateKeySpec(final byte[] pSecuredPrivateKey) throws GordianException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = GordianKeySetRecipe.parseRecipe(theFactory, theSpec, pSecuredPrivateKey, false);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Unwrap the key and return it */
        return theCipher.derivePrivateKeySpec(myParams, pSecuredPrivateKey, GordianKeySetRecipe.HDRLEN);
    }

    @Override
    public byte[] secureKeySet(final GordianKeySet pKeySetToSecure) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Encode the keySet */
            final GordianKeySetASN1 myEncoded = new GordianKeySetASN1((GordianCoreKeySet) pKeySetToSecure);
            final byte[] myBytesToSecure = myEncoded.toASN1Primitive().getEncoded();

            /* secure the keySet */
            return secureBytes(myBytesToSecure);

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to secure KeySet", e);
        }
    }

    @Override
    public GordianCoreKeySet deriveKeySet(final byte[] pSecuredKeySet) throws GordianException {
        /* Unwrap the bytes and resolve them */
        final byte[] mySecuredBytes = deriveBytes(pSecuredKeySet);
        final GordianKeySetASN1 myEncoded = GordianKeySetASN1.getInstance(mySecuredBytes);

        /* Build the keySet and return it */
        return myEncoded.buildKeySet(theFactory);
    }

    /**
     * Secure the factory.
     * @param pFactoryToSecure the factory to secure
     * @return the secure factory
     * @throws GordianException on error
     */
    public byte[] secureFactory(final GordianFactory pFactoryToSecure) throws GordianException {
        /* Protect the operation */
        byte[] myBuffer = null;
        try {
            /* Access the parameters */
            final GordianParameters myParams = ((GordianCoreFactory) pFactoryToSecure).getParameters();

            /* Reject request if this is a namedFactory */
            if (!myParams.isInternal()) {
                throw new GordianDataException("Unable to lock named factory");
            }

            /* Return the encrypted seeds */
            myBuffer = myParams.getSecuritySeeds();
            return encryptBytes(myBuffer);

            /* Clear the buffer */
        } finally {
            if (myBuffer != null) {
                Arrays.fill(myBuffer, (byte) 0);
            }
        }
    }

    /**
     * derive the secured factory.
     * @param pSecuredFactory the secured factory
     * @return the factory
     * @throws GordianException on error
     */
    public GordianFactory deriveFactory(final byte[] pSecuredFactory) throws GordianException {
        /* Decrypt the bytes */
        final byte[] myBytes = decryptBytes(pSecuredFactory);

        /* Check that the buffer is the correct length */
        final int mySeedLen = GordianParameters.SEED_LEN.getByteLength();
        if (myBytes.length != mySeedLen) {
            throw new IllegalArgumentException("Invalid secured factory");
        }

        /* Create parameters and factory */
        final GordianParameters myParams = new GordianParameters(myBytes);
        return theFactory.newFactory(myParams);
    }

    /**
     * build external format.
     * @param pRecipe the recipe
     * @param pBytes the output bytes
     * @return the external form
     */
    private static byte[] buildExternal(final GordianKeySetRecipe pRecipe,
                                        final byte[] pBytes) {
        final byte[] myOutput = new byte[GordianKeySetRecipe.HDRLEN + pBytes.length];
        pRecipe.buildHeader(myOutput);
        System.arraycopy(pBytes, 0, myOutput, GordianKeySetRecipe.HDRLEN, pBytes.length);
        return myOutput;
    }

    /**
     * Declare symmetricKey.
     * @param pKey the key
     * @throws GordianException on error
     */
    void declareSymKey(final GordianKey<GordianSymKeySpec> pKey) throws GordianException {
        /* Access keyType */
        final GordianSymKeySpec myKeyType = pKey.getKeyType();

        /* Check that the key is supported */
        final GordianValidator myValidator = theFactory.getValidator();
        if (!myValidator.supportedKeySetSymKeySpecs(theSpec.getKeyLength()).test(myKeyType)) {
            throw new GordianDataException("invalid keyType");
        }

        /* Look for existing key of this type */
        final GordianKey<GordianSymKeySpec> myExisting = theSymKeyMap.get(myKeyType);
        if (myExisting != null) {
            /* Must be some mistake */
            throw new GordianDataException("keyType already declared");

            /* else new key */
        } else {
            /* Store into map and cipher */
            theSymKeyMap.put(myKeyType, pKey);
            theCipher.declareSymKey(pKey);
        }
    }

    /**
     * Build key set from random.
     * @throws GordianException on error
     */
    void buildFromRandom() throws GordianException {
        /* Loop through the symmetricKeys values */
        final GordianLength myKeyLen = theSpec.getKeyLength();
        final GordianValidator myValidator = theFactory.getValidator();
        final Predicate<GordianSymKeyType> mySymPredicate = myValidator.supportedKeySetSymKeyTypes(myKeyLen);
        for (final GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* If this is supported for a keySet */
            if (mySymPredicate.test(myType)) {
                /* Generate the key */
                final GordianSymKeySpec mySpec = new GordianSymKeySpec(myType, myKeyLen);
                final GordianCipherFactory myCipherFactory = theFactory.getCipherFactory();
                final GordianKeyGenerator<GordianSymKeySpec> myGenerator = myCipherFactory.getKeyGenerator(mySpec);
                final GordianKey<GordianSymKeySpec> myKey = myGenerator.generateKey();

                /* Add to map and cipher */
                theSymKeyMap.put(mySpec, myKey);
                theCipher.declareSymKey(myKey);
            }
        }
    }

    /**
     * Build key set from secret.
     * @param pSecret the secret.
     * @throws GordianException on error
     */
    public void buildFromSecret(final byte[] pSecret) throws GordianException {
        /* Check Secret length */
        if (GordianParameters.SECRET_LEN.getByteLength() != pSecret.length) {
            throw new GordianLogicException("Invalid secret length");
        }

        /* Loop through the symmetricKeys values */
        final GordianLength myKeyLen = theSpec.getKeyLength();
        final GordianValidator myValidator = theFactory.getValidator();
        final Predicate<GordianSymKeyType> mySymPredicate = myValidator.supportedKeySetSymKeyTypes(myKeyLen);
        final Random mySeededRandom = theFactory.getPersonalisation().getSeededRandom(GordianPersonalId.KEYSETGENRANDOM, pSecret);
        for (final GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* If this is supported for a keySet */
            if (mySymPredicate.test(myType)) {
                /* Generate the key and add to map */
                final GordianSymKeySpec mySpec = new GordianSymKeySpec(myType, myKeyLen);
                final GordianKey<GordianSymKeySpec> myKey = generateKey(mySpec, pSecret, mySeededRandom);
                theSymKeyMap.put(mySpec, myKey);
                theCipher.declareSymKey(myKey);
            }
        }
    }

    /**
     * Generate key for a Key Type from the secret and initVector.
     * @param <T> the class of key
     * @param pKeyType the keyType
     * @param pSecret the derived Secret
     * @param pSeededRandom the seededRandom.
     * @return the generated key
     * @throws GordianException on error
     */
    private <T extends GordianKeySpec> GordianKey<T> generateKey(final T pKeyType,
                                                                 final byte[] pSecret,
                                                                 final Random pSeededRandom) throws GordianException {
        /* Generate a new Secret Key from the secret */
        final GordianCipherFactory myFactory = theFactory.getCipherFactory();
        final GordianCoreKeyGenerator<T> myGenerator = (GordianCoreKeyGenerator<T>) myFactory.getKeyGenerator(pKeyType);
        return myGenerator.generateKeyFromSecret(pSecret, pSeededRandom);
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
        return Objects.hash(theFactory, theSpec, theSymKeyMap);
    }
}
