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
package org.bouncycastle.crypto.ext.digests;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.ext.params.Blake2Parameters;
import org.bouncycastle.util.Arrays;

/**
 * Blake2 Tree Hash.
 */
public class Blake2Tree
        implements Digest {
    /**
     * The underlying Blake2 instance.
     */
    private final Blake2 theDigest;

    /**
     * The TreeStore.
     */
    private final Blake2TreeStore theStore;

    /**
     * Hash buffer.
     */
    private final byte[] theHash;

    /**
     * The data processed on current leaf.
     */
    private int theProcessed;

    /**
     * Constructor.
     * @param p2b use Blake2b?
     */
    public Blake2Tree(final boolean p2b) {
        theDigest = p2b ? new Blake2b(512) : new Blake2s(256);
        theStore = new Blake2TreeStore(p2b);
        theHash = new byte[theDigest.getDigestSize()];
        reset();
    }

    @Override
    public String getAlgorithmName() {
        return theDigest.getAlgorithmName() + "Tree";
    }

    @Override
    public int getDigestSize() {
        return theDigest.getDigestSize();
    }

    @Override
    public void update(final byte pIn) {
        update(new byte[]{ pIn }, 0, 1);
    }

    @Override
    public void update(final byte[] pIn,
                       final int pInOff,
                       final int pLen) {
        processData(pIn, pInOff, pLen);
    }

    @Override
    public int doFinal(final byte[] pOut,
                       final int pOutOffset) {
        /* Mark the entry as the last node */
        theDigest.setLastNode();

        /* Finalise the leaf and process the result */
        theDigest.doFinal(theHash, 0);
        theStore.addElement(Arrays.clone(theHash));
        return theStore.calculateTree(pOut, pOutOffset);
    }

    @Override
    public void reset() {
        theDigest.setNodePosition(0, (short) 1);
        theProcessed = 0;
        theStore.reset();
    }

    /**
     * Process data.
     * @param pIn the input buffer
     * @param pInOffSet the starting offset in the input buffer
     * @param pLen the length of data to process
     */
    private void processData(final byte[] pIn,
                             final int pInOffSet,
                             final int pLen) {
        /* Determine space in current block */
        final int blkSize = theDigest.getLeafLen();
        final int mySpace = blkSize - theProcessed;

        /* If all data can be processed by the current leaf */
        if (mySpace >= pLen) {
            /* Update and return */
            theDigest.update(pIn, pInOffSet, pLen);
            theProcessed += pLen;
            return;
        }

        /* Process as much as possible into current leaf */
        if (mySpace > 0) {
            theDigest.update(pIn, pInOffSet, mySpace);
            theProcessed += mySpace;
        }

        /* Loop while we have data remaining */
        int myProcessed = mySpace;
        while (myProcessed < pLen) {
            /* If the current leaf is full */
            if (theProcessed == blkSize) {
                /* Finalise the leaf and process the result */
                theDigest.doFinal(theHash, 0);
                theStore.addElement(theHash);
                theDigest.setNodePosition(theDigest.getNodeOffset() + 1, (short) 1);
            }

            /* Process next block */
            final int myDataLen = Math.min(pLen - myProcessed, blkSize);
            theDigest.update(pIn, pInOffSet + myProcessed, myDataLen);
            theProcessed += myDataLen;
            myProcessed += myDataLen;
        }
    }

    /**
     * Initialise.
     * @param pParams the parameters.
     */
    public void init(final Blake2Parameters pParams) {
        /* Check that we have a fanOut != 1 && MaxDepth != 1*/
        if (pParams.getTreeFanOut() == 1 || pParams.getTreeMaxDepth() == 1) {
            throw new IllegalArgumentException("FanOut/MaxDepth cannot be 1");
        }

        /* Pass selective parameters to the underlying instance */
        theDigest.setKey(pParams.getKey());
        theDigest.setSalt(pParams.getKey());
        theDigest.setPersonalisation(pParams.getPersonalisation());
        theDigest.setTreeConfig(pParams.getTreeFanOut(), pParams.getTreeMaxDepth(), pParams.getTreeLeafLen());
        theDigest.setNodePosition(0, (short) 1);

        /* Reset processed and init the store */
        theProcessed = 0;
        theStore.init(pParams);
    }

    /**
     * The Blake tree.
     */
    private static class Blake2TreeStore {
        /**
         * The underlying Blake2 instance.
         */
        private final Blake2 theDigest;

        /**
         * The Hash Result.
         */
        private final byte[] theResult;

        /**
         * The Array of Hashes.
         */
        private SimpleVector theHashes;

        /**
         * Constructor.
         * @param p2b use Blake2b?
         */
        Blake2TreeStore(final boolean p2b) {
            theDigest = p2b ? new Blake2b(512) : new Blake2s(256);
            theResult = new byte[theDigest.getDigestSize()];
            theHashes = new SimpleVector();
        }

        /**
         * Initialise.
         * @param pParams the parameters.
         */
        public void init(final Blake2Parameters pParams) {
            /* Pass selective parameters to the underlying instance */
            theDigest.setSalt(pParams.getKey());
            theDigest.setPersonalisation(pParams.getPersonalisation());
            theDigest.setTreeConfig(pParams.getTreeFanOut(), pParams.getTreeMaxDepth(), pParams.getTreeLeafLen());
            theDigest.setNodePosition(0, (short) 2);
            theHashes.clear();
        }

        /**
         * Reset the store.
         */
        void reset() {
            theDigest.setNodePosition(0, (short) 2);
            theHashes.clear();
        }

        /**
         * Add intermediate node.
         * @param pHash the intermediate hash
         */
        void addElement(final byte[] pHash) {
            /* Access the base store */
            if (theHashes.isEmpty()) {
                theHashes.addElement(new Vector());
            }
            final SimpleVector myLevel = (SimpleVector) theHashes.firstElement();

            /* Add the element to the vector */
            myLevel.addElement(Arrays.clone(pHash));
        }

        /**
         * Calculate tree result.
         * @param pOut the output buffer
         * @param pOutOffset the offset into the output buffer
         * @return the number of bytes returned
         */
        int calculateTree(final byte[] pOut,
                          final int pOutOffset) {
            /* Check parameters */
            if (pOut.length < pOutOffset + theResult.length) {
                throw new OutputLengthException("Insufficient output buffer");
            }

            /* Loop */
            for (;;) {
                /* Calculate next level and test for completion */
                if (calculateNextLevel()) {
                    /* Return the hash length */
                    System.arraycopy(theResult, 0, pOut, pOutOffset, theResult.length);
                    return theResult.length;
                }
            }
        }

        /**
         * Calculate next level.
         * @return have we completed the tree true/false
         */
        private boolean calculateNextLevel() {
            /* Access the current level */
            final SimpleVector myInput = (SimpleVector) theHashes.lastElement();

            /* Set the depth of the tree */
            final int myCurDepth = (theHashes.size());
            final int myMaxDepth = theDigest.getMaxDepth();
            final int myFanOut = theDigest.getFanOut();
            theDigest.setNodePosition(0, (short) myCurDepth);

            /* If we are at the top of the tree */
            if (myFanOut == 0
                    || myInput.size() <= myFanOut
                    || myCurDepth == myMaxDepth) {
                /* Loop through all the elements */
                final Enumeration myEnumeration = myInput.elements();
                while (myEnumeration.hasMoreElements()) {
                    /* Fold hash into final node */
                    final byte[] myHash = (byte[]) myEnumeration.nextElement();
                    theDigest.update(myHash, 0, myHash.length);
                }

                /* Set flag and calculate the result */
                theDigest.setLastNode();
                theDigest.doFinal(theResult, 0);
                return true;
            }

            /* Add a new level */
            final SimpleVector myResults = new SimpleVector();
            theHashes.addElement(myResults);

            /* Loop through all the elements */
            int myCount = 0;
            int myOffSet = 0;
            final Enumeration myEnumeration = myInput.elements();
            while (myEnumeration.hasMoreElements()) {
                /* If we need to move to the next node  */
                if (myCount == myFanOut) {
                    /* Calculate node and add to level */
                    theDigest.setLastNode();
                    theDigest.doFinal(theResult, 0);
                    myResults.addElement(Arrays.clone(theResult));

                    /* Switch to next node */
                    theDigest.setNodePosition(++myOffSet, (short) myCurDepth);
                    myCount = 0;
                }

                /* Fold hash into current node */
                final byte[] myHash = (byte[]) myEnumeration.nextElement();
                theDigest.update(myHash, 0, myHash.length);
                myCount++;
            }

            /* Calculate final node at this level */
            theDigest.setLastNode();
            theDigest.doFinal(theResult, 0);
            myResults.addElement(Arrays.clone(theResult));

            /* Keep going */
            return false;
        }
    }

    /**
     * Simple Vector class.
     * <p>This is a cut down version of the java Vector class to avoid use of synchronised.
     */
    private static class SimpleVector {
        /**
         * The initial capacity.
         */
        private static final int INITCAPACITY = 8;

        /**
         * The array buffer holding elements.
         */
        private Object[] elementData;

        /**
         * The number of valid components in this {@code SimpleVector} object.
         */
        private int elementCount;

        /**
         * Constructor.
         */
        SimpleVector() {
            elementData = new Object[INITCAPACITY];
        }

        /**
         * Returns the number of components in this vector.
         * @return the vector size
         */
        int size() {
            return elementCount;
        }

        /**
         * Tests if this vector has no components.
         * @return true/false
         */
        boolean isEmpty() {
            return elementCount == 0;
        }

        /**
         * Returns the first component of the vector.
         * @return  the first component of the vector
         * @throws NoSuchElementException if this vector is empty
         */
        Object firstElement() {
            if (elementCount == 0) {
                throw new NoSuchElementException();
            }
            return elementData[0];
        }

        /**
         * Returns the last component of the vector.
         * @return  the last component of the vector, i.e., the component at index
         *          <code>size()&nbsp;-&nbsp;1</code>.
         * @throws NoSuchElementException if this vector is empty
         */
        Object lastElement() {
            if (elementCount == 0) {
                throw new NoSuchElementException();
            }
            return elementData[elementCount - 1];
        }

        /**
         * Adds the specified component to the end of this vector,
         * increasing its size by one. The capacity of this vector is
         * increased if its size becomes greater than its capacity.
         *
         * @param   obj   the component to be added
         */
        void addElement(final Object obj) {
            if (elementCount == elementData.length) {
                final Object[] newData = new Object[elementData.length << 1];
                System.arraycopy(elementData, 0, newData, 0, elementCount);
                elementData = newData;
            }
            elementData[elementCount++] = obj;
        }

        /**
         * Removes all of the elements from this Vector.  The Vector will
         * be empty after this call returns (unless it throws an exception).
         */
        void clear() {
            for (int i = 0; i < elementCount; i++) {
                elementData[i] = null;
            }
            elementCount = 0;
        }

        /**
         * Returns an enumeration of the components of this vector.
         * @return the enumeration
         */
        Enumeration elements() {
            return new Enumeration() {
                private int count;

                public boolean hasMoreElements() {
                    return count < elementCount;
                }

                public Object nextElement() {
                    if (count < elementCount) {
                       return elementData[count++];
                    }
                    throw new NoSuchElementException("Vector Enumeration");
                }
            };
        }
    }
}
