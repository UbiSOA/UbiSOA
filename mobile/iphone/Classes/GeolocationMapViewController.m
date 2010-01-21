//
//  GeolocationMapViewController.m
//  UbiSOA
//
//  Created by Edgardo on 20/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "GeolocationMapViewController.h"


@implementation GeolocationMapViewController
@synthesize map, editing;

- (void)viewDidLoad {
    [super viewDidLoad];
	if (map == nil) {
		map = [[GeolocationMap alloc] init];
		map.name = @"New Map";
	}
	self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(done:)] autorelease];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 3;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	if (section == 2) return @"Geocoordinates of corners";
	return nil;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
	switch (section) {
		case 1: return @"For better resolution, download images with Safari and choose from the Camera Roll.";
		case 2: return @"To get the coordinates, use the same image\nto create a layer in Google Earth and then check the points at the corners.";
	}
	return nil;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
	switch (section) {
		case 1: return 52.0f;
		case 2: return 80.0f;
	}
	return 0;
}

// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 2) return 8;
	return 1;
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	NSString *identifier = (indexPath.section != 1)? @"EditableCell": @"SelectableCell";
	
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (cell == nil) cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:identifier] autorelease];
	
	if (indexPath.section != 1) {
		NSString *label, *text; int tag;
		switch (indexPath.row) {
			case 0:
				label = (indexPath.section == 0)? @"Name": @"NE Latitude";
				tag = (indexPath.section == 0)? 0: 1;
				text = (tag == 0)? (self.map.name == nil)? @"New Map": self.map.name: [NSString stringWithFormat:@"%.7F", self.map.neLat];
				break;
			case 1:
				label = @"NE Longitude"; tag = indexPath.row + 1;
				text = [NSString stringWithFormat:@"%.7F", self.map.neLng];
				break;
			case 2:
				label = @"SE Latitude"; tag = indexPath.row + 1;
				text = [NSString stringWithFormat:@"%.7F", self.map.seLat];
				break;
			case 3:
				label = @"SE Longitude"; tag = indexPath.row + 1;
				text = [NSString stringWithFormat:@"%.7F", self.map.seLng];
				break;
			case 4:
				label = @"SW Latitude"; tag = indexPath.row + 1;
				text = [NSString stringWithFormat:@"%.7F", self.map.swLat];
				break;
			case 5:
				label = @"SW Longitude"; tag = indexPath.row + 1;
				text = [NSString stringWithFormat:@"%.7F", self.map.swLng];
				break;
			case 6:
				label = @"NW Latitude"; tag = indexPath.row + 1;
				text = [NSString stringWithFormat:@"%.7F", self.map.nwLat];
				break;
			case 7:
				label = @"NW Longitude"; tag = indexPath.row + 1;
				text = [NSString stringWithFormat:@"%.7F", self.map.nwLng];
				break;
		}
		
		cell.textLabel.text = label;
		CGRect frame = CGRectMake(0, 0, (indexPath.section == 0)? 220.0f: 150.0f, 21.0f);
		UITextField *textField = [[[UITextField alloc] initWithFrame:frame] autorelease];
		[textField setTextColor:cell.detailTextLabel.textColor];
		[textField setTextAlignment:UITextAlignmentRight];
		[textField setReturnKeyType:UIReturnKeyDone];
		[textField setTag:tag];
		[textField setText:text];
		[textField setClearButtonMode:UITextFieldViewModeWhileEditing];
		[textField setAdjustsFontSizeToFitWidth:YES];
		if (indexPath.row == 0) [textField setAutocapitalizationType:UITextAutocapitalizationTypeWords];
		[textField addTarget:self action:@selector(textFieldDone:) forControlEvents:UIControlEventEditingDidEnd];
		[textField addTarget:self action:@selector(textFieldDoneOnExit:) forControlEvents:UIControlEventEditingDidEndOnExit];
		if (indexPath.section == 2) [textField setKeyboardType:UIKeyboardTypeNumbersAndPunctuation];
		else [textField setKeyboardType:UIKeyboardTypeAlphabet];
		cell.accessoryView = textField;
		cell.selectionStyle = UITableViewCellSelectionStyleNone;
	}
	
	if (indexPath.section == 1) {
		cell.textLabel.text = @"Choose Image";
		cell.detailTextLabel.text = (self.map.file == nil)? @"": [self.map.file lastPathComponent];
		cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
	}
	
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	if (indexPath.section == 1) {
		UIImagePickerController *picker = [[UIImagePickerController alloc] init];
		picker.navigationBar.barStyle = UIBarStyleBlackOpaque;
		picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;		
		picker.delegate = self;
		picker.allowsEditing = NO;
		[self presentModalViewController:picker animated:YES];
		[picker release];
	}
}

