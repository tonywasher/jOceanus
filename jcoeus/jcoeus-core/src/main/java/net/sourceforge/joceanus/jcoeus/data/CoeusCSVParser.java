/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
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
package net.sourceforge.joceanus.jcoeus.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * CSV Parser.
 */
public abstract class CoeusCSVParser {
    /**
     * The Quote Character.
     */
    private static final char QUOTE_CHAR = '"';

    /**
     * The Quote String.
     */
    private static final char COMMA_CHAR = ',';

    /**
     * The Quote String.
     */
    private static final String QUOTE_STR = Character.toString(QUOTE_CHAR);

    /**
     * Headers.
     */
    private final String[] theHeaders;

    /**
     * StringBuilder.
     */
    private final StringBuilder theBuilder;

    /**
     * Date Parser.
     */
    private final TethysDateFormatter theDateParser;

    /**
     * Decimal Parser.
     */
    private final TethysDecimalParser theDecimalParser;

    /**
     * The Decimal size.
     */
    private int theDecimalSize;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pHeaders the expected headers
     */
    public CoeusCSVParser(final MetisDataFormatter pFormatter,
                          final String[] pHeaders) {
        /* Store the headers */
        theHeaders = pHeaders;

        /* Create the string builder */
        theBuilder = new StringBuilder();

        /* Access the formatters */
        theDateParser = pFormatter.getDateFormatter();
        theDecimalParser = pFormatter.getDecimalParser();
    }

    /**
     * Parse a date value.
     * @param pInput the input string
     * @return the parsed date
     * @throws OceanusException on error
     */
    public TethysDate parseDate(final String pInput) throws OceanusException {
        try {
            return theDateParser.parseDateDay(pInput);
        } catch (IllegalArgumentException e) {
            throw new CoeusDataException("Bad date", e);
        }
    }

    /**
     * Parse a decimal value.
     * @param pInput the input string
     * @return the parsed decimal
     * @throws OceanusException on error
     */
    public TethysDecimal parseDecimal(final String pInput) throws OceanusException {
        try {
            return theDecimalParser.parseDecimalValue(pInput, theDecimalSize);
        } catch (IllegalArgumentException e) {
            throw new CoeusDataException("Bad decimal", e);
        }
    }

    /**
     * Parse a money.
     * @param pInput the input string
     * @return the parsed money
     * @throws OceanusException on error
     */
    public TethysMoney parseMoney(final String pInput) throws OceanusException {
        try {
            return theDecimalParser.parseMoneyValue(pInput);
        } catch (IllegalArgumentException e) {
            throw new CoeusDataException("Bad money", e);
        }
    }

    /**
     * Parse a rate.
     * @param pInput the input string
     * @return the parsed rate
     * @throws OceanusException on error
     */
    public TethysRate parseRate(final String pInput) throws OceanusException {
        try {
            return theDecimalParser.parseRateValue(pInput);
        } catch (IllegalArgumentException e) {
            throw new CoeusDataException("Bad rate", e);
        }
    }

    /**
     * Set the Date format.
     * @param pFormat the format
     */
    protected void setDateFormat(final String pFormat) {
        theDateParser.setFormat(pFormat);
    }

    /**
     * Set the Decimal size.
     * @param pSize the size
     */
    protected void setDecimalSize(final int pSize) {
        theDecimalSize = pSize;
    }

