<%@ page import="de.iteratec.osm.measurement.script.Script"%>
<div class="row form-group ${hasErrors(bean: job, field: 'script', 'error')}">
	<label class="span3 text-right" for="script"> <g:message code="job.selectedScript.label" default="script" /> <span class="required-indicator">*</span>
	</label>	
	<div class="span9">
		<g:select class="form-control chosen" name="script.id" id="script" from="${Script.list()}"
			value="${job?.script?.id}" optionValue="label" optionKey="id" onchange="updateScriptEditHref('${ createLink(controller: 'script', action: 'edit', absolute: true) }', \$(this).val());" />
		</div>
</div>

<div class="row form-group">
	<label class="span3 text-right"><g:message code="job.placeholders.label" default="script" /></label>
	<div class="span9">
		<div id="placeholderCandidates" 
			data-noneUsedInScript-message="${ message(code: 'job.placeholders.usedInScript.none') }"></div>
	</div>
</div>

<r:script>

	var editor = null;
	var update = function() {
		if (!editor) {
			return;
		}
		$.ajax({type : 'POST', url : '${createLink(action: 'mergeDefinedAndUsedPlaceholders', absolute: true)}', 
			data: {
				'jobId': $('input#id').val(), 
				'scriptId': $('#script').val()
			},
			success : function(result) {
				$("#placeholderCandidates").html(result);
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				$("#placeholderCandidates").html('');
			}
		});
		
		$.ajax({type : 'POST', url : '${createLink(action: 'getScriptSource', absolute: true)}', 
			data: {
				'scriptId': $('#script').val()
			},
			success : function(result) {
				//var scriptToLoad = result;
				//if(typeof result != 'undefined') {scriptToLoad = result.innerHtml();}
				loadNewContent(editor, result);
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				loadNewContent(editor, 'Error');
			}
		});
		
	}

	$('#scriptTabLink').bind("click", function() {
		update();
	});
	$('#script').bind("change", update);

	$('#script').change();
	$(document).ready(function(){
		if (!editor) {
			editor = createCodemirrorEditor();
			update();
		}
	});
</r:script>

<p style="margin: 0; padding-top: 1em;"><g:message code="job.script.preview.label" /> <a href="" target="_blank" id="editScriptLink">
			<i class="icon-edit icon-large" rel="tooltip" title="${ message(code: 'job.script.edit') }"></i>
		</a>:</p>
<g:render template="../script/codemirror" model="${['code': job?.script?.navigationScript, 'measuredEvents': null, 'autoload': false, 'readOnly': true]}" />