/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.core.control;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIFileSelector;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreFactory;

/**
 * HTML to File.
 */
public class TethysUICoreHTMLToFile {
    /**
     * The logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysUICoreHTMLToFile.class);

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
    private final TethysUICoreHTMLManager theHTMLManager;

    /**
     * The File Selector.
     */
    private final TethysUIFileSelector theFileSelector;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pHTMLManager the HTML Manager
     */
    public TethysUICoreHTMLToFile(final TethysUICoreFactory<?> pFactory,
                                  final TethysUICoreHTMLManager pHTMLManager) {
        /* Store parameters */
        theHTMLManager = pHTMLManager;
        theFileSelector = pFactory.dialogFactory().newFileSelector();
        theFileSelector.setUseSave(true);
        theFileSelector.setExtension(".html");
    }

    /**
     * Write document to file.
     */
    public void writeToFile() {
        try {
            /* Select File */
            final File myFile = theFileSelector.selectFile();
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
        final Document myDoc = createXMLDocument(theHTMLManager.getHTMLString(), theHTMLManager.getProcessedCSS());

        /* Write the document to the file */
        writeDocumentToFile(myDoc, pFile);
    }

    /**
     * Parse XML document.
     * @param pXML the XML String
     * @param pStyleSheet the styleSheet
     * @return the document
     */
    private static Document createXMLDocument(final String pXML,
                                              final String pStyleSheet) {
        /* Parse the document */
        final Document myDoc = Jsoup.parse(pXML);

        /* Adjust the outputSettings */
        final OutputSettings mySettings = myDoc.outputSettings();
        mySettings.charset(StandardCharsets.UTF_8);
        mySettings.escapeMode(EscapeMode.extended);
        mySettings.prettyPrint(true);

        /* Create the style element */
        final Element myElement = myDoc.createElement(ELEMENT_STYLE);
        myElement.text(pStyleSheet);

        /* Obtain the head and add a style element */
        final Element myHead = myDoc.head();
        myHead.appendChild(myElement);

        /* Obtain all link elements */
        final Elements myLinks = myDoc.getElementsByTag(ELEMENT_A);
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
    private static void writeDocumentToFile(final Document pDoc,
                                            final File pFile) throws OceanusException {
        /* Protect the write */
        try (PrintWriter myWriter = new PrintWriter(pFile, StandardCharsets.UTF_8.name())) {
            /* Format the XML and write to stream */
            final String myHTML = pDoc.outerHtml();
            myWriter.print(myHTML);

        } catch (IOException e) {
            throw new TethysDataException("Failed to output XML", e);
        }
    }
}
