/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataException;

/**
 * HTML to File.
 * @param <N> the Node type
 * @param <I> the Icon type
 */
public class TethysHTMLToFile<N, I> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysHTMLToFile.class);

    /**
     * Link element name.
     */
    private static final String ELEMENT_A = "a";

    /**
     * HRef attribute name.
     */
    private static final String ATTR_HREF = "href";

    /**
     * Division element name.
     */
    private static final String ELEMENT_DIV = "div";

    /**
     * Style element name.
     */
    private static final String ELEMENT_STYLE = "style";

    /**
     * The HTML Manager.
     */
    private final TethysHTMLManager<N, I> theHTMLManager;

    /**
     * The File Selector.
     */
    private final TethysFileSelector theFileSelector;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pHTMLManager the HTML Manager
     */
    public TethysHTMLToFile(final TethysGuiFactory<N, I> pFactory,
                            final TethysHTMLManager<N, I> pHTMLManager) {
        /* Store parameters */
        theHTMLManager = pHTMLManager;
        theFileSelector = pFactory.newFileSelector();
        theFileSelector.setUseSave(true);
        theFileSelector.setExtension(".html");
    }

    /**
     * Write document to file.
     */
    public void writeToFile() {
        try {
            /* Select File */
            File myFile = theFileSelector.selectFile();
            if (myFile != null) {
                writeToFile(myFile);
            }
        } catch (OceanusException e) {
            LOGGER.error("Failed to write to file", e);
        }
    }

    /**
     * Write document to file.
     * @param pFile the file to write to
     * @throws OceanusException on error
     */
    private void writeToFile(final File pFile) throws OceanusException {
        /* Create the document */
        Document myDoc = createXMLDocument(theHTMLManager.getHTMLString(), theHTMLManager.getProcessedCSS());

        /* Write the document to the file */
        writeDocumentToFile(myDoc, pFile);
    }

    /**
     * Parse XML document.
     * @param pXML the XML String
     * @param pStyleSheet the styleSheet
     * @return the document
     * @throws OceanusException on error
     */
    private Document createXMLDocument(final String pXML,
                                       final String pStyleSheet) {
        /* Parse the document */
        Document myDoc = Jsoup.parse(pXML);

        /* Create the style element */
        Element myElement = myDoc.createElement(ELEMENT_STYLE);
        myElement.text(pStyleSheet);

        /* Obtain the head and add a style element */
        Element myHead = myDoc.head();
        myHead.appendChild(myElement);

        /* Obtain all link elements */
        Elements myLinks = myDoc.getElementsByTag(ELEMENT_A);
        myLinks.forEach(l -> {
            /* Remove reference attribute and rename to division */
            l.attributes().remove(ATTR_HREF);
            l.tagName(ELEMENT_DIV);
        });

        /* Return the document */
        return myDoc;
    }

    /**
     * Write Document to file.
     * @param pDoc the document to write
     * @param pFile the file to write to
     * @throws OceanusException on error
     */
    private void writeDocumentToFile(final Document pDoc,
                                     final File pFile) throws OceanusException {
        /* Protect the write */
        try (PrintStream myStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(pFile)))) {
            /* Format the XML and write to stream */
            String myHTML = pDoc.outerHtml();
            myStream.print(myHTML);

        } catch (IOException e) {
            throw new TethysDataException("Failed to output XML", e);
        }
    }
}
