package net.sourceforge.JSortedList;

import java.util.Map.Entry;

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
            for (int i = 90; i < 100; i++) {
                int iIndex = binaryChop(i);
                if (iIndex != (i / 10))
                    System.out.println(iIndex);
            }
        } catch (Exception e) {
            System.out.println("Help");
            e.printStackTrace();
        }
        System.exit(0);
    }

    protected static void testHashMap() {
        /* Create a nested hash map */
        NestedHashMap<String, String> myMap = new NestedHashMap<String, String>();

        /* Put a few entries in */
        myMap.put("FirstEntry", "FirstValue");
        myMap.put("SecondEntry", "SecondValue");
        myMap.put("ThirdEntry", "FirstValue");
        myMap.put("FourthEntry", "SecondValue");
        myMap.put("FifthEntry", "FirstValue");
        myMap.put("SixthEntry", "SecondValue");
        myMap.put("SeventhEntry", "FirstValue");
        myMap.put("EighthEntry", "SecondValue");
        myMap.put("NinthEntry", "FirstValue");
        myMap.put("TenthEntry", "SecondValue");
        myMap.put("EleventhEntry", "FirstValue");
        myMap.put("TwelfthEntry", "SecondValue");
        myMap.put("ThirteenthEntry", "FirstValue");
        myMap.put("FourteenthEntry", "SecondValue");
        myMap.put("FifthteenthEntry", "FirstValue");
        myMap.put("SixteenthEntry", "SecondValue");

        /* Loop through the entries */
        for (Entry<String, String> myEntry : myMap.entrySet()) {
            System.out.println(myEntry.getKey());
        }
    }

    static Integer[] myTable = { 0, 10, 20, 30, 40, 50, 60, 70, 80, 90 };

    private static int binaryChop(Integer pValue) {
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
