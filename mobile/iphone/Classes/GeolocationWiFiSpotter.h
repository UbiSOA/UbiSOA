//
//  GeolocationWiFiSpotter.h
//  UbiSOA
//
//  Created by Edgardo on 18/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Constants.h"
#import <dlfcn.h>

@protocol GeolocationWiFiSpotterDelegate <NSObject>
- (void)spotterDidScan;
@end

@interface GeolocationWiFiSpotter : NSObject {
	void *libHandle;
	int (*open)(void *);
	int (*bind)(void *, NSString *);
	int (*close)(void *);
	int (*assoc)(void *, NSDictionary*, NSString*);
	int (*scan)(void *, NSArray **, void *);
    CFArrayRef networks;
	id<GeolocationWiFiSpotterDelegate> delegate;
}

@property (nonatomic, assign) id<GeolocationWiFiSpotterDelegate>delegate;

+ (GeolocationWiFiSpotter *)sharedInstance;
- (void)scan;
- (NSString *)signalData;
- (int)signalDataAPCount;

@end
