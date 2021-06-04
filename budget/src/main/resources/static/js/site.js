//Global Variables
var deletedRow;
var dynamicParent;
var updateRequest;

//Static Functions
function notImplimented(){
	alertWithFade('danger','This feature hasn\'t been implimented yet :(');
}

function isItemValid(name){
	if (name){ return true;} 
	else { return false;}
}
function isCurrencyValid(amount) {
  var regex = /^[0-9]+(\.[0-9]{1,2})?$/;
  return amount != '' && regex.test(amount);
}
(function ($) {
	  $.each(['show', 'hide'], function (i, ev) {
	    var el = $.fn[ev];
	    $.fn[ev] = function () {
	      this.trigger(ev);
	      return el.apply(this, arguments);
	    };
	  });
	})(jQuery);

function updateFocusedBudget(){
	var validInputs = true;
	var items = [];
	var amounts = [];
	var denoms = [];
	$('.editable-item.item-name:visible').each(function(){
		if (!isItemValid($(this).text())){
			$(this).addClass('invalid-form-input');
			alertWithFade('danger','Item name must not be empty');
			$('.saved-indicator').hide();
			$('.save-failed').show();
			$(this).text('_______');
			validInputs = false;
			return false;
		} else {
			$(this).removeClass('invalid-form-input');
		}
		items.push($(this).text());
	})
	$('.editable-item.item-amount:visible').each(function(){
		if ($(this).text() == '') {
			$(this).text('0.00');
		}
		if (!isCurrencyValid($(this).text())){
			$(this).addClass('invalid-form-input');
			alertWithFade('danger','Amount must be a valid number');
			$('.saved-indicator').hide();
			$('.save-failed').show();
			validInputs = false;
			return false;
		} else {
			$(this).text(parseInt($(this).text()).toFixed(2));
			$(this).removeClass('invalid-form-input');
		}
		amounts.push($(this).text());
	})
	$('.focused-budget-table .item-denom:visible').each(function(){
		denoms.push($(this).val());
	})
	updateBillCalculator();
	if (!validInputs){
		return;
	}
	
	var budgetName = $('.focused-budget-name').text();
	var budgetId = $('.focused-budget-id').val();
	updateRequest = $.ajax({
		url: contextpath +  'budget/edit',
		type: 'GET',
		data: {
			item_name: items,
			budgetId: budgetId,
			item_amount: amounts,
			item_max_denom: denoms,
			budgetName: budgetName
		},
		beforeSend: function(){
			if(updateRequest != null) {
				updateRequest.abort();
			}
			$('.saved-indicator').hide();
			$('.saved-indicator.saving').show();
		},
		success: function(response){
			$('.saved-indicator').hide();
			$('.saved-indicator.saved').show();
		},
		error: function(request, textStatus, errorThrown){
			if (request.statusText =='abort') {
				return;
			}
			alertWithFade('danger','Unable to save budget');
			$('.saved-indicator').hide();
			$('.save-failed').show();
		}
	});	
}
function updateBillCalculator(){
	var items = [];
	var maxDenoms = [];
	$('.editable-item.item-amount:visible').each(function(){
		items.push($(this).text());
	});
	$('.focused-budget-table .item-denom:visible').each(function(){
		maxDenoms.push($(this).val());
	});
	billMap = initializeBillMap();
	for (var i=0; i < items.length; i++){
		const itr = billMap[Symbol.iterator]();
		while(items[i] > 0){
			var entry = itr.next().value;
			while (entry[0] > maxDenoms[i]){
				entry = itr.next().value;
			}
			billMap.set(entry[0], entry[1] + parseInt(items[i] / entry[0]));
			items[i] = items[i] % entry[0];
		}
	}
	var total = 0;
	for (var [bill, count] of billMap){
		total = total + (bill * count);
		$('.bill-count.' + bill.toString()).html(count);
	}
	$('.total').html(numberWithCommas(total));
}

function initializeBillMap(){
	var billMap = new Map();
	billMap.set(100,0);
	billMap.set(50,0);
	billMap.set(20,0);
	billMap.set(10,0);
	billMap.set(5,0);
	billMap.set(1,0);
	return billMap;
}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function deleteBudget(id){
	$.ajax({
		url: contextpath +  'budget/disable/' + id,
		type: 'POST',
		success: function(response){
			$('#budgetPicker_' + id).closest('.budget-template-wrapper, .budget-wrapper').removeClass("d-inline-block");
			$('#budgetPicker_' + id).closest('.budget-template-wrapper, .budget-wrapper').hide();
			if (!$('#budgetPicker_' + id).closest('.budget-template-wrapper').length){
				location.reload();
			}
		},
		error: function(){
			alertWithFade('danger','Unable to delete template, try refreshing the page');
		}
	});	
}

