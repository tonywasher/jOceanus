const closedHdr = '⯈';
const openHdr = '⯆';
const prefix = 'id'
function toggleHidden(pId) {
    var myBase = pId.substring(prefix.length)
    var myChild = document.getElementById("table" + myBase);
    if (myChild !== null) {
        myChild.classList.toggle("tblHidden");
    }
    var myHdr = document.getElementById("hdr" + myBase);
    if (myHdr !== null) {
        if (myHdr.textContent === openHdr) {
            myHdr.textContent = closedHdr;
        } else {
            myHdr.textContent = openHdr;
        }
    }
}
document.addEventListener("click", (event) => {
    if (event.target.classList.contains("accordianValue")) {
        toggleHidden(event.target.id);
    }
});
