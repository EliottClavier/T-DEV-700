function sendJSON(){
    const amount = document.querySelector('#soldAmount');
    const days = document.querySelector('#nbDayBeforeExpiration');
    const result = document.getElementById("result");

    // Creating a XHR object
    let xhr = new XMLHttpRequest();
    let url = "/api/payment/qr-code/";

    // open a connection
    xhr.open("POST", url, true);

    // Set the request header i.e. which type of content you are sending
    xhr.setRequestHeader("Content-Type", "application/json");

    // Create a state change callback
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Print received data from server
            console.log("Everything is Okay !");
            result.classList.add("view");
        }
    };

    // Converting JSON data to string
    const data = JSON.stringify({"soldAmount": amount.value, "nbDayOfValidity": days.value});

    // Sending data with the request
    xhr.send(data);

    amount.value = 10;
    days.value = 10;
}