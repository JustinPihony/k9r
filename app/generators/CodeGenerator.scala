package generators

import models.ProjectDescription
import scala.concurrent.Future
import java.io.File
import java.nio.file.Path
import java.nio.file.Files
import global.Dispatchers.ioDispatcher
import java.nio.charset.StandardCharsets

trait CodeGenerator {

  def generateCode(projectDescription: ProjectDescription, root: Path): Future[Path]

}

abstract class TextCodeGenerator extends CodeGenerator {
  def generateCode(projectDescription: ProjectDescription, root: Path): Future[Path] = {
    Future {
      val content = textResult(projectDescription)
      val file = destFile(projectDescription, root)
      Files.write(file, content.getBytes(StandardCharsets.UTF_8))
      file
    }
  }

  def textResult(projectDescription: ProjectDescription): String
  def destFile(projectDescription: ProjectDescription, root: Path): Path
}

object CodeGenerator {

  val SimpleScalaMainScala = new TextCodeGenerator {
    def textResult(projectDescription: ProjectDescription): String =
      code.SimpleScala.txt.main.render(projectDescription).body

    def destFile(projectDescription: ProjectDescription, root: Path): Path =
      mainPackagePath(root, projectDescription)
        .resolve(s"${projectDescription.name}.scala")
  }

  def mainPackagePath(root: Path, projectDescription: ProjectDescription) =
    root
      .resolve("src/main")
      .resolve(projectDescription.language.languageName)
      .resolve(projectDescription.organization.replace(".", "/"))
      .resolve(projectDescription.name.toLowerCase())

  def testPackagePath(root: Path, projectDescription: ProjectDescription) =
    root
      .resolve("src/main")
      .resolve(projectDescription.language.languageName)
      .resolve(projectDescription.organization.replace(".", "/"))
      .resolve(projectDescription.name.toLowerCase())

}