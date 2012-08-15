var dragging = null;
var connections = new Array();
var hasDrawed = false;
var serviceCounter = 0;
var services = new Array();
var recipeEditor = null;
var recipes = new Array();
var apps = new Array();

function resizePanels() {
	hideDropDowns();
	$('#apps .content').css('height', $('#apps').height() - 22);
	$('#apps .content ul').css('width', $('#apps .content').width());
	$('#services .content').css('height', $('#services').height() - 22);
	$('#services .content ul').css('width', $('#services .content').width() - 20);
	$('#recipes .content').css('height', $('#recipes').height() - 22);
	$('#recipes .content ul').css('width', $('#recipes .content').width());
	$('#connections').attr('width', $('#workspace').width());
	$('#connections').attr('height', $('#workspace').height());
	drawConnections();
}

function hidePanels() {
	$('#panels').animate(
		{ left: '-' + $('#panels').width() + 'px' }, 'easeOutQuad',
		function() {
			$('.toggleLink').data('state', 'closed').toggleClass('collapsed');
		}
	);
	$('#workspace').animate(
		{ left: '0' }, 'easeOutQuad'
	);
}

function showPanels() {
	$('#panels').animate(
		{ left: '0' }, 'easeInQuad',
		function() {
			$('.toggleLink').data('state', 'open').toggleClass('collapsed');
		}
	);
	$('#workspace').animate(
		{ left: $('#panels').width() + 'px' }, 'easeInQuad'
	);
}

function implementsFromName(name) {
	return 'http://interfaces.ubisoa.net/v1/' + name + '.wadl';
}

function hostFromName(name) {
	return '127.0.0.1';
}

function portFromName(name) {
	if (name == 'hub') return 8310;
	else if (name == 'app-engine') return 8320;
	else if (name == 'directory') return 8330;
	else if (name == 'sensing') return 8340;
	else if (name == 'rfid') return 8350;
	else if (name == 'leds') return 8360;
	else if (name == 'twitter') return 8370;
	else if (name == 'servo') return 8380;
}

function nameToLabel(name) {
	if (name == 'hub') return 'Hub';
	else if (name == 'app-engine') return 'Applications Engine';
	else if (name == 'directory') return 'Directory';
	else if (name == 'sensing') return 'Sensing';
	else if (name == 'rfid') return 'RFID';
	else if (name == 'leds') return 'LEDs';
	else if (name == 'twitter') return 'Twitter';
	else if (name == 'servo') return 'Servo';
	return name;
}

function makeWrapper(service, top, left) {
	var id = 's' + serviceCounter++;
	var style = 'position: absolute; ' +
		'left: ' + left + 'px; ' +
		'top: ' + top + 'px;';
	var html = '<div id="' + id + '" class="service-wrapper" style="' + style + '">' +
		'<div class="service ' + service + '"></div>' +
		'<div class="input"></div>' +
		'<div class="output"></div>' +
		'</div>';
	
	services[services.length] = {
		id: id,
		name: service,
		host: hostFromName(service),
		port: portFromName(service),
		implements: implementsFromName(service),
		recipe: null
	};
		
	return html;
}

function makeVisualWrapper(service, top, left) {
	service.name = service.name.toLowerCase();
	var style = 'position: absolute; ' +
		'left: ' + left + 'px; ' +
		'top: ' + top + 'px;';
	var html = '<div id="' + service.id + '" class="service-wrapper" style="' + style + '">' +
		'<div class="service ' + service.name + '"></div>' +
		'<div class="input"></div>' +
		'<div class="output"></div>' +
		'</div>';
	return html;
}

function drawTempConnection(event, ui) {
	drawConnections(event, ui);
	var context = document.getElementById('connections').getContext('2d');

	context.shadowOffsetX = 0;
	context.shadowOffsetY = 1;
	context.shadowBlur = 4;
	context.shadowColor = "rgba(0, 0, 0, 0.5)";
	context.strokeStyle = '#7fb064';
	context.lineWidth = 3;
	
	context.beginPath();
	context.moveTo(ui.originalPosition.left + 5, ui.originalPosition.top + 6);
	context.lineTo(ui.position.left + 6, ui.position.top + 6);
	context.stroke();
	hasDrawed = true;
}

