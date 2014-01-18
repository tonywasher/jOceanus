/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice;

/**
 * Class representing a QIF Account record.
 */
public class QIFPrice {
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
     * The security.
     */
    private final QIFSecurity theSecurity;

    /**
     * The date.
     */
    private final JDateDay theDate;

    /**
     * The price.
     */
    private final JPrice thePrice;

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pPrice the price
     */
    protected QIFPrice(final QIFFile pFile,
                       final AccountPrice pPrice) {
        /* Store data */
        theSecurity = pFile.getSecurity(pPrice.getAccountName());
        theDate = pPrice.getDate();
        thePrice = pPrice.getPrice();
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pFormatter the Data Formatter
     * @param pLine the line
     */
    protected QIFPrice(final QIFFile pFile,
                       final JDataFormatter pFormatter,
                       final String pLine) {
        /* Split out the parts */
        String[] myParts = pLine.split(QIF_COMMA);

        /* Strip leading and trailing quotes */
        for (int i = 0; i < myParts.length; i++) {
            String myStr = myParts[i];
            if ((myStr.startsWith(QIF_COMMA))
                && (myStr.endsWith(QIF_COMMA))) {
                myParts[i] = myStr.substring(1, myStr.length() - 2);
            }
        }

        /* Store the data */
        theSecurity = pFile.getSecurity(myParts[0]);
        theDate = pFormatter.getDateFormatter().parseDateDay(myParts[2]);
        thePrice = pFormatter.getDecimalParser().parsePriceValue(myParts[1]);
    }

    /**
     * Format record.
     * @param pFormatter the formatter
     * @param pBuilder the string builder
     */
    protected void formatRecord(final JDataFormatter pFormatter,
                                final StringBuilder pBuilder) {
        /* Format the security */
        pBuilder.append(QIF_QUOTE);
        pBuilder.append(theSecurity.getSymbol());
        pBuilder.append(QIF_QUOTE);
        pBuilder.append(QIF_COMMA);

        /* Format the security */
        pBuilder.append(QIF_QUOTE);
        pBuilder.append(thePrice.toString());
        pBuilder.append(QIF_QUOTE);
        pBuilder.append(QIF_COMMA);

        /* Format the date */
        pBuilder.append(QIF_QUOTE);
        pBuilder.append(pFormatter.formatObject(theDate));
        pBuilder.append(QIF_QUOTE);
        pBuilder.append(QIFRecord.QIF_EOL);
    }
}
