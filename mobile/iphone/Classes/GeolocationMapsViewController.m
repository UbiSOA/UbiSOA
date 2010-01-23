//
//  GeolocationMapsViewController.m
//  UbiSOA
//
//  Created by Edgardo on 19/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "GeolocationMapsViewController.h"


@implementation GeolocationMapsViewController
@synthesize service;


- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)addMap:(id)sender {
	GeolocationMapViewController *map = [[GeolocationMapViewController alloc] initWithNibName:@"GeolocationMapViewController" bundle:[NSBundle mainBundle]];
	[map setTitle:@"New Map"];
	[map setHidesBottomBarWhenPushed:YES];
	[[self navigationController] pushViewController:map animated:YES];
	[map release];
}

- (void)chooseMap:(int)mapIndex animated:(BOOL)animate {
	GeolocationViewController *controller = [[GeolocationViewController alloc] initWithNibName:@"GeolocationViewController" bundle:[NSBundle mainBundle]];
	NSLog(@"ANTES DE: %d", [controller retainCount]);
	controller.service = self.service;
	controller.map = [[[Database sharedInstance] data] objectAtIndex:mapIndex];
	controller.title = controller.map.name;
	controller.hidesBottomBarWhenPushed = YES;
	[self.navigationController pushViewController:controller animated:animate];
	[controller release];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
	[[Database sharedInstance] loadMaps];
	[self.tableView reloadData];
	
	if ([[[Database sharedInstance] data] count] > 0) {
		if (self.tableView.tableHeaderView != nil) {
			hiddenHeader = self.tableView.tableHeaderView;
			[hiddenHeader retain];
		}
		self.tableView.tableHeaderView = nil;
		self.tableView.scrollEnabled = YES;
		self.navigationItem.rightBarButtonItem = self.editButtonItem;
	} else self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addMap:)];
}

- (void)didReceiveMemoryWarning {
	[super didReceiveMemoryWarning];
}

#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [[[Database sharedInstance] data] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {    
    static NSString *CellIdentifier = @"Cell";

    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    
	GeolocationMap *map = [[[Database sharedInstance] data] objectAtIndex:indexPath.row];
    cell.textLabel.text = map.name;
	cell.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;
	
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	[self chooseMap:indexPath.row animated:YES];
}

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath {
	GeolocationMapViewController *controller = [[GeolocationMapViewController alloc] initWithNibName:@"GeolocationMapViewController" bundle:[NSBundle mainBundle]];
	GeolocationMap *map = [[[Database sharedInstance] data] objectAtIndex:indexPath.row];
	[controller setEditing:YES];
	[controller setMap:map];
	[controller setTitle:map.name];
	[controller setHidesBottomBarWhenPushed:YES];
	[self.navigationController pushViewController:controller animated:YES];
	[controller release];
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {    
    if (editingStyle == UITableViewCellEditingStyleDelete) {
		GeolocationMap *map = [[[Database sharedInstance] data] objectAtIndex:indexPath.row];
		NSString *path = [NSString stringWithFormat:@"%@/Documents/%@", NSHomeDirectory(), map.file];
		[[NSFileManager defaultManager] removeItemAtPath:path error:nil];	

		[[Database sharedInstance] removeRow:map.tag ofTable:@"maps"];
		[[Database sharedInstance] loadMaps];
		
        [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:YES];

		if ([[[Database sharedInstance] data] count] == 0) {
			[UIView beginAnimations:@"ShowHiddenHeader" context:nil];
			[UIView setAnimationTransition:UIViewAnimationTransitionCurlDown forView:self.view cache:YES];
			[UIView setAnimationDuration:0.5];
			self.tableView.tableHeaderView = hiddenHeader;
			[self.tableView reloadData];
			self.navigationItem.leftBarButtonItem = nil;
			self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addMap:)];
			self.tableView.scrollEnabled = NO;
			[UIView commitAnimations];
		}
    }
}

- (void)setEditing:(BOOL)editing animated:(BOOL)animate {
	[super setEditing:editing animated:animate];
	if (editing == 1) {
		self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addMap:)];
	} else self.navigationItem.leftBarButtonItem = nil;
}

- (void)dealloc {
	[hiddenHeader release];
	[service release];
    [super dealloc];
}

@end

