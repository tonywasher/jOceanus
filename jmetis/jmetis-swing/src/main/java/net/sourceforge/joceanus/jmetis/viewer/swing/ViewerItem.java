/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.viewer.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.viewer.ViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.ViewerResource;

/**
 * Data Item.
 * @author Tony Washer
 */
public class ViewerItem {
    /**
     * The item text.
     */
    private static final String TEXT_ITEM = ViewerResource.VIEWER_SELECT_ITEM.getValue();

    /**
     * The of text.
     */
    private static final String TEXT_OF = ViewerResource.VIEWER_SELECT_OF.getValue();

    /**
     * The showItem text.
     */
    private static final String TEXT_SHOWITEMS = ViewerResource.VIEWER_SELECT_SHOWITEMS.getValue();

    /**
     * The showHdr text.
     */
    private static final String TEXT_SHOWHDR = ViewerResource.VIEWER_SELECT_SHOWHDR.getValue();

    /**
     * Blank character.
     */
    private static final char CHAR_BLANK = ' ';

    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 1000;

    /**
     * Tick factor.
     */
    private static final int TICK_FACTOR = 10;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 5;

    /**
     * Shift one.
     */
    private static final int SHIFT_ONE = 1;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewerItem.class);

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The list panel.
     */
    private final JPanel theListPanel;

    /**
     * The root entry.
     */
    private ViewerEntry theEntry = null;

    /**
     * The active detail.
     */
    private ViewerDetail theDetail = null;

    /**
     * The next button.
     */
    private final JButton theNext;

    /**
     * The Previous button.
     */
    private final JButton thePrev;

    /**
     * The Toggle button.
     */
    private final JButton theToggle;

    /**
     * The Slider.
     */
    private final JSlider theSlider;

    /**
     * The Label.
     */
    private final JLabel theLabel;

    /**
     * The editor.
     */
    private final JEditorPane theEditor;

    /**
     * HTML formatter.
     */
    private ViewerHTML theFormatter = null;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    protected ViewerItem(final ViewerHTML pFormatter) {
        /* Record the formatter */
        theFormatter = pFormatter;

        /* Create the slider */
        theSlider = new JSlider(SwingConstants.HORIZONTAL);
        theSlider.setMinimum(0);

        /* Create the Buttons */
        theNext = new JButton("+"
                              + SHIFT_ONE);
        thePrev = new JButton("-"
                              + SHIFT_ONE);

        /* Create the toggle button */
        theToggle = new JButton();

        /* Create the label */
        theLabel = new JLabel();

        /* Create the editor pane as non-editable */
        theEditor = new JEditorPane();
        theEditor.setEditable(false);

        /* Create a scroll-pane for the editor */
        JScrollPane myScroll = new JScrollPane(theEditor);

        /* Build the editor style sheet */
        buildEditorStyleSheet(pFormatter);

        /* Create the list panel */
        theListPanel = new JPanel();
        theListPanel.setLayout(new BoxLayout(theListPanel, BoxLayout.X_AXIS));
        theListPanel.setBorder(BorderFactory.createTitledBorder("List Control"));

        /* Add components */
        theListPanel.add(theToggle);
        theListPanel.add(Box.createHorizontalGlue());
        theListPanel.add(theLabel);
        theListPanel.add(Box.createHorizontalGlue());
        theListPanel.add(thePrev);
        theListPanel.add(Box.createHorizontalStrut(STRUT_WIDTH));
        theListPanel.add(theSlider);
        theListPanel.add(Box.createHorizontalStrut(STRUT_WIDTH));
        theListPanel.add(theNext);
        theListPanel.setVisible(false);

        /* Create the complete panel */
        thePanel = new JPanel(new BorderLayout());
        thePanel.add(theListPanel, BorderLayout.NORTH);
        thePanel.add(myScroll, BorderLayout.CENTER);

        /* Create listener */
        ViewerListener myListener = new ViewerListener();

        /* Add hyper-link listener */
        theEditor.addHyperlinkListener(myListener);

        /* Add action Listeners */
        theNext.addActionListener(myListener);
        thePrev.addActionListener(myListener);
        theToggle.addActionListener(myListener);

        /* Add slider listener */
        theSlider.addChangeListener(myListener);
    }

    /**
     * Get the panel.
     * @return the panel
     */
    protected JPanel getPanel() {
        return thePanel;
    }

    /**
     * Set new formatter.
     * @param pFormatter the formatter
     */
    protected void setFormatter(final ViewerHTML pFormatter) {
        /* Record the formatter */
        theFormatter = pFormatter;

        /* Use the new formatter */
        buildEditorStyleSheet(pFormatter);
    }

    /**
     * build editor styleSheet.
     * @param pFormatter the formatter
     */
    protected final void buildEditorStyleSheet(final ViewerHTML pFormatter) {
        /* Add a new editor kit to the editor */
        HTMLEditorKit myKit = new HTMLEditorKit();
        theEditor.setEditorKit(myKit);

        /* Create the style-sheet for the window */
        StyleSheet myStyle = new StyleSheet();
        myStyle.addStyleSheet(myKit.getStyleSheet());
        pFormatter.buildStylesheet(myStyle);
        myKit.setStyleSheet(myStyle);

        /* Create the document for the window */
        Document myDoc = myKit.createDefaultDocument();
        theEditor.setDocument(myDoc);
    }

    /**
     * Display the data entry.
     * @param pEntry the data entry
     */
    protected void displayData(final ViewerEntry pEntry) {
        /* Record the object */
        theEntry = pEntry;

        /* Display the object */
        displayDetail(new ViewerDetail(theFormatter, pEntry.getObject()));
    }

    /**
     * Update the data entry.
     * @param pEntry the entry
     */
    protected void updateData(final ViewerEntry pEntry) {
        /* Take care if we have no active entry */
        if (theEntry == null) {
            return;
        }

        /* If we are updating the active object */
        if (theEntry.getId().equals(pEntry.getId())) {
            /* Display the object */
            displayDetail(new ViewerDetail(theFormatter, pEntry.getObject()));
        }
    }

    /**
     * Display the detail.
     * @param pDetail the detail
     */
    private void displayDetail(final ViewerDetail pDetail) {
        StringBuilder myValue = null;
        StringBuilder myLinks = null;

        /* Store the detail */
        theDetail = pDetail;

        /* If this is a list */
        if (theDetail.isList()) {
            /* Show the list window */
            theListPanel.setVisible(true);

            /* If the detail is a child */
            if (theDetail.isChild()) {
                /* Display item details */
                displayItem();

                /* else display header details */
            } else {
                displayHeader();
            }

            /* else hide the list */
        } else {
            /* Hide the list panel */
            theListPanel.setVisible(false);
        }

        /* Access Data Detail */
        myValue = pDetail.getJDataDetail();

        /* Access history links */
        myLinks = pDetail.getHistoryLinks();

        /* Build the HTML page */
        StringBuilder myText = new StringBuilder(BUFFER_LEN);
        myText.append("<html><body>");

        /* Add the value to the output */
        if (myLinks != null) {
            myText.append(myLinks);
        }
        if (myValue != null) {
            myText.append(myValue);
        }
        myText.append("</body></html>");

        /* Set the report text */
        theEditor.setText(myText.toString());
        theEditor.setCaretPosition(0);
        theEditor.requestFocusInWindow();
    }

    /**
     * Display Item.
     */
    private void displayItem() {
        /* Access the list size */
        int mySize = theDetail.getList().size();

        /* Access position */
        int myPos = theDetail.getIndex();

        /* Show/hide movement buttons */
        boolean doShowSlider = mySize > 1;
        theNext.setVisible(doShowSlider);
        thePrev.setVisible(doShowSlider);
        theSlider.setVisible(doShowSlider);
        theLabel.setVisible(true);

        /* Enable movement buttons */
        theNext.setEnabled(myPos < mySize
                                   - SHIFT_ONE);
        thePrev.setEnabled(myPos >= SHIFT_ONE);

        /* Handle tick spacing */
        determineTickSpacing(mySize);

        /* Build the text */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(TEXT_ITEM);
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(myPos + 1);
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(TEXT_OF);
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(mySize);

        /* Set the text detail */
        theLabel.setText(myBuilder.toString());
        theSlider.setValue(myPos);

        /* Set the text detail */
        theToggle.setText(TEXT_SHOWHDR);
    }

    /**
     * Determine Tick Spacing.
     * @param pSize the size of the list
     */
    private void determineTickSpacing(final int pSize) {
        /* Obtain the maximum value */
        int iMax = pSize;
        int iMajor = 1;

        /* Set the maximum */
        theSlider.setMaximum(pSize - 1);

        /* Calculate nearest power of Ten */
        while (iMax > TICK_FACTOR) {
            /* Divide max by ten and multiply major by ten */
            iMax /= TICK_FACTOR;
            iMajor *= TICK_FACTOR;
        }

        /* If major tick spacing is one */
        if (iMajor == 1) {
            /* Set major and minor ticks to 1 */
            theSlider.setMajorTickSpacing(iMajor);
            theSlider.setMinorTickSpacing(iMajor);

            /* else check on spacing */
        } else {
            /* Determine how many major ticks that gives us */
            int iNumTicks = theSlider.getMaximum()
                            / iMajor;

            /* If we have 5 or more ticks */
            if (iNumTicks >= (TICK_FACTOR >> 1)) {
                /* Use the major ticks */
                theSlider.setMajorTickSpacing(iMajor);

                /* Minor ticks is half the major ticks */
                theSlider.setMinorTickSpacing(iMajor >> 1);
            } else {
                /* Use half the major ticks */
                theSlider.setMajorTickSpacing(iMajor >> 1);

                /* Minor ticks is one-tenth the major ticks */
                theSlider.setMinorTickSpacing(iMajor
                                              / TICK_FACTOR);
            }
        }

        /* Ask for the ticks to be painted */
        theSlider.setPaintTicks(true);
    }

    /**
     * Display Header.
     */
    private void displayHeader() {
        /* Hide movement buttons */
        theNext.setVisible(false);
        thePrev.setVisible(false);
        theSlider.setVisible(false);
        theLabel.setVisible(false);

        /* Set the text detail */
        theToggle.setText(TEXT_SHOWITEMS);
    }

    /**
     * Data Listener class.
     */
    private class ViewerListener
            implements HyperlinkListener, ActionListener, ChangeListener {
        @Override
        public void hyperlinkUpdate(final HyperlinkEvent pEvent) {
            /* If this is an activated event */
            if (pEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                if (pEvent instanceof HTMLFrameHyperlinkEvent) {
                    HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) pEvent;
                    HTMLDocument doc = (HTMLDocument) theEditor.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
                } else {
                    URL url = pEvent.getURL();
                    try {
                        String desc = pEvent.getDescription();
                        if (url == null) {
                            /* Access the referenced link */
                            ViewerDetail myDetail = theDetail.getDataLink(desc);

                            /* Shift to this link */
                            if (myDetail != null) {
                                displayDetail(myDetail);
                            }
                        } else {
                            theEditor.setPage(pEvent.getURL());
                        }
                    } catch (IOException e) {
                        LOGGER.error("Failed to access link", e);
                    }
                }
            }
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            Object o = pEvent.getSource();
            /* If we asked for the next item */
            if (theNext.equals(o)) {
                ViewerDetail myList = theDetail.getPartnerDetail();
                myList.shiftIterator(SHIFT_ONE);
                displayDetail(myList.getPartnerDetail());
                /* If we asked for the previous item */
            } else if (thePrev.equals(o)) {
                ViewerDetail myList = theDetail.getPartnerDetail();
                myList.shiftIterator(-SHIFT_ONE);
                displayDetail(myList.getPartnerDetail());
            } else if (theToggle.equals(o)) {
                /* Toggle the item that we are displaying */
                displayDetail(theDetail.getPartnerDetail());
            }
        }

        @Override
        public void stateChanged(final ChangeEvent e) {
            if ((theSlider.equals(e.getSource()))
                && (!theSlider.getValueIsAdjusting())) {
                /* Access value and current position */
                int myPos = theSlider.getValue();
                int myCurr = theDetail.getIndex();

                /* Ignore if there is no change */
                if (myPos == myCurr) {
                    return;
                }

                /* Shift to the correct item */
                ViewerDetail myList = theDetail.getPartnerDetail();
                myList.shiftIterator(myPos
                                     - myCurr);
                displayDetail(myList.getPartnerDetail());
            }
        }
    }
}
