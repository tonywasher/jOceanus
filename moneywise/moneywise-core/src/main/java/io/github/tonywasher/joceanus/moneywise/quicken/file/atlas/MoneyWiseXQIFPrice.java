/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.quicken.file.atlas;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Class representing a XQIF Price record.
 */
public class MoneyWiseXQIFPrice
        implements Comparable<MoneyWiseXQIFPrice> {
    /**
     * Item type.
     */
    protected static final String XQIF_ITEM = "Prices";

    /**
     * Quicken Quote.
     */
    private static final String XQIF_QUOTE = "\"";

    /**
     * Quicken Comma.
     */
    private static final String XQIF_COMMA = ",";

    /**
     * The file type.
     */
    private final MoneyWiseQIFType theFileType;

    /**
     * The security.
     */
    private final MoneyWiseXQIFSecurity theSecurity;

    /**
     * The date.
     */
    private final OceanusDate theDate;

    /**
     * The price.
     */
    private final OceanusPrice thePrice;

    /**
     * The element list.
     */
    private final List<MoneyWiseXQIFPrice> thePrices;

    /**
     * Constructor.
     *
     * @param pRegister the XQIF Register
     * @param pSecurity the security
     * @param pPrice    the price
     */
    public MoneyWiseXQIFPrice(final MoneyWiseXQIFRegister pRegister,
                              final MoneyWiseXQIFSecurity pSecurity,
                              final MoneyWiseSecurityPrice pPrice) {
        /* Store data */
        theFileType = pRegister.getFileType();
        theSecurity = pSecurity;
        theDate = pPrice.getDate();
        thePrice = pPrice.getPrice();
        thePrices = null;
    }

    /**
     * Constructor.
     *
     * @param pRegister  the XQIF Register
     * @param pFormatter the Data Formatter
     * @param pLine      the line
     */
    private MoneyWiseXQIFPrice(final MoneyWiseXQIFRegister pRegister,
                               final OceanusDataFormatter pFormatter,
                               final String pLine) {
        /* Split out the parts */
        final String[] myParts = pLine.split(XQIF_COMMA);

        /* Strip leading and trailing quotes */
        for (int i = 0; i < myParts.length; i++) {
            final String myStr = myParts[i];
            if (myStr.startsWith(XQIF_QUOTE)
                    && myStr.endsWith(XQIF_QUOTE)) {
                myParts[i] = myStr.substring(1, myStr.length() - 1);
            }
        }

        /* Store the data */
        theFileType = pRegister.getFileType();
        theSecurity = pRegister.getSecurityBySymbol(myParts[0]);
        theDate = pFormatter.getDateFormatter().parseDateBase(myParts[2], MoneyWiseXQIFConstants.XQIF_BASEYEAR);
        thePrice = pFormatter.getDecimalParser().parsePriceValue(myParts[1]);
        thePrices = null;
    }

    /**
     * Constructor.
     *
     * @param pRegister  the XQIF Register
     * @param pFormatter the Data Formatter
     * @param pLines     the lines
     */
    public MoneyWiseXQIFPrice(final MoneyWiseXQIFRegister pRegister,
                              final OceanusDataFormatter pFormatter,
                              final List<String> pLines) {
        /* Build the price list */
        thePrices = new ArrayList<>();

        /* Loop through the lines */
        MoneyWiseXQIFSecurity mySecurity = null;
        for (String myLine : pLines) {
            /* Create the price and add to the list */
            final MoneyWiseXQIFPrice myPrice = new MoneyWiseXQIFPrice(pRegister, pFormatter, myLine);
            mySecurity = myPrice.getSecurity();
            thePrices.add(myPrice);
        }

        /* Store the data */
        theFileType = pRegister.getFileType();
        theSecurity = mySecurity;
        theDate = null;
        thePrice = null;
    }

    /**
     * Obtain the security.
     *
     * @return the security
     */
    public MoneyWiseXQIFSecurity getSecurity() {
        return theSecurity;
    }

    /**
     * Obtain the date.
     *
     * @return the date
     */
    public OceanusDate getDate() {
        return theDate;
    }

    /**
     * Obtain the price.
     *
     * @return the price
     */
    public OceanusPrice getPrice() {
        return thePrice;
    }

    /**
     * Obtain iterator for list.
     *
     * @return the iterator
     */
    public Iterator<MoneyWiseXQIFPrice> priceIterator() {
        return thePrices.iterator();
    }

    /**
     * Format record.
     *
     * @param pFormatter the formatter
     * @param pBuilder   the string builder
     */
    protected void formatRecord(final OceanusDataFormatter pFormatter,
                                final StringBuilder pBuilder) {
        /* Format the security */
        pBuilder.append(XQIF_QUOTE);
        pBuilder.append(theSecurity.getSymbol());
        pBuilder.append(XQIF_QUOTE);
        pBuilder.append(XQIF_COMMA);

        /* Format the price */
        if (theFileType.escapePrices()) {
            pBuilder.append(XQIF_QUOTE);
        }
        pBuilder.append(thePrice.toString());
        if (theFileType.escapePrices()) {
            pBuilder.append(XQIF_QUOTE);
        }
        pBuilder.append(XQIF_COMMA);

        /* Format the date */
        pBuilder.append(XQIF_QUOTE);
        pBuilder.append(pFormatter.formatObject(theDate));
        pBuilder.append(XQIF_QUOTE);
        pBuilder.append(MoneyWiseXQIFRecord.XQIF_EOL);

        /* Add the end of record indicator */
        pBuilder.append(MoneyWiseXQIFRecord.XQIF_EOI);
        pBuilder.append(MoneyWiseXQIFRecord.XQIF_EOL);
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
        final MoneyWiseXQIFPrice myLine = (MoneyWiseXQIFPrice) pThat;

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
        return Objects.hash(theSecurity, thePrice, theDate);
    }

    @Override
    public int compareTo(final MoneyWiseXQIFPrice pThat) {
        return theDate.compareTo(pThat.getDate());
    }
}
