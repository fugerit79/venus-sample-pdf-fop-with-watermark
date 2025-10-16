// generated from template 'DocHelperTest.ftl' on 2025-10-16T14:41:58.215+02:00
package test.org.fugerit.java.demo.venussamplepdffopwithwatermark;

import org.fugerit.java.demo.venussamplepdffopwithwatermark.DocHelper;
import org.fugerit.java.demo.venussamplepdffopwithwatermark.People;

import org.fugerit.java.doc.base.config.DocConfig;
import org.fugerit.java.doc.base.process.DocProcessContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.AllArgsConstructor;

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

    @Test
    void testDocProcess() throws Exception {
        String chainId = "document";
        // handler id
        String handlerId = DocConfig.TYPE_PDF;
        File outputFile = new File( "target/", String.format( "%s.%s", chainId, handlerId ) );
        outputFile.delete();
        try (FileOutputStream baos = new FileOutputStream(outputFile)) {
            // creates the doc helper
            DocHelper docHelper = new DocHelper();
            // create custom data for the fremarker template 'document.ftl'
            List<People> listPeople = Arrays.asList(
                    new People("Luthien", "Tinuviel", "Queen"), new People("Thorin", "Oakshield", "King"));

            // output generation
            docHelper.getDocProcessConfig().fullProcess(chainId,
                    DocProcessContext.newContext("listPeople", listPeople), handlerId, baos);
            Assertions.assertNotEquals(0, outputFile.length() );
        }
    }

}
