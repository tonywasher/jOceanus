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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interface for containers that require postProcessing.
 */
public interface ThemisAnalysisContainer
    extends ThemisAnalysisElement {
    /**
     * Obtain the dataType Map.
     * @return the map
     */
    Map<String, ThemisAnalysisDataType> getDataTypes();

    /**
     * Obtain the list of processed elements.
     * @return the list
     */
    List<ThemisAnalysisElement> getProcessed();

    /**
     * Post process lines.
     */
    default void postProcessLines() {
        /* Create a copy of the processed list and clear original */
        final List<ThemisAnalysisElement> myProcessed = getProcessed();
        final List<ThemisAnalysisElement> myLines = new ArrayList<>(myProcessed);
        myProcessed.clear();

        /* Create the new input parser */
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, myProcessed, getDataTypes());

        /* Loop through the lines */
        while (myParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisElement myElement = myParser.popNextLine();
            boolean processed = false;

            /* If the element is already fully processed */
            if (myElement instanceof ThemisAnalysisProcessed) {
                myProcessed.add(myElement);
                processed = true;
            }

            /* If the element is a container */
            if (!processed
                    && myElement instanceof ThemisAnalysisContainer) {
                /* Access and process the container */
                final ThemisAnalysisContainer myContainer = (ThemisAnalysisContainer) myElement;
                myContainer.postProcessLines();
                myProcessed.add(myContainer);
                processed = true;
            }

            /* Everything should now be a line. */
            if (!(myElement instanceof ThemisAnalysisLine)) {
                throw new IllegalStateException("Unexpected dataType");
            }

            /* process fields and methods */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) myElement;
            if (!processed) {
                processed = myParser.processFieldsAndMethods(myLine);
            }

            /* process fields and methods */
            if (!processed) {
                //processed = myParser.processLines(myLine);
            }
        }

        /* Process extras */
        postProcessExtras();
    }

    /**
     * Post process extra lines.
     */
    default void postProcessExtras() {
        /* NoOp by default */
    }
}
