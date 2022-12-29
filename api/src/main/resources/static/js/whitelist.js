const refreshList = (entity) => {
    const url = `/bank/${entity}`;
    $.ajax({
      type: "GET",
      headers: {
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + localStorage.getItem("token")
      },
      url: url,
      contentType: "application/json; charset=utf-8",
      success: (response) => {
          updateList(response.data);
      },
    });
}

const changeWhitelistStatus = (entity, id) => {
    const status = $("td#"+ id).find("input").prop("checked");
    const url = `/bank/${entity}/${id}/${status ? "whitelist" : "blacklist"}`;
    $.ajax({
        type: "PUT",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': "Bearer " + localStorage.getItem("token")
        },
        url: url,
        contentType: "application/json; charset=utf-8",
        success: (response) => {
            console.log(response)
        },
    });
}

const removeEntity = (entity, id) => {
    const url = `/bank/${entity}/${id}`;
    $.ajax({
        type: "DELETE",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': "Bearer " + localStorage.getItem("token")
        },
        url: url,
        contentType: "application/json; charset=utf-8",
        success: () => {
            $("td#" + id).closest("tr").remove();
        },
    });
}

const createTd = (data) => {
    let td = $("<td class='text-center'></td>");
    td.text(data);
    return td;
}

const createTdButtonRemove = (entity, id) => {
    let button = $("<td class='text-center'><button type='button' class='btn btn-danger'>x</button></td>")
    button.click(() => removeEntity(entity, id));
    return button;
}

const createTdSwitch = (entity, status, id) => {
    let td = $("<td class='align-middle text-center' id='"+ id + "'></td>");
    let div = $("<div class='form-check-inline'></div>");
    let input = $("<input type='checkbox' class='form-check-input' />");
    input.prop("checked", status);
    input.on("change", () => changeWhitelistStatus(entity, id));
    div.append(input);
    td.append(div);
    return td;
}

const updateList = (list) => {
    let table = $("#list");
    $("#list > tr").remove();
    list.map((data) => table.append(createTr(data)));
}
