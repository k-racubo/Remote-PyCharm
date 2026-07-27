package core

import file.FileContentResponse
import file.GetFileContent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import project.OnProjectClosed
import project.WelcomePacket
import project.close.CloseProjectCommand
import project.list.GetProjectsList
import project.list.ProjectsListResponse
import project.open.OpenProjectCommand
import project.open.ProjectFileTreeResponse
import project.run.ResultOfRunResponse
import project.run.RunCurrentConfigCommand
import project.run.StopCurrentConfigCommand

object ApiJson {
    val instance = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        classDiscriminator = "type"
        isLenient = true

        serializersModule = SerializersModule {
            polymorphic(Command::class) {
                subclass(GetProjectsList::class)
                subclass(RunCurrentConfigCommand::class)
                subclass(GetFileContent::class)
                subclass(OpenProjectCommand::class)
                subclass(CloseProjectCommand::class)
                subclass(StopCurrentConfigCommand::class)
            }

            polymorphic(Response::class) {
                subclass(ProjectsListResponse::class)
                subclass(ProjectFileTreeResponse::class)
                subclass(ResultOfRunResponse::class)
                subclass(ErrorResponse::class)
                subclass(FileContentResponse::class)
            }

            polymorphic(Event::class) {
                subclass(WelcomePacket::class)
                subclass(OnProjectClosed::class)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    val pretty = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
    } // only for logging
}