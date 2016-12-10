# dropwizard-json-exceptions

## 2.0.0 2016/12/xx

* Upgraded dropwizard dependency to 1.0.5

## 1.1.2 2016/06/21

* All WebAppExceptions should be mapped to JSON with their own response type

## 1.1.1 2016/03/03

* Added 403s to RuntimeExceptionMapper

## 1.1.0 2015/11/19

* Added configurable MediaType return types for the 2 ExceptionMappers (they're still defaulting to JSON)
* Added ErrorMessage JSON to all types of JsonProcessingExceptionMapper responses

## 1.0.0 2015/11/18

* Initial copy of the JsonProcessingExceptionMapper from https://goo.gl/uhbxtQ
* Initial copy of the RuntimeExceptionMapper from http://gary-rowe.com/agilestack/2012/10/23/how-to-implement-a-runtimeexceptionmapper-for-dropwizard/
