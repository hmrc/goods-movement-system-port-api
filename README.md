goods-movement-system-port-api
==============================

This API provides resources from the Goods Vehicle Movement Service (GVMS) for ports and border locations.

This API is used by ports and border locations to:

* Get a list of controlled GMR details that have embarked to or arrived at a GVMS port or border location
* Get a list of controlled GMR details that are checked-in for departure at a GVMS port or border location
* Get a list of checked-in GMR details at a GVMS port or border location
* Get GVMS reference data

Table of Contents
=================

* [Running and Testing](#running-and-testing)
    * [Running](#running)
    * [Testing](#testing)
* [Endpoints](#endpoints)
* [License](#license)

Running and Testing
===================
By default, the service runs on 8998 port.

Running
-------
For local development use `sbt run` but if its already running in sm2, we should stop it first before running sbt command

Testing
-------
Prior to raising a PR, please ensure that the code compiles and all tests pass by running `sbt fmt clean compile test it:test`

Endpoints
===================
The endpoint in this API is application-restricted.

All ports requiring access to this API must be pre-authorized by HMRC.

```GET  /:portId/arrivals/controlled```

Retrieve all GMRs arriving at a specified port which require an inspection.

Response Body Example
```
HTTP status: 200 (OK)

[ {
  "gmrId" : "GMRA00003I3M",
  "isUnaccompanied" : false,
  "vehicleRegNum" : "AB12 CDE",
  "actualCrossing" : {
    "routeId" : "19",
    "localDateTimeOfArrival" : "2021-08-11T10:58"
  },
  "reportToLocations": [
      {
        "inspectionTypeId": "1",
        "locationIds": [
          "L0029A"
        ]
      }
    ],
} ]
```

```GET /:portId/departures/controlled```

Retrieve all GMRs departing from a specified port which require an inspection.

##### Response codes

```
HTTP status: 200 (OK)

[ {
  "gmrId" : "GMRA00003I3M",
  "isUnaccompanied" : false,
  "vehicleRegNum" : "AB12 CDE",
  "actualCrossing" : {
    "routeId" : "103",
    "localDateTimeOfArrival" : "2021-08-11T10:58"
  },
  "reportToLocations": [
      {
        "inspectionTypeId": "1",
        "locationIds": [
          "L0029A"
        ]
      }
    ],
} ]
```

```GET /:portId/departures```

Retrieve all GMRs departing from a specified port, with ready to embark data where present.

##### Response

```
HTTP status: 200 (OK)

[ {
  "gmrId" : "GMRA00003I3M",
  "isUnaccompanied" : true,
  "updatedDateTime" : "2021-08-31T08:52:04.791Z",
  "inspectionRequired" : true,
  "reportToLocations": [
      {
        "inspectionTypeId": "1",
        "locationIds": [
          "L0029A"
        ]
      }
    ],
  "trailerRegistrationNums" : [ "AB12 CFR", "GB18 WRD" ],
  "containerReferenceNums" : [ "ABXY2", "GB456" ],
  "checkedInCrossing" : {
    "routeId" : "103",
    "localDateTimeOfArrival" : "2021-08-11T10:58"
  }
},
  {
  "gmrId" : "GMRA00003I4M",
  "isUnaccompanied" : true,
  "updatedDateTime" : "2021-08-31T08:52:04.791Z",
  "inspectionRequired" : false,
  "trailerRegistrationNums" : [ "AB12 CFR", "GB18 WRD" ],
  "containerReferenceNums" : [ "ABXY2", "GB456" ],
  "readyToEmbark": true,
  "checkedInCrossing" : {
    "routeId" : "103",
    "localDateTimeOfArrival" : "2021-08-11T10:58"
  }
} ]
```

```GET  /reference-data ```

Retrieves reference data on request.
Response Body Example
```
* **Code:** 200
* **Success**

{
  "routes": [
    {
      "routeId": "102",
      "routeEffectiveFrom": "2020-01-01",
      "routeEffectiveTo": "2020-01-02",
      "departurePortId": 7014,
      "routeDirection": "UK_OUTBOUND",
      "arrivalPortId": 6085,
      "carrierId": 1
    },
    {
      "routeId": "103",
      "routeEffectiveFrom": "2020-01-01",
      "departurePortId": 7054,
      "routeDirection": "UK_OUTBOUND",
      "arrivalPortId": 1401,
      "carrierId": 2
    }
  ],
  "ports": [
    {
      "portId": 7050,
      "portRegion": "Scotland",
      "portEffectiveFrom": "2000-01-01",
      "portDescription": "Aberdeen",
      "chiefPortCode": "",
      "cdsPortCode": "ABDABDABD",
      "officeOfTransitCustomsOfficeCode": "UNKNOWN?",
      "timezoneId": "Europe/London",
      "portCountryCode": "GB"
    },
    {
      "portId": 1401,
      "portRegion": "",
      "portEffectiveFrom": "2000-01-01",
      "portDescription": "Calais",
      "chiefPortCode": "",
      "cdsPortCode": "",
      "officeOfTransitCustomsOfficeCode": "UNKNOWN?",
      "timezoneId": "Europe/Paris",
      "portCountryCode": "FR"
    }
  ],
  "carriers": [
    {
      "carrierId": 1,
      "carrierName": "Stena Line",
      "countryCode": "SE"
    },
    {
      "carrierId": 2,
      "carrierName": "DFDS Seaways",
      "countryCode": "DK"
    }
  ],
  "ruleFailures": [
    {
      "ruleId": "013_6",
      "ruleDescription": "There is a problem with the Entry Summary (ENS) Declaration. Check the declaration details."
    },
    {
      "ruleId": "014_2",
      "ruleDescription": "The EORI cannot be found. Check and try again."
    }
  ]
}
```

License
=======

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").