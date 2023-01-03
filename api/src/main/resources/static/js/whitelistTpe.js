$(document).ready(function () {
    refreshList("tpe");
});

const createTr = (tpe) => {
    let tr = $("<tr></tr>");
    tr.append(createTd(tpe.androidId));
    tr.append(createTdSwitch("tpe", tpe.whitelisted, tpe.id));
    tr.append(createTdButtonRemove("tpe", tpe.id));
    return tr;
}
