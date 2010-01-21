//
//  GeolocationMap.m
//  UbiSOA
//
//  Created by Edgardo on 20/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "GeolocationMap.h"


@implementation GeolocationMap
@synthesize name, file, image, neLat, neLng, seLat, seLng, swLat, swLng, nwLat, nwLng, tag;

- (void)dealloc {
	[name release];
	[file release];
	[image release];
	[super dealloc];
}

- (NSString *)description {
	return [[NSString alloc] initWithFormat:@"Map:%@ Image:%@ Tag:%d (%.7f/%.7f, %.7f/%.7f, %.7f/%.7f, %.7f/%.7f)", self.name, [self.file lastPathComponent], self.tag, self.neLat, self.neLng, self.seLat, self.seLng, self.swLat, self.swLng, self.nwLat,self.nwLng];
}

@end
