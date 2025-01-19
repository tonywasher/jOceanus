# GordianKnot Personalisation

## Overview
GordianKnot can be personalised to ensure differing securitySpaces. The personalisation is achieved via a SecurityPhrase which is processed
via a set of digests to produce two 512-bit arrays (**P** and **IV**) which are then utilised in the various algorithms.
**P** is often split into 16 integers written as **P<sub>1</sub>** through **P<sub>16</sub>**. Similarly
**IV** is often split into 4 128-bit arrays written as **IV<sub>1</sub>** through **IV<sub>4</sub>**

### Sample
```
/* Access factory using security phrase */
final char[] mySecurityPhrase = ...
final GordianFactory myBaseFactory = GordianGenerator.createFactory(mySecurityPhrase);
```

## Generation
The personalisation algorithm is as follows.

1. Initialise intermediate result buffer **I** to all zeroes
2. Create an array of all available digests that have an output length of 256-bits.
3. Initialise all digests with a constant value followed by the security phrase
4. Finish all digests, creating an array **I<sub>A</sub>** of the hashResults and xor each of
these hashes into the intermediate buffer **I**
5. Loop for 2K iterations.
    1. Update each digest with **ALL** the elements of **I<sub>A</sub>**
    2. Finish each digest, updating its hash in **I<sub>A</sub>** and xor-ing into **I**
6. Obtain a seed **S** from the first 8 bytes of **I** and obtain a seeded random **R** using
**S**
7. Obtain an HMAC **H** from the seededRandom **R**
8. Use HKDF with the HMAC **H** to expand the result **I** into **P** and **IV**.

##  Seeded Random
Several algorithms in **GordianKnot** require selection of various algorithms in a deterministic fashion based
on a 4-byte seed **S**. This is performed by creating a 6-byte seed from the combination of the 4-byte seed and
one of the **P<sub>n</sub>** values, specific to the algorithm. This 6-byte seed in used to instantiate an instance of
the deterministic **Random** class and algorithm selection is performed via this seeded Random instance.

