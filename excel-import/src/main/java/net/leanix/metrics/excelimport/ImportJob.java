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
	private final boolean dryRun;

	public ImportJob(net.leanix.dropkit.apiclient.ApiClient metricsClient, String workspaceId, String path,
			boolean debug, boolean dryRun) throws NullPointerException {
		this.metricsClient = Objects.requireNonNull(metricsClient);
		this.workspaceId = Objects.requireNonNull(workspaceId);
		this.path = Objects.requireNonNull(path);
		this.debug = debug;
		this.dryRun = dryRun;
	}

	public void run() throws Exception {
		Map<String, List<Point>> measurements = getMeasurements();
		if (debug) {
			measurements.forEach((measurement, points) -> {
				if (points.isEmpty()) {
					return;
				}
				System.out.println("measurement:" + measurement);
				StringBuilder sb = new StringBuilder("\tpoints[");
				points.forEach((point) -> {
					sb.append("point(workspaceId:").append(point.getWorkspaceId());
					sb.append(";time:").append(point.getTime());
					if (!point.getTags().isEmpty()) {
						sb.append(";tags[");
						point.getTags().forEach((tag) -> {
							sb.append("tag(key:").append(tag.getK());
							sb.append(";value:").append(tag.getV()).append("),");
						});
						sb.deleteCharAt(sb.length() - 1).append("]");
					}
					if (!point.getFields().isEmpty()) {
						sb.append(";fields[");
						point.getFields().forEach((field) -> {
							sb.append("field(key:").append(field.getK());
							if (field.getV() != null) {
								sb.append(";value-double:").append(field.getV()).append("),");
							} else {
								sb.append(";value-string:").append(field.getS()).append("),");
							}
						});
						sb.deleteCharAt(sb.length() - 1).append("]");
					}
					sb.append("),");
				});
				sb.deleteCharAt(sb.length() - 1).append("]");
				System.out.println(sb.toString());
			});
		}
		if (!dryRun) {
			if (debug) {
				System.out.println("Sending points ...");
			}
			saveMeasurements(measurements);
		}
	}

	private Map<String, List<Point>> getMeasurements()
			throws IOException, InvalidOperationException, InvalidFormatException {
		if (debug) {
			System.out.println("Try to read file from " + path);
		}
		File file = new File(path);
		if (debug) {
			System.out.println("Absolute path is " + file.getAbsolutePath());
		}
		try (FileInputStream stream = new FileInputStream(file)) {
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
					Data data = getData(sheet);
					if (data.headers.size() < 2) {
						// no tag or field keys defined (also exclude minimal
						// valid content)
						continue;
					}
					// create the points
					for (int j = 0, len2 = data.pointsData.size(); j < len2; j++) {
						List<Object> pointData = data.pointsData.get(j);
						Point point = new Point();
						points.add(point);
						point.setWorkspaceId(workspaceId);
						point.setMeasurement(measurement);
						// first is 'Time' column
						point.setTime((Date) pointData.get(0));
						// all others are tags or fields
						for (int k = 1, len3 = pointData.size(); k < len3; k++) {
							String header = data.headers.get(k);
							if (data.tagMarkers.get(k)) {
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
								} else if (value != null) {
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

	private static Data getData(Sheet sheet) throws InvalidFormatException {
		Data data = new Data();
		// get all applicable rows
		final List<Row> rows = new ArrayList<>();
		sheet.forEach((row) -> {
			if (isRowApplicable(row)) {
				rows.add(row);
			}
		});
		// just to be sure
		if (rows.size() < 3) {
			// no data
			return data;
		}
		Row headersRow = rows.get(0);
		Row tagsRow = rows.get(1);
		List<Row> dataRows = rows.subList(2, rows.size());
		List<Integer> includedColumns = new ArrayList<>();
		// get the headers
		for (Cell cell : headersRow) {
			if (!isCellApplicable(cell)) {
				continue;
			}
			String header = cell.getStringCellValue();
			if (header != null && !(header = header.trim()).isEmpty()) {
				// normalize string
				header = header.replace("\n", " ");
				data.headers.add(header);
				includedColumns.add(Integer.valueOf(cell.getColumnIndex()));
			}
		}
		// header has no data? (at least the 'Time' column is present)
		if (data.headers.size() < 2) {
			return data;
		}
		// get the tag markers
		int eciIndex = 0;
		for (Cell cell : tagsRow) {
			// threshold reached?
			if (data.tagMarkers.size() == data.headers.size()) {
				break;
			}
			if (!isCellApplicable(cell)) {
				continue;
			}
			// must be included?
			if (!includedColumns.contains(Integer.valueOf(cell.getColumnIndex()))) {
				continue;
			}
			// sanity check if cell has the expected column index (sometimes
			// excel messes things up)
			int expectedColumnIndex = includedColumns.get(eciIndex++);
			if (expectedColumnIndex != cell.getColumnIndex()) {
				throw new InvalidFormatException("While reading the excel file, a column index jump occured."
						+ " This is most likely caused by an empty or wrong formatted cell in the points data area."
						+ " Please check and save again.");
			}
			String marker = cell.getStringCellValue();
			if (marker == null || (marker = marker.trim()).isEmpty()) {
				data.tagMarkers.add(Boolean.FALSE);
			} else {
				data.tagMarkers.add(Boolean.TRUE);
			}
		}
		// first is always for 'Time' column, so set to false
		data.tagMarkers.set(0, Boolean.FALSE);
		// get the points data
		for (Row row : dataRows) {
			List<Object> rowValues = new ArrayList<>();
			data.pointsData.add(rowValues);
			eciIndex = 0; // reset
			for (Cell cell : row) {
				// threshold reached?
				if (rowValues.size() == data.headers.size()) {
					break;
				}
				if (!isCellApplicable(cell)) {
					continue;
				}
				// must be included?
				if (!includedColumns.contains(Integer.valueOf(cell.getColumnIndex()))) {
					continue;
				}
				// sanity check if cell has the expected column index (sometimes
				// excel messes things up)
				int expectedColumnIndex = includedColumns.get(eciIndex++);
				if (expectedColumnIndex != cell.getColumnIndex()) {
					throw new InvalidFormatException("While reading the excel file, a column index jump occured."
							+ " This is most likely caused by an empty or wrong formatted cell in the points data area."
							+ " Please check and save again.");
				}
				Object value = getCellValue(cell);
				// sanity check
				if (value == null || (value instanceof String && value.toString().isEmpty())) {
					throw new InvalidFormatException("BLANK or empty cells are not supported (row: "
							+ cell.getRowIndex() + ", column: " + cell.getColumnIndex() + ", 0-based indices, sheet: "
							+ cell.getSheet().getSheetName() + ").");
				}
				rowValues.add(value);
			}
		}
		return data;
	}

	@SuppressWarnings("deprecation")
	private static Object getCellValue(Cell cell) throws InvalidFormatException {
		switch (cell.getCellTypeEnum()) {
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}
			return Double.valueOf(cell.getNumericCellValue());
		case STRING:
			return cell.getStringCellValue();
		case FORMULA:
			switch (cell.getCachedFormulaResultTypeEnum()) {
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return cell.getDateCellValue();
				}
				return Double.valueOf(cell.getNumericCellValue());
			case STRING:
				return cell.getStringCellValue();
			case BOOLEAN:
				return Boolean.valueOf(cell.getBooleanCellValue());
			case ERROR:
			default:
				throw notSupportedCellType(cell, true);
			}
		case BOOLEAN:
			return Boolean.valueOf(cell.getBooleanCellValue());
		case BLANK:
		case ERROR:
		default:
			throw notSupportedCellType(cell, false);
		}
	}

	@SuppressWarnings("deprecation")
	private static InvalidFormatException notSupportedCellType(Cell cell, boolean formula) {
		return new InvalidFormatException("Cell type of " + cell.getCellTypeEnum() + (formula ? " (FORMULA)" : "")
				+ " is not supported (row: " + cell.getRowIndex() + ", column: " + cell.getColumnIndex()
				+ ", 0-based indices, sheet: " + cell.getSheet().getSheetName() + ").");
	}

	private static boolean isRowApplicable(Row row) {
		return row != null && !row.getZeroHeight()
				&& (row.getRowStyle() != null ? !row.getRowStyle().getHidden() : true);
	}

	private static boolean isSheetApplicable(Sheet sheet) throws InvalidFormatException {
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
				if (firstCell == null) {
					return false;
				}
				Object value = getCellValue(firstCell);
				if (!(value instanceof Date)) {
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

	private static Cell getFirstCell(Row row) throws InvalidFormatException {
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

	private static boolean isCellApplicable(Cell cell) throws InvalidFormatException {
		if (cell == null) {
			return false;
		}
		if (cell.getSheet().isColumnHidden(cell.getColumnIndex())) {
			return false;
		}
		if (cell.getCellStyle() != null && cell.getCellStyle().getHidden()) {
			throw new InvalidFormatException("Hidden cells are not supported (row: " + cell.getRowIndex() + ", column: "
					+ cell.getColumnIndex() + ", 0-based indices, sheet: " + cell.getSheet().getSheetName() + ").");
		}
		return true;
	}

	private static boolean isCellContentApplicable(Cell cell, String content) throws InvalidFormatException {
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

	private static class Data {

		private List<String> headers = new ArrayList<>();
		private List<Boolean> tagMarkers = new ArrayList<>();
		private List<List<Object>> pointsData = new ArrayList<>();
	}
}