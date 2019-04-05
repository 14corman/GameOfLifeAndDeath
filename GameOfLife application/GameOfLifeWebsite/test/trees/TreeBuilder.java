/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trees;

import main.utilities.AnimatedGifEncoder;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.imageio.ImageIO;
import static trees.TreeBuilder.TEST_LOGGER;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PNGExporter;
import org.gephi.io.exporter.preview.SVGExporter;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.force.yifanHu.YifanHuProportional;
import org.gephi.layout.plugin.noverlap.NoverlapLayout;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.types.DependantColor;
import org.gephi.preview.types.EdgeColor;
import org.openide.util.Lookup;

/**
 *
 * @author Cory Edwards
 */
public class TreeBuilder {

    private final ProjectController pc;     //Analogus to an excel document
    private final Workspace workspace;      //Analogus to an excel sheet
    public final Tree tree;                //The random tree
    public final int min = 15;             //Minimum number of nodes in a tree. (Cannot be 0)
    public boolean grayScale = true;       //To set the nodes and edges colored or not.
    
    public static final Logger TEST_LOGGER = Logger.getLogger(TreeBuilder.class.getName());
    
    public TreeBuilder()
    {
        pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        workspace = pc.getCurrentWorkspace();
        tree = new Tree();
    }
    
    public static void setUpLogger()
    {
        try 
        {
            for(Handler h : TEST_LOGGER.getHandlers())
                TEST_LOGGER.removeHandler(h);
            
            Handler exception = new FileHandler("C:\\Server Files\\GameOfLifeWebsite\\testing\\exception.log");
            Handler info = new FileHandler("C:\\Server Files\\GameOfLifeWebsite\\testing\\info.log");
            
            exception.setEncoding("UTF-8");
            info.setEncoding("UTF-8");
            
            exception.setLevel(Level.SEVERE);
            info.setLevel(Level.FINE);
            TEST_LOGGER.setLevel(Level.ALL);
            
            Formatter formater = new SimpleFormatter();
            
            //Could be used to change the format of the string in the logs.
//            System.setProperty("java.util.logging.SimpleFormatter.format", "");
            exception.setFormatter(formater);
            info.setFormatter(formater);
            
            TEST_LOGGER.addHandler(exception);
            TEST_LOGGER.addHandler(info);
        }
        catch (IOException ex) 
        {
            TEST_LOGGER.log(Level.SEVERE, "Error setting log file", ex);
        }
        catch (SecurityException ex)
        {
            TEST_LOGGER.log(Level.SEVERE, "Permission denied while setting logger", ex);
        }
    }
    
