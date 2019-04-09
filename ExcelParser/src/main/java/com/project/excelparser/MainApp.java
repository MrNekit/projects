package com.project.excelparser;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.ChartLegend;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LegendPosition;
import org.apache.poi.ss.usermodel.charts.LineChartData;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;

public class MainApp extends Application {

    private Desktop desktop = Desktop.getDesktop();
    private int maxTimes = 0;
    private List<Entity> list = new ArrayList<>();
    private final List<File> filePath = new ArrayList<>();

    @Override
    public void start(final Stage stage) throws Exception {

        stage.setTitle("ExcelParser");

        final FileChooser fileChooser = new FileChooser();
        final Button chooseButton = new Button("Выбрать файл");
        final Button parseButton = new Button("Обработать");
        final Label pathLabel = new Label();

        chooseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                configureFileChooser(fileChooser);
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    filePath.clear();
                    filePath.add(file);
                    System.out.println(file.getPath());
                    pathLabel.setText(file.getAbsolutePath());
                }
            }
        });

        parseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (filePath.size() > 0) {
                    parseExcel(filePath.get(0).getAbsolutePath());
                }
            }
        });

        final GridPane inputGridPane = new GridPane();

        GridPane.setConstraints(chooseButton, 0, 1);
        GridPane.setConstraints(parseButton, 1, 1);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(chooseButton, parseButton);
        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane, pathLabel);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup));
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Выбор файла");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx")
        );
    }

    private void parseExcel(String path) {
        System.out.println("Начало обработки...");
        readExcel(path);

        writeExcel();
        System.out.println("Обработка завершена!");

    }

    private void readExcel(String path) {
        list.clear();
        FileInputStream inputStream = null;
        try {
            int t = 0;
            inputStream = new FileInputStream(new File(path));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Entity entity = new Entity();
                Row row = rowIterator.next();

                Cell cellT = row.getCell(0);

                if (cellT.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    try {
                        Row row2 = sheet.getRow(row.getRowNum() - 2);
                        Row row1 = sheet.getRow(row.getRowNum() - 1);
                        Cell cell1 = row1.getCell(2);
                        cellT = row2.getCell(2);

                        if (cellT.getCellType() == Cell.CELL_TYPE_STRING
                                && cell1.getCellType() == Cell.CELL_TYPE_STRING) {
                            String time = cellT.getStringCellValue();
                            String tmp = time.substring(0, time.indexOf("."));

                            t = Integer.parseInt(tmp);
                        }

                        if (cellT.getCellType() == Cell.CELL_TYPE_NUMERIC
                                && cell1.getCellType() == Cell.CELL_TYPE_STRING) {
                            t = (int) cellT.getNumericCellValue();
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    continue;
                }

                entity.setParT(t);

                for (int i = 0; i < 6; i++) {
                    Cell cell = row.getCell(i);

                    switch (cell.getColumnIndex()) {
                        case 0:
                            entity.setParI((int) cell.getNumericCellValue());
                            break;
                        case 1:
                            entity.setParX((int) cell.getNumericCellValue());
                            break;
                        case 2:
                            entity.setParZ((float) cell.getNumericCellValue());
                            break;
                        case 3:
                            entity.setParQ((float) cell.getNumericCellValue());
                            break;
                        case 4:
                            entity.setParV((float) cell.getNumericCellValue());
                            break;
                        case 5:
                            entity.setParW((float) cell.getNumericCellValue());
                            break;
                    }
                }

                if (entity.getParT() != 0) {
                    list.add(entity);
                    if (entity.getParI() > maxTimes) {
                        maxTimes = entity.getParI();
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void writeExcel() {
        try {
            FileOutputStream outFile = null;
            XSSFWorkbook workbook = new XSSFWorkbook();
            String fileName = filePath.get(0).getName();

            StringBuilder sb = new StringBuilder();
            sb.append(fileName.substring(0, fileName.indexOf(".xlsx"))).append("_new")
                    .append(fileName.substring(fileName.indexOf(".xlsx"), fileName.length()));

            String pathTo = new File(filePath.get(0).getParent(), sb.toString())
                    .getAbsolutePath();
            XSSFSheet sheetQ = workbook.createSheet("Q");
            fillSheet(sheetQ, 'Q');

            XSSFSheet sheetH = workbook.createSheet("H");
            fillSheet(sheetH, 'H');

            XSSFSheet sheetV = workbook.createSheet("V");
            fillSheet(sheetV, 'V');

            XSSFSheet sheetW = workbook.createSheet("W");
            fillSheet(sheetW, 'W');

            File file = new File(pathTo);
            file.getParentFile().mkdirs();
            outFile = new FileOutputStream(file);
            workbook.write(outFile);
            outFile.close();
            System.out.println("Created file: " + file.getAbsolutePath());

            popupWindow(file.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void popupWindow(final String path) {
        Label secondLabel = new Label("Путь к новому файлу: " + path);
        Class<?> clazz = this.getClass();
        final Button openButton = new Button("Открыть");

        openButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    desktop.open(new File(path));
                } catch (IOException ex) {
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        InputStream input = clazz.getResourceAsStream("/success.jpg");

        Image image = new Image(input);

        ImageView imageView = new ImageView(image);

        FlowPane root = new FlowPane();
        root.setPadding(new Insets(20));

        root.getChildren().addAll(imageView, secondLabel, openButton);

        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(root);

        Scene secondScene = new Scene(secondaryLayout);

        Stage newWindow = new Stage();
        newWindow.setTitle("Результат обработки");
        newWindow.setScene(secondScene);
        newWindow.centerOnScreen();

        newWindow.initModality(Modality.APPLICATION_MODAL);

        newWindow.show();
    }

    private String getTitle(char type) {
        switch (type) {
            case 'Q':
                return "Q (м3/с)";
            case 'H':
                return "Н,см";
            case 'V':
                return "V(м/с)";
            case 'W':
                return "W";
            default:
                return "";
        }
    }

    private void fillSheet(XSSFSheet sheet, char type) {
        String title;
        List<Entity> values = new ArrayList();

        title = getTitle(type);
        int rowNum = 1;
        int columnNum = 0;

        Cell cell;
        Row row;

        row = sheet.createRow(rowNum);

        for (int i = 1; i <= maxTimes; i++) {

            rowNum++;
            cell = row.createCell(columnNum, CellType.STRING);
            cell.setCellValue(i);

            cell = row.createCell(columnNum + 1, CellType.STRING);
            cell.setCellValue("время");

            cell = row.createCell(columnNum + 2, CellType.STRING);
            cell.setCellValue(title);

            findAll(values, i, type);

            row = sheet.createRow(rowNum);

            cell = row.createCell(columnNum, CellType.STRING);
            cell.setCellValue("x=" + values.get(0).getParX());

            cell = row.createCell(columnNum + 1, CellType.STRING);
            cell.setCellValue(values.get(0).getParT());

            cell = row.createCell(columnNum + 2, CellType.STRING);
            cell.setCellValue(getValue(values.get(0), type));

            for (int j = 1; j < values.size(); j++) {
                rowNum++;
                row = sheet.createRow(rowNum);

                cell = row.createCell(columnNum + 1, CellType.STRING);
                cell.setCellValue(values.get(j).getParT());

                cell = row.createCell(columnNum + 2, CellType.STRING);
                cell.setCellValue(getValue(values.get(j), type));
            }

            printLineChart(sheet, rowNum, values.size(), title);

            rowNum += 2;
            row = sheet.createRow(rowNum);
        }

    }

    private void findAll(List values, int num, char type) {
        values.clear();

        for (Entity entity : list) {
            if (entity.getParI() == num) {
                values.add(entity);
            }
        }
    }

    private float getValue(Entity entity, char type) {
        switch (type) {
            case 'Q':
                return entity.getParQ();
            case 'H':
                return entity.getParZ();
            case 'V':
                return entity.getParV();
            case 'W':
                return entity.getParW();
            default:
                return 0;
        }
    }

    private void printLineChart(XSSFSheet sheet, int position, int rows, String title) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, position - rows, 20, position);
        XSSFChart chart = drawing.createChart(anchor);
        ChartLegend legend = chart.getOrCreateLegend();

        legend.setPosition(LegendPosition.BOTTOM);
        LineChartData data = chart.getChartDataFactory().createLineChartData();

        // Use a category axis for the bottom axis.
        ChartAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);//chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);//chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(sheet,
                new CellRangeAddress(position - rows + 1, position, 1, 1));
        ChartDataSource<Number> ys2 = DataSources.fromNumericCellRange(sheet,
                new CellRangeAddress(position - rows + 1, position, 2, 2));

        CTTitle ctCategory = chart.getCTChart().getPlotArea().getCatAxArray()[0].addNewTitle();
        setAxisLabel(ctCategory, "время");

        CTTitle ctValue = chart.getCTChart().getPlotArea().getValAxArray()[0].addNewTitle();
        setAxisLabel(ctValue, title);

        data.addSeries(ys1, ys2);
        chart.plot(data, bottomAxis, leftAxis);

//        chart.plot(data);
    }

    private void setAxisLabel(CTTitle ctTitle, String text) {
        ctTitle.addNewLayout();
        ctTitle.addNewOverlay().setVal(false);
        CTTextBody rich = ctTitle.addNewTx().addNewRich();
        rich.addNewBodyPr();
        rich.addNewLstStyle();
        CTTextParagraph p = rich.addNewP();
        p.addNewPPr().addNewDefRPr();
        p.addNewR().setT(text);
        p.addNewEndParaRPr();
    }
}
