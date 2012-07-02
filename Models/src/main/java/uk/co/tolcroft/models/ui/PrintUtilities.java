/*******************************************************************************
 * JDataModel: Data models
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.models.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

/**
 * Print Utilities class.
 * @author Tony Washer
 */
public class PrintUtilities implements Printable {
    /**
     * Component to be printed.
     */
    private JComponent componentToBePrinted;

    /**
     * Print the component.
     * @param c the component
     * @throws JDataException on error
     */
    public static void printComponent(final JComponent c) throws JDataException {
        new PrintUtilities(c).print();
    }

    /**
     * Constructor.
     * @param pComponentToBePrinted the component
     */
    public PrintUtilities(final JComponent pComponentToBePrinted) {
        componentToBePrinted = pComponentToBePrinted;
    }

    /**
     * Print it.
     * @throws JDataException on error
     */
    public void print() throws JDataException {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (PrinterException pe) {
                throw new JDataException(ExceptionClass.DATA, "Error printing ", pe);
            }
        }
    }

    @Override
    public int print(final Graphics g,
                     final PageFormat pageFormat,
                     final int pageIndex) {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        } else if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            disableDoubleBuffering(componentToBePrinted);
            componentToBePrinted.paint(g2d);
            enableDoubleBuffering(componentToBePrinted);
            return PAGE_EXISTS;
        }
        return NO_SUCH_PAGE;
    }

    /**
     * Disable double buffering.
     * @param c the component
     */
    public static void disableDoubleBuffering(final JComponent c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    /**
     * Enable double buffering.
     * @param c the component
     */
    public static void enableDoubleBuffering(final JComponent c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}