- (void)dealloc {
	[map release];
    [super dealloc];
}

- (IBAction)textFieldDone:(id)sender {
	UITextField *textField = (UITextField *)sender;
	switch (textField.tag) {
		case 0: self.map.name = textField.text; break;
		case 1: self.map.neLat = [textField.text doubleValue]; break;
		case 2: self.map.neLng = [textField.text doubleValue]; break;
		case 3: self.map.seLat = [textField.text doubleValue]; break;
		case 4: self.map.seLng = [textField.text doubleValue]; break;
		case 5: self.map.swLat = [textField.text doubleValue]; break;
		case 6: self.map.swLng = [textField.text doubleValue]; break;
		case 7: self.map.nwLat = [textField.text doubleValue]; break;
		case 8: self.map.nwLng = [textField.text doubleValue]; break;
	}
	[sender resignFirstResponder];
}

- (IBAction)textFieldDoneOnExit:(id)sender {
	[self textFieldDone:sender];
	[self.tableView reloadData];
}

#pragma mark -
#pragma mark UIImagePickerController delegate methods

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
	self.map.image = [info objectForKey:@"UIImagePickerControllerOriginalImage"];
	self.map.file = editing? [NSString stringWithFormat:@"%@/Documents/%@", NSHomeDirectory(), self.map.file]: [self findUniqueSavePath];
	[self.navigationController dismissModalViewControllerAnimated:YES];
	[self.tableView reloadData];
}

- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
	[[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleDefault];
	navigationController.navigationBar.barStyle = UIBarStyleDefault;
}

#pragma mark -
#pragma mark Images management methods

- (NSString *)findUniqueSavePath {
	int i = 1;
	NSString *path;
	do {
		path = [NSString stringWithFormat:@"%@/Documents/IMG_%03d.PNG", NSHomeDirectory(), i++];
	} while ([[NSFileManager defaultManager] fileExistsAtPath:path]);
	return path;
}

- (void)done:(id)sender {
	[self.tableView reloadData];
	if (self.map.name == nil || [self.map.name compare:@""] == 0) {
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:@"Missing Information" message:@"The map must have a name." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] autorelease];
		[alert show];
	} else if (!editing && self.map.image == nil) {
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:@"Missing Information" message:@"You must choose an image." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] autorelease];
		[alert show];
	} else if (self.map.neLat == 0.0 || self.map.neLng == 0.0 ||
		self.map.seLat == 0.0 || self.map.seLng == 0.0 ||
		self.map.swLat == 0.0 || self.map.swLng == 0.0 ||
		self.map.nwLat == 0.0 || self.map.nwLng == 0.0) {
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:@"Missing Information" message:@"The format for some of the coordinates is not valid." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] autorelease];
		[alert show];
	} else {
		BOOL stored = NO;
		if (editing) stored = [[Database sharedInstance] updateMap:self.map];
		else stored = [[Database sharedInstance] addMap:self.map];
		if (stored) {
			if (!editing) [UIImagePNGRepresentation(self.map.image) writeToFile:self.map.file atomically:YES];
			[[Database sharedInstance] loadMaps];
			[self.navigationController popViewControllerAnimated:YES];
		} else {
			UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:@"Storage Error" message:@"Cannot create new map." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] autorelease];
			[alert show];
		}
	}
}

@end