function drawConnections(event, ui) {
	if ($('#workspace .dropdown').css('display') != 'none') hideDropDowns();

	var canvas = document.getElementById('connections');
	var context = canvas.getContext('2d');
	
	if (hasDrawed) {
		context.clearRect(0, 0, canvas.width, canvas.height);
		hasDrawed = false;
	}
	
	context.shadowOffsetX = 0;
	context.shadowOffsetY = 1;
	context.shadowBlur = 4;
	context.shadowColor = "rgba(0, 0, 0, 0.5)";
	context.strokeStyle = '#777';
	context.lineWidth = 3;
	
	for (var i in connections) {
		connection = connections[i];
		if (connection == undefined) continue;
		
		if (connection.valid == null) {
			var sourceService = getService(connection.from);
			var targetService = getService(connection.to);
			var recipe = getRecipe(targetService.recipe);
			if (recipe != null) {
				var isValid = false;
				for (var j in recipe.inboundTypes)
					if (recipe.inboundTypes[j] == sourceService.implements)
						isValid = true;
				connection.valid = isValid;
			}
		}
		
		if (connection.valid) context.strokeStyle = '#7fb064';
		else context.strokeStyle = '#777';
		
		var fromX = $('#' + connection.from).position().left + 50;
		var fromY = $('#' + connection.from).position().top + 19;
		var toX = $('#' + connection.to).position().left - 10;
		var toY = $('#' + connection.to).position().top + 19;
		
		context.beginPath();
		context.moveTo(fromX + 8, fromY + 9);
		context.lineTo(toX + 8, toY + 9);
		context.stroke();
		hasDrawed = true;
	}
}

function createConnectorHelper(event) {
	return $('<div class="connector"></div>');
}

function startDraggingConnector(event, ui) {
	dragging = $(this).parent('.service-wrapper').attr('id');
}

function stopDraggingConnector(event, ui) {
	dragging = null;
	validateServices();
	drawConnections();
}

function createConnection(event, ui) {
	var dropped = $(this).parent('.service-wrapper').attr('id');
	if (dragging == dropped) return;
	var newConnection = { from: dragging, to: dropped };
	var valid = true;
	for (var i in connections) {
		var connection = connections[i];
		if (connection != undefined) {
			connection.valid = null;
			if (connection.from == newConnection.from &&
				connection.to == newConnection.to)
				valid = false;
		}
	}
	if (!valid) return;
	
	connections[connections.length] = {
		from: dragging,
		to: dropped,
		valid: null
	};
}

function serviceContextMenu(e) {
	if ($('#workspace .dropdown').css('display') != 'block')
	hideDropDowns();
	var left = $(e.currentTarget).position().left + 3;
	var top = $(e.currentTarget).position().top + 55;
	$('#workspace .dropdown').slideDown(250).css('left', left).css('top', top);
	$('#workspace .dropdown').data('target', $(e.currentTarget).attr('id'));
    return false;
}

function removeconnectionsForTarget(target) {
	for (var i in connections) {
		var connection = connections[i];
		if (connection == undefined) continue;
		connection.valid = null;
		if (connection.from == target || connection.to == target)
			connections[i] = undefined;
	}
}

function getConnectionCountForTarget(target) {
	var count = 0;
	for (var i in connections) {
		var connection = connections[i];
		if (connection == undefined) continue;
		if (connection.to == target) count++;
	}
	return count;
}

function revalidateConnections() {
	for (var i in connections) {
		var connection = connections[i];
		if (connection != undefined)
			connection.valid = null;
	}
}

function getService(id) {
	for (var i in services) {
		var service = services[i];
		if (service == undefined) continue;
		if (service.id == id) return service;
	}
	return false;
}

function getRecipe(id) {
	for (var i in recipes) {
		var recipe = recipes[i];
		if (recipe == undefined) continue;
		if (recipe.id == id) return recipe;
	}
	return false;
}

function getApp(id) {
	for (var i in apps) {
		var app = apps[i];
		if (app == undefined) continue;
		if (app.id == id) return app;
	}
	return false;
}

function workspaceDrop(event, ui) {
	var service = ui.helper.attr('class').split(' ')[1];
	var top = ui.position.top - $('#workspace').position().top - 3;
	var left = ui.position.left - $('#workspace').position().left + 5;
	var html = makeWrapper(service, top, left);
	$('#workspace').append(html);
	$('#workspace .service-wrapper').draggable({
		containment: '#workspace',
		scroll: false,
		stack: '#workspace .service-wrapper',
		drag: drawConnections,
		stop: drawConnections
	});
	$('#workspace .service-wrapper .output').draggable({
		cursor: '-webkit-grabbing',
		helper: createConnectorHelper,
		containment: '#workspace',
		scroll: false,
		appendTo: '#workspace',
		start: startDraggingConnector,
		stop: stopDraggingConnector,
		drag: drawTempConnection
	});
	$('#workspace .service-wrapper .input').droppable({
		accept: '#workspace .service-wrapper .output',
		drop: createConnection
	});
	$('#workspace .service-wrapper').bind('contextmenu', serviceContextMenu);
	validateServices();
}

