$(document).ready(function() {
    const url = `/bank/shop`;
    $.ajax({
        type: "GET",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': "Bearer " + localStorage.getItem("token")
        },
        url: url,
        contentType: "application/json; charset=utf-8",
        success: (response) => {
            let clients = response.data;
            let select = $("#client");
            clients.map((client) => {
                select.append(`<option value="${client.id}">${client.name}</option>`);
            });
        },
    });
});

$("#generate-qr-code").click(function() {
    let error = $("#error");
    error.hide();

    const amount = $("#amount").val();
    const client = $("#client").val();

    if (amount && client) {
        let url = "/qr-code/";

        $.ajax({
            type: "POST",
            url: url,
            dataType: "json",
            data: JSON.stringify({ amount: amount, clientId: client }),
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token")
            },
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                let result = response;
                window.open(
                    window.location.origin + "/" + window.location.pathname.split( '/' )[1] + "/qr-code/"
                    + result["uuid"],
                    '_blank'
                ).focus();
            },
            error: function (response) {
                error.text(response.responseText);
                error.show();
            }
        });
    } else {
        error.text("Please fill all fields.");
        error.show();
    }
});