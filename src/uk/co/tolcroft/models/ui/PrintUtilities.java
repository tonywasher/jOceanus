package uk.co.tolcroft.models.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

public class PrintUtilities  implements Printable {
	  private JComponent componentToBePrinted;

	  public static void printComponent(JComponent c) {
	    new PrintUtilities(c).print();
	  }
	  
	  public PrintUtilities(JComponent componentToBePrinted) {
	    this.componentToBePrinted = componentToBePrinted;
	  }
	  
	  public void print() {
	    PrinterJob printJob = PrinterJob.getPrinterJob();
	    printJob.setPrintable(this);
	    if (printJob.printDialog())
	      try {
	        printJob.print();
	      } catch(PrinterException pe) {
	        System.out.println("Error printing: " + pe);
	      }
	  }

	  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
	    if (pageIndex > 0) {
	      return(NO_SUCH_PAGE);
	    } else {
	      Graphics2D g2d = (Graphics2D)g;
	      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
	      disableDoubleBuffering(componentToBePrinted);
	      componentToBePrinted.paint(g2d);
	      enableDoubleBuffering(componentToBePrinted);
	      return(PAGE_EXISTS);
	    }
	  }

	  public static void disableDoubleBuffering(JComponent c) {
	    RepaintManager currentManager = RepaintManager.currentManager(c);
	    currentManager.setDoubleBufferingEnabled(false);
	  }

	  public static void enableDoubleBuffering(JComponent c) {
	    RepaintManager currentManager = RepaintManager.currentManager(c);
	    currentManager.setDoubleBufferingEnabled(true);
	  }
}
