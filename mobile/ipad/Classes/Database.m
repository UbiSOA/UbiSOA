//
//  Database.m
//  UbiSOA
//
//  Created by Edgardo on 19/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "Database.h"


@implementation Database
@synthesize data;

static Database *sharedInstance;

#pragma mark -
#pragma mark Core methods

+ (Database *)sharedInstance {
	if (!sharedInstance) sharedInstance = [[self alloc] init];
	return sharedInstance;
}

- (id)init {
	self = [super init];
    if (self) {
		NSString *path = [NSString stringWithFormat:@"%@/Documents/%@", NSHomeDirectory(), kDatabaseFile];
		if (sqlite3_open([path UTF8String], &database) != SQLITE_OK) {
			sqlite3_close(database);
			NSAssert(0, @"Failed to open database.");
		}
		
		char *errorMsg;
		NSString *createSQL = @"CREATE TABLE IF NOT EXISTS maps (id INTEGER PRIMARY KEY, name TEXT, file TEXT, neLat REAL, neLng REAL, seLat REAL, seLng REAL, swLat REAL, swLng REAL, nwLat REAL, nwLng REAL)";
		if (sqlite3_exec(database, [createSQL UTF8String], NULL, NULL, &errorMsg) != SQLITE_OK) {
			sqlite3_close(database);
			NSAssert1(0, @"Error creating table: %s", errorMsg);
		}
    }
    return self;
}

- (void)dealloc {
	sqlite3_close(database);
	[data release];
	[super dealloc];
}

#pragma mark -
#pragma mark Common methods

- (int)countRowsOfTable:(NSString *)aTable {
	NSString *query = [NSString stringWithFormat:@"SELECT COUNT(*) AS n FROM %@", aTable];
	sqlite3_stmt *st; int count = -1;
	if (sqlite3_prepare_v2(database, [query UTF8String], -1, &st, nil) == SQLITE_OK) {
		if (sqlite3_step(st) == SQLITE_ROW) count = sqlite3_column_int(st, 0);
		sqlite3_finalize(st);
	}
	return count;
}

- (void)removeRow:(int)row ofTable:(NSString *)aTable {
	NSString *query = [NSString stringWithFormat:@"DELETE FROM %@ WHERE id='%d'", aTable, row];
	sqlite3_stmt *st;
	if (sqlite3_prepare_v2(database, [query UTF8String], -1, &st, nil) != SQLITE_OK)
		NSLog(@"DB ERROR: %s QUERY: %@", sqlite3_errmsg(database), query);
	if (sqlite3_step(st) != SQLITE_DONE)
		NSLog(@"DB ERROR: %s QUERY: %@", sqlite3_errmsg(database), query);
	else sqlite3_finalize(st);
}

#pragma mark -
#pragma mark Maps methods

- (BOOL)addMap:(id)aMap {
	GeolocationMap *map = (GeolocationMap *)aMap;
	char *query = "INSERT INTO maps (name, file, neLat, neLng, seLat, seLng, swLat, swLng, nwLat, nwLng) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	sqlite3_stmt *stmt;
	if (sqlite3_prepare_v2(database, query, -1, &stmt, nil) == SQLITE_OK) {
		sqlite3_bind_text(stmt, 1, [map.name UTF8String], -1, NULL);
		sqlite3_bind_text(stmt, 2, [[map.file lastPathComponent] UTF8String], -1, NULL);
		sqlite3_bind_double(stmt, 3, map.neLat);
		sqlite3_bind_double(stmt, 4, map.neLng);
		sqlite3_bind_double(stmt, 5, map.seLat);
		sqlite3_bind_double(stmt, 6, map.seLng);
		sqlite3_bind_double(stmt, 7, map.swLat);
		sqlite3_bind_double(stmt, 8, map.swLng);
		sqlite3_bind_double(stmt, 9, map.nwLat);
		sqlite3_bind_double(stmt, 10, map.nwLng);
	}
	if (sqlite3_step(stmt) != SQLITE_DONE) {
		NSLog(@"DB ERROR: %s QUERY: %s", sqlite3_errmsg(database), query);
		sqlite3_close(database);
		return NO;
	}
	sqlite3_finalize(stmt);
	return YES;
}

- (BOOL)updateMap:(id)aMap {
	GeolocationMap *map = (GeolocationMap *)aMap;
	NSString *query = [NSString stringWithFormat:@"UPDATE maps SET name='%@', file='%@', neLat=%.7f, neLng=%.7f, seLat=%.7f, seLng=%.7f, swLat=%.7f, swLng=%.7f, nwLat=%.7f, nwLng=%.7f WHERE id=%d", map.name, [map.file lastPathComponent], map.neLat, map.neLng, map.seLat, map.seLng, map.swLat, map.swLng, map.nwLat, map.nwLng, map.tag];
	sqlite3_stmt *st;
	if (sqlite3_prepare_v2(database, [query UTF8String], -1, &st, nil) != SQLITE_OK)
		NSLog(@"DB:%s QUERY:%@", sqlite3_errmsg(database), query);
	if (sqlite3_step(st) != SQLITE_DONE) {
		NSLog(@"DB:%s QUERY:%@", sqlite3_errmsg(database), query);
		sqlite3_close(database);
	}
	sqlite3_finalize(st);
	return YES;
}

- (void)loadMaps {
	if (self.data != nil) [self.data release];
	self.data = [[NSMutableArray alloc] init];
	
	NSString *query = @"SELECT * FROM maps ORDER BY name";
	sqlite3_stmt *st;
	if (sqlite3_prepare_v2(database, [query UTF8String], -1, &st, nil) == SQLITE_OK) {
		while (sqlite3_step(st) == SQLITE_ROW) {
			GeolocationMap *map = [[GeolocationMap alloc] init];
			map.tag = sqlite3_column_int(st, 0);
			map.name = [NSString stringWithFormat:@"%s", sqlite3_column_text(st, 1)];
			map.file = [NSString stringWithFormat:@"%s", sqlite3_column_text(st, 2)];
			map.neLat = sqlite3_column_double(st, 3);
			map.neLng = sqlite3_column_double(st, 4);
			map.seLat = sqlite3_column_double(st, 5);
			map.seLng = sqlite3_column_double(st, 6);
			map.swLat = sqlite3_column_double(st, 7);
			map.swLng = sqlite3_column_double(st, 8);
			map.nwLat = sqlite3_column_double(st, 9);
			map.nwLng = sqlite3_column_double(st, 10);
			[data addObject:map];
			[map release];
		}
		sqlite3_finalize(st);
	} else NSLog(@"DB ERROR: %s QUERY: %s", sqlite3_errmsg(database), query);
}

@end
