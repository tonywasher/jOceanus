/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.file;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

/**
 * Class representing a QIF Price record.
 */
public class QIFPrice
        implements Comparable<QIFPrice> {
    /**
     * Item type.
     */
    protected static final String QIF_ITEM = "Prices";

    /**
     * Quicken Quote.
     */
    private static final String QIF_QUOTE = "\"";

    /**
     * Quicken Comma.
     */
    private static final String QIF_COMMA = ",";

    /**
     * The file type.
     */
    private final QIFType theFileType;

    /**
     * The security.
     */
    private final QIFSecurity theSecurity;

    /**
     * The date.
     */
    private final TethysDate theDate;

    /**
     * The price.
     */
    private final TethysPrice thePrice;

    /**
     * The element list.
     */
    private final List<QIFPrice> thePrices;

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pSecurity the security
     * @param pPrice the price
     */
    protected QIFPrice(final QIFFile pFile,
                       final QIFSecurity pSecurity,
                       final SecurityPrice pPrice) {
        /* Store data */
        theFileType = pFile.getFileType();
        theSecurity = pSecurity;
        theDate = pPrice.getDate();
        thePrice = pPrice.getPrice();
        thePrices = null;
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pFormatter the Data Formatter
     * @param pLine the line
     */
    private QIFPrice(final QIFFile pFile,
                     final MetisDataFormatter pFormatter,
                     final String pLine) {
        /* Split out the parts */
        String[] myParts = pLine.split(QIF_COMMA);

        /* Strip leading and trailing quotes */
        for (int i = 0; i < myParts.length; i++) {
            String myStr = myParts[i];
            if ((myStr.startsWith(QIF_QUOTE))
                && (myStr.endsWith(QIF_QUOTE))) {
                myParts[i] = myStr.substring(1, myStr.length() - 1);
            }
        }

        /* Store the data */
        theFileType = pFile.getFileType();
        theSecurity = pFile.getSecurityBySymbol(myParts[0]);
        theDate = pFormatter.getDateFormatter().parseDateBase(myParts[2], QIFWriter.QIF_BASEYEAR);
        thePrice = pFormatter.getDecimalParser().parsePriceValue(myParts[1]);
        thePrices = null;
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pFormatter the Data Formatter
     * @param pLines the lines
     */
    protected QIFPrice(final QIFFile pFile,
                       final MetisDataFormatter pFormatter,
                       final List<String> pLines) {
        /* Build the price list */
        thePrices = new ArrayList<>();

        /* Loop through the lines */
        QIFSecurity mySecurity = null;
        for (String myLine : pLines) {
            /* Create the price and add to the list */
            QIFPrice myPrice = new QIFPrice(pFile, pFormatter, myLine);
            mySecurity = myPrice.getSecurity();
            thePrices.add(myPrice);
        }

        /* Store the data */
        theFileType = pFile.getFileType();
        theSecurity = mySecurity;
        theDate = null;
        thePrice = null;
    }

    /**
     * Obtain the security.
     * @return the security
     */
    public QIFSecurity getSecurity() {
        return theSecurity;
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public TethysDate getDate() {
        return theDate;
    }

    /**
     * Obtain the price.
     * @return the security
     */
    public TethysPrice getPrice() {
        return thePrice;
    }

    /**
     * Obtain iterator for list.
     * @return the iterator
     */
    public Iterator<QIFPrice> priceIterator() {
        return thePrices.iterator();
    }

    /**
     * Format record.
     * @param pFormatter the formatter
     * @param pBuilder the string builder
     */
    protected void formatRecord(final MetisDataFormatter pFormatter,
                                final StringBuilder pBuilder) {
        /* Format the security */
        pBuilder.append(QIF_QUOTE);
        pBuilder.append(theSecurity.getSymbol());
        pBuilder.append(QIF_QUOTE);
        pBuilder.append(QIF_COMMA);

        /* Format the price */
        if (theFileType.escapePrices()) {
            pBuilder.append(QIF_QUOTE);
        }
        pBuilder.append(thePrice.toString());
        if (theFileType.escapePrices()) {
            pBuilder.append(QIF_QUOTE);
        }
        pBuilder.append(QIF_COMMA);

        /* Format the date */
        pBuilder.append(QIF_QUOTE);
        pBuilder.append(pFormatter.formatObject(theDate));
        pBuilder.append(QIF_QUOTE);
        pBuilder.append(QIFRecord.QIF_EOL);

        /* Add the end of record indicator */
        pBuilder.append(QIFRecord.QIF_EOI);
        pBuilder.append(QIFRecord.QIF_EOL);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!getClass().equals(pThat.getClass())) {
            return false;
        }

        /* Cast correctly */
        QIFPrice myLine = (QIFPrice) pThat;

        /* Check security */
        if (!getSecurity().equals(myLine.getSecurity())) {
            return false;
        }

        /* Check price */
        if (!getPrice().equals(myLine.getPrice())) {
            return false;
        }

        /* Check date */
        return theDate.equals(myLine.getDate());
    }

    @Override
    public int hashCode() {
        int myResult = QIFFile.HASH_BASE * theSecurity.hashCode();
        myResult += thePrice.hashCode();
        myResult *= QIFFile.HASH_BASE;
        return myResult + theDate.hashCode();
    }

    @Override
    public int compareTo(final QIFPrice pThat) {
        return theDate.compareTo(pThat.getDate());
    }
}
