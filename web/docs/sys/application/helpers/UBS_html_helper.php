<?php

function snippet($code, $lang) {
	return '<div class="snippet">'.
		'<code>'._snippet_highlight($code, $lang).'</code>'.
		'<div class="tab"></div></div>';
}

function _snippet_highlight($code, $lang) {
	switch ($lang) {
		case 'xml': case 'atom': return _snippet_highlight_xml($code);
		default: return _snippet_hightlight_any($code);
	}
}

function _snippet_highlight_xml($s){
	$s = trim($s);
	$s = preg_replace("/(\?\w+)=/", "\\1ubsEQ", $s);
	$s = preg_replace("/(<\/?)(\w+)([^>]*>)/e", "'ubsEntStart\\1\\2\\3ubsEnd'", $s);
	$s = preg_replace("/(\w+)(=)/", "ubsKeyStart\\1\\2ubsEnd", $s);
	$s = preg_replace("/(\")(.+)(\")/", "ubsStrStart\\1\\2\\3ubsEnd", $s);
	$s = htmlentities($s); $s = nl2br($s);
	$s = str_replace("\t", '&nbsp;&nbsp;&nbsp;&nbsp;', $s);
	$s = str_replace('\\', '', $s);
	$s = str_replace('ubsEntStart', '<span class="entity">', $s);
	$s = str_replace('ubsKeyStart', '<span class="keyword">', $s);
	$s = str_replace('ubsStrStart', '<span class="string">', $s);
	$s = str_replace('ubsEnd', '</span>', $s);
	$s = str_replace('ubsEQ', '=', $s);
	if (substr($s, 0, 8) == '&lt;?xml') {
		$s = explode("\n", $s);
		$s[0] = '<span class="comment">'.strip_tags($s[0]).'</span><br />';
		$s = implode("\n", $s);
	}
	return $s;
}

function _snippet_hightlight_any($s) {
	$s = trim($s);
	$s = preg_replace("/(\")(.+)(\")/", "ubsEntStart\\1\\2\\3ubsEnd", $s);
	$s = preg_replace("/([-+]?[0-9]*\.?[0-9]+)/", "ubsStrStart\\1ubsEnd", $s);
	$s = htmlentities($s); $s = nl2br($s);
	$s = str_replace("\t", '&nbsp;&nbsp;&nbsp;&nbsp;', $s);
	$s = str_replace("\\", '', $s);
	$s = str_replace('ubsEntStart', '<span class="entity">', $s);
	$s = str_replace('ubsStrStart', '<span class="number">', $s);
	$s = str_replace('ubsEnd', '</span>', $s);
	$s = str_replace('new', '<span class="keyword">new</span>', $s);
	return $s;
}
