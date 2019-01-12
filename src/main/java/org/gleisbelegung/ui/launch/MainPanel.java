package org.gleisbelegung.ui.launch;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.gleisbelegung.Plugin;
import org.gleisbelegung.ui.lib.node.ButtonFactory;
import org.gleisbelegung.ui.lib.node.LabelFactory;
import org.gleisbelegung.ui.lib.node.TextFieldFactory;
import org.gleisbelegung.ui.lib.panel.PanelInterface;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.TextColor;


public class MainPanel implements PanelInterface {

    private final Plugin plugin;
    private NodeWrapper<Label> connectionLabel;
    private NodeWrapper<TextField> host;
    private NodeWrapper<Button> connect;
    private NodeWrapper<Button> close;

    public MainPanel(Plugin plugin){
        this.plugin = plugin;
    }

    @Override public Pane init() {
        connectionLabel = LabelFactory.create("IP des SIM-Rechners eingeben:", 16);
        connectionLabel.addStyle(new TextColor("white"));

        host = TextFieldFactory.create("localhost", null, 16);

        Runnable runnableConnect = () -> {
            plugin.tryConnection(host.getNode().getText());
        };
        connect = ButtonFactory.create("Verbinden", 16, runnableConnect);

        Runnable runnableClose = Platform::exit;
        close = ButtonFactory.create("Beenden", 16, runnableClose);

        NodeWrapper<Pane> pane = new NodeWrapper<>(new Pane(
                connectionLabel.getNode(), connect.getNode(), close.getNode(),
                host.getNode())
        );
        return pane.getNode();
    }

    @Override public void setSizes() {

    }

    @Override public void onResize(double width, double height) {
        connectionLabel.getNode()
                .setTranslateX(width / 2.0 - connectionLabel.getNode().getWidth() - 20);
        connectionLabel.getNode()
                .setTranslateY(height / 3.0 - connectionLabel.getNode().getHeight() / 2.0 - 10);

        close.getNode().setTranslateX(width * 0.3 - close.getNode().getWidth()/2.0);
        close.getNode().setTranslateY(height * 0.7 - close.getNode().getHeight()/2.0 - 20);

        connect.getNode().setTranslateX(width * 0.7 - connect.getNode().getWidth()/2.0);
        connect.getNode().setTranslateY(height * 0.7 - connect.getNode().getHeight()/2.0 - 20);

        host.getNode().setTranslateX(width / 2.0);
        host.getNode().setTranslateY(height / 3.0 - host.getNode().getHeight() / 2.0 - 10);
    }

    @Override public void onHide() {

    }

    @Override public void onVisible() {

    }

    public void connectionEstablished(){
        System.out.println("Verbindung erfolgreich");
    }
}
