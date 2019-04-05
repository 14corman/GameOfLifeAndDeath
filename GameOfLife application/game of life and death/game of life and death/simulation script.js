function setUpGrid()
{
    var table = document.getElementById("grid");
    for(var i = 0; i < 100;)
    {
        var row = table.insertRow(table.rows.length);
        for(var j = 0; j < 100;)
        {
            var cell = row.insertCell(j);
            cell.className = "cell";
            cell.innerHTML = "&nbsp;";
            j++;
        }
        i++;
    }

    $.get('/GameOfLifeWebsite/simulation?setup=true', function (data) {
        window.setInterval(function () { getGrid() }, 1000);
        showGrid(data);
    }, 'json');
}

function showGrid(grid)
{
    console.log(grid);

    var table = document.getElementById("grid");
    for (var i = 0; i < table.rows.length; i++)
    {
        var row = table.rows[i];
        for (var j = 0; j < row.cells.length; j++)
        {
            var cell = row.cells[j];
            if(grid["grid"][i][j] === 1)
                cell.style.backgroundColor = "red";
            else
                cell.style.backgroundColor = "transparent";
        }
    }
}

function getGrid()
{
    $.get('/GameOfLifeWebsite/simulation', function (data) {
        showGrid(data);
    }, 'json');
}