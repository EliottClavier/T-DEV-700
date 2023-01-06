$(document).ready(function() {
    if (localStorage.getItem("token")) {
        location.replace(window.location.origin + "/" + window.location.pathname.split( '/' )[1] + "/dashboard");
    }
});

$("#submit").click(function() {
    const username = $("#username").val();
    const password = $("#password").val();

    let url = "/auth/manager/login";

    return $.ajax({
        type: "POST",
        url: url,
        dataType: "json",
        data: JSON.stringify({ username: username, password: password }),
        contentType: "application/json; charset=utf-8",
        success: function async(response) {
            localStorage.setItem("token", response["token"]);
            location.replace(window.location.origin + "/" + window.location.pathname.split( '/' )[1] + "/dashboard");
        },
    });
});