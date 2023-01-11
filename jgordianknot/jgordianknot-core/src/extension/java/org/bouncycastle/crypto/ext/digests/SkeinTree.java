/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.ext.digests.SkeinBase.Configuration;
import org.bouncycastle.crypto.ext.params.SkeinXParameters;
import org.bouncycastle.util.Arrays;

/**
 * Skein Tree Hash.
 * <p>This implementation will support all elements of SkeinXParameters except for Maximum OutputLength.
 * This is restricted to the output length of the underlying digest.
 * <p>Tree configuration is required and defaults to unlimited fanOut, maxDepth of 2 and 4K leafLength.
 * <p>The implementation consumes a large message as a standard digest and builds the lowest level of the tree on the fly.
 * On the {@link #doFinal(byte[], int)} call, the remainder of the tree is calculated and the topmost tree node is returned as the hash.
 * This tree is retained until an explicit {@link #reset()} is called. Further update calls are disabled while the tree is retained.
 * <p>While the tree is retained, each leaf may be explicitly replaced via {@link #updateLeaf(int, byte[], int, int)}, leading to a recalculation
 * of the tree node which may be obtained via {@link #obtainResult(byte[], int)}.
 * <p>The last leaf at the bottom level of the tree can be any length from 1 to leafLen. It may be replaced by data that is between 1 to leafLen using the
 * {@link #updateLeaf(int, byte[], int, int)} method where the new length does not have to be the same as the old length.
 * Other leaves must be replaced by data of length leafLen.
 * <p>The number of leaves cannot be increased/decreased once the tree has been built. If the length of data is changed, a new tree should be built.
 * <p>TODO
 * <ul>
 * <li>Replacing a leaf should not automatically trigger a recalculation of the tree. Instead the updated leaves should be left in an update list.
 * This update list will be processed only when the final result is requested, allowing several leaves to be replaced before the tree is recalculated.
 * <li>A duplicate leaf in the update list will replace the earlier leaf.
 * <li>On recalculation the updateList will be validated. The tree can be extended if all additional leaves are present in the update list and the previously last
 * leaf was either leafLen long or is now present as a full leaf in the update list. Only the last leaf may be shorter than the leafLen.
 * <li>A new call should be provided to truncate the tree at a total length. This must be shorter than the current length and will form part of the update list
 * to be processed when calculating the result.
 * </ul>
 */
