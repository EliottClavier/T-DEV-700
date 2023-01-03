$(document).ready(function () {
    refreshList("shop");
});

const createTr = (shop) => {
    let tr = $("<tr></tr>");
    tr.append(createTd(shop.name));
    tr.append(createTdSwitch("shop", shop.whitelisted, shop.id));
    tr.append(createTdButtonRemove("shop", shop.id));
    return tr;
}
