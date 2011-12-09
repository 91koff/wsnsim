package ru.common;

import ru.network.wireless.sensor.Network;
import ru.network.wireless.sensor.Node;
import ru.network.wireless.sensor.RoutingItem;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Yakov
 * Date: 15.03.2010
 * Time: 0:47:23
 * To change this template use File | Settings | File Templates.
 */
public class DefaultVisualizer {

    public static void drawNetwork(final Network network) {

        Runnable runnable = new Runnable() {

            public void run() {
                JFrame frame = new JFrame();
                frame.setLayout(new BorderLayout());

                //размер окна Windows
                final int FRAME_SIZE = 800;
                frame.setSize(FRAME_SIZE+75, FRAME_SIZE+75);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                //считаем коэффициент пропорции
                final double scaleCoefficientX = (double) FRAME_SIZE / network.getFieldBorderX();
                final double scaleCoefficientY = (double) FRAME_SIZE / network.getFieldBorderY();

                JPanel panel = new JPanel() {

                    @Override
                    public void paintComponent(Graphics g) {

                        g.translate(20, 20);

                        for (Node node : network.getAIR()) {

                            int centerX = (int) Math.round((node.getX()) * scaleCoefficientX);
                            int centerY = (int) Math.round((node.getY()) * scaleCoefficientY);

                            if (node.isStartingNode()) {
                                g.setColor(Color.RED);
                                g.fillOval(centerX - 6, centerY - 6, 12, 12);
                            } else if (node.isConnected()) {
                                if (node.isRouterCapable()) {
                                    g.setColor(Color.RED);
                                } else {
                                    g.setColor(Color.GREEN);
                                }
                                g.fillOval(centerX - 2, centerY - 2, 4, 4);
                                g.setColor(Color.BLACK);
                                g.drawString(Integer.toString(node.getAddress()), centerX + 1, centerY + 1);
                            } else if (node.isRouterCapable()) {
                                g.setColor(Color.RED);
                            } else {
                                g.setColor(Color.GREEN);
                            }

                            g.drawOval(centerX - 2, centerY - 2, 4, 4);


                            g.setColor(Color.BLACK);

                            int radius = network.getNodeRange();

                            //считаем координаты центра окружности - области действия узла
                            // int ovalX = (int) Math.round((centerX - radius * scaleCoefficientX));
                            // int ovalY = (int) Math.round((centerY - radius * scaleCoefficientY));
                            //     g.drawOval(ovalX, ovalY, (int)Math.round(radius * 2 * scaleCoefficientX), (int)Math.round(radius * 2 * scaleCoefficientY));
                            //                    g.drawLine(50,50,FRAME_SIZE-50,50);
                            //                    g.drawLine(FRAME_SIZE-50,50,FRAME_SIZE-50, FRAME_SIZE-50);
                            //                    g.drawLine(FRAME_SIZE-50,FRAME_SIZE-50,50,FRAME_SIZE-50);
                            //                    g.drawLine(50,FRAME_SIZE-50,50,50);

                            for (RoutingItem routingItem : node.getRoutingItems()) {
                                int childx = (int) Math.round((routingItem.getChild().getX()) * scaleCoefficientX);
                                int childy = (int) Math.round((routingItem.getChild().getY()) * scaleCoefficientY);
                                g.drawLine(centerX, centerY, childx, childy);
                            }

                        }

                    }
                };
                frame.add(panel, BorderLayout.CENTER);

                frame.setVisible(true);
            }
        };

        SwingUtilities.invokeLater(runnable);

    }
}
