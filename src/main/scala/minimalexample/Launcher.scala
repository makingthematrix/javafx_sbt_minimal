package minimalexample

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.stage.Stage

// this class is used only by SBT
class Launcher extends Application {
  override def start(stage: Stage): Unit = {
    stage.setScene(new Scene(new Label("Hello from Scala!"), 200, 200))
    stage.show()
  }
}
