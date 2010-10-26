var lastData = null;

function checkData() {
	$.ajax({
		url: 'callback?output=json',
		dataType: 'json',
		success: function(data, textStatus, XMLHttpRequest) {
			if (data.items != undefined)
				if (lastData != null) {
					
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
					
				} else lastData = data;
			setTimeout(checkData, 500);
		}
	});
}
checkData();