    /**
     * Create a time line gif of every png file in either "test" folder or "test\gray" folder
     */
    public void createGif()
    {
        String grayName = grayScale ? "\\gray" : "";
        try(BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream("C:\\Server Files\\GameOfLifeWebsite\\testing" + grayName + "\\current.gif")))
        {
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            
            //setDelay(# of seconds * 1000)
            e.setDelay(2000);
            e.start(output);
            
            //Go over every png file in the folder to add to the gif to make a timeline.
            File[] files = new File("C:\\Server Files\\GameOfLifeWebsite\\testing" + grayName).listFiles();
            for(File file : files)
            {
                if(file.getName().contains("png"))
                {
                    BufferedImage nextImage = ImageIO.read(file);
                    e.addFrame(nextImage);
                }
            }
            e.finish();
        } 
        catch (IOException ex)
        {
            TEST_LOGGER.log(Level.SEVERE, "Could not create gif gile", ex);
        }
    }
    
    /**
     * Build a partially random tree with a set number of nodes to put in.
     * The nodes will be randomly put in, but will favor being close to the root.
     * @param numNodes The max number of nodes to add to the tree. 
     */
    public void buildSemiStructuredTree(int numNodes)
    {
        Random rand = new Random();
        boolean nodeAdded = false;  //Check every tier to see if a node was added.
        
        //Loop for the number of nodes to be added.
        while(tree.nodes.size() < numNodes)
        {
            //Get the max tier to loop between tier 0 - max
            int numTiers = tree.getMaxTier();
            for(int i = 0; i <= numTiers; i++)
            {
                //Get all the nodes in that tier
                List<TreeNode> nodes = tree.getNodesWithTier(i);
                for(TreeNode node : nodes)
                {
                    //Randomly decide whether to add a node or not.
                    if(rand.nextBoolean())
                    {
                        node.children.add(tree.addNode(node, "n" + tree.nodes.size()));
                        nodeAdded = true;
                        break;
                    }
                }
                
                //If a node was added, we do not need to go on to the next tier so break.
                if(nodeAdded)
                {
                    nodeAdded = false;
                    break;
                }
            }
        }
        TEST_LOGGER.log(Level.INFO, "Tree size = {0}", tree.nodes.size());
    }
    
    /**
     * Build a random tree with each node having a 50/50 shot of having children,
     * and a random number of children added to that node if it has them
     */
    public void buildRandomTree()
    {
        Random rand = new Random();
        
        //Add a random number of children to the root to get the graph started.
        int numChildren = rand.nextInt(10);
        for(int x = 0; x < numChildren; x++)
        {
            tree.root.children.add(tree.addNode(tree.root, "n" + tree.nodes.size()));
        }
        
        //Set the number of possible tiers between 2 and 11.
        int numLevels = rand.nextInt(10) + 2;
        for(int i = 2; i < numLevels; i++)
        {
            List<TreeNode> nodes = tree.getNodesWithTier(i - 1);
            for(TreeNode node : nodes)
            {
                //Randomly decide if the node will have children or not.
                if(rand.nextBoolean())
                {
                    //Let there be between 0 and 9 children.
                    numChildren = rand.nextInt(10);
                    for(int x = 0; x < numChildren; x++)
                    {
                        node.children.add(tree.addNode(node, "n" + tree.nodes.size()));
                    }
                }
            }
        }
        TEST_LOGGER.log(Level.INFO, "Tree size = {0}", tree.nodes.size());
    }
    
    /**
     * Layout the graph, set nodes' sizes, and set up the preview for exporting.
     * @param scaling The value used in force atlas 2 scale ratio.
     */
    public void buildGraph(double scaling)
    {
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel model = previewController.getModel();
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        
        TEST_LOGGER.info("Algorithms started");
        
        //We want a proportional Yifan Hu layout instead of a normal Yifan Hu layout because the proportional one 
        //because it spaces the outer and central nodes accordingly.
        YifanHuLayout yifan = new YifanHuProportional().buildLayout();
        yifan.resetPropertiesValues(); //Needs to be called to set the defaults for the algorithm
        
        //The natural length of the springs. Bigger values mean nodes will be farther apart.
        yifan.setOptimalDistance(100f);
        
        //Smaller vales mean more accuracy. DEFAULT: 0.0004
        yifan.setConvergenceThreshold(0.00001f);
        
        //The initial step size used in the integration phase. Set this allue to a meaningful size compared to the optimal distance. (10% is a good starting point)
        yifan.setInitialStep(20f);
        
        //The ratio used to update the step size aross iterations.
        yifan.setStepRatio(0.95f);
        
        //The relative strength between electrical force (repulsion) and spring force (attraction).
        yifan.setRelativeStrength(0.2f);
        
        //Add graph model.
        yifan.setGraphModel(graphModel);
        
        //Set up the algorithm.
        yifan.initAlgo();
        
        //Have the algorithm run until it converges.
        while(yifan.canAlgo())
        {
            yifan.goAlgo();
        }
        yifan.endAlgo();
        TEST_LOGGER.fine("Yifan done");
        
        //Set up an auto layout for force atlas 2 and noverlap to run for a set amount of time.
        AutoLayout autoLayout = new AutoLayout(3, TimeUnit.MINUTES);
        autoLayout.setGraphModel(graphModel);

        ForceAtlas2 force = new ForceAtlas2(null);
        force.resetPropertiesValues();
        
        //Default is 1 less than the possible number of cores.
        force.setThreadsCount(2);
        
        //The % size of the size of the node in the calculation as a whole number. The bigger the number, the more spacing there is between nodes.
        force.setScalingRatio(scaling);
        
        //Distributes attraction along outbound edges. Hubs attract less and thus are pushed to the borders.
        force.setOutboundAttractionDistribution(Boolean.TRUE);
        
        //Attracts nodes to the center. Prevents islands from drifting away.
        force.setGravity(0.5);
        
        //Swith ForceAtlas2 model from lin-lin to linlog. Makes clusters more tight.
        force.setLinLogMode(Boolean.TRUE);
        
        //Barnes Hut optimization: n^2 complexity to n.ln(n). Allows larger graphs, but may hurt smaller ones.
        force.setBarnesHutOptimize(Boolean.TRUE);
        
        //If true, anti collision will be turned on.
        force.setAdjustSizes(Boolean.FALSE);
        
        NoverlapLayout noOverlap = new NoverlapLayout(null);
        noOverlap.resetPropertiesValues();
        
        //The amount of extra space around the node.
        noOverlap.setMargin(10.0);
        
        //Ratio is the ratio of the node size with the margin.
        
        //The speed of the algorithm
        noOverlap.setSpeed(15.0);

        autoLayout.addLayout(force, .4f);
        autoLayout.addLayout(noOverlap, .6f);
        TEST_LOGGER.fine("Auto layout started");
        autoLayout.execute();
        TEST_LOGGER.fine("Auto layout ended");
        
        //Need to end these layouts or they keep a thread alive in the background since ForceAtlas2 cannot converge.
        force.endAlgo();
        noOverlap.endAlgo();
        TEST_LOGGER.info("Algorithms done");

        //Preview
        model.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.TRUE); //Set edges to be curved.
        model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 1.5f); //Set edge thickness to 3.
        model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(EdgeColor.Mode.ORIGINAL)); //Set the edge color to be the original color set for the edge.
        model.getProperties().putValue(PreviewProperty.NODE_BORDER_WIDTH, 1.5f); //Set node border witdth to 1.
        model.getProperties().putValue(PreviewProperty.NODE_OPACITY, 100f); //How solid the nodes are. 0 = invisible, 100 = solid.
        
        //Set the initial width and height of the canvas to 1 so we know the center is at (0.5, 0.5) to center the nodes.
        model.getProperties().putValue("width", 1);
        model.getProperties().putValue("height", 1);
        
        centerNodes(directedGraph);
