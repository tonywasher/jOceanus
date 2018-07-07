/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;

import netscape.javascript.JSObject;

/**
 * JavaFX HTML Manager.
 */
public class TethysFXHTMLManager
        extends TethysHTMLManager {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(TethysFXHTMLManager.class);

    /**
     * Click event type.
     */
    private static final String EVENT_TYPE_CLICK = "click";

    /**
     * Head element name.
     */
    private static final String ELEMENT_HEAD = "head";

    /**
     * Style element name.
     */
    private static final String ELEMENT_STYLE = "style";

    /**
     * Anchor element name.
     */
    private static final String ELEMENT_ANCHOR = "a";

    /**
     * Anchor reference attribute.
     */
    private static final String ATTR_REF = "href";

    /**
     * The GuiFactory.
     */
    private final TethysFXGuiFactory theFactory;

    /**
     * The Node.
     */
    private final TethysFXNode theNode;

    /**
     * Pane.
     */
    private final BorderPane thePane;

    /**
     * HyperLink Listener.
     */
    private final EventListener theListener;

    /**
     * WebView.
     */
    private WebView theWebView;

    /**
     * WebEngine.
     */
    private WebEngine theWebEngine;

    /**
     * Pending scroll.
     */
    private String thePendingScroll;

    /**
     * Constructor.
     *
     * @param pFactory the GUI Factory
     */
    TethysFXHTMLManager(final TethysFXGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Store the factory */
        theFactory = pFactory;

        /* Allocate the Pane */
        thePane = new BorderPane();
        theNode = new TethysFXNode(thePane);

        /* Attach a listener to the Factory */
        if (theFactory.getStage() == null) {
            theFactory.getEventRegistrar().addEventListener(e -> allocateWebView());
        } else {
            allocateWebView();
        }

        /* Create the hyperLink listener */
        theListener = this::handleClick;
    }

    @Override
    public TethysFXNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        if (theWebView != null) {
            theWebView.setDisable(!pEnabled);
        }
    }

    @Override
    public void setVisible(final boolean pVisible) {
        if (theWebView != null) {
            theWebView.setManaged(pVisible);
            theWebView.setVisible(pVisible);
        }
    }

    @Override
    protected void loadHTMLContent(final String pHTMLString) {
        /* Load the content */
        if (theWebView != null) {
            theWebEngine.loadContent(pHTMLString);
        }
    }

    @Override
    protected void loadCSSContents() {
        /* reLoad the content */
        loadHTMLContent(getHTMLString());
    }

    /**
     * Allocate webView.
     */
    private void allocateWebView() {
        /* If we do not have a webView */
        if (theWebView == null) {
            /* Create WebView and access the engine */
            theWebView = new WebView();
            theWebEngine = theWebView.getEngine();
            theWebEngine.setJavaScriptEnabled(true);

            /* Attach to the pane */
            thePane.setCenter(theWebView);

            /* Create the load listener */
            theWebEngine.getLoadWorker().stateProperty().addListener((v, o, n) -> handleStateChange(n));

            /* load CSS contents */
            loadCSSContents();
        }
    }

    @Override
    public void scrollToReference(final String pReference) {
        /* If the webPage is fully loaded */
        if (theWebEngine.getLoadWorker().getState().equals(Worker.State.SUCCEEDED)) {
            /* Obtain the key objects */
            final JSObject myWin = (JSObject) theWebEngine.executeScript("window");
            final JSObject myDoc = (JSObject) theWebEngine.executeScript("document");
            final JSObject myEl = (JSObject) myDoc.call("getElementById", pReference);

            /* If we found the reference */
            if (myEl != null) {
                /* Access element rectangle */
                final JSObject myBox = (JSObject) myEl.call("getBoundingClientRect");

                /* Calculate vertical scroll */
                final Integer myHeight = (Integer) myWin.getMember("innerHeight");
                final Double myTop = getJSDouble(myBox.getMember("top"));
                final Double myBottom = getJSDouble(myBox.getMember("bottom"));
                Double myAdjustY = myTop;
                if (myBottom - myTop > myHeight) {
                    myAdjustY = myBottom - myHeight;
                }

                /* Calculate horizontal scroll */
                final Integer myWidth = (Integer) myWin.getMember("innerWidth");
                final Double myLeft = getJSDouble(myBox.getMember("left"));
                final Double myRight = getJSDouble(myBox.getMember("right"));
                Double myAdjustX = myLeft;
                if (myRight - myLeft > myWidth) {
                    myAdjustX = myRight - myWidth;
                }

                /* Perform the scroll */
                final Integer myXOffset = getJSInteger(myWin.getMember("pageXOffset"));
                final Integer myYOffset = getJSInteger(myWin.getMember("pageYOffset"));
                myWin.call("scrollTo", myXOffset + myAdjustX, myYOffset + myAdjustY);
            } else {
                LOGGER.error("Failed to locate reference <%s>", pReference);
            }

            /* else still loading */
        } else {
            /* Register pending scroll */
            thePendingScroll = pReference;
        }
    }

    /**
     * Obtain JSObject Double.
     *
     * @param pObject the returned number (either Double or Integer)
     * @return the Double
     */
    private static Double getJSDouble(final Object pObject) {
        if (pObject instanceof Double) {
            return (Double) pObject;
        }
        if (pObject instanceof Integer) {
            return ((Integer) pObject).doubleValue();
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain JSObject Integer.
     *
     * @param pObject the returned number (either Double or Integer)
     * @return the Integer
     */
    private static Integer getJSInteger(final Object pObject) {
        if (pObject instanceof Integer) {
            return (Integer) pObject;
        }
        if (pObject instanceof Double) {
            return ((Double) pObject).intValue();
        }
        throw new IllegalArgumentException();
    }

    /**
     * Handle click.
     *
     * @param pEvent the event
     */
    private void handleClick(final Event pEvent) {
        if (EVENT_TYPE_CLICK.equals(pEvent.getType())) {
            processReference(((Element) pEvent.getTarget()).getAttribute(ATTR_REF));
        }
    }

    /**
     * Handle load state change.
     *
     * @param pNewState the new state
     */
    private void handleStateChange(final State pNewState) {
        if (pNewState == Worker.State.SUCCEEDED) {
            attachListenerToDoc();
        }
    }

    @Override
    public void printIt() {
        /* Prepare to print the webPage */
        final PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null
                && job.showPrintDialog(theFactory.getStage())) {
            /* Access printer and determine orientation */
            final Printer myPrinter = job.getPrinter();

            /* Create page layout of correct type */
            final PageLayout myLayout = myPrinter.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
            job.getJobSettings().setPageLayout(myLayout);

            /* Print the WebPage */
            theWebEngine.print(job);
            job.endJob();
        }
    }

    /**
     * Attach hyperLinkListener.
     */
    private void attachListenerToDoc() {
        /* LookUp all anchor elements in the HTML */
        final Document myDoc = theWebEngine.getDocument();

        /* If we have CSS to add */
        final String myCSS = getProcessedCSS();
        if (myCSS != null) {
            /* Create the style element */
            final Element myElement = myDoc.createElement(ELEMENT_STYLE);
            final Text myContent = myDoc.createTextNode(myCSS);
            myElement.appendChild(myContent);

            /* Obtain the head and add a style element */
            final NodeList headList = myDoc.getElementsByTagName(ELEMENT_HEAD);
            if (headList.getLength() > 0) {
                headList.item(0).appendChild(myElement);
            }
        }

        /* Attach the listener to listen for clicks */
        final NodeList nodeList = myDoc.getElementsByTagName(ELEMENT_ANCHOR);
        for (int i = 0; i < nodeList.getLength(); i++) {
            /* Only listen to anchors that contain an HRef */
            final org.w3c.dom.Node myNode = nodeList.item(i);
            final NamedNodeMap myAttrMap = myNode.getAttributes();
            if (myAttrMap.getNamedItem(ATTR_REF) != null) {
                ((EventTarget) myNode).addEventListener(EVENT_TYPE_CLICK, theListener, false);
            }
        }

        /* If we have a pending scroll */
        if (thePendingScroll != null) {
            /* We can perform the scroll now */
            scrollToReference(thePendingScroll);
            thePendingScroll = null;
        }
    }
}
