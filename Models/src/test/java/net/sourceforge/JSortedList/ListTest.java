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
            long One = testHashMap(false);
            long Two = testHashMap(true);
            // testOrderedList();
            // testHashMapIterator();

            if (One > Two) {
                System.out.println("Great");
            }
        } catch (Exception e) {
            System.out.println("Help");
            e.printStackTrace();
        }
        System.exit(0);
    }

    protected static void testOrderedList() {
        OrderedList<Integer> myList = new OrderedList<Integer>(Integer.class, 6);
        List<Integer> myCache = new LinkedList<Integer>();

        /* Access a Random generator */
        SecureRandom myRandom = new SecureRandom();

        /* Add random elements */
        for (int i = 0; i < 1000; i++) {
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
            if ((myLast != null) && (myElement < myLast)) {
                /* Print it */
                System.out.println("Out of sequence " + myElement);
                ok = false;
            }
            myLast = myElement;
        }
        if (ok) {
            long myStart = System.nanoTime();
            System.out.println("OK " + myList.size());
            myIterator = myCache.iterator();
            while (myIterator.hasNext()) {
                /* Access the element */
                Integer myElement = myIterator.next();

                /* Remove it from the list */
                if (!myList.remove(myElement))
                    System.out.println("Failed to remove " + myElement);
            }
            System.out.println("OK " + myList.size() + ":" + (System.nanoTime() - myStart));
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
        if (myMap.size() != 0) {
            System.out.println("HelpRemove " + useNested + " " + myMap.size());
        }

        return System.nanoTime() - myStart;
    }

    protected static void testHashMapIterator() {
        /* Access timing */
        Map<String, Integer> myMap;
        Map<String, Integer> myBaseMap;
        int iNumElements = 1000;

        /* Create the maps */
        myMap = new NestedHashMap<String, Integer>(8);
        myBaseMap = new HashMap<String, Integer>();

        /* Build the map */
        for (Integer i = 0; i < iNumElements; i++) {
            myMap.put(i.toString(), i);
            myBaseMap.put(i.toString(), i);
        }

        /* Iterate over the Hash Map */
        Iterator<String> myIterator = myMap.keySet().iterator();
        while (myIterator.hasNext()) {
            String myKey = myIterator.next();
            if (myBaseMap.remove(myKey) == null) {
                System.out.println("failed to remove " + myKey);
            }
        }

        if (myBaseMap.size() != 0) {
            System.out.println("failed to remove all keys" + myBaseMap.size());
        }
    }
}