function workspaceDropDownProperties(event) {
	hideDropDowns();
	event.preventDefault();
	var target = $('#workspace .dropdown').data('target');
	var service = getService(target);
	
	$('#service-recipe').html('<option value="">None</option>');
	for (var i in recipes) {
		var recipe = recipes[i];
		if (recipe == undefined) continue;
		if (recipe.targetType != service.implements) continue;
		var selected = recipe.id == service.recipe ? ' selected="selected"': '';
		var option = '<option value="' + recipe.id + '"' + selected + '>' + recipe.name + '</option>';
		$('#service-recipe').append(option);
	}
	
	$('#service-id').val(service.id);
	$('#service-name').val(nameToLabel(service.name));
	$('#service-host').val(service.host);
	$('#service-port').val(service.port);
	$('#service-implements').val(service.implements);
	
	$('#services-modal').reveal();
	return false;
}

function workspaceDropDownRecipe(event) {
	hideDropDowns();
	event.preventDefault();
	var target = $('#workspace .dropdown').data('target');
	var service = getService(target);
	
	var inputs = '';
	for (var i in connections) {
		var connection = connections[i];
		if (connection == undefined) continue;
		if (connection.to == target) {
			var from = getService(connection.from);
			inputs += from.implements + '\n';
		}
	}
	if (inputs.length > 0)
		inputs = inputs.substr(0, inputs.length - 1);
	
	$('#recipes-modal h2').html('Create Recipe');
	$('#recipe-service').val(target);
	$('#recipe-name').val('');
	$('#recipe-target-type').val(service.implements);
	$('#recipe-inbound-types').val(inputs);
	recipeEditor.getSession().setValue('');
	$('#recipes-modal').reveal();
	$('#recipe-name').focus();
	return false;
}

function workspaceDropDownClear(event) {
	hideDropDowns();
	var target = $('#workspace .dropdown').data('target');
	getService(target).recipe = null;
	removeconnectionsForTarget(target);
	validateServices();
	drawConnections();
	return false;
}

function workspaceDropDownRemove(event) {
	hideDropDowns();
	var target = $('#workspace .dropdown').data('target');
	removeconnectionsForTarget(target);
	validateServices();
	$('#' + target).remove();
	drawConnections();
	for (var i in services)
		if (services[i] != undefined && services[i].id == target)
			services[i] = undefined;
	return false;
}

function storeRecipe() {
	var name = $('#recipe-name').val();
	if (name == '') {
		alert('Please specify a name for the recipe.');
		$('#recipe-name').focus();
		return false;
	}
	
	var targetType = $('#recipe-target-type').val();
	var inboundTypes = $('#recipe-inbound-types').val().split('\n');
	if (inboundTypes == '') inboundTypes = new Array();
	
	var sourceCode = recipeEditor.getSession().getValue();
	if (sourceCode == '') {
		alert('Please specify the source code for the recipe.');
		return false;
	}
	
	var title = $('#recipes-modal h2').html();
	
	if (title == 'Create Recipe') {
		var id = 'r' + recipes.length;
		var target = $('#workspace .dropdown').data('target');
		var service = getService(target);
		service.recipe = id;
		recipes[recipes.length] = recipe;
	}
	else var id = $('#recipe-id').val();

	var recipe = {
		id: id,
		name: name,
		targetType: targetType,
		inboundTypes: inboundTypes,
		sourceCode: encodeURI(sourceCode),
		locked: false
	};

	var json = $.JSON.encode(recipe);
	if (title == 'Create Recipe') $.post('/?category=recipes', json);
	else $.put('/?category=recipes', json);

	$('#recipes-modal').trigger('reveal:close');
	setTimeout(reloadRecipes, 250);
	return false;
}

