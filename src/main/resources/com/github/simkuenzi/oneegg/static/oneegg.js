function updateReferenceNames() {
    $.post("evalRef", $("#ingredients-in").val(), function(data) {
        var selected = $("#reference-name option:selected").text();
        var hasSelection = false;
        $('#reference-name').empty();
        data.forEach(function(item, index) {
            if (item.trim() != "") {
                var option = $("<option></option>").attr("value", index).text(item);
                if (item == selected) {
                    option = option.attr("selected", true);
                    hasSelection = true;
                }
                $('#reference-name').append(option);
            }
        });

        if (!hasSelection && $("#reference-name option").length > 0) {
            $.post("evalDef", $("#ingredients-in").val(), function(data) {
                $("#reference-name option:contains('" + data.productName + "')").attr("selected", true);
            });
        }
    });
}
