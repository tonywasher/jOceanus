/*******************************************************************************
 * JSortedList: A random access linked list implementation
 * Copyright 2012 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.JSortedList;

import java.util.Arrays;

/**
 * Index for an Ordered list.
 * @author Tony Washer
 * @param <T> the data-type of the list
 */
public class OrderedIndex<T extends Comparable<T>> {
    /**
     * Expansion rate of map.
     */
    private static final int EXPANSION_SIZE = 5;

    /**
     * Granularity of map.
     */
    private static final int MAP_GRANULARITY = 50;

    /**
     * The list to which this index is attached.
     */
    private final OrderedList<T> theList;

    /**
     * Array of standard indexes.
     */
    private OrderedNode<T>[] theMap = null;

    /**
     * The length of the map.
     */
    private int theMapLength = 0;

    /**
     * The active length of the map.
     */
    private int theActiveMapLength = 0;

    /**
     * Constructor.
     * @param pList the list
     */
    @SuppressWarnings("unchecked")
    protected OrderedIndex(final OrderedList<T> pList) {
        /* Store the list */
        theList = pList;

        /* Allocate and initialise the map */
        theMap = (OrderedNode<T>[]) new OrderedNode[EXPANSION_SIZE];
        Arrays.fill(theMap, null);
        theMapLength = EXPANSION_SIZE;
    }

    /**
     * Obtain the node at the specified index.
     * @param iIndex the index of the node
     * @return the relevant node (or null)
     */
    protected OrderedNode<T> getNodeAtIndex(final int iIndex) {
        int iMapIndex;
        OrderedNode<T> myNode;

        /* Calculate the map index */
        iMapIndex = iIndex / MAP_GRANULARITY;

        /* Handle out of range */
        if (iMapIndex > theActiveMapLength - 1) {
            return null;
        }

        /* Access the start node for the search */
        myNode = theMap[iMapIndex];

        /* Determine which index we are looking for */
        boolean doSkipHidden = theList.getSkipHidden();

        /* Search for the correct node */
        while (myNode != null) {
            /* Break if we have found the node */
            if (myNode.getIndex(doSkipHidden) == iIndex) {
                break;
            }

            /* Shift to next node */
            myNode = myNode.getNext(doSkipHidden);
        }

        /* Return the correct node */
        return myNode;
    }

    /**
     * Obtain the node for the specified item.
     * @param pItem the item
     * @return the relevant node (or null)
     */
    protected OrderedNode<T> findNodeForObject(final T pItem) {
        /* Empty list cannot contain the entry */
        if (theActiveMapLength == 0) {
            return null;
        }

        /* Determine limits to map search */
        int iMinimum = 0;
        int iMaximum = theActiveMapLength - 1;

        /* Access the first element in the map */
        OrderedNode<T> myTest = theMap[iMinimum];

        /* Check it against the object */
        int iDiff = myTest.compareTo(pItem);

        /* If we have found the object, return it */
        if (iDiff == 0) {
            return myTest;
        }

        /* If the object is before the first in list, then not found */
        if (iDiff > 0) {
            return null;
        }

        /* If we have a maximum element distinct from the first element */
        if (iMaximum > 0) {
            /* Access the last element in the map */
            myTest = theMap[iMaximum];

            /* Check it against the object */
            iDiff = myTest.compareTo(pItem);

            /* If we have found the object, return it */
            if (iDiff == 0) {
                return myTest;
            }

            /* If the element is before the last element */
            if (iDiff > 0) {
                /*
                 * Binary chop to find the search start point We need to loop while we have a search span
                 * greater than granularity
                 */
                while (iMinimum < iMaximum - 1) {
                    /* Access test item */
                    int iTest = (iMinimum + iMaximum) >>> 1;
                    myTest = theMap[iTest];

                    /* Check it against the object */
                    iDiff = myTest.compareTo(pItem);

                    /* If we have found the object, return it */
                    if (iDiff == 0) {
                        return myTest;
                    }

                    /* Adjust limits */
                    if (iDiff < 0) {
                        iMinimum = iTest;
                    } else {
                        iMaximum = iTest;
                    }
                }
            }
        }

        /* We now have a window of granularity in which to search for the item */
        myTest = myTest.getNext();
        while (myTest != null) {
            /* Break if we have found the node */
            iDiff = myTest.compareTo(pItem);

            /* If we have found the object, return it */
            if (iDiff == 0) {
                return myTest;
            }

            /* If we are past the object, then not found */
            if (iDiff > 0) {
                return null;
            }

            /* Shift to next node */
            myTest = myTest.getNext();
        }

        /* Return not found */
        return null;
    }

    /**
     * Obtain the node for the specified item. This slow method is used on the reSort method where we expect
     * the item to be outside its natural ordering and hence not found by the standard search.
     * @param pItem the item
     * @return the relevant node (or null)
     */
    protected OrderedNode<T> findUnsortedNodeForObject(final T pItem) {
        /* Access the first nodes */
        OrderedNode<T> myNode = theList.getHead();

        /* Search for the correct node */
        while (myNode != null) {
            /* Break if we have found the node */
            if (myNode.compareTo(pItem) == 0) {
                break;
            }

            /* Shift to next node */
            myNode = myNode.getNext();
        }

        /* Return the correct node */
        return myNode;
    }

