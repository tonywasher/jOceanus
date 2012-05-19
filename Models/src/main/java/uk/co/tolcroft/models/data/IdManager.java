/*******************************************************************************
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
package uk.co.tolcroft.models.data;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.PreferenceSet;
import net.sourceforge.JDataManager.PreferenceSet.PreferenceManager;
import net.sourceforge.JDataManager.PreferenceSet.PreferenceSetChooser;

public class IdManager<T extends DataItem<T>> implements PreferenceSetChooser {
    /**
     * IdManager Preferences
     */
    private IdManagerPreferences thePreferences = null;

    /**
     * The maximum id
     */
    private int theMaxId = 0;

    /**
     * The id map
     */
    private idMap theMap = null;

    /**
     * Constructor
     */
    protected IdManager() {
        /* Access the idManager preferences */
        thePreferences = (IdManagerPreferences) PreferenceManager.getPreferenceSet(this);
        theMap = new idMap();
    }

    @Override
    public Class<? extends PreferenceSet> getPreferenceSetClass() {
        return IdManagerPreferences.class;
    }

    /**
     * Get Max Id
     * @return the Maximum Id
     */
    protected int getMaxId() {
        return theMaxId;
    }

    /**
     * Set Max Id
     * @param uMaxId the Maximum Id
     */
    protected void setMaxId(int uMaxId) {
        if (uMaxId > theMaxId)
            theMaxId = uMaxId;
    }

    /**
     * Is the Id unique in this list
     * @param uId the Id to check
     * @return Whether the id is unique <code>true/false</code>
     */
    protected boolean isIdUnique(int uId) {
        /* Its unique if its unassigned or greater than the max id */
        if ((uId == 0) || (uId > theMaxId))
            return true;

        /* Locate the id if its possible that we have it */
        T myItem = getItem(uId);

        /* Return result */
        return (myItem == null);
    }

    /**
     * Generate/Record new id
     * @param pItem the item
     */
    protected void setNewId(T pItem) {
        int myId = pItem.getId();

        /* If we need to generate a new id */
        if (myId == 0) {
            /* Increment and use the max Id */
            theMaxId++;
            pItem.setId(theMaxId);
            pItem.setState(DataState.NEW);
        }

        /* else id is already known */
        else {
            /* Update the max Id if required */
            if (theMaxId < myId)
                theMaxId = myId;
            pItem.setState(DataState.CLEAN);
        }
    }

    /**
     * Locate the item by id
     * @param pId the id of the item to retrieve
     * @return the item for the id (or <code>null</code>)
     */
    public T getItem(int pId) {
        /* Locate the item in the map */
        return theMap.getItem(pId);
    }

    /**
     * Store the item by id
     * @param pId the id of the item to retrieve
     * @param pItem the item to store (or <code>null</code>)
     */
    public void setItem(int pId,
                        T pItem) {
        /* Store the item in the map */
        theMap.setItem(pId, pItem);
    }

    /**
     * remove All list items
     */
    public void clear() {
        /* Reinitialise the map */
        theMap = new idMap();
    }

    /**
     * IdManager Preferences
     */
    public static class IdManagerPreferences extends PreferenceSet {
        /**
         * Registry name for Node Elements
         */
        protected final static String nameNodeElems = "NodeElements";

        /**
         * Display name for Node Elements
         */
        protected final static String dispNodeElems = "Elements per Node";

        /**
         * Default NodeElements
         */
        private final static Integer defNodeElems = 10;

        /**
         * Constructor
         * @throws ModelException
         */
        public IdManagerPreferences() throws ModelException {
            super();
        }

        @Override
        protected void definePreferences() {
            /* Define the preferences */
            definePreference(nameNodeElems, PreferenceType.Integer);
        }

        @Override
        protected Object getDefaultValue(String pName) {
            /* Handle default values */
            if (pName.equals(nameNodeElems))
                return defNodeElems;
            return null;
        }

        @Override
        protected String getDisplayName(String pName) {
            /* Handle default values */
            if (pName.equals(nameNodeElems))
                return dispNodeElems;
            return null;
        }
    }

    /**
     * Id map to element
     */
    private class idMap {
        /**
         * The size of a single map element
         */
        private final int maxElements = thePreferences.getIntegerValue(IdManagerPreferences.nameNodeElems);

        /**
         * The depth of this map
         */
        private int theDepth = 0;

        /**
         * The map indexed by id
         */
        private idMap[] theMaps = null;

        /**
         * The map indexed by id
         */
        private T[] theObjects = null;

        /**
         * Build a new map array
         */
        @SuppressWarnings("unchecked")
        private void newMaps() {
            theMaps = new IdManager.idMap[maxElements];
            java.util.Arrays.fill(theMaps, null);
        }

        /**
         * Build a new objects array
         */
        @SuppressWarnings("unchecked")
        private void newObjects() {
            theObjects = (T[]) new DataItem[maxElements];
            java.util.Arrays.fill(theObjects, null);
        }

        /**
         * Locate the item by id
         * @param pId the id of the item to retrieve
         * @return the item for the id (or <code>null</code>)
         */
        private T getItem(int pId) {
            int myIndex = pId;
            int myId = pId;
            int myAdjust = 1;
            idMap myMap;

            /* If we are not the final map pass the call on */
            if (theDepth > 0) {
                /* Loop to find the index in this map */
                for (int i = theDepth; i > 0; i--) {
                    myIndex /= maxElements;
                    myAdjust *= maxElements;
                }

                /* If we are beyond the scope of this map return null */
                if (myIndex >= maxElements)
                    return null;

                /* Access the relevant map */
                myMap = theMaps[myIndex];

                /* If the map is empty return null */
                if (myMap == null)
                    return null;

                /* Adjust the index */
                myId -= (myIndex * myAdjust);

                /* Pass the call on */
                return myMap.getItem(myId);
            }

            /* If we are beyond the scope of this map return null */
            if (myIndex >= maxElements)
                return null;

            /* Return null if we have no objects */
            if (theObjects == null)
                return null;

            /* Calculate final index and return item */
            myIndex = pId % maxElements;
            return theObjects[myIndex];
        }

        /**
         * Store the item by id
         * @param pId the id of the item to retrieve
         * @param pItem the item to store (or <code>null</code>)
         */
        private void setItem(int pId,
                             T pItem) {
            int myIndex = pId;
            int myId = pId;
            int myAdjust = 1;
            idMap myMap = null;

            /* Loop to find the index in this map */
            for (int i = theDepth; i > 0; i--) {
                myIndex /= maxElements;
                myAdjust *= maxElements;
            }

            /* If we are beyond the scope of this map */
            if (myIndex >= maxElements) {
                /* If this map is non-empty */
                if ((theMaps != null) || (theObjects != null)) {
                    /* Create a sub-map based on this one */
                    myMap = new idMap();
                    myMap.theDepth = theDepth;
                    myMap.theMaps = theMaps;
                    myMap.theObjects = theObjects;
                }

                /* Increase the depth of this map */
                theDepth++;

                /* Create a new maps array for this map */
                newMaps();

                /* Store the map */
                theMaps[0] = myMap;

                /* Try with the adjusted map depth */
                setItem(pId, pItem);
                return;
            }

            /* If we are not the final map */
            if (theDepth > 0) {
                /* Access the relevant map */
                myMap = theMaps[myIndex];

                /* If the map is empty */
                if (myMap == null) {
                    /* Create and store a new id map */
                    myMap = new idMap();
                    myMap.newMaps();
                    myMap.theDepth = theDepth - 1;
                    theMaps[myIndex] = myMap;
                }

                /* Adjust the index */
                myId -= (myIndex * myAdjust);

                /* Pass the call on with adjusted id */
                myMap.setItem(myId, pItem);
                return;
            }

            /* Allocate the objects array if needed */
            if (theObjects == null)
                newObjects();

            /* Store the item */
            myIndex = pId % maxElements;
            theObjects[myIndex] = pItem;
        }
    }
}
