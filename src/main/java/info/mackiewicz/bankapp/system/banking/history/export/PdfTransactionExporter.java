package info.mackiewicz.bankapp.system.banking.history.export;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * A service class responsible for exporting financial transactions into a PDF file.
 * Implements the {@link TransactionExporter} interface to provide PDF-specific
 * exporting functionality.
 *<p>
 * The resulting PDF contains a transaction history report, including columns
 * for date, amount, type, source account, destination account, title, and status.
 * The file is downloadable as a response entity containing a byte array.
 */
@Service
public class PdfTransactionExporter implements TransactionExporter {

    private static final DateTimeFormatter PDF_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DeviceRgb HEADER_BACKGROUND = new DeviceRgb(140, 0, 50);
    private static final float[] COLUMN_WIDTHS = {150, 80, 80, 120, 120, 200, 80};

    @Override
    public String getFormat() {
        return "pdf";
    }

    /**
     * Exports a list of financial transactions to a PDF document and returns it as a downloadable byte array
     * wrapped in a {@link ResponseEntity}. The generated PDF includes a title and a table detailing transaction
     * information such as date, amount, account details, and status.
     *
     * @param transactions the list of {@link Transaction} objects representing the transaction history to be exported.
     * @return a {@link ResponseEntity} containing the generated PDF document as a byte array,
     *         along with appropriate HTTP headers for file download.
     * @throws RuntimeException if the PDF generation process fails for any reason.
     */
    @Override
    public ResponseEntity<byte[]> exportTransactions(List<Transaction> transactions) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
            Document document = new Document(pdf);

            addTitle(document);
            addTransactionsTable(document, transactions);

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "transactions.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addTitle(Document document) {
        Paragraph title = new Paragraph("Transaction History Report")
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);
    }

    private void addTransactionsTable(Document document, List<Transaction> transactions) {
        Table table = new Table(UnitValue.createPercentArray(COLUMN_WIDTHS));
        table.setWidth(UnitValue.createPercentValue(100));

        addTableHeader(table);
        addTableData(table, transactions);

        document.add(table);
    }

    private void addTableHeader(Table table) {
        String[] headers = {"Date", "Amount", "Type", "From Account", "To Account", "Title", "Status"};
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header))
                    .setBackgroundColor(HEADER_BACKGROUND)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(cell);
        }
    }

    private void addTableData(Table table, List<Transaction> transactions) {
        for (Transaction t : transactions) {
            // Date
            table.addCell(new Cell().add(new Paragraph(t.getDate().format(PDF_DATE_FORMATTER))));
            
            // Amount
            boolean isPositive = t.getType().toString().equals("DEPOSIT") || t.getType().toString().equals("TRANSFER_IN");
            String amount = (isPositive ? "+" : "-") + t.getAmount().toString();
            Cell amountCell = new Cell()
                    .add(new Paragraph(amount))
                    .setFontColor(isPositive ? ColorConstants.GREEN : ColorConstants.RED);
            table.addCell(amountCell);
            
            // Type
            table.addCell(new Cell().add(new Paragraph(t.getType().toString())));
            
            // From Account
            table.addCell(new Cell().add(new Paragraph(
                    Optional.ofNullable(t.getSourceAccount())
                            .map(Account::getId)
                            .map(String::valueOf)
                            .orElse("-")
            )));
            
            // To Account
            table.addCell(new Cell().add(new Paragraph(
                    Optional.ofNullable(t.getDestinationAccount())
                            .map(Account::getId)
                            .map(String::valueOf)
                            .orElse("-")
            )));
            
            // Title
            table.addCell(new Cell().add(new Paragraph(t.getTitle())));
            
            // Status
            table.addCell(new Cell().add(new Paragraph(t.getStatus().toString())));
        }
    }
}