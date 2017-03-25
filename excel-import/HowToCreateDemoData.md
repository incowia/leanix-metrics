# How to create demo data

## Table of Contents
 
- [Supported excel formats](#supported-excel-formats)
- [Contents](#contents)
- [Examples](#examples)

## Supported excel formats

TODO

## Contents

* ```Measurement``` (key, case-sensitive, required)
	* must be placed in the first row
	* followed by the name of the measurement (value, required) in the next column
* ```Tags``` (key, case-sensitive, required)
	* must be placed right after the measurement row
	* followed by a list of 0-n tag names (top-down, values, optional) in the next column
	* each list element identifies a tag column in the points matrix (all characters are included for comparison, case-sensitive)
* ```Points``` (key, case-sensitive, required)
	* must be placed right after the tags list.
	* followed by a matrix of point data (values, partially required) in the next columns
		* the first column must be ```Time``` and each value must contain a valid date (time, time zone etc. are optional)
		* the next columns are defined by the tags list
			* each element defines a column (order is defined by the list)
			* if a tags list is defined, then these columns are required
			* values are optional
		* all following columns will be interpreted as point fields, values are optional

## Examples

All examples are valid.

### w/o points (minimal content w/o tags)

 #  |  A  |  B
--- | --- | ---
  1 | Measurement | MyMeasurement
  2 | Tags
  3 | Points | Time

### w/o points (minimal content w/ tags)

 #  |  A  |  B  |  C  |  D
--- | --- | --- | --- | ---
  1 | Measurement | MyMeasurement2
  2 | Tags | tag1
  3 | | tag2
  4 | Points | Time | tag1 | tag2

### w/ points (content w/o tags)

 #  |  A  |  B  |  C  |  D
--- | --- | --- | --- | ---
  1 | Measurement | MyMeasurement3
  2 | Tags
  3 | Points | Time | field1 | field2
  4 | | 01.01.2017 01:00 | 1 | 3
  5 | | 01.01.2017 01:00 | 0 | 3

### w/ points (content w/ tags)

 #  |  A  |  B  |  C  |  D  |  E  |  F
--- | --- | --- | --- | --- | --- | ---
  1 | Measurement | app-data-quality-per-businesscap
  2 | Tags | factsheetId
  3 | | name
  4 | Points | Time | factsheetId | name | complete | not complete
  5 | | 01.01.2017 01:00 | 100200300 | MyBusinessCap1 | 1 | 3
  6 | | 01.01.2017 01:00 | 100200301 | MyBusinessCap2 | 0 | 3
  7 | | 01.01.2017 01:00 | 100200302 | MyBusinessCap3 | 4 | 2
  8 | | 01.01.2017 01:00 | 100200303 | MyBusinessCap4 | 2 | 0
  9 | | 02.01.2017 01:00 | 100200300 | MyBusinessCap1 | 3 | 1
 10 | | 02.01.2017 01:00 | 100200301 | MyBusinessCap2 | 1 | 2
 11 | | 02.01.2017 01:00 | 100200302 | MyBusinessCap3 | 5 | 1
 12 | | 02.01.2017 01:00 | 100200303 | MyBusinessCap4 | 2 | 0