function storeService() {
	var name = $('#service-name').val();
	if (name == '') {
		alert('Please specify a name for the service.');
		$('#service-name').focus();
		return false;
	}
	
	var host = $('#service-host').val();
	var port = $('#service-port').val();
	var implements = $('#service-implements').val();
	var recipe = $('#service-recipe').val();
	var id = $('#service-id').val();

	var service = getService(id);
	service.name = name;
	service.host = host;
	service.port = port;
	service.implements = implements;
	service.recipe = recipe != '' ? recipe : null;
	
	$('#services-modal').trigger('reveal:close');
	setTimeout(reloadRecipes, 250);
	return false;
}

function validateServices() {
	for (var i in services) {
		var service = services[i];
		if (service == undefined) continue;
		$('#' + service.id).removeClass('valid');
		
		if (service.recipe != null) {
			var recipe = getRecipe(service.recipe);
			if (recipe == false) {
				service.recipe = null;
				continue;
			}
			
			if (recipe.inboundTypes.length !=
				getConnectionCountForTarget(service.id)) {
				revalidateConnections();
				service.recipe = null;
				continue;
			}
		
			$('#' + service.id).addClass('valid');
			continue;
		}
		
		var targetType = service.implements;
		var inboundTypes = new Array();
		for (var j in connections) {
			var connection = connections[j];
			if (connection == undefined) continue;
			if (connection.to == service.id) {
				var from = getService(connection.from);
				inboundTypes[inboundTypes.length] = from.implements;
			}
		}
		inboundTypes = inboundTypes.join(', ');
		
		for (var j in recipes) {
			var recipe = recipes[j];
			if (recipe == undefined) continue;
			if (recipe.targetType == targetType &&
				recipe.inboundTypes.join(', ') == inboundTypes) {
				service.recipe = recipe.id;
				$('#' + service.id).addClass('valid');
				break;
			}
		}
	}
}

function reloadRecipes() {
	$.getJSON('/?category=recipes', loadRecipes);
}

function loadRecipes(data) {
	$('#recipes .content ul').html('');
	recipes = data.recipes;
	for (var i in recipes) {
		var recipe = recipes[i];
		var html = '<li id="' + recipe.id + '">' +
			'<strong>' + recipe.name + '</strong><br />' + 
			recipe.targetType + '</li>';
		$('#recipes .content ul').append(html);
	}
	$('#recipes .content li').bind('contextmenu', recipeContextMenu);
	validateServices();
	revalidateConnections();
	drawConnections();
}

function recipeContextMenu(e) {
	if ($('#recipes-dropdown').css('display') != 'block') hideDropDowns();
	var left = e.clientX - 16;
	var top = e.clientY - 72;
	$('#recipes-dropdown').fadeIn(250).css('left', left).css('top', top);
	$('#recipes-dropdown').data('target', $(e.currentTarget).attr('id'));
    return false;
}

function recipesDropDownEdit(event) {
	hideDropDowns();
	event.preventDefault();
	
	var id = $('#recipes-dropdown').data('target');
	var recipe = getRecipe(id);

	var inputs = '';
	for (var i in recipe.inboundTypes)
		inputs += recipe.inboundTypes[i] + '\n';
	if (inputs.length > 0)
		inputs = inputs.substr(0, inputs.length - 1);
		
	code = decodeURI(recipe.sourceCode);

	$('#recipes-modal h2').html('Edit Recipe');
	$('#recipe-id').val(id)
	$('#recipe-name').val(recipe.name)
	$('#recipe-target-type').val(recipe.targetType);
	$('#recipe-inbound-types').val(inputs);	
	recipeEditor.getSession().setValue(code);
	$('#recipes-modal').reveal();
	return false;
}

function recipesDropDownRemove(event) {
	hideDropDowns(event);
	var id = $('#recipes-dropdown').data('target');
	var recipe = getRecipe(id);
	if (recipe.locked) {
		alert('The recipe is locked. Cannot be removed.');
		return false;
	}
	
	for (var i in recipes)
		if (recipes[i] != undefined && recipes[i].id == id)
			recipes[i] = undefined;
	$('#' + id).remove();
	for (var i in services)
		if (services[i] != undefined && services[i].recipe == id)
			services[i].recipe = null;
			
	var json = $.JSON.encode({ id: id });		
	$.delete('/?category=recipes', json);
	setTimeout(reloadRecipes, 250);
	return false;
}

function hideDropDowns(event) {
	$('#apps-dropdown').fadeOut(250);
	$('#recipes-dropdown').fadeOut(250);
	$('#workspace .dropdown').slideUp(250);
}

