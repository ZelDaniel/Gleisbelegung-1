package org.gleisbelegung.ui.launch;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import org.gleisbelegung.Plugin;
import org.gleisbelegung.ui.lib.node.ButtonFactory;
import org.gleisbelegung.ui.lib.node.LabelFactory;
import org.gleisbelegung.ui.lib.node.TextFieldFactory;
import org.gleisbelegung.ui.lib.panel.PanelInterface;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.TextColor;

import java.util.concurrent.TimeUnit;


public class MainPanel implements PanelInterface {

    private final Plugin plugin;
    private NodeWrapper<Label> connectionLabel;
    private NodeWrapper<TextField> host;
    private NodeWrapper<Button> connect;
    private NodeWrapper<Button> close;

    /**
     * used to temporay save the host input text while trying to connect to the simulation
     */
    private String hostAddress;

    public MainPanel(Plugin plugin){
        this.plugin = plugin;
    }

    @Override public Pane init() {
        connectionLabel = LabelFactory.create("Zielrechner:", 16);
        connectionLabel.addStyle(new TextColor("white"));
        connectionLabel.getNode().setTooltip(new Tooltip(("z.B. 127.0.0.1, 192.168.1.100")));

        host = TextFieldFactory.create("127.0.0.1", null, 16);
        host.getNode().setPrefWidth(150);

        Runnable runnableConnect = () -> {
            hostAddress = host.getNode().getText();
            host.getNode().setDisable(true);
            host.getNode().setText("Verbinde...");
            connect.getNode().setDisable(true);
            plugin.tryConnection(hostAddress);
        };
        connect = ButtonFactory.create("Verbinden", 16, runnableConnect);

        Runnable runnableClose = Platform::exit;
        close = ButtonFactory.create("Beenden", 16, runnableClose);

        NodeWrapper<Pane> pane = new NodeWrapper<>(new Pane(
                connectionLabel.getNode(), host.getNode(), close.getNode(), connect.getNode()
                )
        );
        return pane.getNode();
    }

    @Override public void setSizes() {

    }

    @Override public void onResize(double width, double height) {
        connectionLabel.getNode()
                .setTranslateX(width / 2.0 - connectionLabel.getNode().getWidth() - 40);
        connectionLabel.getNode()
                .setTranslateY(height / 3.0 - connectionLabel.getNode().getHeight() / 2.0 - 10);

        close.getNode().setTranslateX(width * 0.3 - close.getNode().getWidth()/2.0);
        close.getNode().setTranslateY(height * 0.7 - close.getNode().getHeight()/2.0 - 20);

        connect.getNode().setTranslateX(width * 0.7 - connect.getNode().getWidth()/2.0);
        connect.getNode().setTranslateY(height * 0.7 - connect.getNode().getHeight()/2.0 - 20);

        host.getNode().setTranslateX(width / 2.0 - 20);
        host.getNode().setTranslateY(height / 3.0 - host.getNode().getHeight() / 2.0 - 10);
    }

    @Override public void onHide() {

    }

    @Override public void onVisible() {

    }

    /**
     * called if the connection to the simulation was established
     */
    void connectionEstablished(){
        host.getNode().setText("Verbunden!");
    }

    /**
     * called if the plugin was unable to connect to the simulation
     */
    void connectionFailed(){
        Runnable r = () -> {
            host.getNode().setText("Keine Verbindung!");

            try {
                Thread.sleep(Plugin.CONNECT_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            host.getNode().setText(hostAddress);
            host.getNode().setDisable(false);
            connect.getNode().setDisable(false);
        };
        Thread t = new Thread(r);
        t.setName("UI_ConnectionFailed");
        t.setDaemon(true);
        t.start();
    }
}
