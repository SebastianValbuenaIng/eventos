package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.reports.administracion_empresas;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ReportParticipantesAdminEmpresas {
    public byte[] generateReport(
            List<Map<String, Object>> participantes
    ) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Todas las hojas del Excel
            Sheet sheetParticipantes = workbook.createSheet("Participantes");

            getParticipantes(participantes, sheetParticipantes, workbook);

            // Ajustar el tamaño de las columnas
            for (int i = 0; i < 10; i++) {
                sheetParticipantes.autoSizeColumn(i);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private void getParticipantes(List<Map<String, Object>> participantes, Sheet sheet, Workbook workbook) {
        int row = 0;
        row = getStartRowTextHeaders(sheet, row, "Participantes", workbook);
        row = getStartRowTextHeaders(sheet, row, String.format("Total de participantes: %s", participantes.size()), workbook);
        row += 1;
        createTableParticipantes(sheet, participantes, row, workbook);
    }

    private static int getStartRowTextHeaders(Sheet sheet, int startRow, String title, Workbook workbook) {
        Row titleRow = sheet.createRow(startRow++);
        Cell titleCell = titleRow.createCell(0);

        // Crear un estilo para el título
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true); // Establecer la fuente en negrita
        titleFont.setFontHeightInPoints((short) 14); // Tamaño de la fuente (opcional)
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        titleCell.setCellStyle(titleStyle);
        titleCell.setCellValue(title);

        sheet.addMergedRegion(new CellRangeAddress(
                startRow - 1, // Fila inicial (la fila actual donde se creó titleRow)
                startRow - 1, // Fila final (la misma en este caso)
                0, // Columna inicial
                8  // Columna final (ajusta según el rango que desees combinar)
        ));

        return startRow;
    }

    private void createTableParticipantes(Sheet sheet, List<Map<String, Object>> data, int startRow, Workbook workbook) {
        // Crear un estilo de celda con bordes
        CellStyle borderedCellStyle = workbook.createCellStyle();
        borderedCellStyle.setBorderTop(BorderStyle.THIN);
        borderedCellStyle.setBorderBottom(BorderStyle.THIN);
        borderedCellStyle.setBorderLeft(BorderStyle.THIN);
        borderedCellStyle.setBorderRight(BorderStyle.THIN);

        // Crear el header de la tabla
        Row headerRow = sheet.createRow(startRow++);
        String[] headers = {
                "N° Identificación", "Nombre", "Correo",
                "Teléfono", "Año Grado", "Empresa Actual",
                "Cargo Actual", "Restricción Alimentos", "Asistencia Evento"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(borderedCellStyle);
        }

        // Llenar la tabla con los datos
        for (Map<String, Object> entity : data) {
            Row row = sheet.createRow(startRow++);

            String[] keys = {
                    "documento", "nombre", "correo",
                    "telefono", "anio_grado", "empresa_actual",
                    "cargo_actual", "restriccion_alimentos", "asistencia_evento"
            };

            for (int i = 0; i < keys.length; i++) {
                Cell cell = row.createCell(i);
                String cellValue = entity.get(keys[i]) != null ? String.valueOf(entity.get(keys[i])) : "";
                cell.setCellValue(cellValue);
                cell.setCellStyle(borderedCellStyle);
            }
        }
    }
}
