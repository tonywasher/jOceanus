/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;

/**
 * JavaFX HTML Manager.
 */
public class TethysSwingHTMLManager
        extends TethysHTMLManager<JComponent, Icon> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysSwingHTMLManager.class);

    /**
     * The stream close error.
     */
    private static final String ERROR_STREAM = "Failed to close stream";

    /**
     * EditorPane.
     */
    private final JEditorPane theEditor;

    /**
     * HTMLEditorKit.
     */
    private final HTMLEditorKit theEditorKit;

    /**
     * Base StyleSheet.
     */
    private final StyleSheet theBaseStyleSheet;

    /**
     * Modifications StyleSheet.
     */
    private StyleSheet theModifiedStyleSheet;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysSwingHTMLManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create EditorPane */
        theEditor = new JEditorPane();
        theEditor.setEditable(false);
        theEditor.setContentType("text/html");

        /* Add an editor kit to the editor */
        theEditorKit = new HTMLEditorKit();
        theEditor.setEditorKit(theEditorKit);

        /* Create the document for the window */
        HTMLDocument myDoc = (HTMLDocument) theEditorKit.createDefaultDocument();
        theEditor.setDocument(myDoc);

        /* Obtain the base styleSheet */
        theBaseStyleSheet = myDoc.getStyleSheet();

        /* Add hyperLink listener */
        theEditor.addHyperlinkListener(new HTMLListener());
    }

    @Override
    public JComponent getNode() {
        return theEditor;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theEditor.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theEditor.setEnabled(pEnabled);
    }

    @Override
    protected void loadHTMLContent(final String pHTMLString) {
        /* Set the help text */
        theEditor.setText(pHTMLString);
        theEditor.setCaretPosition(0);
        theEditor.requestFocusInWindow();
    }

    @Override
    protected void loadCSSContents() {
        /* If there is already a styleSheet */
        if (theModifiedStyleSheet != null) {
            /* Remove it */
            theBaseStyleSheet.removeStyleSheet(theModifiedStyleSheet);
            theModifiedStyleSheet = null;
        }

        /* If we have any additional rules */
        String myCSS = getProcessedCSS();
        if (myCSS != null) {
            /* Create the styleSheet */
            theModifiedStyleSheet = new StyleSheet();
            theModifiedStyleSheet.addRule(myCSS);

            /* Load into the editor */
            theBaseStyleSheet.addStyleSheet(theModifiedStyleSheet);
        }
    }

    @Override
    public void scrollToReference(final String pReference) {
        /* Execute the function call */
        theEditor.scrollToReference(pReference);
    }

    @Override
    public void printIt() {
        /* Print the current report */
        try {
            /* Print the data */
            theEditor.print();

        } catch (PrinterAbortException e) {
            return;
        } catch (PrinterException e) {
            LOGGER.error("Failed to print", e);
        }
    }

    /**
     * HyperLinkListener.
     */
    private class HTMLListener
            implements HyperlinkListener {
        @Override
        public void hyperlinkUpdate(final HyperlinkEvent e) {
            /* Ignore non-activated events */
            if (!e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                return;
            }
            /* If this is a Frame hyper link event */
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                HTMLDocument doc = (HTMLDocument) theEditor.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);

                /* else look for a URL */
            } else {
                URL url = e.getURL();
                try {
                    String myDesc = e.getDescription();
                    if (url == null) {
                        /* display the new page */
                        processReference(myDesc);
                    } else {
                        theEditor.setPage(e.getURL());
                    }
                } catch (IOException t) {
                    LOGGER.error(ERROR_STREAM, t);
                }
            }
        }
    }
}
