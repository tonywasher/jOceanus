/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.help.TethysHelpModule;
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
     * EditorPane.
     */
    private final JEditorPane theEditor;

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

        /* Add an editor kit to the editor */
        HTMLEditorKit myKit = new HTMLEditorKit();
        theEditor.setEditorKit(myKit);

        /* Create the document for the window */
        Document myDoc = myKit.createDefaultDocument();
        theEditor.setDocument(myDoc);

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
    public void setHTMLContent(final String pHTMLString,
                               final String pReference) {
        /* Pass call on to store the reference */
        super.setHTMLContent(pHTMLString, pReference);

        /* Set the help text */
        theEditor.setText(pHTMLString);
        theEditor.setCaretPosition(0);
        theEditor.requestFocusInWindow();
    }

    @Override
    protected void scrollToReference(final String pReference) {
        /* Execute the function call */
        theEditor.scrollToReference(pReference);
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
                    LOGGER.error(TethysHelpModule.ERROR_STREAM, t);
                }
            }
        }
    }
}