function appNameClick(event) {
	$('#app-name').select();
}

function appCheckEnter(event) {
	if (event.which == 13) $(this).blur();
}

function appValidateName(event) {
	var name = $('#app-name').val();
	if (name == '') $('#app-name').val('Untitled Application');
}

function appStore(event) {
	var data = {};

	var id = $('#app-id').val();
	if (id == '') {
		id = 'a' + apps.length;
		$('#app-id').val(id);
	}
	data.id = id;
	
	var name = $('#app-name').val();
	data.name = name;
	
	var servicesList = new Array();
	var recipesList = new Array();
	var servicesHaveRecipe = true;
	for (var i in services) {
		var service = services[i];
		if (service == undefined) continue;
		var position = $('#' + service.id).position();
		service.x = position.left;
		service.y = position.top;
		servicesList[servicesList.length] = service;
		if (service.recipe != null)
			recipesList[recipesList.length] = getRecipe(service.recipe);
		else servicesHaveRecipe = false;
	}	
	data.services = servicesList;
	data.recipes = recipesList;
	
	if (servicesList.length == 0) {
		alert('Cannot store the application. ' +
			'There are no service instances.');
		return;
	}
	
	if (!servicesHaveRecipe) {
		alert('Cannot store the application. ' +
			'At least one of the service instances have no recipe.');
		return;
	}
	
	var flow = new Array();
	var flowIsValid = true;
	for (var i in connections) {
		var connection = connections[i];
		if (connection == undefined) continue;
		if (connection.valid == true) {
			flow[flow.length] = {
				from: connection.from,
				to: connection.to
			};
		}
		else flowIsValid = false;
	}
	data.flow = flow;
	
	if (!flowIsValid) {
		alert('Cannot store the application. ' +
			'At least one of the service connections is invalid.');
		return;
	}
	
	data.status = 'idle';
	
	var json = $.JSON.encode(data);
	$.put('/?category=apps', json);
	
	alert('The application "' + name + '" has been successfully stored.');
	closeApp();
	
	setTimeout(reloadApps, 250);
}

function reloadApps() {
	hideDropDowns();
	$.getJSON('/?category=apps', loadApps);
	return false;
}

function loadApps(data) {
	$('#apps .content ul').html('');
	apps = data.apps;
	for (var i in apps) {
		var app = apps[i];
		
		var c;
		if (app.status == 'idle') c = 'idle';
		else if (app.status == 'running') c = 'running';
		else c = 'warning';
		
		var status = app.status;
		if (app.status == 'running') status = 'The application is running.';
		if (app.status == 'idle') status = 'The application is idle.';
		
		var html = '<li id="' + app.id + '">' +
			'<a href="#" class="' + c + '" title="' + status + '"></a>' +
			'<strong>' + app.name + '</strong><br />' + 
			status + '</li>';
		$('#apps .content ul').append(html);
	}
	$('#apps .content li').bind('contextmenu', appsContextMenu);
	$('#apps .content li a').click(appsChangeStatus);
	validateServices();
	drawConnections();
}

function appsContextMenu(e) {
	if ($('#apps-dropdown').css('display') != 'block') hideDropDowns();
	var left = e.clientX - 26;
	var top = e.clientY - 5;
	$('#apps-dropdown').fadeIn(250).css('left', left).css('top', top);
	$('#apps-dropdown').data('target', $(e.currentTarget).attr('id'));
    return false;
}

function appsDropDownEdit(event) {
	hideDropDowns();
	event.preventDefault();
	var id = $('#apps-dropdown').data('target');
	var app = getApp(id);
	
	if (services.length > 0 && !confirm('To start editing the application "' + app.name + '" all present data must be cleared. Do you want to continue?'))
		return false;
	closeApp();
		
	$('#app-id').val(app.id);
	$('#app-name').val(app.name);
	connections = app.flow;
	services = app.services;
	
	for (var i in services) {
		var service = services[i];
		var top = service.y;
		var left = service.x;
		var html = makeVisualWrapper(service, top, left);
		$('#workspace').append(html);
		$('#workspace .service-wrapper').draggable({
			containment: '#workspace',
			scroll: false,
			stack: '#workspace .service-wrapper',
			drag: drawConnections,
			stop: drawConnections
		});
		$('#workspace .service-wrapper .output').draggable({
			cursor: '-webkit-grabbing',
			helper: createConnectorHelper,
			containment: '#workspace',
			scroll: false,
			appendTo: '#workspace',
			start: startDraggingConnector,
			stop: stopDraggingConnector,
			drag: drawTempConnection
		});
		$('#workspace .service-wrapper .input').droppable({
			accept: '#workspace .service-wrapper .output',
			drop: createConnection
		});
		$('#workspace .service-wrapper').bind('contextmenu', serviceContextMenu);
		validateServices();
	}
	validateServices();
	drawConnections();
	return false;
}

