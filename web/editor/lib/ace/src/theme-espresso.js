define("ace/theme/espresso", function(require, exports, module) {

    var dom = require("pilot/dom");

    var cssText = ".ace-espresso .ace_editor {\
  border: 2px solid rgb(159, 159, 159);\
}\
\
.ace-espresso .ace_editor.ace_focus {\
  border: 2px solid #327fbd;\
}\
\
.ace-espresso .ace_gutter {\
  width: 35px;\
  background: whiteSmoke;\
  color: #aaa;\
  font-size: 8px;\
  line-height: 15px;\
  overflow : hidden;\
}\
\
.ace-espresso .ace_gutter-layer {\
  width: 100%;\
  text-align: right;\
}\
\
.ace-espresso .ace_gutter-layer .ace_gutter-cell {\
  padding-right: 6px;\
}\
\
.ace-espresso .ace_print_margin {\
  width: 1px;\
  background: #e8e8e8;\
}\
\
.ace-espresso .ace_scroller {\
  background-color: #FFFFFF;\
}\
\
.ace-espresso .ace_text-layer {\
  cursor: text;\
  color: #555;\
}\
\
.ace-espresso .ace_cursor {\
  border-left: 2px solid #000000;\
}\
\
.ace-espresso .ace_cursor.ace_overwrite {\
  border-left: 0px;\
  border-bottom: 1px solid #000000;\
}\
 \
.ace-espresso .ace_marker-layer .ace_selection {\
  background: #cad7df;\
}\
\
.ace-espresso .ace_marker-layer .ace_step {\
  background: rgb(198, 219, 174);\
}\
\
.ace-espresso .ace_marker-layer .ace_bracket {\
  margin: -1px 0 0 -1px;\
  border: 1px solid #404040;\
}\
\
.ace-espresso .ace_marker-layer .ace_active_line {\
  background: #ecf2f6;\
}\
\
       \
.ace-espresso .ace_invisible {\
  color: #404040;\
}\
\
.ace-espresso .ace_keyword {\
  color:#2F6F9F;\
}\
\
.ace-espresso .ace_keyword.ace_operator {\
  \
}\
\
.ace-espresso .ace_constant, .ace-espresso .ace_constant.ace_support {\
  color:#73B00A;\
}\
\
.ace-espresso .ace_constant.ace_language {\
  color:#7653C2;\
}\
\
.ace-espresso .ace_constant.ace_library {\
  \
}\
\
.ace-espresso .ace_constant.ace_numeric {\
  color:#73B00A;\
}\
\
.ace-espresso .ace_invalid {\
  color:#F9F2CE;\
}\
\
.ace-espresso .ace_invalid.ace_illegal {\
  \
}\
\
.ace-espresso .ace_invalid.ace_deprecated {\
  \
}\
\
.ace-espresso .ace_support {\
  color:#CF4F5F;\
}\
\
.ace-espresso .ace_support.ace_function {\
  color:#4E279A;\
}\
\
.ace-espresso .ace_function.ace_buildin {\
  \
}\
\
.ace-espresso .ace_string {\
  color:#73B00A;\
}\
\
.ace-espresso .ace_string.ace_regexp {\
  \
}\
\
.ace-espresso .ace_comment {\
  color:#AAAAAA;\
}\
\
.ace-espresso .ace_comment.ace_doc {\
  \
}\
\
.ace-espresso .ace_comment.ace_doc.ace_tag {\
  \
}\
\
.ace-espresso .ace_variable {\
  color:#7B8C4D;\
}\
\
.ace-espresso .ace_variable.ace_language {\
  \
}\
\
.ace-espresso .ace_xml_pe {\
  \
}\
\
.ace-espresso .ace_collab.ace_user1 {\
     \
}";

    // import CSS once
    dom.importCssString(cssText),

    exports.cssClass = "ace-espresso"})