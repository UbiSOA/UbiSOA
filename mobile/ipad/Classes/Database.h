//
//  Database.h
//  UbiSOA
//
//  Created by Edgardo on 19/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Constants.h"
#import "/usr/include/sqlite3.h"
#import "GeolocationMap.h"

@interface Database : NSObject {
	sqlite3 *database;
	NSMutableArray *data;
}

@property (nonatomic, retain) NSMutableArray *data;

+ (Database *)sharedInstance;
- (id)init;
- (void)dealloc;
- (int)countRowsOfTable:(NSString *)aTable;
- (void)removeRow:(int)row ofTable:(NSString *)aTable;
- (BOOL)addMap:(id)aMap;
- (BOOL)updateMap:(id)aMap;
- (void)loadMaps;

@end
