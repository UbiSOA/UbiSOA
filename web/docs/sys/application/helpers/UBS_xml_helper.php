<?php

function _libxml_describe_error($error) {
	$level = 'Warning';
	switch ($error -> level) {
		case LIBXML_ERR_ERROR: $level = 'Error'; break;
		case LIBXML_ERR_FATAL: $level = 'Fatal Error'; break;
	}
	return '<strong>'.$level.' #'.$error -> code.'</strong>: '.trim($error -> message).
		' on line <strong>'.$error -> line.'</strong>';
}
	
function _libxml_describe_errors() {
	$errors = libxml_get_errors();
	if ($errors[0] -> file) {
		$file = substr($errors[0] -> file, strlen($_SERVER['DOCUMENT_ROOT']));
		$file = '<p>On parsing file <strong>'.$file.'</strong>:</p>';
	} else $file = '';
	$descrs = array();
	foreach ($errors as $error)
		$descrs[] = '<li>'._libxml_describe_error($error).'</li>';
	libxml_clear_errors();
	return $file.'<ul>'.implode('', $descrs).'</ul>';
}
	
function simplexml_load_valid($xml_path, $schema_path = '', $validate_schemas = true) {
	// Check if file exists.
	if (!$xml_path || !file_exists($xml_path)) show_404();
	
	// Load the file and check if it is a well-formed XML.
	libxml_use_internal_errors(true);
	$dom = new DOMDocument();
	if (!$dom -> load($xml_path))
		show_error(_libxml_describe_errors());

	// Check if file is valid.
	if ($validate_schemas)
		if ($schema_path && !$dom -> schemaValidate($schema_path))
			show_error(_libxml_describe_errors());
		
	// Converting DOM to SimpleXML.
	return simplexml_import_dom($dom);
}
