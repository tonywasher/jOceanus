package net.sourceforge.JSortedList;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

public class ListTest {
    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static void createAndShowGUI() {
        try {
            /*
             * for (int i = 90; i < 100; i++) { int iIndex = binaryChop(i); if (iIndex != (i / 10))
             * System.out.println(iIndex); }
             */
            // long One = testHashMap(false);
            // long Two = testHashMap(true);
            testOrderedList();

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
        OrderedList<Integer> myList = new OrderedList<Integer>(Integer.class);
        List<Integer> myCache = new LinkedList<Integer>();

        /* Access a Random generator */
        SecureRandom myRandom = new SecureRandom();

        /* Add 10 random elements */
        for (int i = 0; i < 10000; i++) {
            Integer j = myRandom.nextInt();
            myList.add(j);
            myCache.add(j);
        }

        /* Loop through the list elements */
        Integer myLast = null;
        boolean ok = true;
        Iterator<Integer> myIterator = myList.iterator();
        while (myIterator.hasNext()) {
            /* Access the element */
            Integer myElement = myIterator.next();
            if (myLast == null)
                continue;
            if (myElement < myLast) {
                /* Print it */
                System.out.println("Out of sequence " + myElement);
                ok = false;
            }
        }
        if (ok) {
            long myStart = System.nanoTime();
            System.out.println("OK " + myList.size());
            myIterator = myList.iterator();
            while (myIterator.hasNext()) {
                /* Access the element */
                Integer myElement = myIterator.next();

                /* Remove it from the list */
                if (!myCache.remove(myElement))
                    System.out.println("Failed to remove " + myElement);
            }
            System.out.println("OK " + myList.size() + ":" + (System.nanoTime() - myStart));
        }
    }

    protected static long testHashMap(boolean useNested) {
        /* Access timing */
        long myStart = System.nanoTime();
        Map<String, Integer> myMap;
        int iNumElements = 10000;
        int iNumLoops = 100;

        /* Create a nested hash map */
        if (useNested) {
            myMap = new NestedHashMap<String, Integer>(8);
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
                    System.out.println("Help " + useNested + " " + myValue + " " + i);
                }
            }
        }

        /* Remove all the entries */
        for (Integer i = 0; i < iNumElements; i++) {
            myMap.remove(i.toString());
        }

        return System.nanoTime() - myStart;
    }

    static Integer[] myTable = { 0, 10, 20, 30, 40, 50, 60, 70, 80, 90 };

    protected static int binaryChop(Integer pValue) {
        int iMinimum = 0;
        int iMaximum = myTable.length - 1;

        /* Binary chop to find the search start point */
        while (iMinimum < iMaximum - 1) {
            /* Access test item */
            int iTest = (iMinimum + iMaximum) >>> 1;
            Integer myTest = myTable[iTest];

            /* Check it against the object */
            int iDiff = myTest.compareTo(pValue);
            if (iDiff == 0)
                return iTest;

            /* Adjust limits */
            if (iDiff < 0)
                iMinimum = iTest;
            else
                iMaximum = iTest;
        }

        return iMinimum;
    }
}
