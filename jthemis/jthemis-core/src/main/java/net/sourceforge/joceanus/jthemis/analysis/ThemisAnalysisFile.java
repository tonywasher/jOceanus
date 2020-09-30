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
import java.util.Deque;
import java.util.List;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisDataException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisDataMap.ThemisAnalysisDataType;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisDataMap.ThemisAnalysisIntermediate;

/**
 * Analysis representation of a java file.
 */
public class ThemisAnalysisFile
    implements ThemisAnalysisContainer, ThemisAnalysisIntermediate {
    /**
     * Object class.
     */
    public interface ThemisAnalysisObject extends ThemisAnalysisDataType {
        /**
         * Obtain the short name.
         * @return the name
         */
        String getShortName();

        /**
         * Obtain the full name of the object.
         * @return the fullName
         */
        String getFullName();

        /**
         * Obtain ancestors.
         * @return the list of ancestors
         */
        List<ThemisAnalysisReference> getAncestors();

        /**
         * Obtain properties.
         * @return the properties
         */
        ThemisAnalysisProperties getProperties();
    }

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
     * The dataMap.
     */
    private final ThemisAnalysisDataMap theDataMap;

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
        theDataMap = new ThemisAnalysisDataMap(thePackage.getDataMap());

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
     * Process the file.
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

            /* Perform initial processing pass */
            initialProcessingPass(myLines);

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw an exception */
            throw new ThemisDataException("Failed to load file "
                    + theLocation.getAbsolutePath());
        }
    }

    @Override
    public ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
    }

    @Override
    public Deque<ThemisAnalysisElement> getContents() {
        return theContents;
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return this;
    }

    /**
     * Obtain the package.
     * @return the package
     */
    String getPackageName() {
        return thePackage.getPackage();
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
     * @throws OceanusException on error
     */
    private static int processLines(final Deque<ThemisAnalysisElement> pLines,
                                    final char[] pBuffer,
                                    final int pNumChars) throws OceanusException {
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
     * @throws OceanusException on error
     */
    private static int findLineFeedInBuffer(final char[] pBuffer,
                                            final int pOffset,
                                            final int pNumChars) throws OceanusException {
        /* Loop through the buffer */
        for (int i = pOffset; i < pNumChars; i++) {
            /* Check for LF and NULL */
            switch (pBuffer[i])  {
                case ThemisAnalysisChar.LF:
                    return i;
                case ThemisAnalysisChar.NULL:
                    throw new ThemisDataException("Null character in file");
                default:
                    break;
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
        if (myLen > 0 && pBuffer[pOffset + myLen - 1] == ThemisAnalysisChar.CR) {
            myLen--;
        }

        /* Build the line */
        return new ThemisAnalysisLine(pBuffer, pOffset, myLen);
    }

    /**
     * Post-process the lines as first Pass.
     * @param pLines the lines to process
     * @throws OceanusException on error
     */
    private void initialProcessingPass(final Deque<ThemisAnalysisElement> pLines) throws OceanusException {
        /* Create the parser */
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(pLines, theContents, this);

        /* Loop through the lines */
        while (myParser.hasLines()) {
            /* Access next line */
            ThemisAnalysisLine myLine = (ThemisAnalysisLine) myParser.popNextLine();

            /* Process comments/blanks/package/imports */
            boolean processed = myParser.processCommentsAndBlanks(myLine)
                    ||  processPackage(myLine)
                    ||  myParser.processImports(myLine);

            /* If we haven't processed yet */
            if (!processed) {
                /* Process the class */
                processed = myParser.processClass(myLine);
                if (!processed) {
                    throw new ThemisDataException("Unexpected construct in file");
                }

                /* Process any trailing blanks/comments */
                while (myParser.hasLines()) {
                    myLine = (ThemisAnalysisLine) myParser.popNextLine();
                    if (!myParser.processCommentsAndBlanks(myLine)) {
                        throw new ThemisDataException("Trailing data in file");
                    }
                }
            }
        }
    }

    /**
     * perform consolidation processing pass.
     * @throws OceanusException on error
     */
    void consolidationProcessingPass() throws OceanusException {
        /* Consolidate classMap */
        theDataMap.consolidateMap();
    }

    /**
     * Perform final processing pass.
     * @throws OceanusException on error
     */
    void finalProcessingPass() throws OceanusException {
        /* Resolve references */
        theDataMap.resolveReferences();

        /* Loop through the lines */
        for (ThemisAnalysisElement myElement : theContents) {
            /* If the element is a container */
            if (myElement instanceof ThemisAnalysisContainer) {
                /* Access and process the container */
                final ThemisAnalysisContainer myContainer = (ThemisAnalysisContainer) myElement;
                myContainer.postProcessLines();
            }
        }

        /* Report unknown items */
        theDataMap.reportUnknown();
    }

    /**
     * Process a potential import line.
     * @param pLine the line
     * @return have we processed the line?
     * @throws OceanusException on error
     */
    private boolean processPackage(final ThemisAnalysisLine pLine) throws OceanusException {
        /* If this is a package line */
        if (ThemisAnalysisPackage.isPackage(pLine)) {
            /* Check that the package is correct named */
            if (!thePackage.getPackage().equals(pLine.toString())) {
                throw new ThemisDataException("Bad package");
            }

            /* Setup the file resources */
            theDataMap.setUpFileResources();

            /* Declare all the files in this package to the dataMap */
            for (ThemisAnalysisFile myFile : thePackage.getFiles()) {
                theDataMap.declareFile(myFile);
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
