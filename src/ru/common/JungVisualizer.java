package ru.common;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import org.apache.commons.collections15.Transformer;
import ru.network.field.RandomAir;
import ru.network.field.UniformAir;
import ru.network.wireless.sensor.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;

/**
 * User: Yakov
 * Date: 28.02.2010
 * Time: 12:54:00
 */
public class JungVisualizer {

    private JFrame jFrame;

    private StaticLayout<Node, String> layout;

    public JungVisualizer(String index) {

        jFrame = new JFrame("myGraph " + index);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        JButton leftButton = new JButton("Uniform");
        leftButton.setPreferredSize(new Dimension(50, 30));
        leftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                  draw(new NetworkBuilder().generate(new AlgorithmDAAM(), new UniformAir())); //todo: add parameter algo
            }
        });

        jFrame.add(leftButton, BorderLayout.PAGE_START);

        JButton rightButton = new JButton("Random");
        rightButton.setPreferredSize(new Dimension(50, 30));
        rightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                draw(new NetworkBuilder().generate(new AlgorithmDAAM(), new RandomAir())); //todo: add parameter algo
            }
        });
        jFrame.add(rightButton, BorderLayout.PAGE_END);

        layout = new StaticLayout<Node, String>(new DirectedSparseGraph<Node, String>(), new Transformer<Node, Point2D>() {
            public Point2D transform(Node node) {
                return new Point2D.Double(node.getX(), node.getY());
            }
        });

        final VisualizationViewer<Node, String> bvs = new VisualizationViewer<Node, String>(layout);
        setColorsAndShapes(bvs);
        HashMap<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>();
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        bvs.setRenderingHints(hints);

        final DefaultModalGraphMouse<Integer, Number> graphMouse = new DefaultModalGraphMouse<Integer, Number>();
        bvs.setGraphMouse(graphMouse);

        jFrame.add(bvs, BorderLayout.CENTER);
        jFrame.pack();
        jFrame.setVisible(true);

    }

    public void draw(final Network network) {
        layout.setGraph(createGraph(network));
        jFrame.repaint();
    }


    /**
     *
     * @param network
     * @return
     *
     * рисуем граф
     */
    private static DirectedSparseGraph<Node, String> createGraph(Network network) {
        DirectedSparseGraph<Node, String> myGraph = new DirectedSparseGraph<Node, String>();
        for (Node node : network.getAIR()) {
            myGraph.addVertex(node);
            for (RoutingItem routingItem : node.getRoutingItems()) {
                String edgeString = node.getAddress() + "----" + routingItem.getChild().getAddress();
                if (routingItem.isLended()) {
                    edgeString += routingItem.getBorrower().getAddress() + ".lended";
                }
                myGraph.addEdge(edgeString, node, routingItem.getChild());
            }
        }
        return myGraph;
    }

    /**
     * @param bvs
     * рисуем узлы
     */
    private static void setColorsAndShapes(VisualizationViewer<Node, String> bvs) {

        RenderContext<Node, String> context = bvs.getRenderContext();

        context.setVertexShapeTransformer(new AbstractVertexShapeTransformer<Node>() {
            public Shape transform(Node node) {
                if (node.isStartingNode()) {
                    return new Ellipse2D.Float(-7, -7, 14, 14);
                }else if (node.hasBorrowedAddress()){
                    return new Rectangle(-3, -3, 6, 6);
                }
                return new Ellipse2D.Float(-3, -3, 6, 6);
            }
        });

        //рисуем кружок
        //если он в сети, то у него окружность черная, если нет, то по типу узла
        context.setVertexDrawPaintTransformer(new Transformer<Node, Paint>() {
            public Paint transform(Node node) {
                if (!node.isConnected()) {
                    if (node.isRouterCapable()) {
                        return Color.RED;
                    } else {
                        return Color.GREEN;
                    }
                }
                return Color.BLACK;
            }
        });

        //заливаем кружок в зависимости от его типа и степени подключенности
        //если он в сети, то заливаем в зависимости от типа
        //если не в сети, то заливаем белым
        context.setVertexFillPaintTransformer(new Transformer<Node, Paint>() {
            public Paint transform(Node node) {
                if (node.isConnected()) {
                    if (node.isRouterCapable()) {
                        return Color.RED;
                    } else {
                        return Color.GREEN;
                    }
                } else {
                    return Color.WHITE;
                }
            }
        });

        context.setVertexLabelTransformer(new Transformer<Node, String>() {
            public String transform(Node node) {
                if (node.getAddress() == -1) {
                    return "";
                } else {
//                    return "";
                    return String.valueOf(node.getNwkCurChildren());
                }
            }
        });

        context.setEdgeStrokeTransformer(new Transformer<String, Stroke>() {
            public Stroke transform(String s) {
                if (s.contains(".lended")) {
                    return new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1, new float[]{10, 20}, 5f);
                } else {
                    return new BasicStroke(1);
                }
            }
        });

        final AbstractEdgeShapeTransformer<Node, String> lineEdgeShape = new EdgeShape.Line<Node,String>();
        final AbstractEdgeShapeTransformer<Node, String> quadEdgeShape = new EdgeShape.QuadCurve<Node,String>();

        context.setEdgeShapeTransformer(new AbstractEdgeShapeTransformer<Node, String>() {
            public Shape transform(Context<Graph<Node, String>, String> graphStringContext) {
                if (!graphStringContext.element.contains(".lended")) {
                    return lineEdgeShape.transform(graphStringContext);
                } else {
                    return quadEdgeShape.transform(graphStringContext);
                }
            }
        });
        
    }
}
