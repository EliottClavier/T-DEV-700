function getUser(){

    const login = document.querySelector('#login');
    const password = document.querySelector('#password');
    let result;

    const User = [{loginAdmin: login, passwordAdmin: password}];
    let url = "/api/admin/auth/login/test";

    $.ajax({
        type: "POST",
        url: url,
        dataType: "json",
        data: JSON.stringify({ User }),
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            result = response;
        },
    })
    return result;
}