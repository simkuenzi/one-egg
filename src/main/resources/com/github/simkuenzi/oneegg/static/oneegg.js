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
                $("#reference-type").empty();
                switch (data.referenceType) {
                    case 'SCALAR':
                        $("#reference-type").append($("<option value='exactly'>exactly</option>"));
                    break;
                    case 'RANGE':
                        $("#reference-type").append($("<option value='at-least'>at least</option>"));
                        $("#reference-type").append($("<option value='at-most'>at most</option>"));
                    break;
                }
            });
        }
    });
}
