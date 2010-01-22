//
//  GeolocationMap.h
//  UbiSOA
//
//  Created by Edgardo on 20/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Database.h"


@interface GeolocationMap : NSObject {
	NSString *name, *file;
	UIImage *image;
	double neLat, neLng, seLat, seLng, swLat, swLng, nwLat, nwLng;
	int tag;
}

@property (nonatomic, retain) NSString *name;
@property (nonatomic, retain) NSString *file;
@property (nonatomic, retain) UIImage *image;
@property (nonatomic) double neLat, neLng, seLat, seLng, swLat, swLng, nwLat, nwLng;
@property (nonatomic) int tag;

@end
