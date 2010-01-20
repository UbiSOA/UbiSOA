//
//  Database.m
//  UbiSOA
//
//  Created by Edgardo on 19/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "Database.h"


@implementation Database
static Database *sharedInstance = nil;

#pragma mark -
#pragma mark Core methods

+ (Database *)sharedInstance {
	if (!sharedInstance) sharedInstance = [[self alloc] init];
	return sharedInstance;
}

- (id)init {
	self = [super init];
    if (self) {
		NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
		NSString *documentsDirectory = [[paths objectAtIndex:0] stringByAppendingPathComponent:kDatabaseFile];
		if (sqlite3_open([documentsDirectory UTF8String], &database) != SQLITE_OK) {
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
	[super dealloc];
}

#pragma mark -
#pragma mark Query methods

- (int)countRows:(NSString *)ofTable {
	NSString *query = [NSString stringWithFormat:@"SELECT COUNT(*) AS n FROM %@", ofTable];
	sqlite3_stmt *statement; int count = -1;
	if (sqlite3_prepare_v2(database, [query UTF8String], -1, &statement, nil) == SQLITE_OK) {
		if (sqlite3_step(statement) == SQLITE_ROW) count = sqlite3_column_int(statement, 0);
		sqlite3_finalize(statement);
	}
	return count;
}

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
		sqlite3_close(database);
		return NO;
	}
	sqlite3_finalize(stmt);
	return YES;
}

@end
