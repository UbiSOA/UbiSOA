<?php

	$main_doc = $xml -> doc;
	$title = $main_doc? trim((string)$main_doc['title']): 'Unknown Interface';
	$descr = $main_doc? trim((string)$main_doc): 'This interface is unknown.';
	$last_updated = date('F j, Y', filemtime('../pub/interfaces/v1/'.$file));
	$resources = $xml -> resources -> resource;
	
	global $main_xml, $core_wadl;
	$core_wadl = simplexml_load_valid(
		'../pub/interfaces/v1/core.wadl', '../pub/schemas/inc/wadl.xsd', true);
	$main_xml = $xml;
	
	function get_param($param) {
		global $core_wadl;
		if ($param['href']) {
			$href = explode('#', $param['href']);
			if ($href[0] == 'core.wadl') {
				$params = $core_wadl -> param;
				foreach ($params as $p)
					if ($p['id'] == $href[1])
						return $p;
			} else show_error('Cannot parse param '.$param['href']);
		} else return $param;
	}
	
	function get_type($param) {
		if ($param['type']) {
			$type = substr((string)$param['type'], 4);
			return $type;
		} else if ($param -> option) {
			return 'string';
		} else show_error('Cannot parse type: '.$param['type']);
	}
	
	function get_default($param) {
		$default = $param['default'];
		return $default? $default: 'none';
	}
	
	function get_descr($param) {
		$descr = (string)$param -> doc;
		$default = (string)$param['default'];
		$options = $param -> option;
		$choices = array();
		if ($options) {
			foreach ($options as $option)
				$choices[] = '<li><strong>'.$option['value'].'</strong> - '.
					($default == $option['value']? ' [<i>default</i>] ': '').
					($option -> doc? $option -> doc.' ': '').
					($option['mediaType']? ' (<i>'.$option['mediaType'].'</i>)': '').'</li>';
			return '<p>'.$descr.' Choose one of the following string values:</p><ul>'.
				implode('', $choices).'</ul>';
		} else return $descr;
	}
	
	function get_repr($repr) {
		global $core_wadl, $main_xml;
		if ($repr['href']) {
			$href = explode('#', $repr['href']);
			if ($href[0] == 'core.wadl') {
				$reprs = $core_wadl -> representation;
				foreach ($reprs as $r)
					if ($r['id'] == $href[1])
						return $r;
			} elseif (!$href[0] && $href[1]) {
				$reprs = $main_xml -> representation;
				foreach ($reprs as $r)
					if ($r['id'] == $href[1])
						return $r;
			} else show_error('Cannot parse repr '.$repr['href']);
		} return $repr;
	}
	
	function get_ns_info($repr) {
		global $main_xml;
		if ($repr['mediaType'] != 'text/xml') show_error('Cannot parse '.$repr);
		$ns = explode(':', $repr['element']);
		$nss = $main_xml -> getDocNamespaces();
		$ns_def = $nss[$ns[0]];
		$res['object_type'] = '{'.$ns_def.'}'.$ns[1];
		if (!$ns_def) show_error('Cannot find namespace definition for: '.$repr['element']);
		$ns_def = explode(':', $ns_def);
		$ns_def = $ns_def[2].'.xsd';
		$grammars = $main_xml -> grammars -> {'include'};
		foreach ($grammars as $g)
			if (strpos($g['href'], $ns_def) !== false) {
				$res['schema'] = (string)$g['href'];
				return $res;
			}
		show_error('Cannot parse namespace '.$repr);
	}
	
	function get_sample_request($repr) {
		$i = 0; while ($i++ < 2) {
			$docs = $repr -> doc;
			if ($docs)
				foreach ($docs as $d)
					if ($d['title'] == 'Sample Request')
						return (string)$d;
			$repr = get_repr($repr);
		}
	}
	
	function get_html_docs($doc) {
		if (!$doc) return;
		$html = str_replace('&', '&amp;', (string)$doc); $res = '';
		if ($html = simplexml_load_string('<xml>'.$html.'</xml>')) {
			$tags = $html -> children();
			foreach ($tags as $tag)
				switch ($tag -> getName()) {
					case 'p':
						$res .= '<p>'.(string)$tag.'</p>';
						break;
					case 'a':
						$res .= '<p class="url">'.(string)$tag.'</p>';
						break;
					case 'link':
						switch ($tag['class']) {
							case 'snippet':
								$href = $tag['href'];
								if (strpos($href, 'http://samples.ubisoa.net/') !== false)
									$href = '../pub/samples/'.
										substr($href, strlen('http://samples.ubisoa.net/'));
								$code = file_get_contents($href);
								$href = explode('.', $href);
								$res .= snippet($code, $href[count($href) - 1]);
								break;
							case 'screenshot':
								$res .= '<p class="screenshot">'.
									'<img src="'.(string)$tag['href'].'"/></p>';
								break;
						}
						break;
					default: show_error('Cannot parse docs '.$tag -> getName());
				}
		} else show_error('Cannot parse docs '.$doc);
		return $res."\n";
	}
	
	function get_resp($resp) {
		switch ($resp['status']) {
			case 200: return 'Response Code 200 (OK)';
			case 201: return 'Response Code 201 (Created)';
			default: show_error('Cannot parse status '.$resp);
		}
	}
	
	// SELF el mismo
	// EDIT modificar o borrar
	// START lista de recursos
	// ALTERNATE versiones alternas del mismo
	
	function get_mtype_header($repr) {
		$doc = trim((string)$repr -> doc);
		return $doc[strlen($doc) - 1] == '.'?
			substr($doc, 0, -1): $doc;
	}

