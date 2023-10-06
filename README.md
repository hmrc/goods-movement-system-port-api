# goods-movement-system-port-api
This API provides resources from the Goods Vehicle Movement Service (GVMS) for ports and border locations.

GVMS is used by hauliers to declare all goods movements crossing certain UK roll-on-roll-off ports.

This API is used by ports and border locations to:

* Get a list of controlled GMR details that have embarked to or arrived at a GVMS port or border location
* Get a list of controlled GMR details that are checked-in for departure at a GVMS port or border location
* Get a list of checked-in GMR details at a GVMS port or border location
* Get GVMS reference data

## Running the service locally
By default, the service runs on 8998 port.

#### Using service manager 2

The following sm2 command starts the service but its also part of `GMS_ALL` profile

`sm2 --start GOODS_MOVEMENT_SYSTEM_PORT_API`

the logs can be viewed by running `sm2 --logs GOODS_MOVEMENT_SYSTEM_PORT_API` in a different terminal window

#### Using sbt
For local development use `sbt run` but if its already running in sm2, we should stop it first before running sbt command

The endpoint in this API is application-restricted.

All ports requiring access to this API must be pre-authorized by HMRC.

service logs can be viewed by running `sm --logs GOODS_MOVEMENT_SYSTEM_PORT_API`

```GET  /:portId/arrivals/controlled```

Port can pull all inspection required GMR's that are arriving at their port.

Response Body Example
```
HTTP status: 200 (OK)

[ {
  "gmrId" : "GMRA00003I3M",
  "isUnaccompanied" : false,
  "vehicleRegNum" : "AB12 CDE",
  "actualCrossing" : {
    "routeId" : "15",
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

Port can pull all inspection required GMR's that are departing from their port.

##### Response codes

```
HTTP status: 200 (OK)

[ {
  "gmrId" : "GMRA00003I3M",
  "isUnaccompanied" : false,
  "vehicleRegNum" : "AB12 CDE",
  "actualCrossing" : {
    "routeId" : "15",
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

Port can pull all GMR's that are departing from their port and view the ready to embark flag. 

##### Response

```
HTTP status: 200 (OK)

[ {
  "gmrId" : "GMRD00003I3M",
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
    "routeId" : "9",
    "localDateTimeOfArrival" : "2021-08-11T10:58"
  }
},
  {
  "gmrId" : "GMRD00003I4M",
  "isUnaccompanied" : true,
  "updatedDateTime" : "2021-08-31T08:52:04.791Z",
  "inspectionRequired" : false,
  "trailerRegistrationNums" : [ "AB12 CFR", "GB18 WRD" ],
  "containerReferenceNums" : [ "ABXY2", "GB456" ],
  "readyToEmbark": true,
  "checkedInCrossing" : {
    "routeId" : "9",
    "localDateTimeOfArrival" : "2021-08-11T10:58"
  }
} ]
```

```GET  /reference-data ``` <br/>
The Carrier can get a reference data at anytime.
Response Body Example
```
* **Code:** 200 <br />
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