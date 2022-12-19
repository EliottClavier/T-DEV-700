function sendJSON(){
    const amount = document.querySelector('#soldAmount');
    const days = document.querySelector('#nbDayBeforeExpiration');
    let result;

    const QrCode = [{soldAmount: amount, nbDayOfValidity: days}];
    let url = "/api/payment/qr-code/";

    $.ajax({
        type: "POST",
        url: url,
        dataType: "json",
        data: JSON.stringify({ QrCode }),
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            result = response;
        },
    })

    amount.value = 10;
    days.value = 10;

    return result;
}