?><!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>UbiSOA Documentation - <?= $title ?></title>
	<link rel="stylesheet" href="http://yui.yahooapis.com/3.1.1/build/cssreset/reset-min.css" />
	<link rel="stylesheet" href="/css/general.css" />
</head>
<body>

	<div id="wrapper">
		<article>
			
			<header>
				<h1><?= $title ?></h1>
				<h2><?= $descr ?></h2>
				<p>Last Updated: <?= $last_updated ?></p>
			</header>
			
			<p>The WADL document for this service interface is located at <a href="http://interfaces.ubisoa.net/v1/<?= $file ?>">http://interfaces.ubisoa.net/v1/<?= $file ?></a></p>
			<p>The <?= $title ?> defines a total of <?= count($resources) ?> resource<?= count($resources) != 1? 's': '' ?>.<?php if (count($resources) > 0): ?> Below you will find the supported methods for each resource, its available parameters, and the specifics on the outputs.<?php endif; ?></p>
<?php if ($resources) foreach ($resources as $resource): ?>

<?php	if ($resource -> method) foreach ($resource -> method as $method): ?>
			<h1><?= (string)$method['name'].' <span>/'.(string)$resource['path'] ?></span></h1>
			<p><?= (string)$method -> doc ?></p>

<?php 		if ($method -> request -> param): ?>
			<h2>Request Parameters</h2>
			<table>
				<tr>
					<th>Parameter</th>
					<th>Type</th>
					<th>Default</th>
					<th>Description</th>
				</tr>
<?php			foreach ($method -> request -> param as $param):
					$param = get_param($param); ?>
				<tr>
					<td><?= (string)$param['name'] ?></td>
					<td><?= get_type($param) ?></td>
					<td><?= get_default($param) ?></td>
					<td><?= get_descr($param) ?></td>
				</tr>
<?php			endforeach; ?>
			</table>
<?php		elseif ($method -> request -> representation):
				 $repr = get_repr($method -> request -> representation);
				 $ns_info = get_ns_info($repr); ?>
			<h2>Request Body</h2>
			<p>An object in XML representation of the type <a href="<?= $ns_info['schema'] ?>"><?= $ns_info['object_type'] ?></a>. The schema document for this service request is located at <a href="<?= $ns_info['schema'] ?>"><?= $ns_info['schema'] ?></a></p>
<?php		endif;
			if ($method -> request -> doc): ?>
			<?= get_html_docs($method -> request -> doc) ?>
<?php 		endif;
			if ($method -> response): $responses = $method -> response;
				foreach ($responses as $response):
					if ($response['status']): ?>
			<h2><?= get_resp($response) ?></h2>
<?php				else: ?>
			<h2><?= (count($responses) > 1? 'Other Responses': 'Response') ?></h2>
<?php				endif;
					$representations = $response -> representation;
					if (count($representations) > 1):
				?>
			<p>The format of the output depends on the value of the parameter with the same name specified in the request. The are <?= count($representations) ?> possible representation types for this resource.</p>
<?php					foreach ($response -> representation as $representation):
							$repr = get_repr($representation); ?>
			<h3><?= get_mtype_header($repr) ?></h3>
			<?= get_html_docs($representation -> doc) ?>
<?php					endforeach;
					elseif (count($representations) == 1):
						$representation = $response -> representation;
						$repr = get_repr($representation); ?>
			<p><?= (string)$repr -> doc; ?></p>
			<?= get_html_docs($representation -> doc) ?>
<?php				else: ?>
			<p>This response does not returns a representation in its body but could contain more information on the response headers. By instance, the "Location" header could point to a relevant resource.</p>
<?php				endif;
				endforeach;
			endif;
		endforeach;
	endforeach; ?>

		</article>
	</div>
	
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
	<script src="/js/general.js"></script>
	
</body>
</html>
