$(document).ready(async function () {

    if (!localStorage.getItem("token")) {
        location.replace(window.location.origin + "/" + window.location.pathname.split('/')[1] + "/login");
    }

    let url = "/qr-code/" + $(location).attr("href").split('/').pop();

    console.log("stp")
    await $.ajax({
        type: "GET",
        url: url,
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token"),
            "Content-Type": "image/jpeg",
        },
        success: async function (response) {
            console.log(response)
            const blob = new Blob([response], {type: 'image/jpeg'});
            var url = URL.createObjectURL(blob);
            var a = document.createElement('a');
            a.href = url;
            a.download = 'image.jpg';
            document.body.appendChild(a);
            a.click();
        },
        error: function (response) {
            console.log(response)
        }
    });
    console.log("stp")
});