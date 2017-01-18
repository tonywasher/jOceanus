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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;
import netscape.javascript.JSObject;

/**
 * JavaFX HTML Manager.
 */
public class TethysFXHTMLManager
        extends TethysHTMLManager<Node, Node> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysFXHTMLManager.class);

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
     * The Stage.
     */
    private final Stage theStage;

    /**
     * WebView.
     */
    private final WebView theWebView;

    /**
     * WebEngine.
     */
    private final WebEngine theWebEngine;

    /**
     * HyperLink Listener.
     */
    private final EventListener theListener;

    /**
     * Pending scroll.
     */
    private String thePendingScroll;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysFXHTMLManager(final TethysFXGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Store the stage */
        theStage = pFactory.getStage();

        /* Create WebView and access the engine */
        theWebView = new WebView();
        theWebEngine = theWebView.getEngine();
        theWebEngine.setJavaScriptEnabled(true);

        /* Create the hyperLink listener */
        theListener = this::handleClick;

        /* Create the load listener */
        theWebEngine.getLoadWorker().stateProperty().addListener((v, o, n) -> handleStateChange(n));
    }

    @Override
    public Node getNode() {
        return theWebView;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theWebView.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theWebView.setManaged(pVisible);
        theWebView.setVisible(pVisible);
    }

    @Override
    protected void loadHTMLContent(final String pHTMLString) {
        /* Load the content */
        theWebEngine.loadContent(pHTMLString);
    }

    @Override
    protected void loadCSSContents() {
        /* reLoad the content */
        loadHTMLContent(getHTMLString());
    }

    @Override
    public void scrollToReference(final String pReference) {
        /* If the webPage is fully loaded */
        if (theWebEngine.getLoadWorker().getState().equals(Worker.State.SUCCEEDED)) {
            /* Obtain the key objects */
            JSObject myWin = (JSObject) theWebEngine.executeScript("window");
            JSObject myDoc = (JSObject) theWebEngine.executeScript("document");
            JSObject myEl = (JSObject) myDoc.call("getElementById", pReference);

            /* If we found the reference */
            if (myEl != null) {
                /* Access element rectangle */
                JSObject myBox = (JSObject) myEl.call("getBoundingClientRect");

                /* Calculate vertical scroll */
                Integer myHeight = (Integer) myWin.getMember("innerHeight");
                Double myTop = getJSDouble(myBox.getMember("top"));
                Double myBottom = getJSDouble(myBox.getMember("bottom"));
                Double myAdjustY = myTop;
                if (myBottom - myTop > myHeight) {
                    myAdjustY = myBottom - myHeight;
                }

                /* Calculate horizontal scroll */
                Integer myWidth = (Integer) myWin.getMember("innerWidth");
                Double myLeft = getJSDouble(myBox.getMember("left"));
                Double myRight = getJSDouble(myBox.getMember("right"));
                Double myAdjustX = myLeft;
                if (myRight - myLeft > myWidth) {
                    myAdjustX = myRight - myWidth;
                }

                /* Perform the scroll */
                Integer myXOffset = getJSInteger(myWin.getMember("pageXOffset"));
                Integer myYOffset = getJSInteger(myWin.getMember("pageYOffset"));
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
     * @param pEvent the event
     */
    private void handleClick(final Event pEvent) {
        if (EVENT_TYPE_CLICK.equals(pEvent.getType())) {
            processReference(((Element) pEvent.getTarget()).getAttribute(ATTR_REF));
        }
    }

    /**
     * Handle load state change.
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
        PrinterJob job = PrinterJob.createPrinterJob();
        if ((job != null)
            && job.showPrintDialog(theStage)) {
            /* Access printer and determine orientation */
            Printer myPrinter = job.getPrinter();

            /* Create page layout of correct type */
            PageLayout myLayout = myPrinter.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
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
        Document myDoc = theWebEngine.getDocument();

        /* If we have CSS to add */
        String myCSS = getProcessedCSS();
        if (myCSS != null) {
            /* Create the style element */
            Element myElement = myDoc.createElement(ELEMENT_STYLE);
            Text myContent = myDoc.createTextNode(myCSS);
            myElement.appendChild(myContent);

            /* Obtain the head and add a style element */
            NodeList headList = myDoc.getElementsByTagName(ELEMENT_HEAD);
            if (headList.getLength() > 0) {
                headList.item(0).appendChild(myElement);
            }
        }

        /* Attach the listener to listen for clicks */
        NodeList nodeList = myDoc.getElementsByTagName(ELEMENT_ANCHOR);
        for (int i = 0; i < nodeList.getLength(); i++) {
            /* Only listen to anchors that contain an HRef */
            org.w3c.dom.Node myNode = nodeList.item(i);
            NamedNodeMap myAttrMap = myNode.getAttributes();
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