    /**
     * Locate the insert point (note that we cannot be an empty list at this point).
     * @param pNode the node
     * @return the node before which the item should be inserted (or null to insert at end of list)
     */
    protected OrderedNode<T> findNodeAfter(final OrderedNode<T> pNode) {
        OrderedNode<T> myCurr;

        /* Access first and last nodes */
        OrderedNode<T> myFirst = theList.getHead();
        OrderedNode<T> myLast = theList.getTail();

        /* Check whether we should add at the end */
        if (myLast.compareTo(pNode) < 0) {
            return null;
        }

        /* Loop through the current items */
        for (myCurr = myFirst; myCurr != null; myCurr = myCurr.getNext()) {
            /* Break if we have found an element that should be later */
            if (myCurr.compareTo(pNode) >= 0) {
                break;
            }
        }

        /* Return the node */
        return myCurr;
    }

    /**
     * Locate the insert point (note that we cannot be an empty list at this point).
     * @param pNode the node
     * @return the node after which the item should be inserted (or null to insert at head of list)
     */
    protected OrderedNode<T> findNodeBefore(final OrderedNode<T> pNode) {
        OrderedNode<T> myCurr;

        /* Access first and last nodes */
        OrderedNode<T> myFirst = theList.getHead();
        OrderedNode<T> myLast = theList.getTail();

        /* Check whether we should add at the head */
        if (myFirst.compareTo(pNode) > 0) {
            return null;
        }

        /* Loop through the current items */
        for (myCurr = myLast; myCurr != null; myCurr = myCurr.getPrev()) {
            /* Break if we have found an element that should be earlier */
            if (myCurr.compareTo(pNode) <= 0) {
                break;
            }
        }

        /* Return the node */
        return myCurr;
    }

    /**
     * Register link between object and node to allow fast lookup of node from object. Standard implementation
     * is a stub.
     * @param pNode the Node
     */
    protected void registerLink(final OrderedNode<T> pNode) {
    }

    /**
     * deRegister link between object and node to allow fast lookup of node from object. Standard
     * implementation is a stub.
     * @param pNode the Node
     */
    protected void deRegisterLink(final OrderedNode<T> pNode) {
    }

    /**
     * Insert a map node.
     * @param pNode the node to insert
     */
    protected void insertNode(final OrderedNode<T> pNode) {
        /* Determine the active map length */
        theActiveMapLength = 1 + ((theList.sizeAll() - 1) / MAP_GRANULARITY);

        /* If we need to extend the map */
        if (theActiveMapLength > theMapLength - 1) {
            /* Extend the map by expansion number of entries */
            theMap = Arrays.copyOf(theMap, theMapLength + EXPANSION_SIZE);

            /* Adjust the map length */
            theMapLength += EXPANSION_SIZE;
        }

        /* Access the index of the node */
        int iIndex = pNode.getIndex();

        /* Calculate the map index */
        int iMapIndex = iIndex / MAP_GRANULARITY;

        /* If this is a mapped node */
        if ((iIndex % MAP_GRANULARITY) == 0) {
            /* Store the node into the map */
            theMap[iMapIndex] = pNode;
        }

        /* For all subsequent nodes */
        while (++iMapIndex < theMapLength) {
            /* Access the node in the map */
            OrderedNode<T> myNode = theMap[iMapIndex];

            /* Break if we have reached the end of the map */
            if (myNode == null) {
                break;
            }

            /* Shift the index to the previous item */
            theMap[iMapIndex] = myNode.getPrev();
        }

        /* Access the last node */
        OrderedNode<T> myLast = theList.getTail();

        /* If the last node has been shifted and needs storing, then store it */
        if ((pNode != myLast) && ((myLast.getIndex() % MAP_GRANULARITY) == 0)) {
            insertNode(myLast);
        }
    }

    /**
     * Remove a map node.
     * @param pNode the node to remove
     */
    protected void removeNode(final OrderedNode<T> pNode) {
        /* Determine the active map length */
        theActiveMapLength = 1 + ((theList.sizeAll() - 1) / MAP_GRANULARITY);

        /* Access the index of the node */
        int iIndex = pNode.getIndex();

        /* Calculate the map index */
        int iMapIndex = iIndex / MAP_GRANULARITY;

        /* Ignore node if it is past end of map */
        if (iMapIndex > theMapLength - 1) {
            return;
        }

        /* If this is a mapped node */
        if ((iIndex % MAP_GRANULARITY) == 0) {
            /* Adjust this node explicitly */
            theMap[iMapIndex] = pNode.getNext();
        }

        /* For all subsequent nodes */
        while (++iMapIndex < theMapLength) {
            /* Access the node in the map */
            OrderedNode<T> myNode = theMap[iMapIndex];

            /* Break if we have reached the end of the map */
            if (myNode == null) {
                break;
            }

            /* Shift the index to the next item */
            theMap[iMapIndex] = myNode.getNext();
        }
    }

    /**
     * Clear the indexMap.
     */
    protected void clear() {
        /* Reinitialise the map to null */
        Arrays.fill(theMap, null);
        theActiveMapLength = 0;
    }
}