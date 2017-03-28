# How to create demo data

## Table of Contents
 
- [Supported excel formats](#supported-excel-formats)
- [Content](#content)
- [Examples](#examples)

## Supported excel formats

TODO

## Content

* all content must be placed on the first worksheet
* ```Measurement``` (key, case-sensitive, required)
	* must be placed in the first row
	* followed by the name of the measurement (value, required) in the next column
* ```Tags``` (key, case-sensitive, required)
	* must be placed right after the measurement row
	* followed by markers for the points matrix columns (values, optional) in the next columns
	* each marker identifies a tag column in the points matrix (any character sequence can be used as marker, an empty cell marks a field column)
* ```Points``` (key, case-sensitive, required)
	* must be placed right after the tag markers
	* followed by a matrix of point data (values, partially required) in the next columns
		* the first column must be ```Time```
			* each value must contain a valid date (e.g. dd.mm.yyyy, time, time zone etc. are optional)
			* values are required
		* any additional column will be interpreted as tag or field column
			* the title of the column will be used as point tag/field key (as it is)
			* values are optional

## Examples

All examples are valid.

### w/o points (minimal content w/o tags)

\#  |  A  |  B
--- | --- | ---
  1 | Measurement | MyMeasurement
  2 | Tags
  3 | Points | Time

### w/o points (minimal content w/ tags)

\#  |  A  |  B  |  C  |  D
--- | --- | --- | --- | ---
  1 | Measurement | MyMeasurement2
  2 | Tags | | x | x
  3 | Points | Time | tag1 | tag2

### w/ points (content w/o tags)

\#  |  A  |  B  |  C  |  D
--- | --- | --- | --- | ---
  1 | Measurement | MyMeasurement3
  2 | Tags
  3 | Points | Time | field1 | field2
  4 | | 01.01.2017 | 1 | 3
  5 | | 02.01.2017 | 0 | 4

### w/ points (content w/ tags)

\#  |  A  |  B  |  C  |  D  |  E  |  F
--- | --- | --- | --- | --- | --- | ---
  1 | Measurement | MyMeasurement4
  2 | Tags | | x | x
  3 | Points | Time | factsheetId | name | field1 | field2
  4 | | 01.01.2017 | 100200300 | MyFactsheet1 | 1 | 3
  5 | | 01.01.2017 | 100200301 | MyFactsheet2 | 0 | 3
  6 | | 02.01.2017 | 100200300 | MyFactsheet1 | 0 | 4
  7 | | 02.01.2017 | 100200301 | MyFactsheet2 | 2 | 1