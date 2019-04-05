/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var isDragging = false;
var startingPos = [];

var s;

function start()
{
     // Add a method to the graph model that returns an
    // object with every neighbors of a node inside:
    sigma.classes.graph.addMethod('neighbors', function(nodeId) {
      var k,
          neighbors = {},
          index = this.allNeighborsIndex[nodeId] || {};

      for (k in index)
        neighbors[k] = this.nodesIndex[k];

      return neighbors;
    });

    sigma.classes.graph.addMethod('bringNodeToFront', function(id) {
        var data = this.nodesArray;

        for(var index in data)
        {
            if(data[index].id === id)
            {
                var node = data[index];
                data.splice(index, 1);
                data.push(node);
            }
        }
        this.nodesArray = data;
    });

    sigma.classes.graph.addMethod('bringEdgeToFront', function(id) {
        var data = this.edgesArray;

        for(var index in data)
        {
            if(data[index].id === id)
            {
                var edge = data[index];
                data.splice(index, 1);
                data.push(edge);
            }
        }
        this.edgesArray = data;
    });

    /**
     * This is a basic example on how to instantiate sigma. A random graph is
     * generated and stored in the "graph" variable, and then sigma is instantiated
     * directly with the graph.
     *
     * The simple instance of sigma is enough to make it render the graph on the on
     * the screen, since the graph is given directly to the constructor.
     */

    // Instantiate sigma:
    s = new sigma({
      renderer: {
        type: 'canvas',
        container: document.getElementById('graph-container')
      },
      settings: {
          defaultEdgeType: "arrow",
          minArrowSize: 2,
          defaultLabelColor: "#9e9e9e",
          labelHoverColor: "#9e9e9e",
          defaultLabelHoverColor: "#9e9e9e",
          defaultHoverLabelBGColor: "#000000",
          labelThreshold: 100000000,
          zoomMin: 0.001,
          zoomMax: 10
      }
    });
    
    sigma.parsers.gexf(
        'current.gexf',
        s,
        function() 
        {
          s.refresh();
        }
    );
    
    var i, g = s.graph;

    // Generate a random graph:
    for (i = 0; i < g.nodes.length; i++)
    {
//                g.nodes[i]["size"] = g.nodes[i]["score"];

        if(g.nodes[i]["id"].startsWith("n"))
            g.nodes[i]["color"] = "#9e9e9e";
        else
            g.nodes[i]["color"] = "#474747";
    }

    for (i = 0; i < g.edges.length; i++)
    {
        g.edges[i]["color"] = "#565656";
        g.edges[i]["size"] = 2;
    }

    // We first need to save the original colors of our
    // nodes and edges, like this:
    s.graph.nodes().forEach(function(n) {
        n.originalColor = n.color;

        var number = 0;
        s.graph.edges().forEach(function(e) {
            if(e.source === n.id)
                number++;
        });
        n.numLinks = number;
    });

    s.graph.edges().forEach(function(e) {
      e.originalColor = e.color;
    });

    // When a node is clicked, we check for each node
    // if it is a neighbor of the clicked one. If not,
    // we set its color as grey, and else, it takes its
    // original color.
    // We do the same for the edges, and we only keep
    // edges that have both extremities colored.
    var nodeClickFunction = function(e) {
        if(!isDragging)
        {
            var nodeId = e.data.node.id,
                toKeep = s.graph.neighbors(nodeId);
            toKeep[nodeId] = e.data.node;

            s.graph.nodes().forEach(function(n) {
              if (toKeep[n.id])
              {
                  s.graph.bringNodeToFront(n.id);
                  n.color = n.originalColor;
              }
              else
              {
                  if(toggled)
                      n.color = '#000000';
                  else
                      n.color = '#ffffff';
              }
            });

            s.graph.edges().forEach(function(e) {
              if (toKeep[e.source] && toKeep[e.target])
              {
                  e.color = e.originalColor;
                  s.graph.bringEdgeToFront(e.id);
              }
              else
              {
                if(toggled)
                    e.color = '#000000';
                else
                    e.color = '#ffffff';
              }
            });

            // Since the data has been modified, we need to
            // call the refresh method to make the colors
            // update effective.
            s.refresh();
        }
    };

    s.bind('clickNode', nodeClickFunction);

    // When the stage is clicked, we just color each
    // node and edge with its original color.
    s.bind('clickStage', function(e) {
        if(!isDragging)
        {
            s.graph.nodes().forEach(function(n) {
              n.color = n.originalColor;
            });

            s.graph.edges().forEach(function(e) {
              e.color = e.originalColor;
            });

            // Same as in the previous event:
            s.refresh();
        }
    });

    s.renderers[0].bind("overNode", function(event){
        var node = event.data.node;

        $("#nodeId")[0].innerHTML = "Id: " + node.id;
        $("#nodeProbability")[0].innerHTML = "Probability: " + node.probability;
        $("#nodeN")[0].innerHTML = "N: " + node.n;
        $("#nodeK")[0].innerHTML = "K:" + node.k;
        $("#nodeScore")[0].innerHTML = "Score: " + node.score;
    });

    s.graph.nodes().forEach(function(n) {
            n.size = n.numLinks;
    });

    $(".sigma-labels, .sigma-mouse, .sigma-scene")
    .mousedown(function (evt) {
        isDragging = false;
        startingPos = [evt.pageX, evt.pageY];
    })
    .mousemove(function (evt) {
        if (Math.abs(startingPos[0] - evt.pageX) > 2 || Math.abs(startingPos[0] - evt.pageX) > 2) {
            isDragging = true;
        }
    })
    .click(function () {
        startingPos = [];
    });
}