package net.leanix.metrics.excelimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.leanix.dropkit.apiclient.ApiException;
import net.leanix.metrics.api.PointsApi;
import net.leanix.metrics.api.models.Field;
import net.leanix.metrics.api.models.Point;
import net.leanix.metrics.api.models.Tag;

public class ImportJob {

	private final net.leanix.dropkit.apiclient.ApiClient metricsClient;
	private final String workspaceId;
	private final String path;
	private final boolean debug;

	public ImportJob(net.leanix.dropkit.apiclient.ApiClient metricsClient, String workspaceId, String path,
			boolean debug) throws NullPointerException {
		this.metricsClient = Objects.requireNonNull(metricsClient);
		this.workspaceId = Objects.requireNonNull(workspaceId);
		this.path = Objects.requireNonNull(path);
		this.debug = debug;
	}

	public void run() throws Exception {
		Map<String, List<Point>> measurements = getMeasurements();
		if (debug) {
			measurements.forEach((measurement, points) -> points.forEach(System.out::println));
		}
		saveMeasurements(measurements);
	}

	private Map<String, List<Point>> getMeasurements()
			throws IOException, InvalidOperationException, InvalidFormatException {
		try (FileInputStream stream = new FileInputStream(new File(path))) {
			try (Workbook wb = getWorkbook(path, stream)) {
				Map<String, List<Point>> measurements = new LinkedHashMap<>();
				if (wb.isHidden()) {
					return measurements;
				}
				for (int i = 0, len = wb.getNumberOfSheets(); i < len; i++) {
					if (wb.isSheetHidden(i) || wb.isSheetVeryHidden(i)) {
						continue;
					}
					Sheet sheet = wb.getSheetAt(i);
					if (!isSheetApplicable(sheet)) {
						continue;
					}
					final String measurement = sheet.getSheetName();
					List<Point> points = new ArrayList<>();
					measurements.put(measurement, points);
					// extract data
					List<String> headers = getHeaders(sheet);
					if (headers.size() < 2) {
						// no tag or field keys defined (also exclude minimal
						// valid content)
						continue;
					}
					List<Boolean> tagMarkers = getTagMarkers(sheet);
					List<List<Object>> pointsData = getPointsData(sheet, headers.size());
					// create the points
					for (int j = 0, len2 = pointsData.size(); j < len2; j++) {
						List<Object> pointData = pointsData.get(j);
						Point point = new Point();
						points.add(point);
						point.setWorkspaceId(workspaceId);
						point.setMeasurement(measurement);
						// first is 'Time' column
						point.setTime((Date) pointData.get(0));
						// all others are tags or fields
						for (int k = 1, len3 = pointData.size(); k < len3; k++) {
							String header = headers.get(k);
							if (header == null) {
								// excluded column
								continue;
							}
							if (tagMarkers.get(k)) {
								// it's a tag
								Tag tag = new Tag();
								tag.setK(header);
								tag.setV(toString(pointData.get(k)));
								point.getTags().add(tag);
							} else {
								// it's a field
								Field field = new Field();
								field.setK(header);
								Object value = pointData.get(k);
								if (value instanceof Double) {
									field.setV((Double) value);
								} else {
									field.setS(toString(value));
								}
								point.getFields().add(field);
							}
						}
					}
				}
				return measurements;
			}
		}
	}

	private static String toString(Object obj) {
		if (obj instanceof Double) {
			return Double.toString((Double) obj);
		} else if (obj instanceof Boolean) {
			return Boolean.toString((Boolean) obj);
		}
		return obj.toString();
	}

	@SuppressWarnings({ "incomplete-switch", "deprecation" })
	private static List<List<Object>> getPointsData(Sheet sheet, int columnSizeThreshold) {
		List<List<Object>> result = new ArrayList<>();
		List<Row> rows = new ArrayList<>();
		int skippedRows = 0;
		for (Row row : sheet) {
			if (!isRowApplicable(row)) {
				continue;
			}
			if (skippedRows < 2) {
				skippedRows++;
				continue;
			}
			rows.add(row);
		}
		for (Row row : rows) {
			List<Object> cells = new ArrayList<>();
			result.add(cells);
			for (Cell cell : row) {
				if (cells.size() == columnSizeThreshold) {
					break;
				}
				if (!isCellApplicable(cell)) {
					continue;
				}
				Object value = null;
				if (DateUtil.isCellDateFormatted(cell)) {
					value = cell.getDateCellValue();
				} else {
					switch (cell.getCellTypeEnum()) {
					case NUMERIC:
						value = Double.valueOf(cell.getNumericCellValue());
						break;
					case STRING:
						value = cell.getStringCellValue();
						break;
					case FORMULA:
						switch (cell.getCachedFormulaResultTypeEnum()) {
						case NUMERIC:
							value = Double.valueOf(cell.getNumericCellValue());
							break;
						case STRING:
							value = cell.getStringCellValue();
							break;
						case BOOLEAN:
							value = Boolean.valueOf(cell.getBooleanCellValue());
							break;
						case ERROR:
							// exclude the cell
							break;
						}
						break;
					case BOOLEAN:
						value = Boolean.valueOf(cell.getBooleanCellValue());
						break;
					case BLANK:
					case ERROR:
						// exclude the cells
						break;
					}
				}
				cells.add(value);
			}
		}
		return result;
	}

