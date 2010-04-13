//
//  GeolocationMapViewController.h
//  UbiSOA
//
//  Created by Edgardo on 20/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Constants.h"
#import "GeolocationMap.h"
#import "GeolocationMapsViewController.h"


@interface GeolocationMapViewController : UITableViewController <UITableViewDataSource, UITableViewDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate> {
	GeolocationMap *map;
	BOOL editing;
}

@property (nonatomic, retain) GeolocationMap *map;
@property (nonatomic) BOOL editing;

- (NSString *)findUniqueSavePath;
- (void)done:(id)sender;

@end
