/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.control;

import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.control.TethysUICoreHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.net.URL;

/**
 * Swing HTML Manager.
 */
public class TethysUISwingHTMLManager
        extends TethysUICoreHTMLManager {
    /**
     * The logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysUISwingHTMLManager.class);

    /**
     * The stream close error.
     */
    private static final String ERROR_STREAM = "Failed to close stream";

    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * EditorPane.
     */
    private final JEditorPane theEditor;

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
    TethysUISwingHTMLManager(final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create EditorPane */
        theEditor = new JEditorPane();
        theEditor.setEditable(false);
        theEditor.setContentType("text/html");

        /* Add an editor kit to the editor */
        final HTMLEditorKit myEditorKit = new HTMLEditorKit();
        theEditor.setEditorKit(myEditorKit);

        /* Create the document for the window */
        final HTMLDocument myDoc = (HTMLDocument) myEditorKit.createDefaultDocument();
        theEditor.setDocument(myDoc);

        /* Obtain the base styleSheet */
        theBaseStyleSheet = myDoc.getStyleSheet();

        /* Add hyperLink listener */
        theEditor.addHyperlinkListener(new HTMLListener());

        /* Create the node */
        theNode = new TethysUISwingNode(theEditor);
    }

    @Override
    public TethysUISwingNode getNode() {
        return theNode;
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
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
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
        final String myCSS = getProcessedCSS();
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

        } catch (PrinterAbortException ignore) {
            /* Ignore exception and just return */
        } catch (PrinterException e) {
            LOGGER.error("Failed to print", e);
        }
    }

    /**
     * Handle HyperLink.
     * @param pEvent the event
     */
    void handleFrameHyperLinkEvent(final HTMLFrameHyperlinkEvent pEvent) {
        final HTMLDocument doc = (HTMLDocument) theEditor.getDocument();
        doc.processHTMLFrameHyperlinkEvent(pEvent);
    }

    /**
     * Handle HyperLink.
     * @param pEvent the event
     */
    void handleHyperLinkEvent(final HyperlinkEvent pEvent) {
        final URL url = pEvent.getURL();
        try {
            final String myDesc = pEvent.getDescription();
            if (url == null) {
                /* display the new page */
                processReference(myDesc);
            } else {
                theEditor.setPage(url);
            }
        } catch (IOException t) {
            LOGGER.error(ERROR_STREAM, t);
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
                handleFrameHyperLinkEvent((HTMLFrameHyperlinkEvent) e);

                /* else look for a URL */
            } else {
                handleHyperLinkEvent(e);
            }
        }
    }
}
