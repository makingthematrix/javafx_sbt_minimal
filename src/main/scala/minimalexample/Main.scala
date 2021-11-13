package minimalexample

import javafx.application.Application

// uncomment for testing with mvn gluonfx:run / mvn gluonfx:build / mvn gluonfx:nativerun
/*
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.stage.Stage

class Main extends Application {
  override def start(stage: Stage): Unit = {
    stage.setScene(new Scene(new Label("Hello from Scala!"), 200, 200))
    stage.show()
  }
}
*/

object Main extends App {
  // change to classOf[Main] for testing with mvn gluonfx:run / mvn gluonfx:build / mvn gluonfx:nativerun
  Application.launch(classOf[Launcher], args: _*)
}
