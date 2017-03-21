package net.leanix.metrics.excelimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
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
				
				int i = 4;
				while(rowIterator.hasNext()){
					Row row = rowIterator.next();
					debug(debug, row);
					//nicht die erste Zeile
					if(row.getRowNum() > 1){
						measurement.setHost(row.getCell(0).getStringCellValue());
						measurement.setToken(row.getCell(1).getStringCellValue());
						measurement.setWorkspaceID(row.getCell(2).getStringCellValue());
						measurement.setName(row.getCell(3).getStringCellValue());
						measurement.setDate(row.getCell(4).getDateCellValue());
						
						Iterator<Cell> cellIterator = row.cellIterator();
					    while (cellIterator.hasNext()) {
					        Cell cell = cellIterator.next();
					        if (cell.getColumnIndex() > 4) {// To match column index
					        	String stringCellValue = cell.getStringCellValue();
					        	if(stringCellValue.equals(Typ.f) == true){
					        		//is typ field
					        		measurement.getListOfFields().put(row.getCell(i++).getStringCellValue(), row.getCell(i++).getStringCellValue());
					        		//is typ tag
					        	}
					          }
					        }
					}
				}
			}
		}
	}// end method

	/**
	 * @param debug
	 * @param row
	 */
	private void debug(boolean debug, Row row) {
		if(debug){
			if(row.getRowNum() !=0){
				System.out.print("\n");
			}
			//For each row, iterate through each columns
			Iterator<Cell> cellIterator = row.cellIterator();
			while(cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
					switch(cell.getCellType()) {
						case Cell.CELL_TYPE_BOOLEAN:
							System.out.print(cell.getBooleanCellValue() + "\t");
							break;
						case Cell.CELL_TYPE_NUMERIC:
							if (HSSFDateUtil.isCellDateFormatted(cell)){
						        //Value variable displaying output as 8/5/16 instead of 8/5/2016
						        System.out.print(cell.getDateCellValue() + "\t");
						    }
							else{
								System.out.print(cell.getNumericCellValue() + "\t");
							}
							break;
						case Cell.CELL_TYPE_STRING:
							System.out.print(cell.getStringCellValue() + "\t");
							break;
					}
				}
		}
	}
}// end class