public class SkeinTree
        implements Digest {
    /**
     * The maximum Byte.
     */
    private static final int MAXBYTE = 255;

    /**
     * The base for the treeConfig.
     */
    private static final int CONFIGBASE = 16;

    /**
     * The underlying Skein instance.
     */
    private final SkeinBase theDigest;

    /**
     * The TreeStore.
     */
    private final SkeinTreeStore theStore;

    /**
     * Hash buffer.
     */
    private final byte[] theHash;

    /**
     * The single byte buffer.
     */
    private final byte[] singleByte = new byte[1];

    /**
     * The leafLength.
     */
    private int theLeafLen;

    /**
     * The shift.
     */
    private int theShift;

    /**
     * The leaf index.
     */
    private int theLeafIdx;

    /**
     * The data processed on current leaf.
     */
    private int theProcessed;

    /**
     * Constructor.
     * @param pDigest the underlying digest.
     */
    public SkeinTree(final SkeinBase pDigest) {
        /* Store parameters and initialise store */
        theDigest = pDigest;
        theStore = new SkeinTreeStore(pDigest);
        theHash = new byte[theDigest.getOutputSize()];

        /* Initialise to default values */
        final SkeinXParameters.Builder myBuilder = new SkeinXParameters.Builder();
        myBuilder.setTreeConfig(1, MAXBYTE, 1);
        init(myBuilder.build());
    }

    @Override
    public String getAlgorithmName() {
        return "SkeinTree-" + (theDigest.getOutputSize() * Byte.SIZE);
    }

    @Override
    public int getDigestSize() {
        return theDigest.getOutputSize();
    }

    @Override
    public void update(final byte pIn) {
        singleByte[0] = pIn;
        update(singleByte, 0, 1);
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
        /* Finalise the leaf and process the result */
        theDigest.calculateNode(theHash, 0);
        theStore.addElement(Arrays.clone(theHash));
        return theStore.calculateTree(pOut, pOutOffset);
    }

    @Override
    public void reset() {
        theLeafIdx = 0;
        theProcessed = 0;
        theDigest.initTreeNode(1, theLeafIdx, theShift);
        theStore.reset();
    }

    /**
     * Obtain the tree result.
     * @param pOut the output buffer
     * @param pOutOffset the offset into the output buffer
     * @return the number of bytes returned
     */
    public int obtainResult(final byte[] pOut,
                            final int pOutOffset) {
        /* Access result from store */
        return theStore.obtainResult(pOut, pOutOffset);
    }

    /**
     * Obtain the leaf length.
     * @return the leafLength.
     */
    public int getLeafLen() {
        return theDigest.getOutputSize() << theLeafLen;
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
        /* Cannot process further data once tree is built */
        if (theStore.treeBuilt()) {
            throw new IllegalStateException("Tree has been built");
        }

        /* Determine space in current block */
        final int blkSize = getLeafLen();
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
                theDigest.calculateNode(theHash, 0);
                theStore.addElement(theHash);
                theDigest.initTreeNode(1, ++theLeafIdx, theShift);
                theProcessed = 0;
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
    public void init(final SkeinXParameters pParams) {
        /* Reject a bad leaf length length */
        final int myLeafLen = pParams.getTreeLeafLen();
        if (myLeafLen < 1 || myLeafLen > MAXBYTE) {
            throw new IllegalArgumentException("Invalid leaf length");
        }

        /* Reject a bad fanOut */
        final short myFanOut = pParams.getTreeFanOut();
        if (myFanOut < 1 || myFanOut > MAXBYTE) {
            throw new IllegalArgumentException("Invalid fanOut");
        }

        /* Reject a bad maxDepth */
        final short myMaxDepth = pParams.getTreeMaxDepth();
        if (myMaxDepth < 2 || myMaxDepth > MAXBYTE) {
            throw new IllegalArgumentException("Invalid maxDepth");
        }

        /* Record the values */
        theLeafLen = myLeafLen;
        theShift = highestBitSet(theDigest.getOutputSize()) + theLeafLen - 1;

        /* Declare the configuration */
        declareConfig(myFanOut, myMaxDepth);

        /* Pass selective parameters to the underlying hash */
        theDigest.init(pParams);

        /* Reset everything */
        reset();
    }

    /**
     * Declare extended configuration.
     * @param pFanOut the fanOut
     * @param pMaxDepth the max depth
     */
    private void declareConfig(final int pFanOut,
                               final int pMaxDepth) {
        /* Declare the configuration */
        final long myLen = theDigest.getOutputSize() * 8L;
        final Configuration myConfig = new TreeConfiguration(myLen, theLeafLen, pFanOut, pMaxDepth);
        theDigest.setConfiguration(myConfig);

        /* Update the store */
        theStore.declareConfig(pFanOut, pMaxDepth);
    }

    /**
     * Update leaf.
     * @param pIndex the index of the leaf
     * @param pInput the input buffer
     * @param pInOffSet the starting offset the the input buffer
     */
    public void updateLeaf(final int pIndex,
                           final byte[] pInput,
                           final int pInOffSet) {
        /* Full leafLen */
        updateLeaf(pIndex, pInput, pInOffSet, getLeafLen());
    }

    /**
     * Update leaf.
     * @param pIndex the index of the leaf
     * @param pInput the input buffer
     * @param pInOffSet the starting offset the the input buffer
     * @param pLen the length of data
     */
    public void updateLeaf(final int pIndex,
                           final byte[] pInput,
                           final int pInOffSet,
                           final int pLen) {
        /* Check index validity */
        final boolean bLast = theStore.checkLeafIndex(pIndex);

        /* Validate the leaf length */
        final int myLeafLen = getLeafLen();
        if (pLen < 0 || pLen > myLeafLen) {
            throw new DataLengthException("Invalid length");
        }

        /* Any leaf that is not the last must be leafLen in length */
        if (!bLast && pLen != myLeafLen) {
            throw new DataLengthException("All but the last leaf must have byteLength " + myLeafLen);
        }

        /* Make sure that the buffer is valid */
        if (pLen + pInOffSet > pInput.length) {
            throw new DataLengthException("Invalid input buffer");
        }

        /* Initialise the treeNode */
        theDigest.initTreeNode(1, pIndex, theShift);

        /* Recalculate the digest */
        theDigest.update(pInput, pInOffSet, pLen);
        theDigest.calculateNode(theHash, 0);

        /* Replace the hash */
        theStore.replaceElement(pIndex, theHash);
    }

    /**
     * Calculate highestBitSet.
     * @param pValue the value to examine
     * @return the index of the highest but set
     */
    private static int highestBitSet(final int pValue) {
        int highestBit = 0;
        int myValue = pValue;
        while (myValue != 0) {
            highestBit++;
            myValue = myValue >>> 1;
        }
        return highestBit;
    }

    /**
     * The Skein tree.
     */
    private static class SkeinTreeStore {
        /**
         * The underlying Blake2 instance.
         */
        private final SkeinBase theDigest;

        /**
         * The Hash Result.
         */
        private final byte[] theResult;

        /**
         * The Array of Hashes.
         */
        private final SimpleVector theHashes;

        /**
         * The fanOut.
         */
        private short theFanOut;

        /**
         * The maxDepth.
         */
        private short theMaxDepth;

        /**
         * The shift.
         */
        private int theShift;

        /**
         * Has the tree been built?.
         */
        private boolean treeBuilt;

        /**
         * Constructor.
         * @param pDigest the underlying digest.
         */
        SkeinTreeStore(final SkeinBase pDigest) {
            /* Store details */
            theDigest = pDigest;
            theResult = new byte[theDigest.getOutputSize()];
            theHashes = new SimpleVector();
        }

        /**
         * Declare extended configuration.
         * @param pFanOut the fanOut
         * @param pMaxDepth the max depth
         */
        void declareConfig(final int pFanOut,
                           final int pMaxDepth) {
            theFanOut = (short) pFanOut;
            theMaxDepth = (short) pMaxDepth;
            theShift = highestBitSet(theDigest.getOutputSize()) + theFanOut - 1;
        }

        /**
         * Has the tree been built?
         * @return true/false
         */
        boolean treeBuilt() {
            return treeBuilt;
        }

        /**
         * Reset the store.
         */
        void reset() {
            theHashes.clear();
            treeBuilt = false;
        }

        /**
         * Add intermediate node.
         * @param pHash the intermediate hash
         */
        void addElement(final byte[] pHash) {
            /* Access the base level */
            if (theHashes.isEmpty()) {
                theHashes.addElement(new SimpleVector());
            }
            final SimpleVector myLevel = (SimpleVector) theHashes.firstElement();

            /* Add the element to the vector */
            myLevel.addElement(Arrays.clone(pHash));
        }

        /**
         * Obtain the tree result.
         * @param pOut the output buffer
         * @param pOutOffset the offset into the output buffer
         * @return the number of bytes returned
         */
        int obtainResult(final byte[] pOut,
                         final int pOutOffset) {
            /* Check parameters */
            if (pOut.length < pOutOffset + theResult.length) {
                throw new OutputLengthException("Insufficient output buffer");
            }
            if (!treeBuilt) {
                throw new IllegalStateException("tree has not been built");
            }

            /* Access the final level */
            final SimpleVector myLevel = (SimpleVector) theHashes.lastElement();
            final byte[] myState = (byte[]) myLevel.firstElement();

            /* Process the output */
            theDigest.restoreForOutput(myState);
            theDigest.output(0, pOut, pOutOffset, theResult.length);

            /* Return the length */
            return theResult.length;
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
            if (treeBuilt) {
                throw new IllegalStateException("tree already built");
            }

            /* Access the only level */
            SimpleVector myLevel = (SimpleVector) theHashes.lastElement();

            /* While we have elements that must be reduced */
            while (myLevel.size() > 1) {
                /* Calculate the next set of hashes */
                myLevel = calculateNextLevel(myLevel);
                theHashes.addElement(myLevel);
            }

            /* Note that the tree has been built */
            treeBuilt = true;

            /* Return the final hash */
            return obtainResult(pOut, pOutOffset);
        }

        /**
         * Calculate next level.
         * @param pInput the current set of hashes
         * @return the next level
         */
        private SimpleVector calculateNextLevel(final SimpleVector pInput) {
            /* Set the depth of the tree */
            final int myCurDepth = theHashes.size() + 1;
            theDigest.initTreeNode(myCurDepth, 0, theShift);

            /* Create the new level */
            final SimpleVector myResults = new SimpleVector();

            /* Determine the number of nodes to combine */
            final int myFanOut = 1 << theFanOut;

            /* Determine whether we are calculating the root node */
            final boolean lastStage =  pInput.size() <= myFanOut
                    || myCurDepth == theMaxDepth;

            /* Loop through all the elements */
            int myCount = 0;
            int myOffSet = 0;
            final Enumeration myEnumeration = pInput.elements();
            while (myEnumeration.hasMoreElements()) {
                /* If we need to move to the next node  */
                if (!lastStage && myCount == myFanOut) {
                    /* Calculate node and add to level */
                    theDigest.calculateNode(theResult, 0);
                    myResults.addElement(Arrays.clone(theResult));

                    /* Switch to next node */
                    theDigest.initTreeNode(myCurDepth, ++myOffSet, theShift);
                    myCount = 0;
                }

                /* Fold hash into current node */
                final byte[] myHash = (byte[]) myEnumeration.nextElement();
                theDigest.update(myHash, 0, myHash.length);
                myCount++;
            }

            /* Calculate final node at this level */
            theDigest.calculateNode(theResult, 0);
            myResults.addElement(Arrays.clone(theResult));

            /* Return the results */
            return myResults;
        }

        /**
         * Check the leaf index.
         * @param pIndex the index of the element
         * @return is this the last element in the tree? true/false
         */
        boolean checkLeafIndex(final int pIndex) {
            /* Cannot replace leaf if not built */
            if (!treeBuilt) {
                throw new IllegalStateException("Tree has not been built");
            }

            /* Check that the index is valid */
            final SimpleVector myLevel = (SimpleVector) theHashes.firstElement();
            if (pIndex < 0 || pIndex >= myLevel.size()) {
                throw new IllegalArgumentException("Invalid index");
            }

            /* Return whether this is the last index */
            return pIndex == myLevel.size() - 1;
        }

        /**
         * Replace the hash for a leaf node.
         * @param pIndex the index of the element
         * @param pHash the new hashValue
         */
        void replaceElement(final int pIndex,
                            final byte[] pHash) {
            /* Check that the index is correct */
            final SimpleVector myLevel = (SimpleVector) theHashes.firstElement();
            if (pIndex < 0 || pIndex >= myLevel.size()) {
                throw new IllegalArgumentException("Invalid index");
            }

            /* Replace the element */
            myLevel.setElementAt(Arrays.clone(pHash), pIndex);

            /* Loop through the levels */
            int myIndex = pIndex;
            for (int i = 2; i <= theHashes.size(); i++) {
                /* Recalculate the parent node */
                myIndex = recalculateParent(i, myIndex);
            }
        }

        /**
         * Recalculate Node.
         * @param pLevel the tree level of the node
         * @param pIndex of the node
         * @return the parent index
         */
        private int recalculateParent(final int pLevel,
                                      final int pIndex) {
            /* Make sure that the level is reasonable */
            if (pLevel < 2 || pLevel > theHashes.size()) {
                throw new IllegalArgumentException("Invalid level");
            }

            /* Access the Vector for the parent and input levels */
            final SimpleVector myInput = (SimpleVector) theHashes.elementAt(pLevel - 2);
            final SimpleVector myLevel = (SimpleVector) theHashes.elementAt(pLevel - 1);

            /* Determine the number of nodes to combine */
            final int myFanOut = 1 << theFanOut;

            /* Determine whether we are calculating the root node */
            final boolean lastStage = myInput.size() <= myFanOut
                    || pLevel == theMaxDepth;

            /* Calculate bounds */
            final int myParentIndex = lastStage
                                      ? 0
                                      : pIndex / myFanOut;
            final int myIndex = myParentIndex * myFanOut;
            final int myNumHashes = myInput.size();
            final int myMaxHash = lastStage ? myNumHashes : Math.min(myFanOut, myNumHashes - myIndex);

            /* Initialise the digest */
            theDigest.initTreeNode(pLevel, myParentIndex, theShift);

            /* Loop through the input hashes */
            for (int i = 0; i < myMaxHash; i++) {
                /* Fold into new hash */
                final byte[] myHash = (byte[]) myInput.elementAt(i + myIndex);
                theDigest.update(myHash, 0, myHash.length);
            }

            /* Calculate new digest and replace it */
            theDigest.calculateNode(theResult, 0);
            myLevel.setElementAt(Arrays.clone(theResult), myParentIndex);
            return myParentIndex;
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
         * Returns the component at the specified index.
         *
         * @param      index   an index into this vector
         * @return     the component at the specified index
         * @throws ArrayIndexOutOfBoundsException if the index is out of range
         *         ({@code index < 0 || index >= size()})
         */
        Object elementAt(final int index) {
            if (index >= elementCount) {
                throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
            }

            return elementData[index];
        }

        /**
         * Sets the component at the specified {@code index} of this
         * vector to be the specified object. The previous component at that
         * position is discarded.
         *
         * <p>The index must be a value greater than or equal to {@code 0}
         * and less than the current size of the vector.
         *
         * @param      obj     what the component is to be set to
         * @param      index   the specified index
         * @throws ArrayIndexOutOfBoundsException if the index is out of range
         *         ({@code index < 0 || index >= size()})
         */
        void setElementAt(final Object obj,
                          final int index) {
            if (index >= elementCount) {
                throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
            }
            elementData[index] = obj;
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
    /**
     * Extended configuration to include Tree details.
     */
    private static class TreeConfiguration
        extends Configuration {
        TreeConfiguration(final long outputSizeBits,
                          final int treeLeafLen,
                          final int treeFanOut,
                          final int treeMaxDepth) {
            /* Initialise main part of config */
            super(outputSizeBits);

            // 16..18 treeConfig
            bytes[CONFIGBASE] = (byte) treeLeafLen;
            bytes[CONFIGBASE + 1] = (byte) treeFanOut;
            bytes[CONFIGBASE + 2] = (byte) treeMaxDepth;
        }
    }
}
