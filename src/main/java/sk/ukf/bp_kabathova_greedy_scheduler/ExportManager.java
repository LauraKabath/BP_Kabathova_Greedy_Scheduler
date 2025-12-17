package sk.ukf.bp_kabathova_greedy_scheduler;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Chart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.PrintWriter;

public class ExportManager {
    private final FileChooser fileChooser;

    public ExportManager() {
        fileChooser = new FileChooser();
    }

    public void exportTableViewToCSV(Stage stage, TableView<?> tableView, String initialFilename) {
        if (tableView.getItems().isEmpty()) {
            Toast.show(stage, "Žiadne údaje na exportovanie!", Toast.ToastType.WARNING, 3000);
            return;
        }

        fileChooser.setTitle("Uložte údaje do CSV");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV súbory", "*.csv"));
        fileChooser.setInitialFileName(initialFilename);

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        int rowCount = tableView.getItems().size();
        int columnCount = tableView.getColumns().size();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);

            for (int i = 0; i < columnCount; i++) {
                writer.print(formatCsvField(tableView.getColumns().get(i).getText()));
                if (i < columnCount - 1) writer.print(",");
            }
            writer.println();

            for (int r = 0; r < rowCount; r++) {
                for (int c = 0; c < columnCount; c++) {
                    Object cellData = tableView.getColumns().get(c).getCellData(r);
                    if (cellData != null) writer.print(formatCsvField(cellData.toString()));
                    if (c < columnCount - 1) writer.print(",");
                }
                writer.println();
            }

            Toast.show(stage, "Súbor CSV bol úspešne exportovaný!", Toast.ToastType.SUCCESS, 2500);
        } catch (Exception exception) {
            Toast.show(stage, "Chyba pri exportovaní CSV súboru!", Toast.ToastType.ERROR, 2500);
            System.out.println(exception.getMessage());
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private String formatCsvField(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    public void exportScheduleDiagramToPNG(Stage stage, ScrollPane schedule, String algorithmName) {
        fileChooser.setTitle("Uložiť rozvrh ako PNG");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG obrázky", "*.png"));
        fileChooser.setInitialFileName("rozvrh_" + algorithmName + ".png");

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        try {
            Node content = schedule.getContent();

            SnapshotParameters params = new SnapshotParameters();
            params.setTransform(Transform.scale(1, 1));

            WritableImage image = new WritableImage((int) content.getBoundsInParent().getWidth(), (int) content.getBoundsInParent().getHeight());
            content.snapshot(params, image);
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

            Toast.show(stage, "Rozvrh bol úspešne exportovaný!", Toast.ToastType.SUCCESS, 2500);
        } catch (Exception e) {
            Toast.show(stage, "Chyba pri exportovaní rozvrhu!", Toast.ToastType.ERROR, 2500);
            System.out.println(e.getMessage());
        }
    }

    public void exportSingleChart(Stage stage, Chart chart, String initialFilename) {
        fileChooser.setTitle("Uložiť graf ako PNG");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG obrázky", "*.png"));
        fileChooser.setInitialFileName(initialFilename);

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        try {
            WritableImage image = chart.snapshot(new SnapshotParameters(), null);
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            Toast.show(stage, "Graf bol úspešne exportovaný!", Toast.ToastType.SUCCESS, 2500);
        } catch (Exception e) {
            Toast.show(stage, "Chyba pri exportovaní grafu!", Toast.ToastType.ERROR, 2500);
            System.out.println(e.getMessage());
        }
    }

    public void exportAllCharts(Stage stage, ChartBox chartBox) {
        fileChooser.setTitle("Uložiť všetky grafy ako PNG");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG obrázky", "*.png"));
        fileChooser.setInitialFileName("grafy.png");

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        try {
            WritableImage image = chartBox.snapshot(new SnapshotParameters(), null);
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            Toast.show(stage, "Všetky grafy boli úspešne exportované!", Toast.ToastType.SUCCESS, 2500);
        } catch (Exception e) {
            Toast.show(stage, "Chyba pri exportovaní grafov!", Toast.ToastType.ERROR, 2500);
            System.out.println(e.getMessage());
        }
    }
}
