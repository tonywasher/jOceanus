/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jmetis.lethe.list.MetisNestedHashMap;
import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedList;

/**
 * Test suite for jSortedList.
 * @author Tony Washer
 */
public class MetisListTester {
    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Create and show the GUI.
     */
    public static void createAndShowGUI() {
        try {
            /*
             * for (int i = 90; i < 100; i++) { int iIndex = binaryChop(i); if (iIndex != (i / 10)) System.out.println(iIndex); }
             */
            // long One = testHashMap(false);
            // long Two = testHashMap(true);
            testOrderedList();
            // testHashMapIterator();

            // if (One > Two) {
            // System.out.println("Great");
            // }
        } catch (Exception e) {
            System.out.println("Help");
            e.printStackTrace();
        }
        System.exit(0);
    }

    protected static void testOrderedList() {
        MetisOrderedList<Integer> myList = new MetisOrderedList<Integer>(Integer.class, 6);
        List<Integer> myCache = new LinkedList<Integer>();

        /* Access a Random generator */
        SecureRandom myRandom = new SecureRandom();

        /* Add random elements */
        long myStart = System.nanoTime();
        for (int i = 0; i < 30000; i++) {
            Integer j = myRandom.nextInt();
            myList.add(j);
            myCache.add(j);
        }
        System.out.println("InsertOK "
                           + myList.size()
                           + ":"
                           + (System.nanoTime() - myStart));

        /* Loop through the list elements */
        Integer myLast = null;
        boolean ok = true;
        Iterator<Integer> myIterator = myList.iterator();
        while (myIterator.hasNext()) {
            /* Access the element */
            Integer myElement = myIterator.next();
            if ((myLast != null)
                && (myElement < myLast)) {
                /* Print it */
                System.out.println("Out of sequence "
                                   + myElement);
                ok = false;
            }
            myLast = myElement;
        }
        if (ok) {
            myStart = System.nanoTime();
            myIterator = myCache.iterator();
            while (myIterator.hasNext()) {
                /* Access the element */
                Integer myElement = myIterator.next();

                /* Remove it from the list */
                if (!myList.remove(myElement))
                    System.out.println("Failed to remove "
                                       + myElement);
            }
            System.out.println("RemoveOK "
                               + myList.size()
                               + ":"
                               + (System.nanoTime() - myStart));
        }
    }

    protected static long testHashMap(boolean useNested) {
        /* Access timing */
        long myStart = System.nanoTime();
        Map<String, Integer> myMap;
        int iNumElements = 100;
        int iNumLoops = 10000;

        /* Create a nested hash map */
        if (useNested) {
            myMap = new MetisNestedHashMap<String, Integer>(8);
        } else {
            myMap = new HashMap<String, Integer>();
        }

        /* Build the map */
        for (Integer i = 0; i < iNumElements; i++) {
            myMap.put(i.toString(), i);
        }

        /* Read all the entries */
        for (int j = 0; j < iNumLoops; j++) {
            for (Integer i = 0; i < iNumElements; i++) {
                Integer myValue = myMap.get(i.toString());
                if (!i.equals(myValue)) {
                    System.out.println("Help "
                                       + useNested
                                       + " "
                                       + myValue
                                       + " "
                                       + i);
                }
            }
        }

        /* Remove all the entries */
        for (Integer i = 0; i < iNumElements; i++) {
            myMap.remove(i.toString());
        }
        if (myMap.size() != 0) {
            System.out.println("HelpRemove "
                               + useNested
                               + " "
                               + myMap.size());
        }

        return System.nanoTime()
               - myStart;
    }

    protected static void testHashMapIterator() {
        /* Access timing */
        Map<String, Integer> myMap;
        Map<String, Integer> myBaseMap;
        int iNumElements = 1000;

        /* Create the maps */
        myMap = new MetisNestedHashMap<String, Integer>(8);
        myBaseMap = new HashMap<String, Integer>();

        /* Build the map */
        for (Integer i = 0; i < iNumElements; i++) {
            myMap.put(i.toString(), i);
            myBaseMap.put(i.toString(), i);
        }

        /* Add null key */
        myMap.put(null, -10);
        myBaseMap.put(null, -10);

        /* Add null value */
        myMap.put("nullValue", null);
        myBaseMap.put("nullValue", null);

        if ((!myMap.containsKey(null))
            || (!myMap.containsValue(null))) {
            System.out.println("failed to check null keyy/value");
        }

        if ((myMap.containsKey("BadKey"))
            || (myMap.containsValue(-14))) {
            System.out.println("failed to check missing key/value with nulls");
        }

        /* Iterate over the Hash Map */
        Iterator<String> myIterator = myMap.keySet().iterator();
        while (myIterator.hasNext()) {
            String myKey = myIterator.next();
            if ((myBaseMap.remove(myKey) == null)
                && (!myKey.equals("nullValue"))) {
                System.out.println("failed to remove "
                                   + myKey);
            }
        }

        if (myBaseMap.size() != 0) {
            System.out.println("failed to remove all keys"
                               + myBaseMap.size());
        }
    }
}
