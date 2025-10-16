// generated from template 'DocHelperTest.ftl' on 2025-10-16T14:41:58.215+02:00
package test.org.fugerit.java.demo.venussamplepdffopwithwatermark;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.fugerit.java.core.io.FileIO;
import org.fugerit.java.demo.venussamplepdffopwithwatermark.DocHelper;
import org.fugerit.java.demo.venussamplepdffopwithwatermark.People;

import org.fugerit.java.doc.base.config.DocConfig;
import org.fugerit.java.doc.base.process.DocProcessContext;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a basic example of Fugerit Venus Doc usage,
 * running this junit will :
 * - creates data to be used in document model
 * - renders the 'document.ftl' template
 * - print the result in markdown format
 *
 * For further documentation :
 * https://github.com/fugerit-org/fj-doc
 *
 * NOTE: This is a 'Hello World' style example, adapt it to your scenario, especially :
 * - change the doc handler and the output mode (here a ByteArrayOutputStream buffer is used)
 */
@Slf4j
class DocHelperTest {

    public static final String ATT_WATERMARK_MODE = "custom";

    public static final String ATT_WATERMARK_MODE_CUSTOM = "custom";


    private File testWorker( String watermarkMode  ) throws Exception {
        String chainId = "document";
        // handler id
        String handlerId = DocConfig.TYPE_PDF;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // creates the doc helper
            DocHelper docHelper = new DocHelper();
            // create custom data for the fremarker template 'document.ftl'
            List<People> listPeople = Arrays.asList(
                    new People("Luthien", "Tinuviel", "Queen"), new People("Thorin", "Oakshield", "King"));

            DocProcessContext context = DocProcessContext.newContext("listPeople", listPeople)
                    .withAtt(ATT_WATERMARK_MODE, watermarkMode );

            // output generation
            docHelper.getDocProcessConfig().fullProcess(chainId, context, handlerId, baos);

            File outputFile = new File( "target/", String.format( "%s-watermark-%s.%s", chainId, watermarkMode, handlerId ) );
            log.info( "delete file? : {} ({})", outputFile.delete(), outputFile );

            if ( ATT_WATERMARK_MODE_CUSTOM.equalsIgnoreCase( watermarkMode ) ) {
                FileIO.writeBytes( addTextWatermark( baos.toByteArray(), "watermark" ), outputFile );
            } else {
                FileIO.writeBytes(baos.toByteArray(), outputFile);
            }



            return outputFile;
        }
    }

    @Test
    void testDocProcessTemplateWatermark() throws Exception {
        File output = this.testWorker( "template" );
        Assertions.assertNotEquals( 0, output.length() );
    }

    @Test
    void testDocProcessCustomHandlerWatermark() throws Exception {
        File output = this.testWorker( ATT_WATERMARK_MODE_CUSTOM );
        Assertions.assertNotEquals( 0, output.length() );
    }

    private static byte[] addTextWatermark( byte[] input, String watermarkText) throws IOException {
        try ( PDDocument document = Loader.loadPDF( input );
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            // Iterate through all pages
            for (PDPage page : document.getPages()) {
                PDPageContentStream contentStream = new PDPageContentStream(
                        document, page, PDPageContentStream.AppendMode.APPEND, true, true);

                // Set transparency
                PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
                graphicsState.setNonStrokingAlphaConstant(0.3f); // 30% opacity
                contentStream.setGraphicsStateParameters(graphicsState);

                // Set font and color
                contentStream.setFont(font, 120);
                contentStream.setNonStrokingColor( 0.2F, 0.2F, 0.2F); // Light gray

                // Calculate position (center of page, diagonal)
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();

                // Position and rotate the text
                contentStream.beginText();
                contentStream.setTextMatrix(
                        Matrix.getRotateInstance(Math.toRadians(45),
                                pageWidth / 5,
                                pageHeight / 5));
                contentStream.showText(watermarkText);
                contentStream.endText();

                contentStream.close();
            }
            document.save(buffer);
            return buffer.toByteArray();
        }
    }

}