//        setCanvasSize(directedGraph, previewController);
        setDynamicCanvasSize(directedGraph, previewController);
    }
    
    /**
     * Export the gexf, png, and svg file of the graph
     */
    public void exportGraph()
    {
        try
        {
            String grayName = grayScale ? "\\gray" : "";
            String filePath = "C:\\Server Files\\GameOfLifeWebsite\\testing" + grayName;
            
            // export gefx
            ExportController ec = Lookup.getDefault().lookup(ExportController.class);
            GraphExporter gexfExporter = (GraphExporter) ec.getExporter("gexf");
            gexfExporter.setWorkspace(workspace);
            ec.exportFile(new File(filePath + "\\current.gexf"), gexfExporter);
            
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HHmm");
            try(FileOutputStream out = new FileOutputStream(filePath + "\\" + sdf.format(new Date(System.currentTimeMillis())) + ".png"))
            {
                PNGExporter exporter = (PNGExporter) ec.getExporter("png");
                exporter.setWorkspace(workspace);
                exporter.setOutputStream(out);
                exporter.execute();
            }

            try(FileOutputStream fos = new FileOutputStream(new File(filePath + "\\" + sdf.format(new Date(System.currentTimeMillis())) + ".svg")))
            {
                try(Writer out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8")))
                {
                    SVGExporter exporter = (SVGExporter) ec.getExporter("svg");
                    exporter.setWorkspace(workspace);
                    exporter.setWriter(out);
                    exporter.execute();
                }
                fos.flush();
            }
        } 
        catch (IOException ex) 
        {
            TEST_LOGGER.log(Level.SEVERE, "Error exporting files. A file could not be saved.", ex);
        }
    }
    
    /**
     * Set the color scheme of the graph for either gray scale or normal
     */
    public void setColorScheme()
    {
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel model = previewController.getModel();
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        
        model.getProperties().putValue(PreviewProperty.NODE_BORDER_COLOR, grayScale ? new DependantColor(Color.DARK_GRAY) : new DependantColor(Color.WHITE));
        model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, grayScale ? Color.BLACK : Color.WHITE);
        model.getProperties().putValue(PreviewProperty.EDGE_OPACITY, grayScale ? 100f : 25f);  //Set the opacity of the edge to xxx%.
        
        for(TreeNode node : tree.nodes)
        {
            directedGraph.getNode(node.label).setColor(node.getColor(grayScale));
        }
        
        for(TreeNode parent : tree.nodes)
        {
            Node parentNode = directedGraph.getNode(parent.label);
            for(TreeNode child : parent.children)
            {
                Node childNode = directedGraph.getNode(child.label);
                directedGraph.getEdge(parentNode, childNode).setColor(child.getColor(grayScale));
            }
        }
    }
    
    public void addNode(GraphModel graphModel, TreeNode node)
    {
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        Random random = new Random();
        Node graphNode = graphModel.factory().newNode(node.label);
        graphNode.setLabel(node.label);
        graphNode.setAttribute("tier", node.tier);
        graphNode.setX(random.nextFloat()* 1000);
        graphNode.setY(random.nextFloat() * 1000);
        graphNode.setSize(5f);
        graphNode.setColor(node.getColor(grayScale));
        directedGraph.addNode(graphNode);
    }
    
    public void addEdge(GraphModel graphModel, TreeNode parent, TreeNode child)
    {
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        Edge edge = graphModel.factory().newEdge(getNode(graphModel, parent), getNode(graphModel, child));
        edge.setColor(child.getColor(grayScale));
        edge.setWeight(1.0);
        directedGraph.addEdge(edge); 
    }
    
    /**
     * Return the gephi node equivalent of the TreeNode given.
     * @param graphModel
     * @param node
     * @return 
     */
    public Node getNode(GraphModel graphModel, TreeNode node)
    {
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        return directedGraph.getNode(node.label);
    }
    
    public void centerNodes(DirectedGraph directedGraph)
    {
        Node[] nodes = directedGraph.getNodes().toArray();
        Node root = directedGraph.getNode(tree.root.label);
        
        //Find how far off the root node is from the center of the canvas.
        float centerX = -1 * (root.x() - 0.5f);
        float centerY = -1 * (root.y() - 0.5f);
        
        
        for(Node node : nodes)
        {
            float x = node.x();
            float y = node.y();
            node.setX(x + centerX);
            node.setY(y + centerY);
        }
    }
    
    /**
     * Set the width and height of the canvas to fit the nodes.
     * @param directedGraph
     * @param previewController 
     */
    public void setDynamicCanvasSize(DirectedGraph directedGraph, PreviewController previewController)
    {
        //Needs to be put to array because if you try and iterate over the 
        //NodeIterable it will have a Lock exception that too many locks will
        //be used at the same time.
        Node[] nodes = directedGraph.getNodes().toArray();
        PreviewModel model = previewController.getModel();
        
        float maxLeft = 0f;
        float maxRight = 0f;
        float maxTop = 0f;
        float maxBottom = 0f;
        
        for (Node n : nodes) 
        {
            float x = n.x();
            float y = n.y();
            float radius = n.size();
            float margin = 5.0f;
            float ratio = 1.2f;

            // Get the rectangle occupied by the node
            float left = x - (radius * ratio + margin);
            float right = x + (radius * ratio + margin);
            float top = y - (radius * ratio + margin);
            float bottom = y + (radius * ratio + margin);
            
            maxLeft = left < maxLeft ? left : maxLeft;
            maxRight = right > maxRight ? right : maxRight;
            maxTop = top < maxTop ? top : maxTop;
            maxBottom = bottom > maxBottom ? bottom : maxBottom;
        }
        
        float width = Math.abs(maxLeft) > maxRight ? Math.abs(maxLeft) * 2 : maxRight * 2;
        float height = Math.abs(maxTop) > maxBottom ? Math.abs(maxTop) * 2 : maxBottom * 2;
        model.getProperties().putValue("width", width);
        model.getProperties().putValue("height", height);
        
        TEST_LOGGER.log(Level.INFO, "New width: {0} New height: {1}", new Object[]{width, height});
    }
    
    /**
     * Set the width and height of the canvas to fit the nodes.
     * @param directedGraph
     * @param previewController 
     * @deprecated Use setDynamicCanvasSize
     */
    public void setCanvasSize(DirectedGraph directedGraph, PreviewController previewController)
    {
        boolean converged = false;
        int iteration = 0;
        
        //Needs to be put to array because if you try and iterate over the 
        //NodeIterable it will have a Lock exception that too many locks will
        //be used at the same time.
        Node[] nodes = directedGraph.getNodes().toArray();
        G2DTarget canvas = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET, workspace);
        PreviewModel model = previewController.getModel();
        
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        
        try
        {
            while(!converged)
            {
                iteration++;
                double cLeft = 0.0 - (width / 2.0);
                double cRight = 0.0 + (width / 2.0);
                double cTop = 0.0 - (height / 2.0);
                double cBottom = 0.0 + (height / 2.0);
                
                TEST_LOGGER.log(Level.FINEST, "Left: {0} Right: {1} Top: {2} Bottom: {3}", new Object[]{cLeft, cRight, cTop, cBottom});
                converged = true;

                for (Node n : nodes) 
                {
                    float x = n.x();
                    float y = n.y();
                    float radius = n.size();
                    float margin = 5.0f;
                    float ratio = 1.2f;

                    // Get the rectangle occupied by the node
                    float left = x - (radius * ratio + margin);
                    float right = x + (radius * ratio + margin);
                    float top = y - (radius * ratio + margin);
                    float bottom = y + (radius * ratio + margin);

                    //If node is NOT inside the canvas then adjust the margin and check again.
                    if(!(right < cRight && bottom < cBottom && left > cLeft && top > cTop))
                    {
                        width += 20;
                        height += 20;
                        converged = false;
                        break;
                    }
                }
            }
            model.getProperties().putValue("width", width);
            model.getProperties().putValue("height", height);
            TEST_LOGGER.log(Level.INFO, "New width: {0} New height: {1}", new Object[]{width, height});
        }
        catch(Exception ex)
        {
            TEST_LOGGER.log(Level.SEVERE, "Error while setting canvas size", ex);
        }
        
        TEST_LOGGER.log(Level.FINE, "Number of iterations for image: {0}", new Object[]{iteration});
    }
    
    /**
     * Size the nodes by tier given that the root (tier 0) is maximum, and the last tier is minimum.
     * @param minimumSize
     * @param maximumSize
     * @param graphModel 
     */
    public void setNodesSizes(int minimumSize,int maximumSize, GraphModel graphModel)
    {
        int minSize = 0;
        int maxSize = 0;
        
        //Get the highest and lowest tiers.
        for(TreeNode node : tree.nodes)
        {
            if(node.tier < minSize)
                minSize = node.tier;
            
            if(node.tier > maxSize)
                maxSize = node.tier;
        }
        
        //If negative then biggest node will be the root, and if possitive the smallest node will be the root.
        int numBins = maxSize - minSize;
        
        //The number of possible sizes there will be (IE number of bins). We need to add 1 because we include tier 0.
        float[] sizes = new float[numBins + 1];
        
        //Get the highest possble bin size.
        float binSize = Math.abs((float) (maximumSize - minimumSize) / numBins);
       
        TEST_LOGGER.log(Level.INFO, "Bin size = {0}", new Object[]{binSize});
        
        for(int i = 0; i < numBins + 1; i++)
        {
            //Determine the possible sizes by either having smallest first or biggest first.
            if(minimumSize < maximumSize)
                sizes[i] = minimumSize + ((i) * binSize);   //Smallest first (root smallest)
            else
                sizes[i] = minimumSize - ((i) * binSize);   //Biggest first (root largest)
            
            TEST_LOGGER.log(Level.FINE, "{0} labeled size is {1}", new Object[]{i, sizes[i]});
        }
        
        TEST_LOGGER.log(Level.INFO, "Start size is {0}", new Object[]{sizes[0]});
        TEST_LOGGER.log(Level.INFO, "End size is {0}", new Object[]{sizes[numBins]});
         
        //Give each node its new size based on its tier.
        for(TreeNode node : tree.nodes)
        {
            Node graphNode = getNode(graphModel, node);
            if(graphNode != null)
            {
                TEST_LOGGER.log(Level.FINEST, "Tier: {0} is {1}", new Object[]{node.tier, sizes[node.tier]});
                graphNode.setSize(sizes[node.tier]);
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        setUpLogger();
        
        TreeBuilder builder = new TreeBuilder();
        
        //Create a gif of all the pngs in "C:\\Server Files\\GameOfLifeWebsite\\testing" and the sub folder "gray"
        builder.createGif();
        builder.grayScale = !builder.grayScale;
        builder.createGif();
        builder.grayScale = !builder.grayScale;
        
        //Keep reseting and making the tree until its number of nodes exceeds min.
//        while(builder.tree.nodes.size() <= builder.min)
//        {
//            builder.tree.reset();
//            builder.buildRandomTree();
//        }

        //Build a semi structured tree.
        builder.buildSemiStructuredTree(1000);

        //Get the graph model for the workspace which will allow us to build and work with the graph.
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(builder.workspace);

        //We want the attribute "tier" inside the nodes, but if it is already
        //in the graph model then it will throw and error, so see if it is there first.
        if(!graphModel.getNodeTable().hasColumn("tier"))
        {
            graphModel.getNodeTable().addColumn("tier", Integer.class);
        }

        //Create the gephi nodes and add them to the graph.
        for(TreeNode node : builder.tree.nodes)
            builder.addNode(graphModel, node);

        //Create the edges for those nodes. The child needs to be non null which
        //is why this cannot go inside the previous for loop as we need to have
        //all nodes built before we work on the edges.
        for(TreeNode parent : builder.tree.nodes)
        {
            for(TreeNode child : parent.children)
                builder.addEdge(graphModel, parent, child);
        }

        //Set the minimum and maximum to be based on the number of nodes in the
        //graph and the number of tiers to try and get the largest bin sizes we can.
        int minimum = (builder.tree.nodes.size() % 5) + 1;
        int maximum = minimum * (builder.tree.getMaxTier() + 1);
        builder.setNodesSizes(maximum, minimum, graphModel);
        
        /*
        Build and layout the graph as well as set up the model to write it out.
        n = [11, 1000]
        scale = [2.0696, 100]
        Scale is the size ratio that will be used when running force atlas 2.
        */
//        float n = builder.tree.nodes.size() / 10f;
//        n = Math.max(n, 11f);
//        n = Math.min(n, 1000f);
//        double scale = Math.log10(n) * 50.0;
        double scale = 50;
        builder.buildGraph(scale);
        
        //Export the graph in both gray scale and normal for examples.
        builder.setColorScheme();
        builder.exportGraph();
        builder.grayScale = !builder.grayScale;
        builder.setColorScheme();
        builder.exportGraph();
    }
}

class Tree 
{
    public TreeNode root;
    public List<TreeNode> nodes;
    
    public Tree()
    {
        reset();
    }
    
    public int getMaxTier()
    {
        int tier = 0;
        
        for(TreeNode node : nodes)
        {
            if(node.tier > tier)
                tier = node.tier;
        }
        
        return tier;
    }
    
    public TreeNode addNode(TreeNode parent, String label)
    {
        TreeNode node = new TreeNode(label, parent.tier + 1);
        nodes.add(node);
        return node;
    }
    
    public final void reset()
    {
        root = new TreeNode("n" + 0, 0);
        nodes = new ArrayList();
        nodes.add(root);
    }
    
    /**
     * Get a list of all the nodes that are in the given tier
     * @param tier The tier to get all the nodes.
     * @return A list of nodes
     */
    public List<TreeNode> getNodesWithTier(int tier)
    {
        List<TreeNode> nodeList = new ArrayList();
        for(TreeNode node : nodes)
        {
            if(node.tier == tier)
                nodeList.add(node);
        }
        return nodeList;
    }
}

class TreeNode
{
    //Label and ID
    public String label;
    public int tier;
    public List<TreeNode> children;
    
    public TreeNode(String label, int tier)
    {
        this.label = label;
        this.tier = tier;
        children = new ArrayList();
    }
    
    /**
     * Determine the color to give the node based on its tier.
     * @param grayScale If true then the color will be a shade of gray.
     * @return The Color of the node.
     */
    public Color getColor(boolean grayScale)
    {
        TEST_LOGGER.log(Level.FINEST, "Tier: {0} color is {1}", new Object[]{tier, (tier % 8)});
        switch(tier % 8)
        {
            case 0:
                return grayScale ? new Color(15, 15, 15) : new Color(255, 0, 0);
            case 1:
                return grayScale ? new Color(36, 36, 36) : new Color(0, 255, 0);
            case 2:
                return grayScale ? new Color(72, 72, 72) : new Color(0, 0, 255);
            case 3:
                return grayScale ? new Color(108, 108, 108) : new Color(150, 0, 255);
            case 4:
                return grayScale ? new Color(144, 144, 144) : new Color(255, 150, 0);
            case 5:
                return grayScale ? new Color(180, 180, 180) : new Color(0, 255, 255);
            case 6:
                return grayScale ? new Color(216, 216, 216) : new Color(221, 221, 0);
            case 7:
                return grayScale ? new Color(252, 252, 252) : new Color(0, 0, 0);
            default:
                return null;
        }
    }
}
