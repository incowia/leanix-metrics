package net.leanix.metrics.excelimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Read a excel sheet
 */
public class ReadExcel {
	
	public List<Measurement> readExcel(String path, boolean debug) throws IOException{
		
		List<Measurement> measurementList = new ArrayList<>();
		try(FileInputStream file = new FileInputStream(new File(path))){
			
			//Get the workbook instance for XLSX file 
			try(XSSFWorkbook workbook = new XSSFWorkbook(file)){
				
				//Get first sheet from the workbook
				XSSFSheet sheet = workbook.getSheetAt(0);

				//Get iterator to all the rows in current sheet
				Iterator<Row> rowIterator = sheet.iterator();
				
				while(rowIterator.hasNext()){
					Row row = rowIterator.next();
					debug(debug, row);
					Measurement measurement = new Measurement();
					//nicht die erste Zeile
					if(row.getRowNum() > 0){
						measurement.setHost(row.getCell(0).getStringCellValue());
						measurement.setToken(row.getCell(1).getStringCellValue());
						measurement.setWorkspaceID(row.getCell(2).getStringCellValue());
						measurement.setName(row.getCell(3).getStringCellValue());
						measurement.setDate(row.getCell(4).getDateCellValue());
						
						Iterator<Cell> cellIterator = row.cellIterator();
					    while (cellIterator.hasNext()) {
					        Cell cell = cellIterator.next();
					        if (cell.getColumnIndex() > 4 && cell.getCellTypeEnum() == CellType.STRING) {// To match column index
					        	String stringCellValue = cell.getStringCellValue();
					        	//is typ field
					        	if(stringCellValue.equals(Typ.f.toString())){
<<<<<<< HEAD
					        		measurement.getListOfFields().put(row.getCell(cell.getColumnIndex()+1).getStringCellValue(), row.getCell(cell.getColumnIndex()+2).getNumericCellValue());
=======
					        		measurement.getListOfFields().put(row.getCell(cell.getColumnIndex()+1).getStringCellValue(), row.getCell(cell.getColumnIndex()+2).getStringCellValue());
>>>>>>> 64bc94a3a9944e10d3f7310638c7ce71ef2de9c2
					        	}
					        	//is typ tag
					        	if(stringCellValue.equals(Typ.t.toString())){
					        		measurement.getListOfTags().put(row.getCell(cell.getColumnIndex()+1).getStringCellValue(), row.getCell(cell.getColumnIndex()+2).getStringCellValue());
					        	}
					          }
					        }
					    measurementList.add(measurement);
					}
				}
			}
		}
		return measurementList;
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
