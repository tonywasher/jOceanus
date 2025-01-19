# GordianKnot Lock

## Overview
GordianKnot provide three kinds of locks that are password protected.

1. **keySetLock**s are provided to secure a keySet using a password. This keySet can be
automatically generated or be pre-existing. It can only be unlocked by the same factory that was used to lock it.
2.**factoryLock**s are provided to secure a factory using a password. The factory to be locked can be
automatically generated or be pre-existing. It can be unlocked by any factory running on the same machine.
3. **keyPairLock**s are provided to secure a randomly generated keySet using a password and a private asymmetric key.
It can be unlocked using any factory and the corresponding public asymmetric key.
             
## Password Algorithms

### Creation Algorithm

1. A random 32-bit seed **S** and a random 128-bit initVector **V** are generated.
2. A seededRandom based on **S** and the personalisation value
**I<sub>3</sub>** is created, and used to create the following items
    1. A set of three hMacs (all different) **M<sub>1</sub>**, **M<sub>2</sub>** and **M<sub>3</sub>**.
    2. A single secret hMac **M<sub>S</sub>** with an 512-bit output.
    3. A 512-bit length digest **X** is selected from the same seeded Random. 
3. Each of the hMacs is initialised with the password as the key
4. Each of the macs is updated with **P**, **IV** and the number of iterations **L** 
5. Set input **D<sub>1-3</sub>** and **D<sub>S</sub>** to **V**
6. Repeat the following loop **L** times 
    1. Update **M<sub>1</sub>** with **D<sub>2</sub>** and **D<sub>3</sub>**
    2. Update **M<sub>2</sub>** with **D<sub>1</sub>** and **D<sub>3</sub>**
    3. Update **M<sub>3</sub>** with **D<sub>1</sub>** and **D<sub>2</sub>**
    4. Update **M<sub>S</sub>** with **D<sub>S</sub>** and **D<sub>1-3</sub>**
    5. Build new **D<sub>S</sub>** as the result of **M<sub>S</sub>** and xor the result into **C<sub>S</sub>**. Repeat for other macs. 
7. Update digest **X** with **C<sub>1-3</sub>** and calculate the digest as **R<sub>X</sub>**
8. Calculate external lock as the concatenation of **S||V||R<sub>X</sub>**
9. Create keys for the keySet using **C<sub>S</sub>** as the secret

### Derivation Algorithm
                
1. Extract **S**, **V** and **R<sub>X</sub>** from the external lock.
2. Repeat creation algorithm with **S**, **V** and the password
3. Compare the calculated **R<sub>X</sub>** with the one extracted from the hash. Only create the keySet if it matches.

## keySetLock
A keySetLock can be either be used to secure an existing keySet, or it can be used to create a new random keySet.

### Sample
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianLockFactory myLockFactory = myBaseFactory.getLockFactory();

/* Create a lock for a new keySet */
final GordianPasswordLockSpec mySpec = new GordianPasswordLockSpec(new GordianKeySetSpec(GordianLength.LEN_256));
final char[] myPassword = ...
final GordianKeySetLock myLock = myLockFactory.newKeySetLock(mySpec, myPassword);
final GordianKeySet myKeySet = myLock.getKeySet();
final byte[] myLockBytes = myLock.getLockBytes();

/* Resolve the lock */
final GordianKeySetLock myResolved = myLockFactory.resolveKeySetLock(myLockBytes, myPassword);
```               

## factoryLock
A factoryLock can either be used to secure an existing factory, or it can be used to create a new random factory.

### Sample
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianLockFactory myLockFactory = myBaseFactory.getLockFactory();

/* Create a lock for a new factory */
final GordianPasswordLockSpec mySpec = new GordianPasswordLockSpec(new GordianKeySetSpec(GordianLength.LEN_256));
final char[] myPassword = ...
final GordianFactoryLock myLock = myLockFactory.newFactoryLock(mySpec, myPassword);
final GordianFactory myFactory = myLock.getFactory();
final GordianKeySet myKeySet = myFactory.getEmbeddedKeySet();
final byte[] myLockBytes = myLock.getLockBytes();

/* Resolve the lock */
final GordianFactpryLock myResolved = myLockFactory.resolveFactoryLock(myLockBytes, myPassword);
```

## keyPairLock
A keyPairLock can be used to create a new secured random keySet using an agreement-capable keypair.

### Sample
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianLockFactory myLockFactory = myBaseFactory.getLockFactory();
final GordianKeyPairFactory myKeyPairFactory = myBase.getKeyPairFactory();
final GordianAgreementFactory myAgreementFactory = myKeyPairFactory.getAgreementFactory();

/* Access keyPairGenerator and create sending/receiving pairs */
final GordianKeyPairSpec mySpec = GordianKeyPairSpec.dh(GordianDHGroup.FFDHE2048);
final GordianKeyPairGenerator myGenerator = myKeyPairFactory.getKeyPairGenerator(mySpec);
final GordianKeyPair myKeyPair = myGenerator.generateKeyPair();

/* Create a lock for a new keySet */
final GordianPasswordLockSpec mySpec = new GordianPasswordLockSpec(new GordianKeySetSpec(GordianLength.LEN_256));
final char[] myPassword = ...
final GordianKeyPairLock myLock = myLockFactory.newKeyPairLock(mySpec, myKeyPair, myPassword);
final GordianKeySet myKeySet = myLock.getKeySet();
final byte[] myLockBytes = myLock.getLockBytes();

/* Resolve the lock */
final GordianKeyPairLock myResolved = myLockFactory.resolveKeyPairLock(myLockBytes, myKeyPair, myPassword);
```                
