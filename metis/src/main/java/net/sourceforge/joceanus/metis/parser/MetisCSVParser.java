/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012-2026 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.metis.parser;

import net.sourceforge.joceanus.metis.exc.MetisDataException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateFormatter;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimalParser;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CSV Parser.
 */
public abstract class MetisCSVParser {
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
     * Date Parser.
     */
    private final OceanusDateFormatter theDateParser;

    /**
     * Decimal Parser.
     */
    private final OceanusDecimalParser theDecimalParser;

    /**
     * The Decimal size.
     */
    private int theDecimalSize;

    /**
     * CheckHeaders.
     */
    private boolean checkHeaders;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pHeaders the expected headers
     */
    protected MetisCSVParser(final OceanusDataFormatter pFormatter,
                             final String[] pHeaders) {
        /* Store the headers */
        theHeaders = Arrays.copyOf(pHeaders, pHeaders.length);

        /* Access the formatters */
        theDateParser = pFormatter.getDateFormatter();
        theDecimalParser = pFormatter.getDecimalParser();
        checkHeaders = true;
    }

    /**
     * Parse a date value.
     * @param pInput the input string
     * @return the parsed date
     * @throws OceanusException on error
     */
    public OceanusDate parseDate(final String pInput) throws OceanusException {
        try {
            return theDateParser.parseDate(pInput);
        } catch (IllegalArgumentException e) {
            throw new MetisDataException("Bad date", e);
        }
    }

    /**
     * Parse a decimal value.
     * @param pInput the input string
     * @return the parsed decimal
     * @throws OceanusException on error
     */
    public OceanusDecimal parseDecimal(final String pInput) throws OceanusException {
        try {
            return theDecimalParser.parseDecimalValue(pInput, theDecimalSize);
        } catch (IllegalArgumentException e) {
            throw new MetisDataException("Bad decimal", e);
        }
    }

    /**
     * Parse a money.
     * @param pInput the input string
     * @return the parsed money
     * @throws OceanusException on error
     */
    public OceanusMoney parseMoney(final String pInput) throws OceanusException {
        try {
            return theDecimalParser.parseMoneyValue(pInput);
        } catch (IllegalArgumentException e) {
            throw new MetisDataException("Bad money", e);
        }
    }

    /**
     * Parse a rate.
     * @param pInput the input string
     * @return the parsed rate
     * @throws OceanusException on error
     */
    public OceanusRate parseRate(final String pInput) throws OceanusException {
        try {
            return theDecimalParser.parseRateValue(pInput);
        } catch (IllegalArgumentException e) {
            throw new MetisDataException("Bad rate", e);
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
    public void parseFile(final Path pInput) throws OceanusException {
        /* Create a StringBuilder for the line */
        final StringBuilder myBuilder = new StringBuilder();

        /* Reset the fields */
        checkHeaders = true;
        resetFields();

        /* Protect against exceptions */
        try (BufferedReader myReader = Files.newBufferedReader(pInput, StandardCharsets.UTF_8)) {
            /* Loop through the file */
            for (;;) {
                /* Read next line and exit on EOF */
                final String myLine = myReader.readLine();
                if (myLine == null) {
                    break;
                }

                /* Add to the current buffer */
                myBuilder.append(myLine);
                final int myCount = countQuotes(myBuilder);

                /* If we have an even number of quotes, then we have a full line */
                if (myCount % 2 == 0) {
                    /* Parse the line and clear the buffer */
                    final List<String> myFields = parseLine(myBuilder);
                    myBuilder.setLength(0);

                    /* Check the field count */
                    checkFieldCount(myFields);

                    /* Process the line */
                    processLine(myFields);
                }
            }

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw an exception */
            throw new MetisDataException("Failed to load resource ", e);
        }
    }

    /**
     * process line.
     * @param pFields the fields
     * @throws OceanusException on error
     */
    private void processLine(final List<String> pFields) throws OceanusException {
        /* If we should check the headers */
        if (checkHeaders) {
            /* Validate the header */
            checkHeaders(pFields);
            checkHeaders = false;

            /* else we should process the fields */
        } else {
            /* Process the fields */
            processFields(pFields);
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
    protected abstract void processFields(List<String> pFields) throws OceanusException;

    /**
     * Parse the line.
     * @param pLine the line to parse
     * @return the list of fields
     */
    private static List<String> parseLine(final StringBuilder pLine) {
        /* Create a StringBuilder for the line */
        final StringBuilder myBuilder = new StringBuilder();

        /* Initialise variables */
        final List<String> myFields = new ArrayList<>();
        boolean inQuotes = false;
        boolean maybeQuote = false;

        /* Loop through the Line */
        int iIndex = 0;
        final int iLength = pLine.length();
        while (iIndex < iLength) {
            /* Obtain the character */
            final char myChar = pLine.charAt(iIndex);

            /* If this is a quote */
            if (myChar == QUOTE_CHAR) {
                /* If this is an escaped quote */
                if (maybeQuote) {
                    /* Copy to field and reset state */
                    myBuilder.append(myChar);
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
                    && myChar == COMMA_CHAR) {
                /* Add the value to the list */
                myFields.add(myBuilder.toString());
                myBuilder.setLength(0);
                maybeQuote = false;

                /* else its a valid character */
            } else {
                /* Copy to field */
                myBuilder.append(myChar);
                maybeQuote = false;
            }

            /* Shift index */
            iIndex++;
        }

        /* Add the final value to the list */
        myFields.add(myBuilder.toString());

        /* Return the fields */
        return myFields;
    }

    /**
     * Count quotes.
     * @param pBuilder the string builder
     * @return the # of quotes
     */
    private static int countQuotes(final StringBuilder pBuilder) {
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
            throw new MetisDataException("Invalid # of fields in record");
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
            final String myHeader = pFields.get(i);
            if (!theHeaders[i].equals(myHeader.trim())) {
                throw new MetisDataException(myHeader, "Invalid header");
            }
        }
    }
}
