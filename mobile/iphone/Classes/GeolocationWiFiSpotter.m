//
//  GeolocationWiFiSpotter.m
//  UbiSOA
//
//  Created by Edgardo on 18/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import "GeolocationWiFiSpotter.h"


@implementation GeolocationWiFiSpotter
@synthesize networks;

- (void)open {
	libHandle = dlopen("/System/Library/SystemConfiguration/WiFiManager.bundle/WiFiManager", RTLD_LAZY);
	open  = dlsym(libHandle, "Apple80211Open");
	bind  = dlsym(libHandle, "Apple80211BindToInterface");
	close = dlsym(libHandle, "Apple80211Close");
	associate = dlsym(libHandle, "Apple80211Associate");
	scan  = dlsym(libHandle, "Apple80211Scan");
	
	#if !TARGET_IPHONE_SIMULATOR
	open(&libHandle);
	bind(libHandle, @"en0");
	#endif
}

- (void)close {
	#if !TARGET_IPHONE_SIMULATOR
	close(libHandle);
	dlclose(libHandle);
	#endif
}

- (void)scan {
	[NSThread detachNewThreadSelector:@selector(performScan) toTarget:self withObject:nil];
}

- (void)performScan {
#if !TARGET_IPHONE_SIMULATOR
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
	
	CFDictionaryRef parameters = CFDictionaryCreateMutable(NULL, 0, &kCFTypeDictionaryKeyCallBacks, &kCFTypeDictionaryValueCallBacks);
    scan(libHandle, &networks, parameters);
		
	[pool release];
#endif
	[[NSNotificationCenter defaultCenter] postNotificationName:kSpotterNotif object:self];
}

@end
