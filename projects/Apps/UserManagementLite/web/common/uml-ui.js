// Standard javascript function to unselect all the options in an HTML select element

function UnselectOptions(id)
{
    for (var i = 0; i < document.getElementById(id).options.length; i++) {
        if (document.getElementById(id).options[i].selected) {
            document.getElementById(id).options[i].selected=false;
        }
    }
}