function appsDropDownRemove(event) {
	hideDropDowns(event);
	var id = $('#apps-dropdown').data('target');
	var app = getApp(id);
	
	if (!confirm('Are you sure you want to remove the application "' + app.name + '"?'))
		return false;
		
	if (id == $('#app-id').val()) closeApp();
	
	var json = $.JSON.encode({ id: id });		
	$.delete('/?category=apps', json);
	setTimeout(reloadApps, 250);
	return false;
}

function appsChangeStatus(event) {
	var id = $(this).parent('li').attr('id');
	var app = getApp(id);
	
	var newStatus = 'idle';
	if (app.status != 'running') newStatus = 'running';
	
	var json = $.JSON.encode({ id: id, newStatus: newStatus });
	$.put('/?category=status', json);
	setTimeout(reloadApps, 100);
	return false;
}

function closeApp() {
	for (var i in services)
		if (services[i] != undefined)
			$('#' + services[i].id).remove();
	connections = new Array();
	services = new Array();
	$('#app-id').val('');
	$('#app-name').val('Untitled Application');
	drawConnections();
}

$(function() {

	$('#panels').resizable({
		handles: 'e, w',
		resize: function(d, c) {
			$('#panels').css('height', 'auto');
			$('#workspace').css('left', $('#panels').css('width'));
		}
	});
	$('#services').resizable({
		handles: 'n',
		resize: function(d, c) {
			$('#services').css('height', 'auto').css('width', 'auto');
			$('#apps').css('height', $('#services').css('top'));
			resizePanels();
		},
		containment: '#panels'
	});
	$('#recipes').resizable({
		handles: 'n',
		resize: function(d, c) {
			$('#recipes').css('top', 'auto').css('width', 'auto');
			$('#services').css('bottom', $('#recipes').css('height'));
			resizePanels();
		},
		containment: '#panels'
	});
	resizePanels();
	$(window).resize(function() { resizePanels(); } );
	
	$('.toggleLink').data('state', 'open').click(function() {
		if ($(this).data('state') == 'open') hidePanels();
		else showPanels();
		return false;
	});
	
	$('#services .service').draggable({
		helper: 'clone',
		cursor: 'copy',
		appendTo: 'body',
		zIndex: '3000',
		revert: 'invalid',
		revertDuration: 200,
		opacity: 0.5,
		scroll: false
	});
	
	$('#workspace').droppable({
		accept: '.service',
		drop: workspaceDrop
	});
	
	$('#workspace').click(hideDropDowns);
	$('#panels').click(hideDropDowns);
	$('#apps .content').scroll(hideDropDowns);
	$('#recipes .content').scroll(hideDropDowns);
	
	$('#workspace .dropdown a.properties').click(workspaceDropDownProperties);
	$('#workspace .dropdown a.recipe').click(workspaceDropDownRecipe);
	$('#workspace .dropdown a.clear').click(workspaceDropDownClear);
	$('#workspace .dropdown a.remove').click(workspaceDropDownRemove);
	
	$('#recipes-dropdown a.edit').click(recipesDropDownEdit);
	$('#recipes-dropdown a.remove').click(recipesDropDownRemove);
	
	$('#apps-dropdown a.edit').click(appsDropDownEdit);
	$('#apps-dropdown a.remove').click(appsDropDownRemove);
	$('#apps-dropdown a.refresh').click(reloadApps);
	
	$('#recipe').submit(storeRecipe);
	$('#service').submit(storeService);
	
	$.getJSON('/?category=recipes', loadRecipes);
	$.getJSON('/?category=apps', loadApps);

	$('#app-name').click(appNameClick).keypress(appCheckEnter).blur(appValidateName);
	$('#app-store').click(appStore);
});

window.onload = function() {
    recipeEditor = ace.edit("editor");
    recipeEditor.setTheme("ace/theme/espresso");
    var PhpMode = require("ace/mode/php").Mode;
    recipeEditor.getSession().setMode(new PhpMode());
};