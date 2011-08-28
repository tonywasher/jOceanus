package uk.co.tolcroft.models.ui;

import java.awt.Color;
import java.awt.Font;

public class RenderData {
	private java.lang.String theToolTipText = null;
	private Font			 theFont		= null;
	private Color            theForeGround  = null;
	private Color            theBackGround  = null;
	private int				 theRow			= 0;
	private int				 theCol			= 0;
	private boolean			 isSelected		= false;
	private boolean			 isFixed		= false;
	public  Color            getForeGround() { return theForeGround; }
	public  Color            getBackGround() { return theBackGround; }
	public  Font             getFont() 		{ return theFont; }
	public  java.lang.String getToolTip()   { return theToolTipText; }
	public  int				 getRow()   	{ return theRow; }
	public  int				 getCol()		{ return theCol; }
	public  boolean			 isSelected() 	{ return isSelected; }
	public  boolean			 isFixed() 		{ return isFixed; }
	public 	RenderData(boolean isFixed) { this.isFixed = isFixed;}
	public  void setData(Color pFore, Color pBack, 
						 Font pFont, java.lang.String pTooltip) { 
		theForeGround 	= pFore;
		theBackGround 	= pBack;
		theFont		  	= pFont;
		theToolTipText 	= pTooltip; }
	public void setPosition(int row, int col, boolean isSelected) {
		theRow 			= row;
		theCol 			= col;
		this.isSelected = isSelected; }
}
