//
//  GeolocationMapsViewController.h
//  UbiSOA
//
//  Created by Edgardo on 19/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Database.h"
#import "GeolocationMapViewController.h"
#import "GeolocationViewController.h"


@interface GeolocationMapsViewController : UITableViewController {
	UIView *hiddenHeader;
	NSNetService *service;
}

@property (nonatomic, retain) NSNetService *service;

- (void)chooseMap:(int)mapIndex animated:(BOOL)animate;

@end
