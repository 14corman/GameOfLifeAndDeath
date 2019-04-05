function start()
{
    var holder = document.getElementById("holder");

    for(var i = 0; i < 100;)
    {
        var p = document.createElement("p");
        for(var j = 0; j < 100;)
        {
            var div = document.createElement("div");
            div.className = "cell";
            div.id = i.toString() + "," + j.toString();
            p.appendChild(div);
            j++;
        }
        holder.appendChild(p);
        i++;
    }

    $.get('/GameOfLife/simulation?setup=true', function (data) {
        window.setInterval(function () { getGrid(); }, 1000);
        showGrid(data);
    }, 'json');
}

var toggled = true;
function toggle()
{
    toggled = !toggled;
    
    var toggleString = "solid";
    
    if(!toggled)
        toggleString = "none";
    
    for (var i = 0; i < 100;)
    {
        for (var j = 0; j < 100;)
        {
            document.getElementById(i.toString() + "," + j.toString()).style.borderStyle = toggleString;

            j++;
        }
        i++;
    }
    
}

function showGrid(grid)
{
    for (var i = 0; i < 100;)
    {
        for (var j = 0; j < 100;)
        {
            if (grid["grid"][i][j] === 1)
                document.getElementById(i.toString() + "," + j.toString()).style.backgroundColor = "red";
            else
                document.getElementById(i.toString() + "," + j.toString()).style.backgroundColor = "transparent";

            j++;
        }
        i++;
    }
}

function getGrid() {
    $.get('/GameOfLife/simulation', function (data) {
        showGrid(data);
    }, 'json');
}