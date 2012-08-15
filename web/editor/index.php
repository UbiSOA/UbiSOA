<?php

	$category = isset($_GET['category']) ? $_GET['category'] : '';
	$method = $_SERVER['REQUEST_METHOD'];
	$request = $method.' '.$category;
		
	if ($request == 'POST recipes'):
		$data = @json_decode(file_get_contents('dat/recipes.json'));
		$post = json_decode(file_get_contents('php://input'));
		$data -> recipes[] = $post;
		file_put_contents('dat/recipes.json', json_encode($data));
		exit();
		
	elseif ($request == 'GET recipes'):
		readfile('dat/recipes.json');
		exit();
		
	elseif ($request == 'DELETE recipes'):
		$data = @json_decode(file_get_contents('dat/recipes.json'));
		$post = json_decode(file_get_contents('php://input'));
		$recipes = array();
		foreach ($data -> recipes as $key => $recipe)
			if ($recipe -> id != $post -> id)
				$recipes[] = $recipe;
		$data -> recipes = $recipes;
		file_put_contents('dat/recipes.json', json_encode($data));
		exit();
		
	elseif ($request == 'PUT recipes'):
		$data = @json_decode(file_get_contents('dat/recipes.json'));
		$post = json_decode(file_get_contents('php://input'));
		$recipes = array();
		foreach ($data -> recipes as $recipe)
			if ($recipe -> id == $post -> id) $recipes[] = $post;
			else $recipes[] = $recipe;
		$data -> recipes = $recipes;
		file_put_contents('dat/recipes.json', json_encode($data));
		exit();
		
	elseif ($request == 'PUT apps'):
		$data = @json_decode(file_get_contents('dat/apps.json'));
		$post = json_decode(file_get_contents('php://input'));
		$apps = array();
		$done = false;
		if ($data -> apps)
			foreach ($data -> apps as $app)
				if ($app -> id == $post -> id) {
					$apps[] = $post;
					$done = true;
				}
				else $apps[] = $app;
		if (!$done) $apps[] = $post;
		$data -> apps = $apps;
		file_put_contents('dat/apps.json', json_encode($data));
		exit();
		
	elseif ($request == 'GET apps'):
		readfile('dat/apps.json');
		exit();
		
	elseif ($request == 'DELETE apps'):
		$data = @json_decode(file_get_contents('dat/apps.json'));
		$post = json_decode(file_get_contents('php://input'));
		$apps = array();
		foreach ($data -> apps as $key => $app)
			if ($app -> id != $post -> id)
				$apps[] = $app;
		$data -> apps = $apps;
		file_put_contents('dat/apps.json', json_encode($data));
		exit();
		
	elseif ($request == 'PUT status'):
		$data = @json_decode(file_get_contents('dat/apps.json'));
		$post = json_decode(file_get_contents('php://input'));
		if ($data -> apps)
			foreach ($data -> apps as $app)
				if ($app -> id == $post -> id)
					$app -> status = $post -> newStatus;
		file_put_contents('dat/apps.json', json_encode($data));
		system('cd ../services/app-engine; php handler.php');
		exit();
	
	endif;

?><!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>UbiSOA Editor</title>
	<link rel="stylesheet" href="http://yui.yahooapis.com/3.1.1/build/cssreset/reset-min.css" />
	<link rel="stylesheet" href="/lib/reveal/reveal.css" />
	<link rel="stylesheet" href="general.css" />
