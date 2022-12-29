$("#logout").click(function() {
    localStorage.removeItem("token");
    location.replace(window.location.origin + "/" + window.location.pathname.split( '/' )[1] + "/login");
});