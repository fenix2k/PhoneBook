$(document).ready( function () {

  initDataTable();
  enablePriorityChange();

  $(".delete-action").click(function (e) {
    if(confirm("Are you sure you want to due this?"))
      return true;
    else
      return false;
  });

  $(".button-update-employee-priority").click(function (e) {
    e.preventDefault();
    const href = $(this).attr('href');
    $.ajax({
      url: href,
      method: 'PUT',
    }).done(function(data, textStatus, jqXHR) {
      console.log(data);
      location.href = "";
    });
  });

} );

/** Инициализация и настройки DataTable */
function initDataTable() {
  const dataTable = $('#data-table').DataTable({
    columnDefs:
      [{
        targets: [0, -1, -2],
        searchable: false,
        orderable: false,
      }],
    scrollX: false,
    fixedColumns: false,
    //order: [[1, 'asc']]
  });
  dataTable
    .on( 'order.dt search.dt', function () {
      dataTable.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell, i) {
        cell.innerHTML = i+1;
      } );
    })
    .on( 'draw.dt', function () {})
    .draw();
}

/** Активация функционала изменения приоритета записей */
function enablePriorityChange() {
  let previousPriorityValues = new Map();

  /**
   * Handle button priority enable
   */
  $(".button-priority-enable").click(function (e) {
    e.preventDefault();
    $(this).addClass("d-none");
    $(".button-priority-save").removeClass("d-none");
    $(".button-priority-cancel").removeClass("d-none");

    $("#data-table .field-displayPriority").each(function (e) {
      let field = $(this);
      let value = field.text();
      let id = field.parent().children(".entity-id").text();
      previousPriorityValues.set(id, value);
    });

    $("#data-table .checkbox-column").each(function (e) {
      $(this).removeClass("d-none");
    });
  });

  /**
   * Handle checkbox click
   */
  $("#data-table .checkbox-column").click(function () {
    let elem = $(this);
    let field = elem.parent().children(".field-displayPriority");
    if(elem.children("input").prop('checked') === true) {
      field.html("<input type='text' value='" + field.text() + "' style='width: 6em;' />");
      $(".field-displayPriority input").focus(function () {
        $(this).select();
      }).select();
      $(".field-displayPriority input").keyup(function () {
        if(!$(this).val().match("^[0-9]+$"))
          $(this).val($(this).val().substring(0, $(this).val().length-1));
      });
    }
    else {
      let id = field.parent().children(".entity-id").text();
      field.html(previousPriorityValues.get(id));
    }
  });

  /**
   * Handle button click priority save
   */
  $(".button-priority-save").click(function (e) {
    e.preventDefault();
    let priorities = []
    const href = $(this).attr("href");

    const checkedElements = $("#data-table .checkbox-column input:checked");
    checkedElements.each(function (e) {
      const id = $(this).parent().siblings(".entity-id").text();
      const value = $(this).parent().siblings(".field-displayPriority").children("input").val();
      priorities.push({id: id, value: value});
      $(this).siblings(".field-displayPriority").html(value);
    });

    if(priorities.length === 0)
      return;

    $.ajax({
      url: href,
      method: 'GET',
      dataType: 'json',
      data: {
        priorities :JSON.stringify(priorities)
      },
    }).done(function(data, textStatus, jqXHR) {
      console.log(data);
      location.href = data.message;
    });

    checkedElements.each(function (e) {
      const element = $(this).parent().siblings(".field-displayPriority");
      element.html(element.children("input").val());
    });

    $("#data-table .checkbox-column").each(function () {
      $(this).children("input").prop("checked", false);
      $(this).addClass("d-none");
    });
    $(this).addClass("d-none");
    $(".button-priority-cancel").addClass("d-none");
    $(".button-priority-enable").removeClass("d-none");
  });

  /**
   * Handle button click priority save
   */
  $(".button-priority-cancel").click(function (e) {
    e.preventDefault();
    $(this).addClass("d-none");
    $(".button-priority-save").addClass("d-none");
    $(".button-priority-enable").removeClass("d-none");

    $("#data-table .checkbox-column").each(function () {
      const id = $(this).siblings(".entity-id").text();
      if(typeof id !== 'undefined' && id.length !== 0) {
        if(previousPriorityValues.has(id))
          $(this).siblings(".field-displayPriority").html(previousPriorityValues.get(id));
      }
      $(this).children("input").prop("checked", false);
      $(this).addClass("d-none");
    });
  });

}