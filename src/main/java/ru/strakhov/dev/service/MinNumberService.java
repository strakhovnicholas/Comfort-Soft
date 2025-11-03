package ru.strakhov.dev.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class MinNumberService {

    public ResponseEntity<?> findNthMinDigitFromFile(MultipartFile file, int n) {
        if (n <= 0) {
            return ResponseEntity.badRequest().body("N должно быть положительным числом (N >= 1).");
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл не может быть пустым.");
        }

        if (!Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().body("Файл должен быть в формате .xlsx");
        }

        Double result = null;
        String errorMessage = null;
        HttpStatus httpStatus = null;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            PriorityQueue<Double> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());

            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    Double value = cell.getNumericCellValue();
                    if (maxHeap.size() < n) {
                        maxHeap.offer(value);
                    } else if (value < maxHeap.peek()) {
                        maxHeap.poll();
                        maxHeap.offer(value);
                    }
                }
            }

            if (maxHeap.size() == n) {
                result = maxHeap.peek();
            } else {
                int foundCount = maxHeap.size();
                errorMessage = "Недостаточно числовых значений в файле для N=" + n + ". Найдено: " + foundCount;
                httpStatus = HttpStatus.BAD_REQUEST;
            }

        } catch (IOException e) {
            errorMessage = "Ошибка чтения файла: " + e.getMessage();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        if (result != null)
            return ResponseEntity.ok(String.valueOf(result));

        return ResponseEntity.status(httpStatus).body(errorMessage);
    }
}