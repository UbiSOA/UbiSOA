<?php

	function print_line($line, $category) {
		echo $category.' '.$line."\n";
	}
	
	function print_value($value, $category) {
		$lines = explode("\n", $value);
		foreach ($lines as $line)
			print_line($line, $category);
	}

	function parse_input($raw_input) {
		global $method, $query, $uri, $get, $headers, $input;
	
		// Getting a string per input line.
		$lines = explode("\n", $raw_input);
		
		// Getting the request verb and action.
		print_line($lines[0], '>');
		$request = explode(' ', $lines[0]);
		$method = $request[0];
		$query = $request[1];
		$uri = substr($query, 0, strpos($query, '?'));
		$get = substr($query, strpos($query, '?') + 1);
		if (strpos($query, '?') === false) {
			$uri = $query;
			$get = array();
		} else parse_str($get, $get);
		
		// Getting the request headers.
		$headers = array();
		$i = 1;
		while (trim($lines[$i]) != '' && $i < count($lines))
		{
			$header = $lines[$i++];
			print_line($header, '>');
			$key = substr($header, 0, strpos($header, ': '));
			$value = substr($header, strpos($header, ': ') + 2, -1);
			$headers[$key] = $value;
		}
		
		// Getting the request body.
		print_line('', '>');
		$input = @trim($lines[$i + 1]);
		if ($input) print_line($input, '>');
	}

	set_time_limit(0);
	$address = '127.0.0.1';
	$port = 8320;
	
	$socket = socket_create(AF_INET, SOCK_STREAM, 0);
	socket_bind($socket, $address, $port) or die('Error.');
	socket_listen($socket);
	
	print_line('Ready, listening for requests.', '*');
	
	for (;;) {
		$retrieve = '';
		$client = socket_accept($socket);
		
		$raw_input = socket_read($client, 1024);
		parse_input($raw_input);

		if ($headers['Content-Type'] == 'application/json') {	
			$input = json_decode($input);
			print_value(print_r($input, true), '*');
		
			if ($method == 'POST' && $uri == '/callback') {
				$retrieve = $input -> topic;
				print_line('Flagged to download: '.$retrieve, '*');
			}
			else print_line("METHOD $method, URI $uri", '*');
		}
		
		if ($uri == '/callback') {
			$xml = $get['hub_challenge'];
			if ($xml) print_line('VERIFIED ['.$xml.']', '*');
		}
		else $xml = "<html><body>OK</body></html>";
		$out = "HTTP/1.0 200 OK\n".
			"Content-Type: text/plain\n".
			"Content-Length: ".strlen($xml)."\n\n".$xml;
		print_value($out, '<');
		
		socket_write($client, $out);
		
		socket_close($client);
		
		if ($retrieve) {
			print_line('Retrieving '.$retrieve, '*');
			system('curl -s '.$retrieve.' > /tmp/request.txt');
			$value = file_get_contents('/tmp/request.txt');
			if (strpos($retrieve, 'json') !== false)
				$value = print_r(json_decode($value), true);
			print_value($value, '*');
		}
	}