function loadBudget(id){
	$('#focusedBudgetForm').load(contextpath + 'get-budget/' + id + '?view=dynamic%2Ffocused-budget', function(){
		$('#budgetPicker_' + id + ' > .budget-container').addClass('focused');
	});
}

function sortTable(tableSelector){
	$(tableSelector).sortable({
		items: 'tr',
		handle: '.sort-grip',
		cursor: 'grabbing',
		axis: 'y',
		delay: 100,
		update: function(){
			updateFocusedBudget();
		}
	});
}
function sortTemplate(){
	$('.template-table').sortable({
		items: 'tr',
		handle: 'td',
		cursor: 'grabbing',
		axis: 'y',
		delay: 100
	});
}

function openEditTemplateModal(id){
	$('.modal').modal('hide');
	$('#editTemplateForm').load(contextpath + 'get-budget/' + id + '?view=dynamic%2Ftemplate-form', function(){
		$('<input>').attr({
			type: 'hidden',
			name: 'redirect',
			value: '/#create-budget'
		}).appendTo('#editTemplateForm');
	});
	$('#editTemplateModal').modal('show');
}

function openNewBudgetModal(){
	$('#createBudgetModal').modal('show');
}

function openNewTemplateModal(){
	$('.modal').modal('hide');
	$('#createTemplateModal').modal('show');
}

function alertWithoutFade(alertType,msg){
	if (alertType == 'success'){
		alertType = 'info';
	}
	var dynamicElement = $('#custom-alert').clone();
	$(dynamicElement).find('.alert-message').html(msg);
	$(dynamicElement).addClass('alert-' + alertType);
	$(dynamicElement).appendTo($('.alert-container'));
}
function alertWithFade(alertType, msg){
	if (alertType == 'success'){
		alertType = 'info';
	}
	var dynamicElement = $('#custom-alert').clone();
	$(dynamicElement).find('.alert-message').html(msg);
	$(dynamicElement).addClass('alert-' + alertType);
	$(dynamicElement).hover(function(){
		$(this).data('mousein',true);
	}, function(){
		$(this).data('mousein',false);
		setAlertToFadeOut(this);
	});
	$(dynamicElement).appendTo($('.alert-container'));
	setAlertToFadeOut(dynamicElement);
}
function setAlertToFadeOut(alertToFade){
	setTimeout(function(){
		if ($(alertToFade).data('mousein') == false){
			$(alertToFade).alert('close');
		}
	}, 3000);
}

function downloadPdf(id){
	var win = window.open(contextpath + 'download/' + id + '/View%20PDF', '_blank');
	win.focus();
}

function emailBudget(id){
	$('#emailBudgetModal input.budget-id').val(id);
	$('#emailBudgetModal').modal('show');
}

