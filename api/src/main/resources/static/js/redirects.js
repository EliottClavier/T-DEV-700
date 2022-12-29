$("#redirect-generate-qr-code").click(function() {
    location.replace(window.location.origin + "/" + window.location.pathname.split( '/' )[1] + "/qr-code/generate");
});

$("#redirect-whitelist-tpe").click(function() {
    location.replace(window.location.origin + "/" + window.location.pathname.split( '/' )[1] + "/whitelist/tpe");
});

$("#redirect-whitelist-shop").click(function() {
    location.replace(window.location.origin + "/" + window.location.pathname.split( '/' )[1] + "/whitelist/shop");
});

$("#redirect-dashboard").click(function() {
    location.replace(window.location.origin + "/" + window.location.pathname.split( '/' )[1] + "/dashboard");
});