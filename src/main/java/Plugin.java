import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;


public class Plugin extends Application {

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.show();

        try {
            primaryStage.getIcons().add(new Image(Plugin.class.getResourceAsStream(
                    "icon.png")));
        } catch (Exception e) {
            try {
                File f = new File("src/main/resources/icon.png");
                primaryStage.getIcons().add(new Image(new FileInputStream(f)));

            } catch (Exception e1) {
                e.printStackTrace();
                e1.printStackTrace();
            }
        }
    }
}
