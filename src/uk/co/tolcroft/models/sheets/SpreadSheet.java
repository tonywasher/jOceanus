package uk.co.tolcroft.models.sheets;

import java.io.File;

import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataSet;

public abstract class SpreadSheet<T extends DataSet<?>> {
	/**
	 *  Obtain a sheet reader
	 *  @param pThread Thread Control for task
	 *  @return the sheet reader
	 */
	protected abstract SheetReader<T> getSheetReader(statusCtl pThread);
	
	/**
	 *  Obtain a sheet writer
	 *  @param pThread Thread Control for task
	 *  @return the sheet writer
	 */
	protected abstract SheetWriter<T> getSheetWriter(statusCtl pThread);
	
	/**
	 *  Load a Backup Workbook
	 *  @param pThread Thread Control for task
	 *  @param pFile the backup file to load from
	 *  @return the newly loaded data
	 */
	public T loadBackup(statusCtl 	pThread,
	  		 			File 		pFile) throws Exception {
		/* Create a sheet reader object */
		SheetReader<T> myReader = getSheetReader(pThread);
			
		/* Load the backup */
		T myData = myReader.loadBackup(pFile);
		
		/* Return the data */
		return myData;
	}
		
	/**
	 *  Create a Backup Workbook
	 *  @param pThread Thread Control for task
	 *  @param pData Data to write out
	 *  @param pFile the backup file to write to
	 */
	public void createBackup(statusCtl 	pThread,
							 T			pData,
							 File 		pFile) throws Exception {
		/* Create a sheet writer object */
		SheetWriter<T> myWriter = getSheetWriter(pThread);
			
		/* Create the backup */
		myWriter.createBackup(pData, pFile);
	}
		
	/**
	 *  Load an Extract Workbook
	 *  @param pThread Thread Control for task
	 *  @param pFile the extract file to load from
	 *  @return the newly loaded data
	 */
	public T loadExtract(statusCtl pThread,
	  		 				  	   File 		pFile) throws Exception {
		/* Create a Sheet Reader object */
		SheetReader<T> myReader = getSheetReader(pThread);
			
		/* Load the extract file */
		T myData = myReader.loadExtract(pFile);
		
		/* Return the data */
		return myData;
	}
	
	/**
	 *  Create an Extract Workbook
	 *  @param pThread Thread Control for task
	 *  @param pData Data to write out
	 *  @param pFile the extract file to write to
	 */
	public void createExtract(statusCtl 	pThread,
							  T				pData,
							  File 			pFile) throws Exception {
		/* Create a SheetWriter object */
		SheetWriter<T> myWriter = getSheetWriter(pThread);
			
		/* Create the Extract file */
		myWriter.createExtract(pData, pFile);
	}
	
	/**
	 * Spreadsheet types
	 */
	public enum SheetType {
		BACKUP,
		EXTRACT;
	}
}