	private static List<Boolean> getTagMarkers(Sheet sheet) {
		List<Boolean> result = new ArrayList<>();
		Row tagsRow = null;
		boolean skippedFirstRow = false;
		for (Row row : sheet) {
			if (!isRowApplicable(row)) {
				continue;
			}
			if (!skippedFirstRow) {
				skippedFirstRow = true;
				continue;
			}
			tagsRow = row;
			break;
		}
		// just to be sure
		if (tagsRow == null) {
			return result;
		}
		for (Cell cell : tagsRow) {
			if (!isCellApplicable(cell)) {
				continue;
			}
			String marker = cell.getStringCellValue();
			if (marker == null || (marker = marker.trim()).isEmpty()) {
				result.add(Boolean.FALSE);
			} else {
				result.add(Boolean.TRUE);
			}
		}
		// first is always for 'Time' column, so set to false
		result.set(0, Boolean.FALSE);
		return result;
	}

	private static List<String> getHeaders(Sheet sheet) {
		List<String> result = new ArrayList<>();
		Row headerRow = null;
		for (Row row : sheet) {
			if (!isRowApplicable(row)) {
				continue;
			}
			headerRow = row;
			break;
		}
		// just to be sure
		if (headerRow == null) {
			return result;
		}
		for (Cell cell : headerRow) {
			if (!isCellApplicable(cell)) {
				continue;
			}
			String header = cell.getStringCellValue();
			if (header != null && !(header = header.trim()).isEmpty()) {
				result.add(header);
			} else {
				result.add(null);
			}
		}
		return result;
	}

	private static boolean isRowApplicable(Row row) {
		return row != null && !row.getZeroHeight()
				&& (row.getRowStyle() != null ? !row.getRowStyle().getHidden() : true);
	}

	private static boolean isSheetApplicable(Sheet sheet) {
		/*
		 * sheet must contain at least 2 rows, at least 1 column, at least a
		 * 'Time' column as first column, a 'Tags' row as second row, size must
		 * be valid (see below), 'Time' column must contains Date values (except
		 * 'Tags' row)
		 */
		if (sheet == null || sheet.getPhysicalNumberOfRows() < 1) {
			return false;
		}
		// avoid crappy hidden and 'break' rows and ... that's odd
		Row firstRow = null, secondRow = null;
		boolean sizeCheck = true;
		for (Row row : sheet) {
			if (!isRowApplicable(row)) {
				continue;
			}
			if (firstRow == null) {
				firstRow = row;
			} else if (secondRow == null) {
				secondRow = row;
				// size check: firstRow and secondRow must have the same size
				sizeCheck = firstRow.getLastCellNum() == secondRow.getLastCellNum();
			} else {
				// size check: all other rows must be equal or greater (maybe
				// additional columns that should be excluded, means no key
				// defined)
				if (sizeCheck) {
					sizeCheck = row.getLastCellNum() >= firstRow.getLastCellNum();
				}
				if (!sizeCheck) {
					return false;
				}
				Cell firstCell = getFirstCell(row);
				if (firstCell == null || !DateUtil.isCellDateFormatted(firstCell)
						|| firstCell.getDateCellValue() == null) {
					return false;
				}
			}
		}
		if (firstRow == null || firstRow.getPhysicalNumberOfCells() < 0 || secondRow == null
				|| secondRow.getPhysicalNumberOfCells() < 0) {
			return false;
		}
		return isCellContentApplicable(getFirstCell(firstRow), "Time")
				&& isCellContentApplicable(getFirstCell(secondRow), "Tags");
	}

	private static Cell getFirstCell(Row row) {
		// short hand: try firstCellNum
		Cell firstCell = row.getCell(row.getFirstCellNum());
		if (isCellApplicable(firstCell)) {
			return firstCell;
		}
		// okay, have to search now
		for (Cell cell : row) {
			if (isCellApplicable(cell)) {
				return cell;
			}
		}
		return null;
	}

	private static boolean isCellApplicable(Cell cell) {
		if (cell == null) {
			return false;
		}
		if (cell.getSheet().isColumnHidden(cell.getColumnIndex())) {
			return false;
		}
		return !cell.getCellStyle().getHidden();
	}

	private static boolean isCellContentApplicable(Cell cell, String content) {
		if (!isCellApplicable(cell)) {
			return false;
		}
		try {
			return content.equals(cell.getStringCellValue());
		} catch (Exception e) {
			return false;
		}
	}

	private static Workbook getWorkbook(String path, InputStream stream) throws IOException, InvalidFormatException {
		if (path.endsWith(".xlsx")) {
			return new XSSFWorkbook(stream);
		} else if (path.endsWith(".xls")) {
			return new HSSFWorkbook(stream, false);
		}
		throw new InvalidFormatException("file extension is not *.xlsx nor *.xls");
	}

	private void saveMeasurements(Map<String, List<Point>> measurements) throws ApiException {
		PointsApi pointsApi = new PointsApi(metricsClient);
		for (String measurement : measurements.keySet()) {
			List<Point> points = measurements.get(measurement);
			if (points == null || points.isEmpty()) {
				continue;
			}
			if (debug) {
				System.out.println("Sending points for " + measurement);
			}
			for (Point p : points) {
				pointsApi.createPoint(p);
			}
		}
	}
}