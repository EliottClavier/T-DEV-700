$(document).ready(function () {
    refreshTpeList();
});

const refreshTpeList = () => {
    const url = "/bank/tpe";
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

const changeWhitelistStatus = (id) => {
    console.log(id)
    const status = $("td#"+ id).find("input").prop("checked");
    const url = `/bank/tpe/${id}/${status ? "whitelist" : "blacklist"}`;
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

const removeTpe = (id) => {
    const url = `/bank/tpe/${id}`;
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

const createTdButtonRemove = (id) => {
    let button = $("<td class='text-center'><button type='button' class='btn btn-danger'>x</button></td>")
    button.click(() => removeTpe(id));
    return button;
}

const createTdSwitch = (status, id) => {
    let td = $("<td class='align-middle text-center' id='"+ id + "'></td>");
    let div = $("<div class='form-check-inline'></div>");
    let input = $("<input type='checkbox' class='form-check-input' />");
    input.prop("checked", status);
    input.on("change", () => changeWhitelistStatus(id));
    div.append(input);
    td.append(div);
    return td;
}

const createTpeTr = (tpe) => {
    let tr = $("<tr></tr>");
    tr.append(createTd(tpe.androidId));
    tr.append(createTdSwitch(tpe.whitelisted, tpe.id));
    tr.append(createTdButtonRemove(tpe.id));
    return tr;
}

const updateList = (list) => {
    let table = $("#list");
    $("#list > tr").remove();
    list.map((tpe) => table.append(createTpeTr(tpe)))
}
