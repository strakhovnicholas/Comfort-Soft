package ru.strakhov.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.strakhov.dev.service.MinNumberService;

@Tag(name = "Min Number API", description = "API для поиска N-го минимального числа в файле")
@RestController
@RequestMapping("/api/findMin")
public class MinNumberController {
    private final MinNumberService fileService;

    public MinNumberController(MinNumberService fileService) {
        this.fileService = fileService;
    }

    @Operation(
            summary = "Найти N-е минимальное число",
            description = "Принимает XLSX файл и номер N, возвращает N-е минимальное число из первого столбца."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешно найдено N-е минимальное число",
            content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(type = "number", format = "double", example = "3.0"),
                    examples = @ExampleObject(name = "Успешный результат", value = "3.0")
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации: файл пуст, неверный формат, N больше количества чисел и т.д.",
            content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(type = "string", example = "Недостаточно числовых значений в файле для N=5. Найдено: 3"),
                    examples = @ExampleObject(name = "Ошибка валидации", value = "Недостаточно числовых значений в файле для N=5. Найдено: 3")
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера при обработке файла",
            content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(type = "string", example = "Ошибка чтения файла: ..."),
                    examples = @ExampleObject(name = "Ошибка сервера", value = "Ошибка чтения файла: java.io.IOException: ...")
            )
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> findNthMinDigitFromFile(
            @Parameter(
                    description = "Файл XLSX, содержащий числа в первом столбце",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            )
            @RequestParam("file") MultipartFile incomingFile,

            @Parameter(
                    description = "Позиция (номер) минимального числа (1-е минимальное, 2-е и т.д.)",
                    required = true,
                    schema = @Schema(type = "integer", minimum = "1", example = "5")
            )
            @RequestParam("n") Integer n) {

        return this.fileService.findNthMinDigitFromFile(incomingFile, n);
    }
}