$(document).ready(function(){

	if (location.href.includes('#create-template')){
		$('#createTemplateModal').modal('show');
	} else if (location.href.includes('#create-budget')){
		$('#createBudgetModal').modal('show');
	}

	$('#newBudgetMonth option[value="' + new Date().getMonth() +'"]').prop('selected', true);
	$(document).on('change','select.starting-template',function(){
		$('#newTemplateForm').load(contextpath + 'get-budget/' + $(this).val() + '?view=dynamic%2Ftemplate-form');
	});
	$(document).on('keydown','.template-form tbody tr:visible:last .currency',function(){
		$(this).closest('tr').find('input').addClass('required');
		$(this).closest('tr').find('.remove-row-container').show();
		$(this).closest('.template-form').find('tbody tr:last').after($('.partial-container .budget-row').clone());
	})
	$(document).on('submit','#newTemplateForm, #editTemplateForm',function(e){
		$(this).find('input[type="text"]').not('.required').prop('disabled',true);
		$(this).find('tr:hidden').remove();
		if (!isFormFilled(this)) {
			e.preventDefault();
		}
	});
	
	$(document).on('submit','#emailBudgetForm',function(e){
		e.preventDefault();
		if (isFormFilled(this)){
			var id = $(this).find('input.budget-id').val();
			$.ajax({
				url: contextpath +  'email/budget/' + id,
				type: 'POST',
				data: new FormData(this),
				processData: false,
				contentType: false,
				beforeSend: function(){
					$(this).find('btn.btn-info').prop('disabled',true);
				},
				success: function(response){
					alertWithFade('success','Email sent sucessfully');
					$('#emailBudgetModal').modal('hide');
				},
				error: function(){
					alertWithFade('danger','Unable to email budget');
				}, 
				complete: function(){
					$(this).find('btn.btn-info').prop('disabled',false);
				}
			});	
		}
	})

	$(document).on('hidden.bs.modal','.modal',function(){
		window.history.replaceState('home','home',window.location.href.split('#')[0]);
	});

	$(document).on('click','.edit-budget-icon',function(e){
		openEditTemplateModal($(this).closest('.budget-container').data('target'));
	});

	$(document).on('click','.remove-row',function(){
		deletedRow = $(this).closest('tr');
		$(deletedRow).hide();
		alertWithFade('warning','Budget item deleted (<a href="javascript:void(0)" class="text-white" onclick="$(deletedRow).show()">undo</a>)');
	});

	$(document).on('click','.create-from-template .delete-budget-icon',function(e){
		deleteBudget($(this).closest('.budget-container').data('target'));
	});

	$(document).on('click','.budget-picker .delete-budget-icon',function(e){
		var id = $(this).closest('.budget-container').data('target');
		if (id == $('.focused-budget-id').val()){
			loadBudget(id);
		}
		deleteBudget(id);
	});
	

	$(document).on('hide show','.focused-budget-table tr',function(){
			setTimeout(function(){updateFocusedBudget();},300);
	});

	$(document).on('click', '.new-budget-btn',function(){
		$('<form action="' + contextpath + 'create/budget" method="POST"><input type="hidden" name="month" value="' + $('#newBudgetMonth').val() +'"><input type="hidden" name="budgetDescription" value="' + $('#budgetDescription').val() + '"></form>').appendTo('body').submit();	
	});
	$(document).on('click', '.create-from-template .budget-container:not(.new-template)',function(e){
		if (!$(e.target).is('.delete-budget-icon,.edit-budget-icon,img')){
			$('<form action="' + contextpath + 'create/budget" method="POST"><input type="hidden" name="month" value="' + $('#newBudgetMonth').val() +'"><input type="hidden" name="template" value="' + $(this).data('target') +'"><input type="hidden" name="budgetDescription" value="' + $('#budgetDescription').val() + '"></form>').appendTo('body').submit();	
		}
	});
	$(document).on('click', '.budget-picker .budget-container',function(e){
		$('.budget-container').removeClass('focused');
		if (!$(e.target).is('.delete-budget-icon,img')){
			loadBudget($(this).data('target'));
		}
	});
	$(document).on('click','.editable-item',function(){
		var item = $(this).text().replace("_______","");
		if (parseInt(item)){
			item = parseInt(item);
		}
		var parent = $(this);
		$(this).html('');
		$('<input></input>')
			.attr({
				'type': 'text',
				'id': 'dynamicInput',
				'class': 'dynamic-input form-control'
			})
			.appendTo(parent);
		$('#dynamicInput').focus().val(item);
		dynamicParent = parent;
		$(dynamicParent).removeClass('editable-item');
		$('.focused-budget-table').sortable('disable');
	});
	
	$(document).on('blur','.new-row',function() {
		var newParent = $(this).parent();
		$(newParent).text($(this).val());
		$(newParent).addClass('editable-item');
		if (!$('.new-row:visible').length){
			updateFocusedBudget();
			$('.focused-budget-table').sortable('enable');
		}
	});

	$(document).on('change','.item-denom', function(){
		updateFocusedBudget();
	});

	$(document).on('blur','#dynamicInput', function(){
		$(dynamicParent).text($(this).val().trim());
		$(dynamicParent).addClass('editable-item');
		updateFocusedBudget();
		$('.focused-budget-table').sortable('enable');
	});
	$(document).on('click','.insert-row', function(){
		$('.focused-budget-table').find('tbody tr:last').after($('.new-focused-row.collapse').clone());
		$('.focused-budget-table .new-focused-row:not(:last)').removeClass('collapse');
		$('.focused-budget-table').sortable('disable');
	});
	$(document).on('keypress','#dynamicInput, .new-row',function(e){
		if (e.which == 13){
			$(this).blur();
		}
	})
});