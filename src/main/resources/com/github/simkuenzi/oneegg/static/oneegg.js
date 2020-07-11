function updateReferenceNames() {
    $.post("evalRef", $("#ingredients-in").val(), function(data) {
        var selected = $("#reference-name option:selected").text();
        $('#reference-name').empty();
        data.forEach(function(item, index) {
            var option = $("<option></option>").attr("value", index).text(item);
            if (item == selected) {
                option = option.attr("selected", true);
            }
            $('#reference-name').append(option);
        });
    });
}
