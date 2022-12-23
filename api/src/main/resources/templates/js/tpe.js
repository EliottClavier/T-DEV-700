function refreshTpeList() {
    /* fetch('http://localhost:8080/api/bank/tpe', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYW5hZ2VyIENvbm5lY3Rpb24iLCJpZGVudGlmaWVyIjoiYWRtaW4iLCJpc3MiOiJDYXNoIE1hbmFnZXIiLCJpYXQiOjE2NzE4MjAyODd9.v9EjfbOsIE3Fu973xDbiFpFh8HNor75FOFIzr0Fy-vk'
    }
  })
    .then(response => response.json())
    .then(data => {
      // Do something with the data
      console.log(data);
    })
    .catch(error => {
      // Handle any errors
        console.log(error);
    }); */

    var list = [{id: 1, name: "TPE 1"}, {id: 2, name: "TPE 2"}, {id: 3, name: "TPE 3"}];
    updateList(list);
    console.log("List refreshed");
}

function updateList(list) {
    var table = document.getElementById("list");
    var i;
    
    while (table.hasChildNodes()) {
        table.removeChild(table.firstChild);
    }
    
    for (i = 0; i < list.length; i++) {
        // Create a list item element
        var listItem = document.createElement("tr");

        var itemId = document.createElement("td");
        itemId.innerHTML = list[i].id;

        var itemName = document.createElement("td");
        itemName.innerHTML = list[i].name;

        var itemWhitelist = document.createElement("td");
        // Create a button element
        var button = document.createElement("button");
        button.innerHTML = "Button";
        button.id = list[i].id;
        itemWhitelist.appendChild(button);


        // Append the button to the list item
        listItem.appendChild(itemId);
        listItem.appendChild(itemName);
        listItem.appendChild(itemWhitelist);

        // Append the list item to the list
        table.appendChild(listItem);

        removeTpe(list[i].id);
    }
}

function removeTpe(id) {
    var removeButton = document.getElementById(id);

    removeButton.addEventListener("click", function () {
        console.log("TPE " + id + " removed");
    });
}
