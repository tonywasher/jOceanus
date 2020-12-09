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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisDataException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile.ThemisAnalysisObject;

/**
 * Interface for containers that require postProcessing.
 */
public interface ThemisAnalysisContainer
    extends ThemisAnalysisProcessed {
    /**
     * Adoptable interface.
     */
    interface ThemisAnalysisAdoptable {
        /**
         * Set the parent of this container.
         * @param pParent the parent
         */
        void setParent(ThemisAnalysisContainer pParent);
    }

    /**
     * Obtain the dataMap.
     * @return the map
     */
    default ThemisAnalysisDataMap getDataMap() {
        return getParent().getDataMap();
    }

    /**
     * Obtain the contents.
     * @return the contents
     */
    Deque<ThemisAnalysisElement> getContents();

    /**
     * Obtain the parent of this container.
     * @return the parent
     */
    ThemisAnalysisContainer getParent();

    /**
     * Determine the full name of the child object.
     * @param pChildName the child name
     * @return the fullName
     */
    default String determineFullChildName(final String pChildName) {
        /* Loop */
        ThemisAnalysisContainer myContainer = this;
        for (;;) {
            if (myContainer instanceof ThemisAnalysisObject) {
                return ((ThemisAnalysisObject) myContainer).getFullName() + ThemisAnalysisChar.PERIOD + pChildName;
            }
            if (myContainer instanceof ThemisAnalysisFile) {
                return ((ThemisAnalysisFile) myContainer).getPackageName() + ThemisAnalysisChar.PERIOD + pChildName;
            }
            myContainer = myContainer.getParent();
        }
    }

    /**
     * Post process lines.
     * @throws OceanusException on error
     */
    default void postProcessLines() throws OceanusException {
        /* Create a copy of the contents list and clear original */
        final Deque<ThemisAnalysisElement> myContents = getContents();
        final Deque<ThemisAnalysisElement> myLines = new ArrayDeque<>(myContents);
        myContents.clear();

        /* Create the new input parser */
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, myContents, getParent());

        /* Loop through the lines */
        while (myParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisElement myElement = myParser.popNextLine();

            /* If the element is an embedded block */
            if (myElement instanceof ThemisAnalysisEmbedded) {
                /* Access and process the block */
                final ThemisAnalysisEmbedded myEmbedded = (ThemisAnalysisEmbedded) myElement;
                final ThemisAnalysisElement myResult = myParser.processEmbedded(myEmbedded);
                myContents.add(myResult);

                /* If the element is a container */
            } else if (myElement instanceof ThemisAnalysisContainer) {
                /* Access and process the container */
                final ThemisAnalysisContainer myContainer = (ThemisAnalysisContainer) myElement;
                myContainer.postProcessLines();
                myContents.add(myContainer);

                /* If the element is already fully processed */
            } else if (myElement instanceof ThemisAnalysisProcessed) {
                myContents.add(myElement);

                /* process lines */
            } else if (myElement instanceof ThemisAnalysisLine) {
                final ThemisAnalysisLine myLine = (ThemisAnalysisLine) myElement;
                final ThemisAnalysisElement myResult = myParser.processFieldsAndMethods(myLine);

                /* If we have a field/method */
                if (myResult != null) {
                    /* Add the element and postProcess any Containers */
                    myContents.add(myResult);

                    /* else */
                } else {
                    /* Process statement */
                    myContents.add(myParser.processStatement(myLine));
                }

                /* Everything should now be a line. */
            } else {
                throw new ThemisDataException("Unexpected dataType");
            }
        }

        /* Process extras */
        postProcessExtras();
    }

    /**
     * Post process extra lines.
     * @throws OceanusException on error
     */
    default void postProcessExtras() throws OceanusException {
        /* NoOp by default */
    }

    /**
     * Obtain iterator for chained containers.
     * @return the iterator
     */
    default Iterator<ThemisAnalysisContainer> containerIterator() {
        return Collections.emptyIterator();
    }
}
