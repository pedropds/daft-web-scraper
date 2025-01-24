package service

import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait DaftServiceTrait {
  def shutdown()(implicit system: ActorSystem, ec: ExecutionContext): Unit = {
    system.terminate().onComplete {
      case Success(_) => println("ActorSystem terminated successfully.")
      case Failure(error) => println(s"Failed to terminate ActorSystem: ${error.getMessage}")
    }
  }
}
