/*
 *  Constants.h
 *  UbiSOA
 *
 *  Created by Edgardo on 16/12/09.
 *  Copyright 2009 CICESE. All rights reserved.
 *
 */

#define kMapFile		@"map-dcc-full.png"
#define kSpotterNotif	@"Notif-WiFiSpotter"
#define kDatabaseFile	@"Data.sqlite3"

typedef enum _UBGeolocationMode {
	UBTrainingGeolocationMode,
	UBTrackingGeolocationMode,
	UBLoggingGeolocationMode
} UBGeolocationMode;