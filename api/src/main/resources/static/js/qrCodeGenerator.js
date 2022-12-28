$("#generate-qr-code").click(function() {
    const amount = $("#soldAmount").val();
    const days = $("#nbDayBeforeExpiration").val();
    let result;

    let url = "/qr-code/";

    $.ajax({
        type: "POST",
        url: url,
        dataType: "json",
        data: JSON.stringify({ soldAmount: amount, nbDayOfValidity: days }),
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            result = response;
            console.log(result)
            window.open(
                window.location.origin + "/" + window.location.pathname.split( '/' )[1] + "/qr-code/"
                + result["uuid"],
            '_blank'
            ).focus();
        },
    });
});