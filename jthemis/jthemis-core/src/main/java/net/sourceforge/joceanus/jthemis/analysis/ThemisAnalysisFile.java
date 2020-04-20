/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jthemis.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisDataException;

/**
 * Analysis representation of a java file.
 */
public class ThemisAnalysisFile
    implements ThemisAnalysisContainer, ThemisAnalysisDataType {
    /**
     * The lineFeed character.
     */
    private static final char LF = '\n';

    /**
     * The carriageReturn character.
     */
    private static final char CR = '\r';

    /**
     * The buffer length (must be longer than longest line).
     */
    private static final int BUFLEN = 1024;

    /**
     * The location of the file.
     */
    private final File theLocation;

    /**
     * The name of the file.
     */
    private final String theName;

    /**
     * The package file.
     */
    private final ThemisAnalysisPackage thePackage;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The number of lines in the file.
     */
    private int theNumLines;

    /**
     * Constructor.
     * @param pPackage the package
     * @param pFile the file to analyse
     */
    ThemisAnalysisFile(final ThemisAnalysisPackage pPackage,
                       final File pFile) {
        /* Store the parameters */
        thePackage = pPackage;
        theLocation = pFile;
        theName = pFile.getName().replace(ThemisAnalysisPackage.SFX_JAVA, "");

        /* Create the list */
        theContents = new ArrayDeque<>();
    }

    /**
     * Obtain the name of the fileClass.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Constructor.
      * @throws OceanusException on error
     */
    void processFile() throws OceanusException {
        /* Create the queue */
        final Deque<ThemisAnalysisElement> myLines = new ArrayDeque<>();

        /* create a read buffer */
        final char[] myBuffer = new char[BUFLEN];
        int myOffset = 0;

        /* Protect against exceptions */
        try (InputStream myStream = new FileInputStream(theLocation);
             InputStreamReader myInputReader = new InputStreamReader(myStream, StandardCharsets.UTF_8);
             BufferedReader myReader = new BufferedReader(myInputReader)) {

            /* Read the header entry */
            for (;;) {
                /* Read some characters into the buffer */
                final int myChars = myReader.read(myBuffer, myOffset, BUFLEN - myOffset);
                if (myChars == -1 && myOffset == 0) {
                    break;
                }

                /* Process lines in the buffer */
                myOffset = processLines(myLines, myBuffer, myChars + myOffset);
            }

            /* Record the number of lines */
            theNumLines = myLines.size();

            /* Post process the lines */
            firstPassProcessLines(myLines);
            secondPassProcessLines();

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw an exception */
            throw new ThemisDataException("Failed to load file "
                    + theLocation.getAbsolutePath());
        }
    }

    @Override
    public Map<String, ThemisAnalysisDataType> getDataTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Deque<ThemisAnalysisElement> getContents() {
        return theContents;
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return this;
    }

    @Override
    public int getNumLines() {
        return theNumLines;
    }

    /**
     * Process lines.
     * @param pLines the list of lines to build
     * @param pBuffer the character buffer
     * @param pNumChars the number of characters in the buffer
     * @return the remaining characters in the buffer
     */
    private static int processLines(final Deque<ThemisAnalysisElement> pLines,
                                    final char[] pBuffer,
                                    final int pNumChars) {
        /* The start of the current line */
        int myOffset = 0;

        /* Look for line feed in the buffer */
        int myLF  = findLineFeedInBuffer(pBuffer, 0, pNumChars);
        while (myLF != -1) {
            /* Build the line */
            final ThemisAnalysisLine myLine = buildLine(pBuffer, myOffset, myLF);
            pLines.add(myLine);

            /* Look for next lineFeed */
            myOffset = myLF + 1;
            myLF  = findLineFeedInBuffer(pBuffer, myOffset, pNumChars);
        }

        /* Copy remaining characters down */
        final int myRemaining = pNumChars - myOffset;
        if (myRemaining > 0) {
            System.arraycopy(pBuffer, myOffset, pBuffer, 0, myRemaining);
        }

        /* Return the number remaining */
        return myRemaining;
    }

    /**
     * Find lineFeed in buffer.
     * @param pBuffer the character buffer
     * @param pOffset the starting offset
     * @param pNumChars the number of characters in the buffer
     * @return the remaining characters in the buffer
     */
    private static int findLineFeedInBuffer(final char[] pBuffer,
                                            final int pOffset,
                                            final int pNumChars) {
        /* Loop through the buffer */
        for (int i = pOffset; i < pNumChars; i++) {
            if (pBuffer[i] == LF) {
                return i;
            }
        }

        /* Look for line feed in the buffer */
        return -1;
    }

    /**
     * Build line from buffer.
     * @param pBuffer the character buffer
     * @param pOffset the starting offset
     * @param pLineFeed the location of the lineFeed
     * @return the new line
     */
    private static ThemisAnalysisLine buildLine(final char[] pBuffer,
                                                final int pOffset,
                                                final int pLineFeed) {
        /* Strip any trailing cr */
        int myLen = pLineFeed - pOffset;
        if (myLen > 0 && pBuffer[pOffset + myLen - 1] == CR) {
            myLen--;
        }

        /* Build the line */
        return new ThemisAnalysisLine(pBuffer, pOffset, myLen);
    }

    /**
     * Post-process the lines as first Pass.
     * @param pLines the lines to process
     */
    void firstPassProcessLines(final Deque<ThemisAnalysisElement> pLines) {
        /* Create the parser */
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(pLines, theContents);

        /* Loop through the lines */
        while (myParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) myParser.popNextLine();

            /* Process comments and blanks */
            boolean processed = myParser.processCommentsAndBlanks(myLine);

            /* Process package */
            if (!processed) {
                processed = processPackage(myParser, myLine);
            }

            /* Process imports */
            if (!processed) {
                processed = myParser.processImports(myLine);
            }

            /* If we haven't processed yet */
            if (!processed) {
                /* Process the class */
                processed = myParser.processClass(myLine);

                /* Must have finished by now */
                if (!processed || myParser.hasLines()) {
                    throw new IllegalStateException();
                }
            }
        }
    }

    /**
     * Post-process the lines as second Pass.
     */
    void secondPassProcessLines() {
        /* Loop through the lines */
        for (ThemisAnalysisElement myElement : theContents) {
            /* If the element is a container */
            if (myElement instanceof ThemisAnalysisContainer) {
                /* Access and process the container */
                final ThemisAnalysisContainer myContainer = (ThemisAnalysisContainer) myElement;
                myContainer.postProcessLines();
            }
        }
    }

    /**
     * Process a potential import line.
     * @param pParser the parser
     * @param pLine the line
     * @return have we processed the line?
     */
    private boolean processPackage(final ThemisAnalysisParser pParser,
                                   final ThemisAnalysisLine pLine) {
        /* If this is a package line */
        if (ThemisAnalysisPackage.isPackage(pLine)) {
            /* Check that the package is correct named */
            if (!thePackage.getPackage().equals(pLine.toString())) {
                throw new IllegalStateException("Bad package");
            }

            /* Add the class name of each of the packages to the dataTypes */
            final Map<String, ThemisAnalysisDataType> myMap = pParser.getDataTypes();
            for (ThemisAnalysisFile myFile : thePackage.getClasses()) {
                myMap.put(myFile.getName(), myFile);
            }

            /* Process the package line */
            theContents.add(thePackage);

            /* Processed */
            return true;
        }

        /* return false */
        return false;
    }

    @Override
    public String toString() {
        return getName();
    }
}
