//
//  GeolocationWiFiSpotter.m
//  UbiSOA
//
//  Created by Edgardo on 18/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import "GeolocationWiFiSpotter.h"


@implementation GeolocationWiFiSpotter
@synthesize delegate;
static GeolocationWiFiSpotter *sharedInstance;


+ (GeolocationWiFiSpotter *)sharedInstance {
	if (!sharedInstance) sharedInstance = [[self alloc] init];
	return sharedInstance;
}

- (id)init {
	self = [super init];
	libHandle = dlopen("/System/Library/SystemConfiguration/WiFiManager.bundle/WiFiManager", RTLD_LAZY);
	open  = dlsym(libHandle, "Apple80211Open");
	bind  = dlsym(libHandle, "Apple80211BindToInterface");
	close = dlsym(libHandle, "Apple80211Close");
	assoc = dlsym(libHandle, "Apple80211Associate");
	scan  = dlsym(libHandle, "Apple80211Scan");
	
	#if !TARGET_IPHONE_SIMULATOR
	open(&libHandle);
	bind(libHandle, @"en0");
	#endif
	return self;
}

- (void)close {
#if !TARGET_IPHONE_SIMULATOR
	close(libHandle);
	dlclose(libHandle);
#endif
}

- (BOOL)scan {
	if (busy) return NO;
	[NSThread detachNewThreadSelector:@selector(performScan) toTarget:self withObject:nil];
	busy = YES;
	return YES;
}

- (void)performScan {
#if !TARGET_IPHONE_SIMULATOR
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
	CFDictionaryRef parameters = CFDictionaryCreateMutable(NULL, 0, &kCFTypeDictionaryKeyCallBacks, &kCFTypeDictionaryValueCallBacks);
    scan(libHandle, &networks, parameters);
	[pool release];
#else
	[NSThread sleepForTimeInterval:2.0];
#endif	
	if (self.delegate != nil) [self.delegate spotterDidScan:networks];
	else NSLog(@"SPOTTER TRIED TO UPDATE RELEASED OBJECT!");
	busy = NO;
}

- (void)dealloc {
	[self close];
	[delegate release];
	[super dealloc];
}

@end