</head>
<body>
	<header>
		<h1><span>UbiSOA</span> Editor <em>&alpha;</em></h1>
	</header>
	<div class="shadow"></div>
	<div id="workspace">
		<canvas id="connections"></canvas>
		<div class="dropdown">
			<ul>
				<li><a href="#" class="properties">View Properties</a></li>
				<li><a href="#" class="recipe">Create Recipe</a></li>
				<li><a href="#" class="clear">Clear Connections</a></li>
				<li><a href="#" class="remove">Remove Instance</a></li>
			</ul>
		</div>
		<input id="app-name" value="Untitled Application" type="text" />
		<input id="app-store" value="Store Changes" type="button" />
		<input id="app-id" value="" type="hidden" />
	</div>
	<div id="panels">
		<a class="toggleLink" href="#"></a>
		<div id="apps" class="box">
			<div class="title-bar">
				<h2>Applications</h2>
			</div>
			<div class="content">
				<ul></ul>
			</div>
		</div>
		<div id="services" class="box">
			<div class="title-bar">
				<h2>Services</h2>
			</div>
			<div class="content">
				<ul>
					<li>
						<div class="service sensing"></div>
						<h3>Sensing</h3>
					</li>
					<li>
						<div class="service rfid"></div>
						<h3>RFID</h3>
					</li>
					<li>
						<div class="service leds"></div>
						<h3>LEDs</h3>
					</li>
					<li>
						<div class="service twitter"></div>
						<h3>Twitter</h3>
					</li>
					<li>
						<div class="service servo"></div>
						<h3>Servo</h3>
					</li>
				</ul>
			</div>
		</div>
		<div id="recipes" class="box">
			<div class="title-bar">
				<h2>Recipes</h2>
			</div>
			<div class="content">
				<ul></ul>
			</div>
		</div>
	</div>
	
	<div id="recipes-modal" class="reveal-modal">
		<h2>Create Recipe</h2>
		<form id="recipe">
			<div>
				<label for="recipe-name">Name:</label>
				<input id="recipe-name" name="name" type="text" placeholder="Short description" />
			</div>
			<div>
				<label for="recipe-target-type">Target Type:</label>
				<input id="recipe-target-type" name="targetType" type="text" placeholder="Target interface" /></div>
			<div>
				<label for="recipe-inbound-types">Inbound Types:</label>
				<textarea id="recipe-inbound-types" name="inboundTypes" placeholder="No input interfaces"></textarea>
			</div>
			<div class="editor-wrapper">
				<label for="editor">Source Code:</label>
				<pre id="editor"></pre>
			</div>
			<div class="submit-wrapper">
				<input type="submit" id="recipe-submit" value="Store Changes" />
				<input type="hidden" id="recipe-service" name="service" />
				<input type="hidden" id="recipe-id" name="recipe" />
			</div>
		</form>
		<a class="close-reveal-modal">&#215;</a>
	</div>
	
	<div id="services-modal" class="reveal-modal">
		<h2>Edit Service Instance</h2>
		<form id="service">
			<div>
				<label for="service-name">Name:</label>
				<input id="service-name" name="name" type="text" placeholder="Service Name" />
			</div>
			<div>
				<label for="service-host">Host:</label>
				<input id="service-host" name="host" type="text" placeholder="localhost" />
			</div>
			<div>
				<label for="service-port">Port:</label>
				<input id="service-port" name="port" type="text" placeholder="80" />
			</div>
			<div>
				<label for="service-implements">Implements:</label>
				<input id="service-implements" name="implements" type="url" placeholder="http://interfaces.ubisoa.net/" />
			</div>
			<div>
				<label for="service-recipe">Recipe:</label>
				<select id="service-recipe" name="recipe"></select>
			</div>
			<div class="submit-wrapper">
				<input type="submit" id="service-submit" value="Store Changes" />
				<input type="hidden" id="service-id" name="service" />
			</div>
		</form>
	</div>
	
	<div id="recipes-dropdown" class="dropdown inverted">
		<ul>
			<li><a href="#" class="edit">Edit Recipe</a></li>
			<li><a href="#" class="remove">Remove Recipe</a></li>
		</ul>
	</div>
	
	<div id="apps-dropdown" class="dropdown">
		<ul>
			<li><a href="#" class="edit">Edit Application</a></li>
			<li><a href="#" class="remove">Remove Application</a></li>
			<li><a href="#" class="refresh">Refresh</a></li>
		</ul>
	</div>
	
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
	<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js"></script>
	<script src="/lib/reveal/jquery.reveal.js"></script>
	<script src="/lib/ace/src/ace.js" type="text/javascript" charset="utf-8"></script>
	<script src="/lib/ace/src/theme-espresso.js" type="text/javascript" charset="utf-8"></script>
	<script src="/lib/ace/src/mode-php.js" type="text/javascript" charset="utf-8"></script>
	<script src="/lib/jquery.json/jquery.json.js"></script>
	<script src="/lib/jquery.rest/jquery.rest.js"></script>
	<script src="general.js"></script>
</body>
</html>
