package com.example.basic

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Part
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Get
import io.micronaut.http.multipart.CompletedFileUpload

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@Controller
class FileController {

    private val pathDirectory = "src/main/resources/img"

    @Post(value = "/upload", consumes = [MediaType.MULTIPART_FORM_DATA])
    @Throws(IOException::class)
    fun uploadFile(@Part file: CompletedFileUpload): Boolean {
        val targetFile = File("$pathDirectory/${file.filename}")
        Files.copy(
            file.inputStream,
            targetFile.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
        return true
    }

    @Get(value = "/download")
    @Throws(IOException::class)
    fun downLoadFile(): HttpResponse<ByteArray> {
        val filePath = "$pathDirectory/Wallpaper.jpg"
        val fileBytes = Files.readAllBytes(Path.of(filePath))
        return HttpResponse.ok(fileBytes)
            .header("Content-type", "application/octet-stream")
            .header("Content-disposition", "attachment; filename=\"name.jpg\"")
    }


    @Get(value = "/open/{filename}", produces = [MediaType.IMAGE_JPEG])
    @Throws(IOException::class)
    fun openFile(filename: String): HttpResponse<ByteArray> {
        val filePath = "$pathDirectory/$filename"
        val fileBytes = Files.readAllBytes(Path.of(filePath))
        return HttpResponse.ok(fileBytes)
    }



    @Get(value = "/image/open")
    @Throws(IOException::class)
    fun openImage(): HttpResponse<ByteArray> {
        val filePath = "$pathDirectory/Wallpaper.jpg"
        val fileBytes = Files.readAllBytes(Path.of(filePath))
        return HttpResponse.ok(fileBytes)
            .header("Content-type", "image/jpeg")
    }

}
