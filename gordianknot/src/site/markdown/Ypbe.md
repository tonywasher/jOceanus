# GordianKnot Symmetric Password-Based Ciphers

## Overview
PBE Ciphers are supported via the **GordianCipherFactory** interface.
Password-based encryption is available for both Block and Stream Ciphers. To use Password-based, the parameters for the
init call are modified to provide a **GordianPBESpec** rather than a key

### Sample
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianCipherFactory myCipherFactory = myBaseFactory.getCipherFactory();

/* Create cipher */
final GordianSymKeySpec myKeySpec = GordianSymKeySpecBuilder.aes(GordianLength.LEN_256);
final GordianSymCipherSpec myCipherSpec = GordianSymSpecBuilder.cbc(myKeySpec, GordianPadding.PKCS7);
final GordianSymCipher myCipher = myCipherFactory.createSymKeyCipher(myCipherSpec);

/* Create pbeSpec */
final GordianPBESpec myPBESpec = GordianPBESpecBuilder.pbKDF2(GordianDigestSpec.sha2(GordianLength.LEN_256), 2000);
final byte[] myPassword = ....

/* Encrypt message with random nonce */
GordianCipherParameters myParams = GordianCipherParameters.pbe(myPBESpec, myPassword);
myCipher.init(true, myParams);
final byte[] myMessage = ...
int myOutLen = myCipher.getOutputLength(myMessage.length);
final byte[] myEncrypted = new byte[myOutLen];
int myProcessed = myCipher.update(myMessage, 0, myMessage.length, myEncrypted);
myCipher.finish(myEncrypted, myProcessed);

/* Decrypt message */
myParams = GordianCipherParameters.pbeAndNonce(myPBESpec, myCipher.getPBESalt());
myCipher.init(false, myParams);
myOutLen = myCipher.getOutputLength(myEncrypted.length);
final byte[] myResult = new byte[myOutLen];
myProcessed = myCipher.update(myEncrypted, 0, myEncrypted.length, myResult);
myCipher.finish(myResult, myProcessed);
```

## Algorithms
The following pbe algorithms are supported.
<table class="defTable">
  <tr><th class="defHdr">Algorithm</th></tr>
  <tr><td>pbKDF2</td></tr>
  <tr><td>pkcs12</td></tr>
  <tr><td>scrypt</td></tr>
  <tr><td>argon2</td></tr>
</table>
