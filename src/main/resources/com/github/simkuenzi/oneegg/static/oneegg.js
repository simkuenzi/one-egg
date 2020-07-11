function updateReferenceNames() {
    $.post("evalRef", $("#ingredients-in").val(), function(data) {
        $('#reference-name').empty();
        data.forEach(function(item, index) {
            $('#reference-name').append($("<option></option>").attr("value", index).text(item));
        });
    });
}