    /**
     * Parse file.
     * @param pInput the input file
     * @throws OceanusException on error
     */
    public void parseFile(final File pInput) throws OceanusException {
        /* Create a StringBuilder for the line */
        StringBuilder myBuilder = new StringBuilder();

        /* Reset the fields */
        resetFields();

        /* Protect against exceptions */
        try (FileInputStream myInput = new FileInputStream(pInput);
             InputStreamReader myInputReader = new InputStreamReader(myInput, StandardCharsets.UTF_8);
             BufferedReader myReader = new BufferedReader(myInputReader)) {

            /* Loop through the file */
            for (;;) {
                /* Read next line and exit on EOF */
                String myLine = myReader.readLine();
                if (myLine == null) {
                    break;
                }

                /* Add to the current buffer */
                myBuilder.append(myLine);
                int myCount = countQuotes(myBuilder);

                /* If we have an even number of quotes, then we have a full line */
                if ((myCount % 2) == 0) {
                    /* Parse the line and clear the buffer */
                    List<String> myFields = parseLine(myBuilder);
                    myBuilder.setLength(0);

                    /* Check the field count */
                    checkFieldCount(myFields);

                    /* Process the fields */
                    processFields(myFields);
                }
            }

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw an exception */
            throw new TethysDataException("Failed to load resource ", e);
        }
    }

    /**
     * Reset the fields.
     */
    protected abstract void resetFields();

    /**
     * Process the fields.
     * @param pFields the fields
     * @throws OceanusException on error
     */
    protected abstract void processFields(final List<String> pFields) throws OceanusException;

    /**
     * Parse the line.
     * @param pLine the line to parse
     * @return the list of fields
     */
    private List<String> parseLine(final StringBuilder pLine) {
        /* Initialise variables */
        List<String> myFields = new ArrayList<>();
        boolean inQuotes = false;
        boolean maybeQuote = false;

        /* Reset the builder */
        theBuilder.setLength(0);

        /* Loop through the Line */
        int iIndex = 0;
        int iLength = pLine.length();
        while (iIndex < iLength) {
            /* Obtain the character */
            char myChar = pLine.charAt(iIndex);

            /* If this is a quote */
            if (myChar == QUOTE_CHAR) {
                /* If this is an escaped quote */
                if (maybeQuote) {
                    /* Copy to field and reset state */
                    theBuilder.append(myChar);
                    maybeQuote = false;
                    inQuotes = true;

                    /* else */
                } else {
                    /* Flip Quote state and prepare for possible escaped quote */
                    maybeQuote = inQuotes;
                    inQuotes = !inQuotes;
                }

                /* If we are outside quotes and have found a comma */
            } else if (!inQuotes
                       && (myChar == COMMA_CHAR)) {
                /* Add the value to the list */
                myFields.add(theBuilder.toString());
                theBuilder.setLength(0);
                maybeQuote = false;

                /* else its a valid character */
            } else {
                /* Copy to field */
                theBuilder.append(myChar);
                maybeQuote = false;
            }

            /* Shift index */
            iIndex++;
        }

        /* Add the final value to the list */
        myFields.add(theBuilder.toString());
        return myFields;
    }

    /**
     * Count quotes.
     * @param pBuilder the string builder
     * @return the # of quotes
     */
    private int countQuotes(final StringBuilder pBuilder) {
        /* Initialise variables */
        int myCount = 0;

        /* Search for quotes */
        int myIndex = pBuilder.indexOf(QUOTE_STR);
        while (myIndex != -1) {
            /* Increment count and repeat search */
            myCount++;
            myIndex = pBuilder.indexOf(QUOTE_STR, myIndex + 1);
        }

        /* Return the count */
        return myCount;
    }

    /**
     * Check Field Count.
     * @param pFields the fields
     * @throws OceanusException on error
     */
    private void checkFieldCount(final List<String> pFields) throws OceanusException {
        /* Check the # of fields */
        if (pFields.size() != theHeaders.length) {
            throw new CoeusDataException("Invalid # of fields in record");
        }
    }

    /**
     * Check Headers.
     * @param pFields the fields
     * @throws OceanusException on error
     */
    protected void checkHeaders(final List<String> pFields) throws OceanusException {
        /* Check names are correct */
        for (int i = 0; i < theHeaders.length; i++) {
            /* Check name */
            String myHeader = pFields.get(i);
            if (!theHeaders[i].equals(myHeader)) {
                throw new CoeusDataException(myHeader, "Invalid header");
            }
        }
    }
}
