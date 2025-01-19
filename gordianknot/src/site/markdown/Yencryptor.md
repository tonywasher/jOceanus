# GordianKnot Asymmetric Encryptors

## Overview
Encryptors are supported via the **GordianEncryptorFactory** interface.

Algorithms are represented by **GordianEncryptorSpec**. A **GordianEncryptor** is obtained via the encryptorSpec,
and then messages are encrypted/decrypted by the encryptor.

### Sample
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianKeyPairFactory myKeyPairFactory = myBase.getKeyPairFactory();
final GordianEncryptorFactory myEncryptorFactory = myKeyPairFactory.getEncryptorFactory();

/* Access keyPairGenerator */
final GordianKeyPairSpec mySpec = GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048);
final GordianKeyPairGenerator myGenerator = myKeyPairFactory.getKeyPairGenerator(mySpec);
final GordianKeyPair myPair = myGenerator.generateKeyPair();

/* Access encryptor */
final GordianEncryptorSpec myEncryptSpec = GordianEncryptorSpecBuilder.rsa(GordianDigestSpec.sha2(GordianLength.LEN_256));
final GordianEncryptor myEncryptor = myEncryptorFactory.createEncryptor(myEncryptSpec);

/* Encrypt message */
final byte[] message = ....;
myEncryptor.initForEncrypt(myPair);
final byte[] myEncrypted = myEncryptor.encrypt(myMessage);

/* Decrypt message */
myEncryptor.initForDecrypt(myPair);
final byte[] myResult = myEncryptor.decrypt(myEncrypted);
```

## Composite Encryptors
Composite encryptions may be created by a composite keyPair, as long as each element of the composite keyPair is assigned a valid encryptorSpec.

The encryption is achieved by using the cipherText of the first encryptor as the input to the second encryptor and so on. 

### Sample
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianKeyPairFactory myKeyPairFactory = myBase.getKeyPairFactory();
final GordianEncryptorFactory myEncryptorFactory = myKeyPairFactory.getEncryptorFactory();

/* Access keyPairGenerator */
final GordianKeyPairSpec mySpec = GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048),
                                                                      GordianKeyPairSpecBuilder.elGamal(GordianDHGroup.rfc7919_ffdhe2048));
final GordianKeyPairGenerator myGenerator = myKeyPairFactory.getKeyPairGenerator(mySpec);
final GordianKeyPair myPair = myGenerator.generateKeyPair();

/* Access encryptor */
final GordianEncryptorSpec myEncryptSpec = GordianEncryptorSpecBuilder.composite(GordianEncryptorSpecBuilder.rsa(GordianDigestSpec.sha2(GordianLength.LEN_256)),
                                                                                 GordianEncryptorSpecBuilder.elGamal(GordianDigestSpec.sha2(GordianLength.LEN_256)));
final GordianEncryptor myEncryptor = myEncryptorFactory.createEncryptor(myEncryptSpec);

/* Encrypt message */
final byte[] message = ....;
myEncryptor.initForEncrypt(myPair);
final byte[] myEncrypted = myEncryptor.encrypt(myMessage);

/* Decrypt message */
myEncryptor.initForDecrypt(myPair);
final byte[] myResult = myEncryptor.decrypt(myEncrypted);
```

## Algorithms
The following encryptor algorithms are supported
<table class="defTable">
  <tr><th class="defHdr">Algorithm</th><th class="defHdr">Variants</th><th>Notes</th></tr>
  <tr><td>RSA</td><td>SHA224, SHA256, SHA384, SHA512</td><td/></tr>
  <tr><td>ElGamal</td><td>SHA224, SHA256, SHA384, SHA512</td><td/></tr>
  <tr><td>EC</td><td/><td>Available for EC, GOST2012 and SM2 keys, Not available for JCA</td></tr>
  <tr><td>SM2</td><td>C1C2C3, C1C3C2</td><td>Available for EC, GOST2012 and SM2 keys</td></tr>
</table>

