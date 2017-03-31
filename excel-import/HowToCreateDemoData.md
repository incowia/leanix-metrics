# How to create demo data

## Table of Contents
 
- [Supported excel formats & compatibility](#supported-excel-formats-and-compatibility)
- [Content](#content)
- [Examples](#examples)

## Supported excel formats and compatibility

* Excel 97 - 2003 (*.xls)
* *.xlsx

Values in the points data area can contain excel functions (excel usually stores pre-computed values by default, if disabled please make sure to trigger a computation before saving the file), but macros are not supported. It's possible to hide entire rows and/or columns, but not individual cells. Please avoid any special filter, sorting etc. You can check the compatibility of an excel file by using the ```-dryRun -debug``` cli options. This will output the parsed content as metrics points, which can be analyzed.

## Content

* every excel file can contain multiple measurements, one per sheet
* the name of a measurement is the sheet name
* the content of a sheet is considered applicable if
	* the first cell in the first visible row has ```Time``` as content (case-sensitive)
	* the first cell in the second visible row has ```Tags``` as content (case-sensitive)
	* all values of the ```Time``` must contain a valid date (e.g. dd.mm.yyyy, time, time zone etc. are optional), except for the ```Tags``` row
* the next visible row after the ```Tags``` row marks the beginning of the point data area
* every column after the ```Time``` column will be interpreted as tag or field column, if a header is present (columns can be excluded via hiding or marked as excluded by a blank header cell)
* a tag column is marked by a string (e.g. 'x') in the column of the ```Tags``` row, a blank cell means it's a field column
* blank cells in the point data area are not allowed
* cells marked with an error in the point data area are not allowed

## Examples

All examples are valid.

### w/o points (minimal content)

\#  |  A
--- | ---
  1 | Time
  2 | Tags

### w/ points (content w/o tags)

\#  |  A  |  B  |  C
--- | --- | --- | ---
  1 | Time | field1 | field2
  2 | Tags
  3 | 01.01.2017 | 1 | 3
  4 | 02.01.2017 | 0 | 4

### w/ points (content w/ tags)

\#  |  A  |  B  |  C  |  D  |  E
--- | --- | --- | --- | --- | ---
  1 | Time | tag1 | tag2 | field1 | field2
  2 | Tags | x | x
  3 | 01.01.2017 | 100200300 | MyFactsheet1 | 1 | 3
  4 | 01.01.2017 | 100200301 | MyFactsheet2 | 0 | 3
  5 | 02.01.2017 | 100200300 | MyFactsheet1 | 0 | 4
  6 | 02.01.2017 | 100200301 | MyFactsheet2 | 2 | 1