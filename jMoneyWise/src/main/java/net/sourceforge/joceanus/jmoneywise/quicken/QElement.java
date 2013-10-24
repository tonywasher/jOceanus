/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.quicken;

import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QLineType;

/**
 * Quicken item.
 */
public abstract class QElement {
    /**
     * Quicken Item type.
     */
    protected static final String QIF_ITEMTYPE = "!Type:";

    /**
     * Quicken End of Item indicator.
     */
    protected static final String QIF_EOI = "^";

    /**
     * Quicken New line.
     */
    protected static final char QIF_EOL = '\n';

    /**
     * Transfer begin char.
     */
    protected static final char QIF_XFERSTART = '[';

    /**
     * Transfer end char.
     */
    protected static final char QIF_XFEREND = ']';

    /**
     * AutoExpense name suffix.
     */
    protected static final String QIF_AUTOEXPSFX = "Expense";

    /**
     * String Builder.
     */
    private final StringBuilder theBuilder;

    /**
     * Data Formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * QIF file type.
     */
    private final QIFType theQIFType;

    /**
     * Obtain the data Formatter.
     * @return the formatter
     */
    protected JDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the QIF type.
     * @return the QIF type
     */
    protected QIFType getQIFType() {
        return theQIFType;
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pType the QIF type
     */
    protected QElement(final JDataFormatter pFormatter,
                       final QIFType pType) {
        /* Store parameters */
        theFormatter = pFormatter;
        theQIFType = pType;

        /* Create builder */
        theBuilder = new StringBuilder();
    }

    /**
     * Reset builder.
     */
    protected void reset() {
        /* Reset */
        theBuilder.setLength(0);
    }

    /**
     * Get Buffered String.
     * @return the buffered string
     */
    protected String getBufferedString() {
        /* Return the string */
        return theBuilder.toString();
    }

    /**
     * Build string.
     */
    protected void endItem() {
        /* End the item */
        append(QIF_EOI);
        endLine();
    }

    /**
     * Complete the item.
     * @return the completed item
     */
    protected String completeItem() {
        /* End the item */
        endItem();

        /* Return the string */
        return theBuilder.toString();
    }

    /**
     * Append String to String Builder.
     * @param pValue string to append.
     */
    protected void append(final String pValue) {
        theBuilder.append(pValue);
    }

    /**
     * Append Character to String Builder.
     * @param pValue character to append.
     */
    protected void append(final char pValue) {
        theBuilder.append(pValue);
    }

    /**
     * End line.
     */
    protected void endLine() {
        append(QIF_EOL);
    }

    /**
     * Add Line Type.
     * @param pType the line type
     */
    protected void addLineType(final QLineType pType) {
        append(pType.getSymbol());
    }

    /**
     * Add Flag.
     * @param pType the line type
     */
    protected void addFlag(final QLineType pType) {
        addLineType(pType);
        endLine();
    }

    /**
     * Add Date.
     * @param pDate the date value
     */
    protected void addDate(final JDateDay pDate) {
        append(theFormatter.formatObject(pDate));
    }

    /**
     * Add Date Line.
     * @param pType the line type
     * @param pDate the date value
     */
    protected void addDateLine(final QLineType pType,
                               final JDateDay pDate) {
        addLineType(pType);
        addDate(pDate);
        endLine();
    }

    /**
     * Add Decimal.
     * @param pValue the decimal value
     */
    protected void addDecimal(final JDecimal pValue) {
        append(theFormatter.formatObject(pValue));
    }

    /**
     * Add Simple Decimal.
     * @param pValue the decimal value
     */
    protected void addSimpleDecimal(final JDecimal pValue) {
        append(pValue.toString());
    }

    /**
     * Add Decimal Line.
     * @param pType the line type
     * @param pValue the decimal value
     */
    protected void addDecimalLine(final QLineType pType,
                                  final JDecimal pValue) {
        addLineType(pType);
        addDecimal(pValue);
        endLine();
    }

    /**
     * Add Account.
     * @param pAccount the account
     */
    protected void addAccount(final Account pAccount) {
        append(pAccount.getName());
    }

    /**
     * Add Account Line.
     * @param pType the line type
     * @param pAccount the account
     */
    protected void addAccountLine(final QLineType pType,
                                  final Account pAccount) {
        addLineType(pType);
        addAccount(pAccount);
        endLine();
    }

    /**
     * Add Auto Account Line.
     * @param pType the line type
     * @param pAccount the account
     */
    protected void addAutoAccountLine(final QLineType pType,
                                      final Account pAccount) {
        addLineType(pType);
        addAccount(pAccount);
        append(QIF_AUTOEXPSFX);
        endLine();
    }

    /**
     * Add Transfer Account Line.
     * @param pType the line type
     * @param pAccount the account
     */
    protected void addXferAccountLine(final QLineType pType,
                                      final Account pAccount) {
        addLineType(pType);
        append(QIF_XFERSTART);
        addAccount(pAccount);
        append(QIF_XFEREND);
        endLine();
    }

    /**
     * Add Category.
     * @param pCategory the category
     */
    protected void addCategory(final EventCategory pCategory) {
        append(pCategory.getName());
    }

    /**
     * Add Category Line.
     * @param pType the line type
     * @param pCategory the category
     */
    protected void addCategoryLine(final QLineType pType,
                                   final EventCategory pCategory) {
        addLineType(pType);
        addCategory(pCategory);
        endLine();
    }

    /**
     * Add String Line.
     * @param pType the line type
     * @param pDetail the line detail
     */
    protected void addStringLine(final QLineType pType,
                                 final String pDetail) {
        addLineType(pType);
        append(pDetail);
        endLine();
    }

    /**
     * Add String Line.
     * @param pType the line type
     * @param pEnum the enum
     */
    protected void addEnumLine(final QLineType pType,
                               final Enum<?> pEnum) {
        addStringLine(pType, pEnum.toString());
    }
}
