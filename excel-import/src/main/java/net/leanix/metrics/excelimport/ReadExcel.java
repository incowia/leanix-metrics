package net.leanix.metrics.excelimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * Read a excel sheet
 */
public class ReadExcel {
	
	public void readExcel(String path, Measurement measurement, boolean debug) throws IOException{
		
		try(FileInputStream file = new FileInputStream(new File(path))){
			
			//Get the workbook instance for XLSX file 
			try(XSSFWorkbook workbook = new XSSFWorkbook(file)){
				
				//Get first sheet from the workbook
				XSSFSheet sheet = workbook.getSheetAt(0);

				//Get iterator to all the rows in current sheet
				Iterator<Row> rowIterator = sheet.iterator();
				
				while(rowIterator.hasNext()){
					Row row = rowIterator.next();
					//For each row, iterate through each columns
					Iterator<Cell> cellIterator = row.cellIterator();
					while(cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						if(debug){
							System.out.println(cell.getStringCellValue() + "\t\t");
						}
					}
				}
			}
			/*while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				
				//For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				while(cellIterator.hasNext()) {
					
					Cell cell = cellIterator.next();
					
					switch(cell.getCellType()) {
						case Cell.CELL_TYPE_BOOLEAN:
							System.out.print(cell.getBooleanCellValue() + "\t\t");
							break;
						case Cell.CELL_TYPE_NUMERIC:
							System.out.print(cell.getNumericCellValue() + "\t\t");
							break;
						case Cell.CELL_TYPE_STRING:
							System.out.print(cell.getStringCellValue() + "\t\t");
							break;
					}
				}
			}*/
			
				
		}
	}// end method

}// end class
