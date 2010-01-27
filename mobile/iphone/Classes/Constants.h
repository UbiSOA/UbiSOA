/*
 *  Constants.h
 *  UbiSOA
 *
 *  Created by Edgardo on 16/12/09.
 *  Copyright 2009 CICESE. All rights reserved.
 *
 */

#define kDatabaseFile @"Data.sqlite3"
#define kShadowColor [UIColor colorWithRed:0.13 green:0.25 blue:0.66 alpha:1.0]
#define kSimulatedSignalData @"0:9:5b:51:6b:76=-66,0:7:e:7d:91:90=-49,0:11:24:21:15:a2=-48"
#define kSimulatedWiFiSpotterDelay 1.2

typedef enum _UBGeolocationMode {
	UBTrainingGeolocationMode,
	UBTrackingGeolocationMode,
	UBTestingGeolocationMode
} UBGeolocationMode;

typedef enum _UBGeolocationActionType {
	UBTrainingGeolocationActionType = 1,
	UBTrackingGeolocationActionType,
	UBTestingGeolocationActionType,
	UBLocateGeolocationActionType
} UBGeolocationActionType;
