$(document).ready(function () {

    if (!localStorage.getItem("token")) {
        location.replace(window.location.origin + "/" + window.location.pathname.split('/')[1] + "/login");
    }

    let qrCodeName = $(location).attr("href").split('/').pop();

    let url = "/qr-code/" + qrCodeName;

    $.ajax({
        type: "GET",
        url: url,
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token"),
        },
        contentType: "image/jpeg",
        cache:false,
        xhrFields:{
            responseType: 'blob'
        },
        success: function (response) {
            const blob = new Blob([response], {type: 'image/jpeg'});
            let url = URL.createObjectURL(blob);

            $("#qr-code").attr("src", url);

            let a = document.createElement('a');
            a.href = url;
            a.download = `${qrCodeName}.jpg`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
        },
        error: function (response) {
            location.replace(window.location.origin + "/" + window.location.pathname.split('/')[1] + "/qr-code/generate");
        }
    });
});