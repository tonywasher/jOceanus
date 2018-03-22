package net.sourceforge.joceanus.jmetis;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellRange;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetWorkBookType;
import net.sourceforge.joceanus.jtethys.OceanusException;

public class MetisOasisJDom2 {
    /**
     * The office nameSpace.
     */
    private static Namespace OFFICENS;

    /**
     * The table nameSpace.
     */
    private static Namespace TABLENS;

    /**
     * The text nameSpace.
     */
    private static Namespace TEXTNS;

    /**
     * The rangeMap.
     */
    private static Map<String, MetisSheetCellRange> RANGEMAP = new HashMap<>();

    /**
     * The sheetMap.
     */
    private static Map<String, Element> SHEETMAP = new HashMap<>();

    /**
     * Main entry point.
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        Document myDoc = loadSpreadSheet(new File("C:\\Users\\Tony\\Documents\\MoneyWiseNI.ods"));
        Element myRoot = myDoc.getRootElement();
        OFFICENS = myRoot.getNamespace("office");
        TABLENS = myRoot.getNamespace("table");
        TEXTNS = myRoot.getNamespace("text");
        Element myBody = myRoot.getChild("body", OFFICENS);
        Element myWorkbook = myBody.getChild("spreadsheet", OFFICENS);
        buildRangeMap(myWorkbook);
        buildSheetMap(myWorkbook);
        displayRange("Finance82");
    }

    /**
     * Load an ODS spreadSheet.
     * @return the parsed document
     */
    private static Document loadSpreadSheet(final File pFile) {
        /* Protect against exceptions */
        try (FileInputStream myInFile = new FileInputStream(pFile);
             BufferedInputStream myInBuffer = new BufferedInputStream(myInFile);
             ZipInputStream myZipStream = new ZipInputStream(myInBuffer)) {
            /* Loop through the Zip file entries */
            ZipEntry myEntry;
            for (;;) {
                /* Read next entry */
                myEntry = myZipStream.getNextEntry();

                /* If this is EOF or a header record break the loop */
                if (myEntry == null) {
                    break;
                }

                /* If we have found the contents */
                if ("content.xml".equals(myEntry.getName())) {
                    SAXBuilder myBuilder = new SAXBuilder();
                    return myBuilder.build(myZipStream);
                }
            }

        } catch (IOException
                | JDOMException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Build named-range map.
     * @param pSpreadsheet document
     */
    private static void buildRangeMap(final Element pSpreadsheet) {
        Element myRanges = pSpreadsheet.getChild("named-expressions", TABLENS);
        for (Element myRange : myRanges.getChildren("named-range", TABLENS)) {
            MetisSheetCellRange myCellRange = new MetisSheetCellRange(myRange.getAttributeValue("cell-range-address", TABLENS));
            RANGEMAP.put(myRange.getAttributeValue("name", TABLENS), myCellRange);
        }
    }

    /**
     * Declare the named range.
     * @param pSpreadsheet document
     * @param pName the name of the range
     * @param pRange the range to declare
     * @throws OceanusException on error
     */
    protected void declareRange(final Element pSpreadsheet,
                                final String pName,
                                final MetisSheetCellRange pRange) throws OceanusException {
        /* Check for existing range */
        if (RANGEMAP.get(pName) != null) {
            throw new MetisLogicException("Name "
                                          + pName
                                          + "already exists in workbook");
        }

        /* Add the new range */
        Element myRanges = pSpreadsheet.getChild("named-expressions", TABLENS);
        Element myRange = new Element("named-range", TABLENS);
        myRange.setAttribute("cell-range-address", pRange.toString(), TABLENS);
        myRanges.addContent(myRange);
        RANGEMAP.put(pName, pRange);
    }

    /**
     * Obtain named-sheet map.
     * @param pSpreadsheet document
     */
    private static void buildSheetMap(final Element pSpreadsheet) {
        for (Element mySheet : pSpreadsheet.getChildren("table", TABLENS)) {
            SHEETMAP.put(mySheet.getAttributeValue("name", TABLENS), mySheet);
        }
    }

    /**
     * Display a range
     */
    private static void displayRange(final String pName) {
        /* Obtain the address */
        MetisSheetCellRange myRange = RANGEMAP.get(pName);
        if (myRange == null) {
            System.out.println(pName + " range was not found");
            return;
        }

        /* Access the sheet */
        Element mySheet = SHEETMAP.get(myRange.getFirstCell().getSheetName());
        OasisView myView = new OasisView(mySheet.getChildren("table-row", TABLENS), myRange);
        System.out.println(myView);
    }

    /**
     * A View detail
     */
    private static class OasisView {
        /**
         * The Rows.
         */
        private final List<OasisRow> theRows;

        /**
         * Constructor.
         */
        OasisView(final List<Element> pRows,
                  final MetisSheetCellRange pRange) {
            /* Create the row list */
            theRows = new ArrayList<>();

            /* Determine start and end rows */
            int myFirst = pRange.getFirstCell().getRow();
            int myLast = pRange.getLastCell().getRow();

            /* Iterate through the list */
            int iIndex = 0;
            int numSkip = myFirst - iIndex;
            Iterator<Element> myIterator = pRows.iterator();
            while (myIterator.hasNext()) {
                Element myRowElement = myIterator.next();

                /* Determine how many rows this represents */
                String myRepeatStr = myRowElement.getAttributeValue("number-rows-repeated", TABLENS);
                int myRepeat = myRepeatStr == null
                                                   ? 1
                                                   : Integer.parseInt(myRepeatStr);

                /* If we need to skip some cells */
                if (numSkip > 0) {
                    /* If we do not have enough rows */
                    if (myRepeat <= numSkip) {
                        /* Adjust and move to next row */
                        iIndex += myRepeat;
                        numSkip -= myRepeat;
                        continue;
                    }

                    /* Adjust to correct values */
                    iIndex += numSkip;
                    myRepeat -= numSkip;
                    numSkip = 0;
                }

                /* For the number of repeats */
                List<Element> myCells = myRowElement.getChildren("table-cell", TABLENS);
                for (int i = 0; i < myRepeat; i++) {
                    /* Add element */
                    theRows.add(myCells.isEmpty()
                                                  ? null
                                                  : new OasisRow(myCells, pRange));

                    /* Adjust index and return if complete */
                    iIndex++;

                    if (iIndex > myLast) {
                        return;
                    }
                }
            }
        }

        @Override
        public String toString() {
            return theRows.toString();
        }
    }

    /**
     * A Row detail
     */
    private static class OasisRow {
        /**
         * The Cells.
         */
        private final List<OasisCell> theCells;

        /**
         * Constructor.
         */
        OasisRow(final List<Element> pCells,
                 final MetisSheetCellRange pRange) {
            /* Create the cell list */
            theCells = new ArrayList<>();

            /* Determine start and end cells */
            int myFirst = pRange.getFirstCell().getColumn();
            int myLast = pRange.getLastCell().getColumn();

            /* Iterate through the list */
            int iIndex = 0;
            int numSkip = myFirst - iIndex;
            Iterator<Element> myIterator = pCells.iterator();
            while (myIterator.hasNext()) {
                Element myCellElement = myIterator.next();

                /* Determine how many cells this represents */
                String myRepeatStr = myCellElement.getAttributeValue("number-columns-repeated", TABLENS);
                int myRepeat = myRepeatStr == null
                                                   ? 1
                                                   : Integer.parseInt(myRepeatStr);

                /* If we need to skip some cells */
                if (numSkip > 0) {
                    /* If we do not have enough cells */
                    if (myRepeat <= numSkip) {
                        /* Adjust and move to next cell */
                        iIndex += myRepeat;
                        numSkip -= myRepeat;
                        continue;
                    }

                    /* Adjust to correct values */
                    iIndex += numSkip;
                    myRepeat -= numSkip;
                    numSkip = 0;
                }

                /* For the number of repeats */
                boolean isEmpty = myCellElement.getAttributeValue("value-type", OFFICENS) == null;
                for (int i = 0; i < myRepeat; i++) {
                    /* Add element */
                    theCells.add(isEmpty
                                         ? null
                                         : new OasisCell(myCellElement));

                    /* Adjust index and return if complete */
                    iIndex++;

                    if (iIndex > myLast) {
                        return;
                    }
                }
            }
        }

        @Override
        public String toString() {
            return theCells.toString() + "\n";
        }
    }

    /**
     * A Cell detail
     */
    @MetisSheetService(MetisSheetWorkBookType.EXCELXLS)
    private static class OasisCell {
        /**
         * The DataType.
         */
        private final String theDataType;

        /**
         * The Value.
         */
        private final String theValue;

        /**
         * The Text.
         */
        private final String theText;

        /**
         * Constructor.
         * @param pCell the cell
         */
        OasisCell(final Element pCell) {
            theDataType = pCell.getAttributeValue("value-type", OFFICENS);
            theValue = pCell.getAttributeValue("value", OFFICENS);
            theText = pCell.getChildTextTrim("p", TEXTNS);
        }

        @Override
        public String toString() {
            return "DT=" + theDataType + ",V=" + theValue + ",T=" + theText;
        }
    }

    /**
     * Test Annotation.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MetisSheetService {
        @SuppressWarnings("javadoc")
        MetisSheetWorkBookType value();
    }
}
