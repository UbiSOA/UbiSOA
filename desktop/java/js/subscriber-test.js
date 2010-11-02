var lastData = null;
var initialized = false;

function renderData(data) {
	if (data.items == undefined) return;
	if (lastData == null) lastData = data;
	
			var lastCount = lastData.items.length;
			var thisCount = data.items.length;
			if (thisCount - lastCount > 0) {
				var offset = lastCount;
				var total = thisCount - lastCount;
				
				for (var i = 0; i < total; i++) {
					if ($('ul li').size() > 0)
						$('ul li:eq(0)').before('<li class="hidden" style="display: none"><strong>' + data.items[i + offset].title + '.</strong> ' + data.items[i + offset].content + '</li>');
					else $('ul').html('<li class="hidden" style="display: none"><strong>' + data.items[i + offset].title + '.</strong> ' + data.items[i + offset].content + '</li>');
				}
				lastData = data;
				$('.hidden').slideDown(250);
				$('.hidden').removeClass('hidden');
			}
		
}

function checkData() {
	$.ajax({
		url: 'callback?output=json' + (!initialized? '&init=true': ''),
		dataType: 'json',
		success: function(data, textStatus, XMLHttpRequest) {
			$.ajax({
				url: data.topic != undefined? data.topic: 'http://localhost:8311?output=json',
				dataType: 'jsonp',
				success: function(data) {
					renderData(data);
				}
			});
			checkData();
		},
		error: function(request, status, error) {
			if (console != undefined) {
				console.log('Request', request);
				console.log('Status', status);
				console.log('Error', error);
			}
			setTimeout(checkData, 2000);
		}
	});
	initialized = true;
}
checkData();
