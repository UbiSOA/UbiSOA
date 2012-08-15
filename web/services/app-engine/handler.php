<?php

function get_recipe($id) {
	global $app;
	foreach ($app -> recipes as $recipe)
		if ($recipe -> id == $id) return $recipe;
	return false;
}

function get_service($id) {
	global $app;
	foreach ($app -> services as $service)
		if ($service -> id == $id) return $service;
	return false;
}

function track_obj($label, $obj, $char) {
	echo "\n$label ".$obj -> id.": ".$obj -> name." ".str_pad('', 8, $char)."\n";
}

function process_app() {
	global $app, $service;
	track_obj('APP', $app, '=');
	
	if ($app -> status != 'running') {
		echo "Application isn't running.\n";
		return;
	}
	foreach ($app -> services as $service)
		process_service();
}

function process_service() {
	global $app, $service, $recipe, $target, $inbound;
	track_obj('SRV', $service, '-');
	
	$recipe = get_recipe($service -> recipe);
	if ($recipe === false) {
		$app -> status = 'Service '.$service -> name.' has no recipe.';
		return;
	}
	
	if (!requires_handling()) return;
	
	$target = $service;
	$code = urldecode($recipe -> sourceCode);
	$res = eval($code);
	if ($data) $service -> data = $data;
	
	if ($res !== null) $app -> status =
		'There was an error running recipe: '.$recipe -> name;
}

function requires_handling() {
	global $app, $service, $recipe, $inbound;
	
	if (count($recipe -> inboundTypes) == 0) return true;

	$inbound = array();
	foreach ($app -> flow as $flow)
		if ($flow -> to == $service -> id)
			$inbound[] = get_service($flow -> from);

	$hasData = true;
	foreach ($inbound as $in)
		if (!$in -> data)
			$hasData = false;
			
	print_r($inbound);
	
	return $hasData;
}

function last($array) {
	return $array[count($array) - 1];
}

function subscribe($service_obj) {
	global $service;
	$topic = 'http://'.$service_obj -> host.':'.$service_obj -> port.'/?output=json';
	echo 'Subscription request: '.$topic."\n";
	if ($service_obj -> subscribedTo == $topic) {
		echo "Already subscribed.\n";
		return;
	}

	$data['hub.callback'] = 'http://127.0.0.1:8320/callback';
	$data['hub.mode'] = 'subscribe';
	$data['hub.topic'] = $topic;
	$data['hub.verify'] = 'async';
	$data['hub.verify_token'] = md5(time());
	$post_fields = array();
	foreach ($data as $k => $v)
		$post_fields[] = $k.'='.$v;
	$post_fields = implode('&', $post_fields);

	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, 'http://127.0.0.1:8310/');
	curl_setopt($ch, CURLOPT_POST, 1);
	curl_setopt($ch, CURLOPT_POSTFIELDS, $post_fields);
	$res = curl_exec($ch);
	curl_close($ch);
	
	$service_obj -> subscribedTo = $topic;	
}

function post($service_obj, $data) {
	global $service;
	$target_url = 'http://'.$service_obj -> host.':'.$service_obj -> port.'/';
	$post_fields = array();
	foreach ($data as $k => $v)
		$post_fields[] = $k.'='.$v;
	$post_fields = implode('&', $post_fields);
	
	if ($service -> lastPost == $post_fields) {
		echo "Post fields: ".$post_fields."\n";
		echo "Already sent post data.\n";
		return;
	}
	
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $target_url);
	curl_setopt($ch, CURLOPT_POST, 1);
	curl_setopt($ch, CURLOPT_POSTFIELDS, $post_fields);
	$res = curl_exec($ch);
	curl_close($ch);
	
	$service -> lastPost = $post_fields;
	
	echo 'Sent post to: '.$target_url."\n";
	echo 'With data: '.$post_fields."\n";
}

//for (;;) {
	$data = json_decode(file_get_contents('../../editor/dat/apps.json'));
	foreach ($data -> apps as $app)
		process_app();
	echo "\n";
	file_put_contents('../../editor/dat/apps.json', json_encode($data));
//	sleep(1);
//}

