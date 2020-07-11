function updateReferenceNames() {
    initSelects("", "");
}

function updateReferenceTypes() {
    initReferenceTypes("");
}

function initSelects(referenceType, referenceName) {
    $.post("evalRef", $("#ingredients-in").val(), function(data) {
        var selected = referenceName != "" ? referenceName : $("#reference-name").val();
        var hasSelection = false;
        $('#reference-name').empty();
        data.forEach(function(item, index) {
            if (item.trim() != "") {
                var option = $("<option></option>").attr("value", item).text(item);
                if (item == selected) {
                    option = option.attr("selected", true);
                    hasSelection = true;
                }
                $('#reference-name').append(option);
            }
        });

        if (!hasSelection && $("#reference-name option").length > 0) {
            $.post("evalDef", $("#ingredients-in").val(), function(data) {
                $("#reference-name option[value='" + data + "']").attr("selected", true);
                initReferenceTypes(referenceType);
            });
        } else {
            initReferenceTypes(referenceType);
        }
    });
 }

function initReferenceTypes(referenceType) {
    $.post("ingredient/" + encodeURIComponent($("#reference-name").val()) + "/evalType", $("#ingredients-in").val(), function(data) {
        $("#reference-type").empty();
        switch (data) {
            case 'SCALAR':
                $("#reference-type").append($("<option value='EXACT'>exactly</option>"));
            break;
            case 'RANGE':
                $("#reference-type").append($("<option value='AT_LEAST'>at least</option>"));
                $("#reference-type").append($("<option value='AT_MOST'>at most</option>"));
            break;
        }

        if (referenceType != "") {
           $("#reference-type").val(referenceType);
        }
    });
}