$(document).ready(function() {
    if (!localStorage.getItem("token")) {
        location.replace(window.location.origin + "/" + window.location.pathname.split( '/' )[1] + "/login");
    }
});